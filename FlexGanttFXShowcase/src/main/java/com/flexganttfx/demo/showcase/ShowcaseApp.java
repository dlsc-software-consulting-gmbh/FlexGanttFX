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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Main JavaFX Application entry point for the FlexGanttFX Feature Showcase.
 */
public class ShowcaseApp extends Application {

    private static final String FONTS_BASE = "/com/flexganttfx/demo/showcase/fonts/";

    /**
     * Loads the fonts used by the showcase. Also used when a single demo gets
     * launched standalone via its own main method.
     */
    public static void loadShowcaseFonts() {
        String[] variants = {
            "Inter-Regular.ttf",
            "Inter-Italic.ttf",
            "Inter-Light.ttf",
            "Inter-LightItalic.ttf",
            "Inter-Medium.ttf",
            "Inter-MediumItalic.ttf",
            "Inter-SemiBold.ttf",
            "Inter-SemiBoldItalic.ttf",
            "Inter-Bold.ttf",
            "Inter-BoldItalic.ttf"
        };
        for (String variant : variants) {
            Font.loadFont(ShowcaseApp.class.getResourceAsStream(FONTS_BASE + variant), 13);
        }

        Font.loadFont(ShowcaseApp.class.getResourceAsStream(FONTS_BASE + "JetBrainsMono-Regular.ttf"), 13);
    }

    @Override
    public void start(Stage stage) {
        loadShowcaseFonts();

        ShowcaseView view = new ShowcaseView(stage, getHostServices());

        Scene scene = new Scene(view, 1400, 900);
        // Apply persisted theme (falls back to PrimerDark on first launch)
        ShowcaseView.applyPersistedTheme(scene);

        scene.getStylesheets().add(Objects.requireNonNull(ShowcaseApp.class.getResource("/com/flexganttfx/demo/showcase/showcase.css")).toExternalForm());

        stage.setTitle("FlexGanttFX — Feature Showcase");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
