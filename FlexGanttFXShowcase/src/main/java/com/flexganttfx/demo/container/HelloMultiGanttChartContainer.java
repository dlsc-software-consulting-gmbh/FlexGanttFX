/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.container;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.container.MultiGanttChartContainer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HelloMultiGanttChartContainer extends FlexGanttFXSampleBase {

    class DemoRow extends Row<DemoRow, DemoRow, Activity> {
        public DemoRow(String name) {
            super(name);
        }
    }

    private MultiGanttChartContainer multiGanttChart;
    private final List<Entry> entries = new ArrayList<>();
    private GanttChart<DemoRow> masterGC;

    @Override
    public Node getPanel(Stage stage) {
        multiGanttChart = new MultiGanttChartContainer();
        entries.clear();

        masterGC = new GanttChart<>(new DemoRow("Master"));
        masterGC.getGraphics().setShowVerticalCursor(true);
        masterGC.setId("gantt-master");

        for (int i = 0; i < 3; i++) {
            DemoRow root = new DemoRow("Gantt #" + (i + 1));
            GanttChart<DemoRow> gc = new GanttChart<>(root);
            gc.setId("gantt-" + i);
            Entry entry = new Entry();
            entry.name = "Gantt #" + (i + 1);
            entry.gc = gc;
            entries.add(entry);
        }

        multiGanttChart.getGanttCharts().add(masterGC);
        multiGanttChart.getGanttCharts().addAll(entries.stream().map(entry -> entry.gc).collect(Collectors.toList()));
        multiGanttChart.resetDividerPositions();

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(multiGanttChart);
        borderPane.setTop(new GanttChartToolBar<>(masterGC));

        return borderPane;
    }

    @Override
    public void dispose() {
        super.dispose();
        multiGanttChart = null;
        entries.clear();
        masterGC = null;

    }

    class Entry {
        String name;
        GanttChart<DemoRow> gc;
        ToggleButton toggleButton;
    }

    @Override
    public Node getControlPanel() {
        HBox box = new HBox(10);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        for (Entry entry : entries) {
            ToggleButton button = new ToggleButton(entry.name);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setSelected(true);
            button.selectedProperty().addListener(it -> updateContainer());
            entry.toggleButton = button;
            box.getChildren().add(button);
        }

        return box;
    }

	private void updateContainer() {
        multiGanttChart.getGanttCharts().setAll(
                entries.stream()
                        .filter(entry -> entry.toggleButton != null && entry.toggleButton.isSelected())
                        .map(entry -> entry.gc)
                        .collect(Collectors.toList()));
        multiGanttChart.getGanttCharts().add(0, masterGC);
        Platform.runLater(() -> multiGanttChart.resetDividerPositions());
    }

    @Override
    public String getSampleName() {
        return "Multi";
    }

    @Override
    public String getSampleDescription() {
        return "The multi Gantt chart container class can be used to display an arbitrary number "
                + "of Gantt charts. The selected charts in the toggle buttons below will be shown inside "
                + "the Gantt chart container";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
