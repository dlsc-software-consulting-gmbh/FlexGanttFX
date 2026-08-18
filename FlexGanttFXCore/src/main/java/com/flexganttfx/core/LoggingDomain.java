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
package com.flexganttfx.core;

import java.util.logging.Logger;

/**
 * Various predefined logging domains used for logging different aspects of the
 * framework. Applications should use these loggers instead of creating their own
 * ad-hoc loggers, as this allows the logging output of the framework to be
 * configured in a fine-grained way.
 *
 * <h2>Code Example</h2>
 *
 * <pre>
 * LoggingDomain.RENDERING.fine("drawing activity " + activity.getName());
 *
 * // turn on all framework logging
 * for (Logger logger : LoggingDomain.ALL) {
 *     logger.setLevel(Level.FINE);
 * }
 * </pre>
 *
 * @see LoggingFormatter
 * @since 1.0
 */
public final class LoggingDomain {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private LoggingDomain() {
    }

    private static final String PREFIX = "com.flexganttfx";

    /**
     * A logger used for logging information relevant for the performance of the
     * framework, for example the time needed to draw a single row.
     */
    public static final Logger PERFORMANCE = Logger.getLogger(PREFIX + ".performance");

    /**
     * A logger used for logging configuration information, for example the version
     * number of the framework.
     */
    public static final Logger CONFIG = Logger.getLogger(PREFIX + ".config");

    /**
     * A logger used for logging the interaction with activity repositories, for
     * example the number of activities returned for a time interval.
     */
    public static final Logger REPOSITORY = Logger.getLogger(PREFIX + ".repository");

    /**
     * A logger used for logging changes made to the model, for example rows being
     * added or removed.
     */
    public static final Logger MODEL = Logger.getLogger(PREFIX + ".model");

    /**
     * A logger used for logging the interactive editing of activities, for example
     * the resizing of an activity bar.
     */
    public static final Logger EDITING = Logger.getLogger(PREFIX + ".editing");

    /**
     * A logger used for logging navigation operations, for example scrolling or
     * zooming within the timeline.
     */
    public static final Logger NAVIGATION = Logger.getLogger(PREFIX + ".navigation");

    /**
     * A logger used for logging the rendering of the graphics area, for example the
     * renderer that gets used for a given activity type.
     */
    public static final Logger RENDERING = Logger.getLogger(PREFIX + ".rendering");

    /**
     * A logger used for logging the events fired by the framework.
     */
    public static final Logger EVENTS = Logger.getLogger(PREFIX + ".events");

    /**
     * A logger used for logging drag and drop operations.
     */
    public static final Logger DND = Logger.getLogger(PREFIX + ".dnd");

    /**
     * All logging domains defined by this class. Useful for configuring the logging
     * level of the entire framework at once.
     */
    public static final Logger[] ALL = {PERFORMANCE, REPOSITORY, EDITING, NAVIGATION, EVENTS, RENDERING, CONFIG, DND, MODEL};
}
