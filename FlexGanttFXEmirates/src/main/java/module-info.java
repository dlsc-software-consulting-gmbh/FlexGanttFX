/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
module com.flexganttfx.emirates {

    requires jpro.webapi;

    requires javafx.swing;

    requires com.flexganttfx.view;
    requires com.flexganttfx.extras;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    exports com.flexganttfx.emirates;

    opens com.flexganttfx.emirates.model;
}