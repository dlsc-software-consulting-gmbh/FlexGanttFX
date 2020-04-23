/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
module com.flexganttfx.emirates {

    requires miglayout.core;
    requires miglayout.swing;
    requires opencsv;

    requires jpro.webapi;

    requires java.xml;
    requires java.xml.bind;
    requires com.sun.xml.bind;

    requires java.activation;

    requires javafx.swing;

    requires com.flexganttfx.view;
    requires com.flexganttfx.extras;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    exports com.flexganttfx.emirates;

    opens com.flexganttfx.emirates.model;
}