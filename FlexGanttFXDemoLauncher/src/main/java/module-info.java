/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
module com.flexganttfx.launcher {

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    requires java.desktop;

    requires com.flexganttfx.core;
    requires com.flexganttfx.emirates;
    requires com.flexganttfx.msproject;
    requires com.flexganttfx.covid;
    requires com.flexganttfx.factory;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    exports com.flexganttfx.launcher;
}
