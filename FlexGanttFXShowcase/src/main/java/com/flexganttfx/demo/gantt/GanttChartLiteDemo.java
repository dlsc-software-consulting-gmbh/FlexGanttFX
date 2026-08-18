/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.DemoRow;
import com.flexganttfx.demo.GanttChartDemoBase;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.GanttChartLite;
import javafx.application.Application;

import java.time.Instant;
import java.time.ZonedDateTime;

public class GanttChartLiteDemo extends GanttChartDemoBase {

    private GanttChartLite<DemoRow> gc;

    @Override
    public void dispose() {
        super.dispose();
        gc = null;
    }

    @Override
    protected GanttChartBase<?> createGanttChart() {
        gc = new GanttChartLite<>();
        gc.setScrollBarType(GanttChartBase.ScrollBarType.FIXED_HORIZON);
        gc.setAutoHideScrollBar(false);
        gc.getTimeline().getModel().setHorizonStartTime(Instant.now());
        gc.getTimeline().getModel().setHorizonEndTime(ZonedDateTime.now().plusMonths(4).toInstant());
        for (int i = 0; i < 100; i++) {
            DemoRow row = new DemoRow("Row " + i);
            gc.getRows().add(row);
        }

        return gc;
    }

    @Override
    public String getDescription() {
        return "A Gantt chart without a tree table on its left-hand side.";
    }

    @Override
    public String getName() {
        return "Gantt Chart Lite";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
