/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.covid;

import com.flexganttfx.core.FlexGanttFX;
import javafx.application.Application;
import javafx.stage.Stage;

public class CovidApp extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        new CovidUI(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
