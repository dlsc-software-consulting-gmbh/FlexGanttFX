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
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.space.view.SpaceMissionView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Sampler wrapper for the standalone Space Mission Control Center demo.
 */
public class SpaceMissionSample extends FlexGanttFXSampleBase {

    @Override
    public String getSampleName() {
        return "Space Mission Control";
    }

    @Override
    public String getSampleDescription() {
        return "A dual-chart mission control demo showing spacecraft manoeuvres, contact windows, "
            + "and maintenance operations in one chart alongside ground-station scheduling in a second. "
            + "Demonstrates DualGanttChartContainer, multiple layers, ChartLayout for burn-delta "
            + "visualisation, and a RadarView / LayersView side panel.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new SpaceMissionView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
