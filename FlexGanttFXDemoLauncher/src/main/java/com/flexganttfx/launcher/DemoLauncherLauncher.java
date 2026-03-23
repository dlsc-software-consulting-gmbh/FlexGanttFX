/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.launcher;

/**
 * Plain launcher that works around the JavaFX module restriction which requires
 * the main class to extend {@code Application} only when launched from an
 * unnamed module.
 */
public class DemoLauncherLauncher {
    public static void main(String[] args) {
        DemoLauncherApp.main(args);
    }
}
