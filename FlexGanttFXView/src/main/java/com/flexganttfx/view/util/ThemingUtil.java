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
package com.flexganttfx.view.util;

import com.flexganttfx.core.StringUtils;
import javafx.application.Application;
import javafx.scene.Scene;

import java.util.Locale;

/**
 * Utility methods for working with the active application theme.
 * The current implementation focuses on detecting whether AtlantaFX styling is in use.
 */
public final class ThemingUtil {

    private ThemingUtil() {
    }

    /**
     * Returns {@code true} when the application is currently using an AtlantaFX
     * theme. Detection is done by checking whether the user-agent stylesheet URL
     * contains the string {@code "atlantafx"}. No compile-time dependency on the
     * AtlantaFX library is required.
     *
     * @param scene the scene whose user-agent stylesheet will be inspected, may be
     *              {@code null} in which case the application-wide user-agent
     *              stylesheet is used
     * @return {@code true} if an AtlantaFX theme is active
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
