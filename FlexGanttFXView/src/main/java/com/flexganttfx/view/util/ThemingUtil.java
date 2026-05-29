/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
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
