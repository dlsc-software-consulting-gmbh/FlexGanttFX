module com.flexganttfx.ical {
    requires ical4j;
    requires com.flexganttfx.view;

    exports com.flexganttfx.ical.model.calendar;
    exports com.flexganttfx.ical.model.repository;
    exports com.flexganttfx.ical.renderer;
}