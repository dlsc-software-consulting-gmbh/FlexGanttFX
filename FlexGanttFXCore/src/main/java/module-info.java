/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
/**
 * A very small module used for supporting logging and licensing.
 */
module com.flexganttfx.core {

    requires license4j;
    requires transitive java.logging;

    exports com.flexganttfx.core;
}