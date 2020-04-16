/**
 * A couple of non-essential custom controls designed to work with the Gantt charts.
 */
module com.flexganttfx.extras {

    requires transitive com.flexganttfx.view;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;
    exports com.flexganttfx.extras;
    exports com.flexganttfx.extras.properties;
    exports com.flexganttfx.extras.properties.layer;
    exports com.flexganttfx.extras.properties.renderer;
    exports com.flexganttfx.extras.properties.view;
    exports com.flexganttfx.extras.util;
    exports impl.com.flexganttfx.extras.skin;
    opens com.flexganttfx.extras;
    opens com.flexganttfx.extras.util;
}