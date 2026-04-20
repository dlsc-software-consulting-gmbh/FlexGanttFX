/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.scene.Node;

public class HelloGanttChartEmpty extends FlexGanttFXSample {

    private GanttChart<HelloRow> gc;

    @Override
    public void dispose() {
        super.dispose();
        gc = null;
    }

    @Override
    protected GanttChart<?> createGanttChart() {
        return gc = new GanttChart<>();
    }

    @Override
    public String getSampleName() {
        return "Gantt Chart (Empty)";
    }

    @Override
    public String getSampleDescription() {
        return "A simple Gantt chart with no layers and no activities.";
    }

    @Override
    public Node getControlPanel() {
        return null;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
