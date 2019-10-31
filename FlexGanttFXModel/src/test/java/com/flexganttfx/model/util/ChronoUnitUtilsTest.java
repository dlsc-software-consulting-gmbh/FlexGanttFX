/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ChronoUnitUtilsTest {

    @Test
    public void shouldReturnMondayForWednesday() {
        // given
        LocalDate date = LocalDate.of(2016, 1, 27); // a wednesday
        LocalTime time = LocalTime.of(14, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault());

        // when
        ZonedDateTime truncated = ChronoUnitUtils.truncate(zonedDateTime, ChronoUnit.WEEKS, 1, DayOfWeek.MONDAY);

        // then
        assertThat(truncated.toLocalDate(), is(equalTo(LocalDate.of(2016, 1, 25))));
    }

    @Test
    public void shouldReturnMondayForSaturday() {
        // given
        LocalDate date = LocalDate.of(2016, 1, 30); // a saturday
        LocalTime time = LocalTime.of(14, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault());

        // when
        ZonedDateTime truncated = ChronoUnitUtils.truncate(zonedDateTime, ChronoUnit.WEEKS, 1, DayOfWeek.MONDAY);

        // then
        assertThat(truncated.toLocalDate(), is(equalTo(LocalDate.of(2016, 1, 25))));
    }

    @Test
    public void shouldReturnMondayForSunday() {
        // given
        LocalDate date = LocalDate.of(2016, 1, 31); // a sunday
        LocalTime time = LocalTime.of(14, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault());

        // when
        ZonedDateTime truncated = ChronoUnitUtils.truncate(zonedDateTime, ChronoUnit.WEEKS, 1, DayOfWeek.MONDAY);

        // then
        assertThat(truncated.toLocalDate(), is(equalTo(LocalDate.of(2016, 1, 25))));
    }

    @Test
    public void shouldReturnWednesdayForMonday() {
        // given
        LocalDate date = LocalDate.of(2016, 2, 1); // a monday
        LocalTime time = LocalTime.of(14, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault());

        // when
        ZonedDateTime truncated = ChronoUnitUtils.truncate(zonedDateTime, ChronoUnit.WEEKS, 1, DayOfWeek.WEDNESDAY);

        // then
        assertThat(truncated.toLocalDate(), is(equalTo(LocalDate.of(2016, 1, 27))));
    }

    @Test
    public void shouldReturnWednesdayForSaturday() {
        // given
        LocalDate date = LocalDate.of(2016, 1, 30); // a saturday
        LocalTime time = LocalTime.of(14, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault());

        // when
        ZonedDateTime truncated = ChronoUnitUtils.truncate(zonedDateTime, ChronoUnit.WEEKS, 1, DayOfWeek.WEDNESDAY);

        // then
        assertThat(truncated.toLocalDate(), is(equalTo(LocalDate.of(2016, 1, 27))));
    }

    @Test
    public void shouldReturnWednesdayForSunday() {
        // given
        LocalDate date = LocalDate.of(2016, 1, 31); // a sunday
        LocalTime time = LocalTime.of(14, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault());

        // when
        ZonedDateTime truncated = ChronoUnitUtils.truncate(zonedDateTime, ChronoUnit.WEEKS, 1, DayOfWeek.WEDNESDAY);

        // then
        assertThat(truncated.toLocalDate(), is(equalTo(LocalDate.of(2016, 1, 27))));
    }
}
