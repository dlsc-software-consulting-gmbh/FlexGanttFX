/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.extras.properties.view.GanttChartPropertySheet;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.scene.Node;

public class HelloGanttChartEmpty extends FlexGanttFXSample {

    private GanttChart<HelloRow> gc = new GanttChart<>();

    @Override
    protected GanttChart<?> createGanttChart() {
        return gc;
    }

    @Override
    public String getSampleName() {
        return "Gantt Chart (Empty)";
    }

    @Override
    public String getJavaDocURL() {
        return getJavaDocBase() + "com/flexganttfx/view/GanttChart.html";
    }

    @Override
    public Node getControlPanel() {
        return new GanttChartPropertySheet<>(gc);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
