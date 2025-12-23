/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import com.flexganttfx.core.FlexGanttFX;
import fxsampler.SampleBase;

public abstract class FlexGanttFXSampleBase extends SampleBase {

    protected FlexGanttFXSampleBase() {
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
        }
    }

    @Override
    public String getSampleSourceURL() {
        return getSampleSourceBase() + getClass().getSimpleName() + ".txt";
    }

    private String getSampleSourceBase() {
        return "https://dlsc.com/wp-content/html/flexganttfx/sampler/";
    }

    @Override
    public final String getJavaDocURL() {
        return "https://www.flexganttfx.com/api/index.html";
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
