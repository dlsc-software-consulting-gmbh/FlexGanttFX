/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.showcase;

import com.flexganttfx.demo.container.*;
import com.flexganttfx.demo.demos.*;
import com.flexganttfx.demo.gantt.*;
import com.flexganttfx.demo.layout.*;
import com.flexganttfx.demo.model.*;
import com.flexganttfx.demo.timeline.*;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.util.List;

/**
 * Central registry of all showcase categories and samples.
 */
public class SampleRegistry {

    public static final List<SampleCategory> CATEGORIES = List.of(

            new SampleCategory("Demos", MaterialDesign.MDI_ROCKET, "#FF6B35",
                    List.of(
                            EmiratesSample::new,
                            MSProjectSample::new,
                            FactorySample::new,
                            WeatherSample::new,
                            SpaceMissionSample::new,
                            AirportSample::new,
                            SprintSample::new
                    )
            ),

            new SampleCategory("Gantt Chart", MaterialDesign.MDI_CHART_GANTT, "#4A90D9",
                    List.of(
                            HelloGanttChart::new,
                            HelloGanttChartLite::new,
                            HelloGanttChartEmpty::new,
                            HelloDragAndDrop::new,
                            HelloMultiLine::new,
                            HelloLinksStressTest::new,
                            HelloRowHeaderColumn::new,
                            HelloScrollBars::new,
                            HelloSystemLayers::new,
                            HelloToolTip::new,
                            HelloCanvasBuffer::new,
                            HelloGraphicsView::new,
                            HelloPrinting::new,
                            HelloGlobalActivities::new
                    )
            ),

            new SampleCategory("Containers", MaterialDesign.MDI_VIEW_GRID, "#7B68EE",
                    List.of(
                            HelloDualGanttChartContainer::new,
                            HelloDualGanttChartLiteContainer::new,
                            HelloMultiGanttChartContainer::new,
                            HelloMultiGanttChartLiteContainer::new,
                            HelloQuadGanttChartContainer::new,
                            HelloQuadGanttChartLiteContainer::new
                    )
            ),

            new SampleCategory("Layout", MaterialDesign.MDI_VIEW_COLUMN, "#50C878",
                    List.of(
                            HelloChartLayout::new,
                            HelloAgendaLayout::new,
                            HelloMixedLayouts::new
                    )
            ),

            new SampleCategory("Model", MaterialDesign.MDI_DATABASE, "#FF8C00",
                    List.of(
                            HelloGanttChartModel::new,
                            HelloGanttChartLiteModel::new,
                            HelloLazyLoading::new,
                            HelloLinks::new,
                            HelloTimeZones::new
                    )
            ),

            new SampleCategory("Timeline", MaterialDesign.MDI_CHART_TIMELINE, "#E64980",
                    List.of(
                            HelloChronoUnitTimeline::new,
                            HelloSimpleUnitTimeline::new
                    )
            )
    );
}
