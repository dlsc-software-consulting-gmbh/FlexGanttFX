/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
open module com.flexganttfx.emirates {

    requires jpro.webapi;

    requires javafx.swing;

    requires com.flexganttfx.view;
    requires com.flexganttfx.extras;
    requires atlantafx.base;
    requires java.prefs;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    exports com.flexganttfx.emirates;
    exports com.flexganttfx.emirates.model;
    exports com.flexganttfx.emirates.view;
}