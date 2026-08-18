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
package com.flexganttfx.demo.showcase;

import com.flexganttfx.demo.DemoBase;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Shows a single {@link DemoBase} inside its own stage. Used when a demo gets launched
 * standalone via its own main method. The demo is presented with the same chrome as
 * inside the showcase application: the showcase fonts, the persisted theme, the showcase
 * stylesheet, a header with name and description, and the optional control panel.
 */
public final class StandaloneDemoLauncher {

    private static final double WIDTH = 1400;
    private static final double HEIGHT = 900;

    private StandaloneDemoLauncher() {
    }

    /**
     * Shows the given demo in the given stage.
     *
     * @param demo the demo to show
     * @param stage  the stage to show the demo in, usually the primary stage
     */
    public static void show(DemoBase demo, Stage stage) {
        Objects.requireNonNull(demo, "demo can not be null");
        Objects.requireNonNull(stage, "stage can not be null");

        ShowcaseApp.loadShowcaseFonts();

        DemoContentView contentView = new DemoContentView(stage);
        contentView.showDemo(demo, null);

        Scene scene = new Scene(contentView, WIDTH, HEIGHT);
        ShowcaseView.applyStandaloneTheme(scene);
        scene.getStylesheets().add(Objects.requireNonNull(StandaloneDemoLauncher.class.getResource("/com/flexganttfx/demo/showcase/showcase.css")).toExternalForm());

        stage.setTitle("FlexGanttFX — " + demo.getName());
        stage.setScene(scene);
        stage.show();
    }
}
