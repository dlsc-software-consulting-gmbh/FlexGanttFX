/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.flexganttfx.model.util.SimpleUnit;

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
