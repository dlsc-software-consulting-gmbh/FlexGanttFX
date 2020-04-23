/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
module com.flexganttfx.msproject {

    requires transitive mpxj;
    requires transitive com.flexganttfx.extras;

    requires jpro.webapi;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    exports com.flexganttfx.msproject;
    exports com.flexganttfx.msproject.model;
    exports com.flexganttfx.msproject.view;
}