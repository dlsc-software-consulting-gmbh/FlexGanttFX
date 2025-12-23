/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ChartActivityBaseTest {

	@Test
	public void shouldHaveCorrectInitialValues() {
		// given
		ChartActivity activity = new ChartActivityBase<String>();

		// when
		assertThat(activity.getChartValue() == 0, is(true));
	}
}
