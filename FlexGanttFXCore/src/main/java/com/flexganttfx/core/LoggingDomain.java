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

import java.util.logging.Logger;

/**
 * Various predefined logging domains used for logging different aspects of the
 * framework.
 *
 * @since 1.0
 */
public final class LoggingDomain {

    private static final String PREFIX = "com.flexganttfx";

    public static final Logger PERFORMANCE = Logger.getLogger(PREFIX + ".performance");

    public static final Logger CONFIG = Logger.getLogger(PREFIX + ".config");

    public static final Logger REPOSITORY = Logger.getLogger(PREFIX + ".repository");

    public static final Logger MODEL = Logger.getLogger(PREFIX + ".model");

    public static final Logger EDITING = Logger.getLogger(PREFIX + ".editing");

    public static final Logger NAVIGATION = Logger.getLogger(PREFIX + ".navigation");

    public static final Logger RENDERING = Logger.getLogger(PREFIX + ".rendering");

    public static final Logger EVENTS = Logger.getLogger(PREFIX + ".events");

    public static final Logger DND = Logger.getLogger(PREFIX + ".dnd");

    public static final Logger[] ALL = {PERFORMANCE, REPOSITORY, EDITING, NAVIGATION, EVENTS, RENDERING, CONFIG, DND, MODEL};
}
