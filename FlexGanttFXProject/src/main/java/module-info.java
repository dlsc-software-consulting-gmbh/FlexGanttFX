/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
module com.flexganttfx.project {
    requires javafx.controls;
    requires com.flexganttfx.view;
    exports com.flexganttfx.project.model.business;
    exports com.flexganttfx.project.model.view;
    exports com.flexganttfx.project.view;
}