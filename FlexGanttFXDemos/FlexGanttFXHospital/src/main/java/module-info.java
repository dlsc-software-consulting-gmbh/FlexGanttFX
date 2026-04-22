/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
module com.flexganttfx.hospital {

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    requires com.flexganttfx.view;
    requires com.flexganttfx.extras;
    requires com.calendarfx.view;

    requires atlantafx.base;
    requires java.prefs;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    exports com.flexganttfx.hospital;
    exports com.flexganttfx.hospital.model;
    exports com.flexganttfx.hospital.renderer;
    exports com.flexganttfx.hospital.view;
}
