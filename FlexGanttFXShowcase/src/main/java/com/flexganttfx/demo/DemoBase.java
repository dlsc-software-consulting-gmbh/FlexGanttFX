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
package com.flexganttfx.demo;

import com.flexganttfx.demo.showcase.StandaloneDemoLauncher;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * The base class for all demos of the showcase application. Extend this class and implement
 * {@link #getPanel(Stage)} and {@link #getName()} at minimum. Optionally override
 * {@link #getControlPanel()} to provide an interactive settings panel.
 *
 * <p>Demos can also be launched standalone via their own main method. In that case the demo
 * gets shown in its own stage with the same look and feel as inside the showcase application.
 */
public abstract class DemoBase extends Application {

    @Override
    public void start(Stage primaryStage) {
        StandaloneDemoLauncher.show(this, primaryStage);
    }

    /**
     * A short, most likely single-word, name to show to the user - e.g. "Gantt Chart".
     */
    public abstract String getName();

    /**
     * A short, multiple sentence description of the demo.
     */
    public String getDescription() {
        return "";
    }

    /**
     * Returns the main demo panel.
     */
    public abstract Node getPanel(Stage stage);

    /**
     * Returns the panel to display to the user that allows for manipulating the demo. May
     * return null if the demo does not support any interactive settings.
     */
    public Node getControlPanel() {
        return null;
    }

    /**
     * Returns true if this demo requires an AtlantaFX theme to function correctly. When true,
     * the demo will be hidden from the sidebar when the Modena theme is active.
     */
    public boolean requiresAtlantaFX() {
        return false;
    }

    /**
     * Provides a place to dispose of any resources when the demo is deselected.
     */
    public void dispose() {
    }
}
