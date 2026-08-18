/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.model.dateline;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getYear() % 5, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getYear() % 10, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getYear() % 100, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
	}

	// ROUND DOWN MONTHS

	public void shouldRoundDownMonthsToTwo() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MONTHS, 2);
		Instant time = Instant.from(ZonedDateTime.now().withMonth(2));

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMonthValue() % 2, is(equalTo(1)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownMonthsToFour() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MONTHS, 4);
		Instant time = Instant.from(ZonedDateTime.now().withMonth(2));

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMonthValue() % 4, is(equalTo(1)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
	}

	@Test
	public void shouldRoundDownMonthsToSix() {

		// given
		ChronoUnitGrid grid = new ChronoUnitGrid("", ChronoUnit.MONTHS, 6);
		Instant time = Instant.from(ZonedDateTime.now().withMonth(2));

		// when
		time = grid.adjustTime(time, ZoneId.systemDefault(), false,
				DayOfWeek.MONDAY);

		// then
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMonthValue() % 6, is(equalTo(1)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getDayOfYear() % 2, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getDayOfYear() % 4, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getDayOfYear() % 6, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour() % 2, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour() % 4, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getHour() % 6, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute(), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute() % 5, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute() % 15, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getMinute() % 60, is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond(), is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond() % 5, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond() % 15, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getSecond() % 60, is(equalTo(0)));
		assertThat(
				LocalDateTime.ofInstant(time, ZoneId.systemDefault()).get(
						ChronoField.MILLI_OF_SECOND), is(equalTo(0)));
		assertThat(
                LocalDateTime.ofInstant(time, ZoneId.systemDefault()).getNano(), is(equalTo(0)));
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
