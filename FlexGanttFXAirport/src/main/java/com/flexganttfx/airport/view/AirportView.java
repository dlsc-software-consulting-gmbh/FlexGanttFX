/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.airport.view;

import com.flexganttfx.airport.model.AircraftRow;
import com.flexganttfx.airport.model.Flight;
import com.flexganttfx.airport.model.GroundOp;
import com.flexganttfx.airport.model.SampleDataGenerator;
import com.flexganttfx.airport.model.Terminal;
import com.flexganttfx.airport.renderer.FlightRenderer;
import com.flexganttfx.airport.renderer.GroundOpRenderer;
import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.container.DualGanttChartContainer;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Main view for the Airport Ground Operations demo. Displays a
 * {@link DualGanttChartContainer} with the aircraft view on top and the
 * gate/terminal hierarchy below, plus a toolbar, status bar and a simulation
 * toggle.
 */
public class AirportView extends BorderPane {

    private static final DateTimeFormatter SIM_FMT =
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    private final GanttChart<AircraftRow> aircraftChart;
    private final GanttChart<Terminal> gateChart;

    private Timeline simTimeline;
    private Instant simClock;

    public AirportView() {
        Layer flightsLayer = new Layer("Flights");
        Layer groundOpsLayer = new Layer("Ground Operations");

        SampleDataGenerator data = new SampleDataGenerator(flightsLayer, groundOpsLayer);

        // ── Aircraft chart (top) ──────────────────────────────────────────────
        aircraftChart = new GanttChart<>(data.getFleetRoot());
        aircraftChart.getLayers().addAll(flightsLayer, groundOpsLayer);

        GraphicsBase<AircraftRow> aircraftGraphics = aircraftChart.getGraphics();
        aircraftGraphics.setActivityRenderer(Flight.class, GanttLayout.class,
                new FlightRenderer(aircraftGraphics));
        aircraftGraphics.setActivityRenderer(GroundOp.class, GanttLayout.class,
                new GroundOpRenderer(aircraftGraphics));

        // Add sequencing links to the aircraft chart
        data.getLinks().forEach(link -> aircraftChart.getLinks().add(link));

        aircraftChart.getTimeline().showTemporalUnit(ChronoUnit.HOURS, 12);
        aircraftChart.getGraphics().showEarliestActivities();

        // ── Gate chart (bottom) ───────────────────────────────────────────────
        gateChart = new GanttChart<>(data.getTerminalsRoot());
        gateChart.getLayers().addAll(flightsLayer, groundOpsLayer);

        GraphicsBase<Terminal> gateGraphics = gateChart.getGraphics();
        gateGraphics.setActivityRenderer(Flight.class, GanttLayout.class,
                new FlightRenderer(gateGraphics));
        gateGraphics.setActivityRenderer(GroundOp.class, GanttLayout.class,
                new GroundOpRenderer(gateGraphics));

        // ── Dual container ────────────────────────────────────────────────────
        DualGanttChartContainer container = new DualGanttChartContainer(aircraftChart, gateChart);

        // ── Toolbar & status bar ──────────────────────────────────────────────
        GanttChartToolBar<AircraftRow> toolBar = new GanttChartToolBar<>(aircraftChart);

        Label simLabel = new Label("Sim: --:--");
        simLabel.setPadding(new Insets(0, 8, 0, 8));

        ToggleButton simButton = new ToggleButton("▶ Simulate");
        simButton.setOnAction(e -> {
            if (simButton.isSelected()) {
                startSimulation(simButton, simLabel);
            } else {
                stopSimulation(simButton, simLabel);
            }
        });

        HBox simBox = new HBox(6, simButton, simLabel);
        simBox.getStyleClass().add("showcase-gantt-toolbar");
        simBox.setAlignment(Pos.CENTER_LEFT);
        simBox.setPadding(new Insets(4, 8, 4, 8));

        VBox topArea = new VBox(toolBar, simBox);
        VBox.setVgrow(container, Priority.ALWAYS);

        GanttChartStatusBar<AircraftRow> statusBar = new GanttChartStatusBar<>(aircraftChart);

        setTop(topArea);
        setCenter(container);
        setBottom(statusBar);
    }

    private void startSimulation(ToggleButton btn, Label label) {
        simClock = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .plus(6, ChronoUnit.HOURS);

        simTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            simClock = simClock.plus(15, ChronoUnit.MINUTES);
            aircraftChart.getTimeline().showTime(simClock);
            label.setText("Sim: " + SIM_FMT.format(simClock));

            // Stop after reaching end of day
            if (simClock.isAfter(LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .plus(22, ChronoUnit.HOURS))) {
                stopSimulation(btn, label);
                btn.setSelected(false);
            }
        }));
        simTimeline.setCycleCount(Timeline.INDEFINITE);
        simTimeline.play();
        btn.setText("⏹ Stop");
    }

    private void stopSimulation(ToggleButton btn, Label label) {
        if (simTimeline != null) {
            simTimeline.stop();
            simTimeline = null;
        }
        btn.setText("▶ Simulate");
        label.setText("Sim: --:--");
    }
}
