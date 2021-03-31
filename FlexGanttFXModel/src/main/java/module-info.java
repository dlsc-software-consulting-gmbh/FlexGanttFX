/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
/**
 * Contains all model classes used for the various Gantt chart controls.
 */
module com.flexganttfx.model {

    requires java.logging;
    requires transitive javafx.base;

    requires transitive com.flexganttfx.core;

    exports com.flexganttfx.model;
    exports com.flexganttfx.model.activity;
    exports com.flexganttfx.model.calendar;
    exports com.flexganttfx.model.dateline;
    exports com.flexganttfx.model.exception;
    exports com.flexganttfx.model.layout;
    exports com.flexganttfx.model.repository;
    exports com.flexganttfx.model.timeline;
    exports com.flexganttfx.model.util;
}