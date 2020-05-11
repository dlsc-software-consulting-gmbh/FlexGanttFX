/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import com.flexganttfx.core.FlexGanttFX;
import fxsampler.SampleBase;

public abstract class FlexGanttFXSampleBase extends SampleBase {

    protected FlexGanttFXSampleBase() {
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=CO_FLEXFX_998;VEN=ComponentSource;VER=STANDARD;PRO=11_11;RUN=no;CTR=0;SignCode=3F;Signature=302C0214449DB4F9A907EB223632AD6C99569CEAA48E0E4E021419258FED88B71303A9321E250A1A53ABAD140DFE");
        }
    }

    @Override
    public String getSampleSourceURL() {
        return getSampleSourceBase() + getClass().getSimpleName() + ".java";
    }

    private String getSampleSourceBase() {
        return "https://dlsc.com/wp-content/html/flexganttfx/sampler/";
    }

    @Override
    public final String getJavaDocURL() {
        return "https://www.dlsc.com";
    }

    @Override
    public final String getProjectName() {
        return "FlexGanttFX";
    }

    @Override
    public final String getProjectVersion() {
        return FlexGanttFX.getVersion();
    }

    @Override
    public final String getControlStylesheetURL() {
        return null;
    }

    @Override
    public double getControlPanelDividerPosition() {
        return .8;
    }
}
