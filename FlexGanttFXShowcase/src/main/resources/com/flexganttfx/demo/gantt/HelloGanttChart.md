This sample focuses on the standard `GanttChart` control with its combined tree-table and graphics area. It is a good starting point for understanding how rows, layers, activities, and the timeline work together in the full-featured chart variant.

```java
/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class HelloGanttChart extends FlexGanttFXSample {

    private GanttChart<HelloRow> gc;

    @Override
    public void dispose() {
        super.dispose();
        gc = null;
    }

    @Override
    protected GanttChart<?> createGanttChart() {
        gc = new GanttChart<>();

        HelloRow root = new HelloRow("root");

        Layer layer = new Layer("layer");
        gc.getLayers().add(layer);
        gc.setAutoHideScrollBar(false);

        gc.getTimeline().getModel().setHorizonStartTime(ZonedDateTime.now().minusMonths(3).truncatedTo(ChronoUnit.DAYS).toInstant());
        gc.getTimeline().getModel().setHorizonEndTime(ZonedDateTime.now().plusMonths(3).truncatedTo(ChronoUnit.DAYS).toInstant());

        HelloActivity activity = new HelloActivity();
        activity.setStartTime(Instant.now());
        activity.setEndTime(Instant.now().plus(Duration.ofDays(7)));
        root.addActivity(layer, activity);

        for (int i = 0; i < 200; i++) {
            HelloRow row = new HelloRow("Row " + (i + 1));
            row.setHeight(20 + Math.random() * 100);
            root.getChildren().add(row);
        }

        gc.setRoot(root);

        return gc;
    }

    @Override
    public String getSampleName() {
        return "Gantt Chart";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
```
