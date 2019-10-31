/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ChartActivityBaseTest {

	@Test
	public void shouldHaveCorrectInitialValues() {
		// given
		ChartActivity activity = new ChartActivityBase<String>();

		// when
		assertThat(activity.getChartValue() == 0, is(true));
	}
}
