/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
open module com.flexganttfx.sampler {

    requires opencsv;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.web;

    requires org.controlsfx.controls;

    requires com.flexganttfx.view;
    requires com.flexganttfx.extras;
    //requires com.flexganttfx.msproject;

    provides fxsampler.FXSamplerProject with com.flexganttfx.demo.FlexGanttFXSamplerProject;

    uses fxsampler.FXSamplerProject;
    uses fxsampler.FXSamplerConfiguration;
}