/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import com.flexganttfx.core.FlexGanttFX;
import fxsampler.FXSampler;

public class FlexGanttFXSampler extends FXSampler {

	public static void main(String[] args) {
		if (!FlexGanttFX.isLicenseKeySet()) {
			FlexGanttFX.setLicenseKey("LIC=CO_FLEXFX_998;VEN=ComponentSource;VER=STANDARD;PRO=11_11;RUN=no;CTR=0;SignCode=3F;Signature=302C0214449DB4F9A907EB223632AD6C99569CEAA48E0E4E021419258FED88B71303A9321E250A1A53ABAD140DFE");
		}
		FXSampler.main(args);
	}
}
