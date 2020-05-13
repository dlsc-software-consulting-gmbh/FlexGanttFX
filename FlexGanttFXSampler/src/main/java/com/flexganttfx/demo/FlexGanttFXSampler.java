/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import com.flexganttfx.core.FlexGanttFX;
import fxsampler.FXSampler;

public class FlexGanttFXSampler extends FXSampler {

	public static void main(String[] args) {
		if (!FlexGanttFX.isLicenseKeySet()) {
			FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
		}
		FXSampler.main(args);
	}
}
