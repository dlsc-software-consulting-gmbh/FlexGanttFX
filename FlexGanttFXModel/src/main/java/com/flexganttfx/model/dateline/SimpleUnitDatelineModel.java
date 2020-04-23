/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import com.flexganttfx.model.util.SimpleUnit;

public final class SimpleUnitDatelineModel extends DatelineModel<SimpleUnit> {

	public SimpleUnitDatelineModel() {
		for (SimpleUnit unit : SimpleUnit.values()) {
			addResolution(new SimpleUnitResolution(unit, "", 1));
		}
	}

	@Override
	public SimpleUnit nextTemporalUnit(SimpleUnit unit) {
		int ordinal = unit.ordinal();
		if (ordinal < SimpleUnit.values().length - 1) {
			return SimpleUnit.values()[ordinal + 1];
		}

		return null;
	}
}
