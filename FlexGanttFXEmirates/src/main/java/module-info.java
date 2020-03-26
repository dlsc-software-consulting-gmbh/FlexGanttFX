module com.flexganttfx.emirates {

    requires miglayout.core;
    requires miglayout.swing;
    requires opencsv;

    requires java.xml;
    requires java.xml.bind;
    requires com.sun.xml.bind;

    requires java.activation;

    requires javafx.swing;

    requires com.flexganttfx.view;
    requires com.flexganttfx.extras;

    exports com.flexganttfx.emirates;

    opens com.flexganttfx.emirates.model;
}