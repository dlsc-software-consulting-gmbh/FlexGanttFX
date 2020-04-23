/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;

import org.junit.Test;

public class SimpleUnitTest {

	@Test
	public void shouldAddDuration() {
		for (SimpleUnit unit : SimpleUnit.values()) {
			// given
			Instant instant = Instant.ofEpochMilli(0);

			// when
			instant = instant.plus(1, unit);

			// then
			assertThat(instant.toEpochMilli(), is(equalTo(unit.getMillis())));
		}
	}
}
