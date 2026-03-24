/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.factory;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.factory.view.FactoryView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the FlexGanttFX Factory scheduling demo.
 */
public class FactoryApp extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
        }
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new FactoryView(), 1200, 700);
        stage.setScene(scene);
        stage.setTitle("FlexGanttFX – Factory Scheduling Demo");
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
