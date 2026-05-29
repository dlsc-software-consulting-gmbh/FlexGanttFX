/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.model.util;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
