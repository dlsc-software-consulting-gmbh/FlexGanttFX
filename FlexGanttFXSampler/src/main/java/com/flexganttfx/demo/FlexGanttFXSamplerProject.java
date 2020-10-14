/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;


import fxsampler.FXSamplerProject;
import fxsampler.model.WelcomePage;

public class FlexGanttFXSamplerProject implements FXSamplerProject {

	@Override
	public String getProjectName() {
		return "FlexGanttFX";
	}

	@Override
	public String getSampleBasePackage() {
		return "com.flexganttfx.demo";
	}

	@Override
	public WelcomePage getWelcomePage() {
		return new FlexGanttFXSamplerWelcome();
	}
}
