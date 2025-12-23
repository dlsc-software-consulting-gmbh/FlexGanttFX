/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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

    @Test
    public void shouldThrowNullPointerExceptionWhenBothArgumentsMissing() {
        Assertions.assertThrows(NullPointerException.class, () ->
                new TimeInterval(null, null));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenFirstArgumentMissing() {
        Assertions.assertThrows(NullPointerException.class, () ->
                new TimeInterval(null, Instant.now()));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenSecondArgumentMissing() {
        Assertions.assertThrows(NullPointerException.class, () ->
                new TimeInterval(Instant.now(), null));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenStartAfterEnd() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new TimeInterval(Instant.now().plus(Duration.ofDays(1)), Instant.now()));
    }
}
