/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.extras.properties.view.GanttChartPropertySheet;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.GanttChartLite;
import javafx.application.Application;
import javafx.scene.Node;

public class HelloGanttChartLite extends FlexGanttFXSample {

    private GanttChartLite<HelloRow> gc;

    @Override
    public void dispose() {
        super.dispose();
        gc = null;
    }

    @Override
    protected GanttChartBase<?> createGanttChart() {
        gc = new GanttChartLite<>();
        for (int i = 0; i < 100; i++) {
            HelloRow row = new HelloRow("Row " + i);
            gc.getRows().add(row);
        }

        return gc;
    }

    @Override
    public String getSampleName() {
        return "Gantt Chart Lite";
    }

    @Override
    public Node getControlPanel() {
        return new GanttChartPropertySheet<>(gc);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
