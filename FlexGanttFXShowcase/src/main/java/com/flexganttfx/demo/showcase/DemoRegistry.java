/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.demo.showcase;

import com.flexganttfx.demo.container.*;
import com.flexganttfx.demo.demos.*;
import com.flexganttfx.demo.gantt.*;
import com.flexganttfx.demo.layout.*;
import com.flexganttfx.demo.model.*;
import com.flexganttfx.demo.timeline.*;
import com.flexganttfx.demo.DemoBase;
import com.jpro.webapi.WebAPI;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Central registry of all showcase categories and demos.
 */
public class DemoRegistry {

    public static final List<DemoCategory> CATEGORIES = buildCategories();

    private static List<DemoCategory> buildCategories() {
        List<Supplier<DemoBase>> ganttDemos = new ArrayList<>(List.of(
                AtlantaFXStylingDemo::new,
                GanttChartDemo::new,
                GanttChartLiteDemo::new,
                GanttChartEmptyDemo::new,
                MultiLineDemo::new,
                LinksStressTestDemo::new,
                RowHeaderColumnDemo::new,
                ScrollBarsDemo::new,
                SystemLayersDemo::new,
                ToolTipDemo::new,
                CanvasBufferDemo::new,
                GraphicsViewDemo::new,
                PrintingDemo::new,
                GlobalActivitiesDemo::new
        ));
        if (!WebAPI.isBrowser()) {
            ganttDemos.add(4, DragAndDropDemo::new);
        }

        return List.of(

            new DemoCategory("Demos", MaterialDesign.MDI_ROCKET, "#FF6B35",
                    List.of(
                            EmiratesDemo::new,
                            MSProjectDemo::new,
                            FactoryDemo::new,
                            HospitalDemo::new,
                            WeatherDemo::new,
                            SpaceMissionDemo::new,
                            AirportDemo::new,
                            SprintDemo::new,
                            F1RaceStrategyDemo::new,
                            EarthquakeDemo::new,
                            NaturalEventsDemo::new,
                            SpaceXLaunchesDemo::new
                    )
            ),

            new DemoCategory("Gantt Chart", MaterialDesign.MDI_CHART_GANTT, "#4A90D9",
                    ganttDemos
            ),

            new DemoCategory("Containers", MaterialDesign.MDI_VIEW_GRID, "#7B68EE",
                    List.of(
                            DualGanttChartContainerDemo::new,
                            DualGanttChartLiteContainerDemo::new,
                            MultiGanttChartContainerDemo::new,
                            MultiGanttChartLiteContainerDemo::new,
                            QuadGanttChartContainerDemo::new,
                            QuadGanttChartLiteContainerDemo::new
                    )
            ),

            new DemoCategory("Layout", MaterialDesign.MDI_VIEW_COLUMN, "#50C878",
                    List.of(
                            ChartLayoutDemo::new,
                            AgendaLayoutDemo::new,
                            MixedLayoutsDemo::new
                    )
            ),

            new DemoCategory("Model", MaterialDesign.MDI_DATABASE, "#FF8C00",
                    List.of(
                            GanttChartModelDemo::new,
                            GanttChartLiteModelDemo::new,
                            LazyLoadingDemo::new,
                            LinksDemo::new,
                            TimeZonesDemo::new
                    )
            ),

            new DemoCategory("Timeline", MaterialDesign.MDI_CHART_TIMELINE, "#E64980",
                    List.of(
                            ChronoUnitTimelineDemo::new,
                            SimpleUnitTimelineDemo::new
                    )
            )
        );
    }
}
