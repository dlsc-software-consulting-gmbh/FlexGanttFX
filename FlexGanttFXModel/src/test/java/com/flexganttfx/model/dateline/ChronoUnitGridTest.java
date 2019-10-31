/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import org.junit.Ignore;
import org.junit.Test;

public class ChronoUnitGridTest {

	// ROUND DOWN YEARS

	@Test
	public void shouldRoundDownYearsToFive() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.YEARS, 5);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.YEAR) % 5, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownYearsToTens() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.YEARS, 10);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.SUNDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.YEAR) % 10, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownYearsToHundreds() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.YEARS, 100);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.YEAR) % 100, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	// ROUND DOWN MONTHS

	@Ignore
	public void shouldRoundDownMonthsToTwo() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MONTHS, 2);
		Instant time = Instant.from(ZonedDateTime.now().with(
				ChronoField.MONTH_OF_YEAR, 2));

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MONTH_OF_YEAR) % 2, is(equalTo(1)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownMonthsToFour() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MONTHS, 4);
		Instant time = Instant.from(ZonedDateTime.now().with(
				ChronoField.MONTH_OF_YEAR, 2));

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MONTH_OF_YEAR) % 4, is(equalTo(1)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownMonthsToSix() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MONTHS, 6);
		Instant time = Instant.from(ZonedDateTime.now().with(
				ChronoField.MONTH_OF_YEAR, 2));

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MONTH_OF_YEAR) % 6, is(equalTo(1)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	// ROUND DOWN WEEKS

	@Test
	public void shouldRoundDownWeeksToTwo() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.WEEKS, 2);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownWeeksToFour() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.WEEKS, 4);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownWeeksToSix() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.WEEKS, 6);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	// ROUND DOWN DAYS

	@Test
	public void shouldRoundDownDaysToTwo() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.DAYS, 2);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.DAY_OF_YEAR) % 2, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownDaysToFour() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.DAYS, 4);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.DAY_OF_YEAR) % 4, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownDaysToSix() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.DAYS, 6);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.DAY_OF_YEAR) % 6, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	// ROUND DOWN HOURS

	@Test
	public void shouldRoundDownHoursToTwo() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.HOURS, 2);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY) % 2, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownHoursToFour() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.HOURS, 4);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY) % 4, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownHoursToSix() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.HOURS, 6);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.HOUR_OF_DAY) % 6, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	// ROUND DOWN MINUTES

	@Test
	public void shouldRoundDownMinutesOfHourToFive() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MINUTES, 5);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR) % 5, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownMinutesOfHourToFifteen() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MINUTES, 15);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR) % 15, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownMinutesOfHourToZero() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MINUTES, 60);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MINUTE_OF_HOUR) % 60, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	// ROUND DOWN SECONDS

	@Test
	public void shouldRoundDownSecondsToFive() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.SECONDS, 5);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE) % 5, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownSecondsToFifteen() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.SECONDS, 15);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE) % 15, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownSecondsToZero() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.SECONDS, 60);
		Instant time = Instant.now();

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.SECOND_OF_MINUTE) % 60, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.NANO_OF_SECOND), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownLocalTimeToFifteenMinutes() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MINUTES, 15);

		int hour = 18;
		int minute = 19;

		LocalTime time = LocalTime.of(hour, minute);

		// when
		time = grid.adjustTime(time, false);

		// then
		assertThat(time.getMinute() % 15, is(equalTo(0)));
		assertThat(time.getMinute(), is(equalTo(15)));
		assertThat(time.getHour(), is(equalTo(hour)));
	}

	@Test
	public void shouldRoundUpLocalTimeToFifteenMinutes() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MINUTES, 15);

		int hour = 18;
		int minute = 19;

		LocalTime time = LocalTime.of(hour, minute);

		// when
		time = grid.adjustTime(time, true);

		// then
		assertThat(time.getMinute() % 15, is(equalTo(0)));
		assertThat(time.getMinute(), is(equalTo(30)));
		assertThat(time.getHour(), is(equalTo(hour)));
	}

	@Test
	public void shouldRoundDownLocalTimeToFifteenMinutes2() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MINUTES, 15);

		int hour = 18;
		int minute = 31;

		LocalTime time = LocalTime.of(hour, minute);

		// when
		time = grid.adjustTime(time, false);

		// then
		assertThat(time.getMinute() % 15, is(equalTo(0)));
		assertThat(time.getMinute(), is(equalTo(30)));
		assertThat(time.getHour(), is(equalTo(hour)));
	}

	@Test
	public void shouldRoundDownLocalTimeToFifteenMinutes3() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MINUTES, 15);

		int hour = 18;
		int minute = 29;

		LocalTime time = LocalTime.of(hour, minute);

		// when
		time = grid.adjustTime(time, false);

		// then
		assertThat(time.getMinute() % 15, is(equalTo(0)));
		assertThat(time.getMinute(), is(equalTo(15)));
		assertThat(time.getHour(), is(equalTo(hour)));
	}

	@Test
	public void shouldRoundDownLocalTimeToHours() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.HOURS, 1);

		int hour = 18;
		int minute = 19;

		LocalTime time = LocalTime.of(hour, minute);

		// when
		time = grid.adjustTime(time, false);

		// then
		assertThat(time.getHour(), is(equalTo(18)));
		assertThat(time.getMinute(), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownLocalTimeToHours2() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.HOURS, 1);

		int hour = 18;
		int minute = 0;

		LocalTime time = LocalTime.of(hour, minute);

		// when
		time = grid.adjustTime(time, false);

		// then
		assertThat(time.getHour(), is(equalTo(18)));
		assertThat(time.getMinute(), is(equalTo(0)));
	}
}
