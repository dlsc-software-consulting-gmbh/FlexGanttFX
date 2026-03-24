/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.factory;

/**
 * Plain launcher that works around the JavaFX module restriction which
 * requires the main class to extend {@link javafx.application.Application}
 * only when launched from an unnamed module.
 */
public class FactoryAppLauncher {

    public static void main(String[] args) {
        FactoryApp.main(args);
    }
}
