/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
module com.flexganttfx.factory {

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    requires com.flexganttfx.view;
    requires com.flexganttfx.extras;

    requires atlantafx.base;
    requires java.prefs;

    exports com.flexganttfx.factory;
    exports com.flexganttfx.factory.model;
    exports com.flexganttfx.factory.view;
}
