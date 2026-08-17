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
package com.flexganttfx.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Utility class for setting the license key.
 *
 * @since 1.0
 */
public final class FlexGanttFX {

    private static String version;

    /**
     * Returns the FlexGanttFX version number in the format major.minor.bug
     * (1.0.0).
     *
     * @return the FlexGanttFX version number
     * @since 1.0
     */
    public static String getVersion() {
        if (version == null) {
            InputStream stream = FlexGanttFX.class.getResourceAsStream("version.properties");
            Properties props = new Properties();
            try {
                props.load(stream);
            } catch (IOException ex) {
                LoggingDomain.CONFIG.throwing(FlexGanttFX.class.getName(), "getVersion()", ex);
            }
            version = props.getProperty("flexganttfx.version", "1.0.0");

            LoggingDomain.CONFIG.info("FlexGanttFX Version: " + version);
        }
        return version;
    }
}