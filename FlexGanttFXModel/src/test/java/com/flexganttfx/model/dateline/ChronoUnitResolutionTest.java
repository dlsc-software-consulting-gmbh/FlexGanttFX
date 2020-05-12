/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class ChronoUnitResolutionTest {

	@Test
	public void shouldIncrementDSTStart() {
		// given
		LocalDate date = LocalDate.of(2017, 3, 26);
		LocalTime time = LocalTime.of(0, 0);
		ZoneId zoneId = ZoneId.of("Europe/Helsinki");
		ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time,zoneId);
		ChronoUnitResolution resolution = new ChronoUnitResolution(ChronoUnit.HOURS, "", 3);

		// when
		Instant instant = resolution.increment(zonedDateTime.toInstant(), zoneId);

		// then
		assertThat(resolution.isDSTStartIncrement(), is(true));
		assertThat(instant.isAfter(zonedDateTime.toInstant()), is(true));
		ZonedDateTime newZonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
		assertThat(Duration.between(zonedDateTime, newZonedDateTime), is(Duration.ofHours(3)));
	}

	@Test
	public void shouldIncrementDSTEnd() {
		// given
		LocalDate date = LocalDate.of(2017, 10, 29);
		LocalTime time = LocalTime.of(0, 0);
		ZoneId zoneId = ZoneId.of("Europe/Helsinki");
		ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time,zoneId);
		ChronoUnitResolution resolution = new ChronoUnitResolution(ChronoUnit.HOURS, "", 6);

		// when
		Instant instant = resolution.increment(zonedDateTime.toInstant(), zoneId);

		// then
		assertThat(resolution.isDSTEndIncrement(), is(true));
		assertThat(instant.isAfter(zonedDateTime.toInstant()), is(true));
		ZonedDateTime newZonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
		assertThat(Duration.between(zonedDateTime, newZonedDateTime), is(Duration.ofHours(6)));
	}

	@Test
	public void shouldTruncateYearsProperly() {
		// given
		ChronoUnitResolution res = new ChronoUnitResolution(ChronoUnit.YEARS,
				"yyyy", 1);
		ZonedDateTime date = ZonedDateTime.now();
		int year = 2000;
		date.withYear(year);
		date.withMonth(1);
		date.withDayOfMonth(16);

		ZoneId zoneId = ZoneId.systemDefault();

		// when
		for (int i = 0; i < 100; i++) {
			date = date.withYear(year + i);
			Instant instant = Instant.from(date);
			instant = res.truncate(instant, zoneId, DayOfWeek.MONDAY);
			date = ZonedDateTime.ofInstant(instant, zoneId);

			assertThat(date.getYear(), is(equalTo(year + i)));
			assertThat(date.getMonth(), is(equalTo(Month.JANUARY)));
			assertThat(date.getDayOfYear(), is(equalTo(1)));
			assertThat(date.toLocalTime(), is(equalTo(LocalTime.MIN)));
		}
	}

	@Test
	public void shouldCalculateEndTimeProperly() {
		// given
		ChronoUnitResolution res = new ChronoUnitResolution(ChronoUnit.YEARS,
				"yyyy", 1);
		ZonedDateTime date = ZonedDateTime.now();
		int year = 2000;
		date.withYear(year);
		date.withMonth(1);
		date.withDayOfMonth(16);

		ZoneId zoneId = ZoneId.systemDefault();

		date = ZonedDateTime.ofInstant(
				res.truncate(Instant.from(date), zoneId, DayOfWeek.MONDAY),
				zoneId);

		// when
		for (int i = 0; i < 100; i++) {
			date = date.withYear(year + i);
			Instant instant = Instant.from(date);
			instant = res.increment(instant, zoneId);
			date = ZonedDateTime.ofInstant(instant, zoneId);

			assertThat(date.getYear(), is(equalTo(year + i + 1)));
			assertThat(date.getMonth(), is(equalTo(Month.JANUARY)));
			assertThat(date.getDayOfYear(), is(equalTo(1)));
			assertThat(date.toLocalTime(), is(equalTo(LocalTime.MIN)));
		}
	}
}
