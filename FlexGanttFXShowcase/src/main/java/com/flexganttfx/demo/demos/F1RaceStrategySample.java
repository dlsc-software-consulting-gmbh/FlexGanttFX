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
import com.flexganttfx.f1.view.F1View;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Sampler wrapper for the Formula 1 Race Strategy demo.
 */
public class F1RaceStrategySample extends FlexGanttFXSampleBase {

    @Override
    public String getSampleName() {
        return "F1 Race Strategy";
    }

    @Override
    public String getSampleDescription() {
        return "Live Formula 1 tire stint data from the OpenF1 API. Each row represents a driver "
                + "and the chart displays tire stints colored by compound (soft, medium, hard, "
                + "intermediate, wet). Select a season and race from the toolbar to load data in real time.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new F1View();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }
}
