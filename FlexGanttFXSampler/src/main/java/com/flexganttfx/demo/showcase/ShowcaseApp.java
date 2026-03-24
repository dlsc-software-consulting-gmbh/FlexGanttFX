/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.showcase;

import com.flexganttfx.core.FlexGanttFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX Application entry point for the FlexGanttFX Feature Showcase.
 */
public class ShowcaseApp extends Application {

    @Override
    public void start(Stage stage) {
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
        }

        ShowcaseView view = new ShowcaseView(stage, getHostServices());

        Scene scene = new Scene(view, 1400, 900);
        scene.getStylesheets().add(
            ShowcaseApp.class.getResource("/com/flexganttfx/demo/showcase/showcase.css").toExternalForm()
        );

        stage.setTitle("FlexGanttFX — Feature Showcase");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
