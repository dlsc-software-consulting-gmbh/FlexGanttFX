/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.model;

import com.flexganttfx.model.activity.ChartActivity;

public class Group extends ModelObject<Group, Aircraft, ChartActivity> {

	public Group(String name) {
		setName(name);
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Group)) {
			return false;
		}

		Group g = (Group) obj;
		return getName().equals(g.getName());
	}
}
