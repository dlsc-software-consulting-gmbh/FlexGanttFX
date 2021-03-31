/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
/**
 * The view layer containing the various custom controls for creating Gantt charts.
 */
module com.flexganttfx.view {

    requires java.logging;
    requires java.prefs;

    requires transitive org.controlsfx.controls;
    requires transitive com.flexganttfx.model;

    exports com.flexganttfx.view;
    exports com.flexganttfx.view.container;
    exports com.flexganttfx.view.graphics;
    exports com.flexganttfx.view.graphics.layer;
    exports com.flexganttfx.view.graphics.renderer;
    exports com.flexganttfx.view.timeline;
    exports com.flexganttfx.view.util;

    exports impl.com.flexganttfx.skin;
    exports impl.com.flexganttfx.skin.container;
    exports impl.com.flexganttfx.skin.graphics;
    exports impl.com.flexganttfx.skin.timeline;
    exports impl.com.flexganttfx.skin.treetable;
    exports impl.com.flexganttfx.skin.util;

    opens com.flexganttfx.view;
    opens com.flexganttfx.view.graphics;
    opens com.flexganttfx.view.timeline;
    opens com.flexganttfx.view.util;
}