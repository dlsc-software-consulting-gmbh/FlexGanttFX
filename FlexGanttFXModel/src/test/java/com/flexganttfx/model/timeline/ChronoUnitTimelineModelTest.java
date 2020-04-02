/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.timeline;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ChronoUnitTimelineModelTest {

    private ChronoUnitTimelineModel timelineModel;

    @Before
    public void setup() {
        timelineModel = new ChronoUnitTimelineModel();
    }

    @Test
    public void shouldNotThrowAnExceptionWhenUsingInstantMinOrMax() {

        // when
        timelineModel.calculateLocationForTime(Instant.MAX);
        timelineModel.calculateLocationForTime(Instant.MIN);
    }

    @Test
    public void shouldNotThrowAnExceptionWhenUsingDoubleMinOrMax() {

        // when
        timelineModel.calculateTimeForLocation(Double.MAX_VALUE);
        timelineModel.calculateTimeForLocation(Double.MIN_VALUE);
    }

    @Test
    public void shouldReturnZeroForStartTimeLocation() {

        // when
        double location = timelineModel.calculateLocationForTime(timelineModel.getStartTime());

        // then
        assertThat(location, is(equalTo(0.0)));
    }

    @Test
    public void shouldReturnMultipleOfUnitWidthForNextDays() {
        // given
        for (int i = 1; i < 30; i++) {
            Instant nextDay = timelineModel.getStartTime();
            nextDay.plus(Duration.ofDays(i));

            // when
            double location = timelineModel.calculateLocationForTime(nextDay);

            // then
            assertThat(location % timelineModel.getMillisPerPixel(), is(equalTo(0.0)));
        }
    }

    @Test
    public void shouldUpdateMillisPerPixel() {
        // given
        timelineModel.setMinimumMillisPerPixel(100);
        timelineModel.setMaximumMillisPerPixel(500);

        // when
        timelineModel.setMillisPerPixel(222.0);

        // then
        assertThat(timelineModel.getMillisPerPixel(), is(equalTo(222.0)));
    }

    @Test
    public void shouldUpdateStartTime() {
        // given
        Instant time = Instant.now().plus(Duration.ofDays(1));

        // when
        timelineModel.setStartTime(time);

        // then
        assertThat(timelineModel.getStartTime(), is(equalTo(time)));
    }
}
