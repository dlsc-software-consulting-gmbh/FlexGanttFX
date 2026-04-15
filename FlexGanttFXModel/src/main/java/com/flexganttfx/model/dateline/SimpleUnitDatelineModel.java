/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import com.flexganttfx.model.util.SimpleUnit;

/**
 * Represents a dateline model for managing temporal resolutions using the SimpleUnit enumeration.
 * This class extends the generic {@code DatelineModel} class and provides specific behaviors
 * for handling {@code SimpleUnit}-based resolutions.
 *
 * The model initializes by adding resolutions corresponding to all available values of the
 * {@code SimpleUnit} enumeration, each mapped to an increment value of 1.
 *
 * @since 1.0
 */
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
