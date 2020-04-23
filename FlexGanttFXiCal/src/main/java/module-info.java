/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
module com.flexganttfx.ical {
    requires ical4j;
    requires com.flexganttfx.view;

    exports com.flexganttfx.ical.model.calendar;
    exports com.flexganttfx.ical.model.repository;
    exports com.flexganttfx.ical.renderer;
}