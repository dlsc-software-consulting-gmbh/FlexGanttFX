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
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.scene.Node;

public class GanttChartEmptyDemo extends GanttChartDemoBase {

    private GanttChart<DemoRow> gc;

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
    public String getName() {
        return "Gantt Chart (Empty)";
    }

    @Override
    public String getDescription() {
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
