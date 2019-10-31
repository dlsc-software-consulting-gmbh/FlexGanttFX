/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.view;

import com.flexganttfx.view.GanttChartBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class GanttChartConfigurationView extends TabPane  {

    private GanttChartPropertySheet controlsSheet = new GanttChartPropertySheet();
    private GanttChartPropertySheet backgroundLayersSheet = new GanttChartPropertySheet();
    private GanttChartPropertySheet foregroundLayersSheet = new GanttChartPropertySheet();
    private GanttChartPropertySheet renderersSheet = new GanttChartPropertySheet();

    public GanttChartConfigurationView() {
        setSide(Side.RIGHT);

        Tab controlsTab = new Tab("Controls", controlsSheet);
        Tab backgroundLayersTab = new Tab("Background Layers", backgroundLayersSheet);
        Tab foregroundLayersTab = new Tab("Foreground Layers", foregroundLayersSheet);
        Tab renderersTab = new Tab("Renderers", renderersSheet);

        getTabs().setAll(controlsTab, backgroundLayersTab, foregroundLayersTab, renderersTab);

        ganttChart.addListener(it -> update());
    }

    public GanttChartConfigurationView(GanttChartBase<?> ganttChart) {
        this();
        setGanttChart(ganttChart);
    }

    public void update() {
        GanttChartBase ganttChart = getGanttChart();
        controlsSheet.getTargets().setAll(ganttChart);
        backgroundLayersSheet.getTargets().setAll(ganttChart.getGraphics().getBackgroundSystemLayers());
        foregroundLayersSheet.getTargets().setAll(ganttChart.getGraphics().getForegroundSystemLayers());
        renderersSheet.getTargets().setAll(ganttChart.getGraphics().getAllActivityRenderers());
    }

    private final ObjectProperty<GanttChartBase> ganttChart = new SimpleObjectProperty<>(this, "ganttChart");

    public final GanttChartBase getGanttChart() {
        return ganttChart.get();
    }

    public final ObjectProperty<GanttChartBase> ganttChartProperty() {
        return ganttChart;
    }

    public final void setGanttChart(GanttChartBase ganttChart) {
        this.ganttChart.set(ganttChart);
    }
}
