/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.showcase;

import com.flexganttfx.extras.util.StageManager;
import com.flexganttfx.core.FlexGanttFX;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Main JavaFX Application entry point for the FlexGanttFX Feature Showcase.
 */
public class ShowcaseApp extends Application {

    private static final String FONTS_BASE = "/com/flexganttfx/demo/showcase/fonts/";

    private void loadInterFonts() {
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
    }

    @Override
    public void start(Stage stage) {
        loadInterFonts();

        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
        }

        // Apply persisted theme (falls back to PrimerDark on first launch)
        ShowcaseView.applyPersistedTheme();

        ShowcaseView view = new ShowcaseView(stage, getHostServices());

        Scene scene = new Scene(view, 1400, 900);
        scene.getStylesheets().add(ShowcaseApp.class.getResource("/com/flexganttfx/demo/showcase/showcase.css").toExternalForm());

        stage.setTitle("FlexGanttFX — Feature Showcase");
        stage.setScene(scene);
        CSSFX.start(scene);
        StageManager.install(stage, "flexganttfx-showcase");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
