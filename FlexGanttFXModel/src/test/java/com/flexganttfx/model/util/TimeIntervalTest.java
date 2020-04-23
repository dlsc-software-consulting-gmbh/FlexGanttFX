/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TimeIntervalTest {

	@Test
	public void shouldCreateInterval() {
		// given
		Instant startTime = Instant.now();
		Instant endTime = Instant.now().plus(Duration.ofDays(1));

		// when
		TimeInterval interval = new TimeInterval(startTime, endTime);

		// then
		assertThat(interval.getStartTime(), is(equalTo(startTime)));
		assertThat(interval.getEndTime(), is(equalTo(endTime)));
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerExceptionWhenBothArgumentsMissing() {
		new TimeInterval(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerExceptionWhenFirstArgumentMissing() {
		new TimeInterval(null, Instant.now());
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerExceptionWhenSecondArgumentMissing() {
		new TimeInterval(Instant.now(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenStartAfterEnd() {
		new TimeInterval(Instant.now().plus(Duration.ofDays(1)), Instant.now());
	}
}
