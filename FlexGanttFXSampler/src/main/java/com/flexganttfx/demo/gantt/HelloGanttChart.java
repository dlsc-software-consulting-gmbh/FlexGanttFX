/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.extras.properties.view.GanttChartConfigurationView;
import com.flexganttfx.model.Layer;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.scene.Node;

import java.time.Duration;
import java.time.Instant;

public class HelloGanttChart extends FlexGanttFXSample {

    private GanttChart<HelloRow> gc = new GanttChart<>();

    @Override
    protected GanttChart<?> createGanttChart() {
        HelloRow root = new HelloRow("root");

        Layer layer = new Layer("layer");
        gc.getLayers().add(layer);

        HelloActivity activity = new HelloActivity();
        activity.setStartTime(Instant.now());
        activity.setEndTime(Instant.now().plus(Duration.ofDays(7)));
        root.addActivity(layer, activity);

        for (int i = 0; i < 200; i++) {
            HelloRow row = new HelloRow("Row " + (i + 1));
            root.getChildren().add(row);
        }

        gc.setRoot(root);

        return gc;
    }

    @Override
    public String getSampleName() {
        return "Gantt Chart";
    }

    @Override
    public String getJavaDocURL() {
        return getJavaDocBase() + "com/flexganttfx/view/GanttChart.html";
    }

    @Override
    public Node getControlPanel() {
        return new GanttChartConfigurationView(gc);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
