/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space;

/**
 * Non-modular launcher that avoids the "JavaFX Application class must extend
 * Application" constraint when launching from a plain executable JAR.
 */
public class SpaceMissionAppLauncher {

    public static void main(String[] args) {
        SpaceMissionApp.main(args);
    }
}
