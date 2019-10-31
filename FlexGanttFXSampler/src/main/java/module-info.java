open module com.flexganttfx.sampler {

    requires fxsampler;
    requires opencsv;

    requires javafx.graphics;
    requires javafx.controls;
    requires transitive javafx.web;

    requires org.controlsfx.controls;

    requires com.flexganttfx.view;
    requires com.flexganttfx.extras;

    exports com.flexganttfx.demo to org.controlsfx.fxsampler;
    exports com.flexganttfx.demo.container to org.controlsfx.fxsampler;
    exports com.flexganttfx.demo.gantt to org.controlsfx.fxsampler;
    exports com.flexganttfx.demo.layout to org.controlsfx.fxsampler;
    exports com.flexganttfx.demo.model to org.controlsfx.fxsampler;
    exports com.flexganttfx.demo.timeline to org.controlsfx.fxsampler;

    provides fxsampler.FXSamplerProject with com.flexganttfx.demo.FlexGanttFXSamplerProject;
}