/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.util;

import com.flexganttfx.core.StringUtils;
import javafx.application.Application;
import javafx.scene.Scene;

import java.util.Locale;

public final class ThemingUtil {

    private ThemingUtil() {
    }

    /**
     * Returns {@code true} when the application is currently using an AtlantaFX
     * theme. Detection is done by checking whether the user-agent stylesheet URL
     * contains the string {@code "atlantafx"}. No compile-time dependency on the
     * AtlantaFX library is required.
     *
     * @param scene optional
     */
    public static boolean isAtlantaFXActive(Scene scene) {
        String uas = null;

        if (scene != null) {
            uas = scene.getUserAgentStylesheet();
        }

        if (StringUtils.isBlank(uas)) {
            uas = Application.getUserAgentStylesheet();
        }

        if (uas == null) {
            return false;
        }

        return uas.toLowerCase(Locale.ROOT).contains("atlantafx");
    }
}
