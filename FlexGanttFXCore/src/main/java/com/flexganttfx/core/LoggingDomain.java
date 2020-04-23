/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
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
