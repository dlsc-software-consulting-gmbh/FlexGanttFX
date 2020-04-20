/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.licensing;

public enum Version {

	VERSION_1("1"),
	VERSION_11("11"),
	VERSION_11_11("11_11");

	String text;

	Version(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
