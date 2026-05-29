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
import com.flexganttfx.factory.view.FactoryView;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Sampler wrapper for the standalone Factory scheduling demo.
 */
public class FactorySample extends FlexGanttFXSampleBase {

    @Override
    public String getSampleName() {
        return "Factory Scheduling";
    }

    @Override
    public String getSampleDescription() {
        return "A manufacturing floor demo showing production lines, machines, and jobs " +
               "with colour-coded status (Scheduled, In Progress, Done, Delayed). " +
               "Demonstrates GanttChart with a custom activity renderer.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new FactoryView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }
}
