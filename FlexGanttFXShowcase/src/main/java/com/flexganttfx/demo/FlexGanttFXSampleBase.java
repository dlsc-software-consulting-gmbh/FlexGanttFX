/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.demo;

import com.flexganttfx.core.FlexGanttFX;

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
