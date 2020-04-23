/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
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

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class HelloGanttChartLite extends FlexGanttFXSample {

    private GanttChartLite<HelloRow> ganttChartLite = new GanttChartLite<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    @Override
    protected GanttChartBase<?> createGanttChart() {
        for (int i=0; i<100; i++) {
            HelloRow row = new HelloRow("Row " + i);
            ganttChartLite.getRows().add(row);
        }

        return ganttChartLite;
    }

    @Override
    public String getSampleName() {
        return "Gantt Chart Lite";
    }

    @Override
    public String getJavaDocURL() {
        return getJavaDocBase() + "com/flexganttfx/view/GanttChartLite.html";
    }

    @Override
    public Node getControlPanel() {
        return new GanttChartPropertySheet<>(ganttChartLite);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
