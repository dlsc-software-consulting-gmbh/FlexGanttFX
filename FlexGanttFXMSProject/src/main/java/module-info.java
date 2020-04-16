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