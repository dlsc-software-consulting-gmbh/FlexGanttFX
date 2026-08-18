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
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.DemoBase;
import com.flexganttfx.factory.view.FactoryView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Showcase wrapper for the standalone Factory scheduling demo.
 */
public class FactoryDemo extends DemoBase {

    @Override
    public String getName() {
        return "Factory Scheduling";
    }

    @Override
    public String getDescription() {
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

    public static void main(String[] args) {
        Application.launch(args);
    }
}
