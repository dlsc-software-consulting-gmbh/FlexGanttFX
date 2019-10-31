/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.view.GanttChart;

import fxsampler.SampleBase;

public abstract class FlexGanttFXSampleBase extends SampleBase {

    protected FlexGanttFXSampleBase() {
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=11;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302D0215009651BAED65BD0B554000F4B355AF1F17D6D4D7A20214051C06EF255EB67663E3446294E8408B29F94E88");
        }
    }

    protected String getJavaDocBase() {
        return "https://dlsc.com/wp-content/html/flexganttfx/apidocs/";
    }

    @Override
    public String getSampleSourceURL() {
        return getSampleSourceBase() + getClass().getSimpleName() + ".java";
    }

    private String getSampleSourceBase() {
        return "https://dlsc.com/wp-content/html/flexganttfx/sampler/";
    }

    @Override
    public String getJavaDocURL() {
        return getJavaDocBase() + "index.html?sampler=true";
    }

    @Override
    public String getProjectName() {
        return "FlexGanttFX";
    }

    @Override
    public String getProjectVersion() {
        return FlexGanttFX.getVersion();
    }

    @Override
    public String getControlStylesheetURL() {
        return "/" + GanttChart.class.getPackage().getName().replace('.', '/') + "/gantt.css";
    }

    @Override
    public double getControlPanelDividerPosition() {
        return .8;
    }
}
