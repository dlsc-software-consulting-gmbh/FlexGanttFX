/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.view;

import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.extras.LayersView;
import com.flexganttfx.extras.RadarView;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.space.model.ContactWindow;
import com.flexganttfx.space.model.GroundStation;
import com.flexganttfx.space.model.MaintenanceOp;
import com.flexganttfx.space.model.Maneuver;
import com.flexganttfx.space.model.MissionControl;
import com.flexganttfx.space.model.SampleDataGenerator;
import com.flexganttfx.space.model.ScienceOp;
import com.flexganttfx.space.model.Spacecraft;
import com.flexganttfx.space.model.SpaceFleet;
import com.flexganttfx.space.model.TelemetryActivity;
import com.flexganttfx.space.renderer.ContactWindowRenderer;
import com.flexganttfx.space.renderer.MaintenanceOpRenderer;
import com.flexganttfx.space.renderer.ManeuverRenderer;
import com.flexganttfx.space.renderer.ScienceOpRenderer;
import com.flexganttfx.space.renderer.TelemetryRenderer;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.container.DualGanttChartContainer;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Main view for the Space Mission Control demo. Contains:
 * <ul>
 *   <li>A {@link DualGanttChartContainer} with a spacecraft chart (top) and a
 *       ground-station chart (bottom).</li>
 *   <li>A right panel with a {@link RadarView} and a {@link LayersView}.</li>
 *   <li>A {@link GanttChartToolBar} and a {@link GanttChartStatusBar}.</li>
 *   <li>A real-time now-line that updates every second.</li>
 * </ul>
 */
public class SpaceMissionView extends VBox {

    private final GanttChart<Spacecraft>    spacecraftChart;
    private final GanttChart<GroundStation> groundChart;
    private final GanttChartToolBar<Spacecraft>    toolBar;
    private final GanttChartStatusBar<Spacecraft>  statusBar;

    public SpaceMissionView() {
        // ---------- Layers ----------
        Layer contactLayer    = new Layer("Contact Windows");
        Layer maneuverLayer   = new Layer("Maneuvers");
        Layer scienceLayer    = new Layer("Science Ops");
        Layer maintenanceLayer = new Layer("Maintenance");
        Layer telemetryLayer  = new Layer("Telemetry");

        // ---------- Sample data ----------
        SampleDataGenerator data = new SampleDataGenerator(
                contactLayer, maneuverLayer, scienceLayer, maintenanceLayer, telemetryLayer);

        SpaceFleet    fleet          = data.getFleet();
        MissionControl missionControl = data.getMissionControl();

        // ---------- Spacecraft chart (primary / top) ----------
        // fleet already has its children populated by SampleDataGenerator
        spacecraftChart = new GanttChart<>(fleet);
        spacecraftChart.getLayers().addAll(contactLayer, maneuverLayer, scienceLayer, maintenanceLayer, telemetryLayer);

        // Set ChartLayout on the dedicated telemetry row (last child of fleet)
        Spacecraft telemetryRow = fleet.getChildren().get(fleet.getChildren().size() - 1);
        ChartLayout telemetryLayout = new ChartLayout();
        telemetryLayout.setMinValue(0.0);
        telemetryLayout.setMaxValue(1.0);
        telemetryRow.setLayout(telemetryLayout);

        configureSpacecraftChart(spacecraftChart);

        // ---------- Ground station chart (secondary / bottom) ----------
        // missionControl already has its children populated by SampleDataGenerator
        groundChart = new GanttChart<>(missionControl);
        groundChart.getLayers().add(contactLayer);
        groundChart.getGraphics().setActivityRenderer(
                ContactWindow.class, GanttLayout.class,
                new ContactWindowRenderer(groundChart.getGraphics()));
        groundChart.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 30);

        // ---------- Activity links ----------
        data.getLinks().forEach(link -> spacecraftChart.getGraphics().getLinks().add(link));

        // ---------- DualGanttChartContainer ----------
        DualGanttChartContainer dual = new DualGanttChartContainer(spacecraftChart, groundChart);
        VBox.setVgrow(dual, Priority.ALWAYS);

        // ---------- Toolbar & status bar ----------
        toolBar   = new GanttChartToolBar<>(spacecraftChart);
        statusBar = new GanttChartStatusBar<>(spacecraftChart);

        // ---------- Right panel: RadarView + LayersView ----------
        VBox rightPanel = buildRightPanel(spacecraftChart.getGraphics());

        // ---------- Center layout ----------
        BorderPane center = new BorderPane();
        center.setCenter(dual);
        center.setRight(rightPanel);
        VBox.setVgrow(center, Priority.ALWAYS);

        // ---------- Assemble ----------
        getChildren().addAll(toolBar, center, statusBar);

        // ---------- Real-time NowLine ----------
        startNowLineTimer();
    }

    @SuppressWarnings("unchecked")
    private void configureSpacecraftChart(GanttChart<Spacecraft> chart) {
        GraphicsBase<Spacecraft> graphics = chart.getGraphics();

        graphics.setActivityRenderer(ContactWindow.class,  GanttLayout.class,
                new ContactWindowRenderer(graphics));
        graphics.setActivityRenderer(Maneuver.class,       GanttLayout.class,
                new ManeuverRenderer(graphics));
        graphics.setActivityRenderer(ScienceOp.class,      GanttLayout.class,
                new ScienceOpRenderer(graphics));
        graphics.setActivityRenderer(MaintenanceOp.class,  GanttLayout.class,
                new MaintenanceOpRenderer(graphics));
        graphics.setActivityRenderer(TelemetryActivity.class, ChartLayout.class,
                new TelemetryRenderer(graphics));

        // Show row headers
        graphics.setShowRowHeaders(true);
        graphics.setRowHeadersWidth(120);

        chart.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 30);
        chart.getTimeline().getModel().setNow(Instant.now());

        // Show earliest activities so the data is visible on startup
        chart.getGraphics().showEarliestActivities();
    }

    private VBox buildRightPanel(GraphicsBase<Spacecraft> graphics) {
        RadarView<Spacecraft>  radarView  = new RadarView<>();
        LayersView<Spacecraft> layersView = new LayersView<>();

        radarView.setGraphics(graphics);
        layersView.setGraphics(graphics);

        radarView.setPrefHeight(180);
        radarView.setPrefWidth(220);
        layersView.setPrefWidth(220);

        TitledPane radarPane  = new TitledPane("Radar Overview", radarView);
        TitledPane layersPane = new TitledPane("Layers", layersView);
        radarPane.setCollapsible(false);
        layersPane.setCollapsible(false);

        Label title = new Label("Mission Control Panel");
        title.setStyle("-fx-font-weight: bold; -fx-padding: 4 8 4 8;");

        VBox panel = new VBox(4, title, new Separator(Orientation.HORIZONTAL),
                radarPane, layersPane);
        panel.setPadding(new Insets(4));
        panel.setPrefWidth(240);
        return panel;
    }

    private void startNowLineTimer() {
        javafx.animation.Timeline timer = new javafx.animation.Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    Instant now = Instant.now();
                    spacecraftChart.getTimeline().getModel().setNow(now);
                    groundChart.getTimeline().getModel().setNow(now);
                }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    public GanttChart<Spacecraft> getSpacecraftChart() {
        return spacecraftChart;
    }

    public GanttChart<GroundStation> getGroundChart() {
        return groundChart;
    }

    public GanttChartToolBar<Spacecraft> getToolBar() {
        return toolBar;
    }

    public GanttChartStatusBar<Spacecraft> getStatusBar() {
        return statusBar;
    }
}
