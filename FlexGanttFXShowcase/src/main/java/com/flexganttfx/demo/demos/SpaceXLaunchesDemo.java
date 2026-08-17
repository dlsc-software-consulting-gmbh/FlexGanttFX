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

import com.flexganttfx.demo.DemoBase;
import com.flexganttfx.spacex.view.SpaceXView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Showcase wrapper for the SpaceX Launches demo.
 */
public class SpaceXLaunchesDemo extends DemoBase {

    @Override
    public String getName() {
        return "SpaceX Launches";
    }

    @Override
    public String getDescription() {
        return "Historical SpaceX launch data from the public SpaceX REST API. Launches are grouped "
                + "by rocket type and launchpad, with activities colored by outcome (success, failure, "
                + "or upcoming). Covers the full launch history from Falcon 1 through Starship.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new SpaceXView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
