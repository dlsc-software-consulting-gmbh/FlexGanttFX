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
package com.flexganttfx.demo;

import com.flexganttfx.demo.showcase.StandaloneSampleLauncher;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * A convenient base class for showcase samples. Extend this class and implement
 * {@link #getPanel(Stage)} at minimum. Optionally override {@link #getControlPanel()}
 * to provide an interactive settings panel.
 *
 * <p>Samples can also be launched standalone via their own main method. In that case
 * the sample gets shown in its own stage with the same look and feel as inside the
 * showcase application.
 */
public abstract class SampleBase extends Application implements Sample {

    @Override
    public void start(Stage primaryStage) {
        StandaloneSampleLauncher.show(this, primaryStage);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Node getControlPanel() {
        return null;
    }

    @Override
    public double getControlPanelDividerPosition() {
        return 0.75;
    }

    @Override
    public String getSampleDescription() {
        return "";
    }

    @Override
    public String getProjectName() {
        return "FlexGanttFX";
    }

    @Override
    public String getProjectVersion() {
        return "";
    }

    @Override
    public String getJavaDocURL() {
        return "https://www.flexganttfx.com/api/index.html";
    }

    @Override
    public String getControlStylesheetURL() {
        return null;
    }

    @Override
    public String getSampleSourceURL() {
        return null;
    }

    @Override
    public void dispose() {
    }

    protected final String code(String code) {
        return "```java\n" + code + "\n```";
    }
}
