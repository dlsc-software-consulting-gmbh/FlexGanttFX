/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import com.flexganttfx.model.util.SimpleUnit;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SimpleUnitDatelineModelTest {

	private SimpleUnitDatelineModel datelineModel = new SimpleUnitDatelineModel();

	@Test
	public void shouldReturnResolutionsForAllChronoUnits() {

		for (SimpleUnit unit : SimpleUnit.values()) {
			assertThat("no resolution for unit " + unit, datelineModel
					.getResolutions(unit).hasNext(), is(true));
		}
	}
}
