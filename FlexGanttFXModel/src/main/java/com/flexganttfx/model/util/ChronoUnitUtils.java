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
package com.flexganttfx.model.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * A utility class providing methods used in combination with {@link ChronoUnit}.
 */
public class ChronoUnitUtils {

    /**
     * Constructs a new utility instance. All methods of this class are static,
     * hence instances of it are normally not needed.
     */
    public ChronoUnitUtils() {
    }

    /**
     * Truncates the given time for the given chrono unit. The method
     * {@link ZonedDateTime#truncatedTo(java.time.temporal.TemporalUnit)} is not
     * sufficient as it only works for small units (hours, minutes, seconds). It
     * does not work for any unit that has a variable duration (a month can be
     * 28, 30, or 31 days long). We also want to be able to support a
     * "step rate" (e.g. "truncate to minutes, to 5 minutes, to 15 minutes").
     *
     * @param time
     *            the time to truncate
     * @param unit
     *            the chrono unit on which the truncation will be based
     * @param stepRate
     *            the step rate (1, 5, 15, ....)
     * @param firstDayOfWeek
     *            the first day of the week, needed for truncating weeks
     * @return the truncated time
     */
    public static ZonedDateTime truncate(ZonedDateTime time, ChronoUnit unit,
            int stepRate, DayOfWeek firstDayOfWeek) {
        switch (unit) {
        case DAYS:
            return adjustField(time, DAY_OF_YEAR, stepRate).truncatedTo(unit);
        case HALF_DAYS:
            return time.truncatedTo(unit);
        case HOURS:
            return adjustField(time, HOUR_OF_DAY, stepRate).truncatedTo(unit);
        case MINUTES:
            return adjustField(time, MINUTE_OF_HOUR, stepRate).truncatedTo(unit);
        case SECONDS:
            return adjustField(time, SECOND_OF_MINUTE, stepRate).truncatedTo(unit);
        case MILLIS:
            return adjustField(time, MILLI_OF_SECOND, stepRate).truncatedTo(unit);
        case MICROS:
            return adjustField(time, MICRO_OF_SECOND, stepRate).truncatedTo(unit);
        case NANOS:
            return adjustField(time, NANO_OF_SECOND, stepRate).truncatedTo(unit);
        case MONTHS:
            return time
                    .with(MONTH_OF_YEAR,
                            Math.max(
                                    1,
                                    time.get(MONTH_OF_YEAR)
                                            - time.get(MONTH_OF_YEAR)
                                            % stepRate)).withDayOfMonth(1)
                    .truncatedTo(DAYS);
        case YEARS:
            return adjustField(time, ChronoField.YEAR, stepRate).withDayOfYear(1).truncatedTo(DAYS);
        case WEEKS:
            ZonedDateTime result = time.with(DAY_OF_WEEK, firstDayOfWeek.getValue()).truncatedTo(DAYS);
            if (result.isAfter(time)) {
                result = result.minusWeeks(1);
            }
            return result;
        case DECADES:
            int decade = time.getYear() / 10 * 10;
            return time.with(ChronoField.YEAR, decade).withDayOfYear(1).truncatedTo(DAYS);
        case CENTURIES:
            int century = time.getYear() / 100 * 100;
            return time.with(ChronoField.YEAR, century).withDayOfYear(1).truncatedTo(DAYS);
        case MILLENNIA:
            int millennium = time.getYear() / 1000 * 1000;
            return time.with(ChronoField.YEAR, millennium).withDayOfYear(1).truncatedTo(DAYS);
        default:
        }

        return time;
    }

    /**
     * Truncates the given time for the given chrono unit. The method
     * {@link ZonedDateTime#truncatedTo(java.time.temporal.TemporalUnit)} is not
     * sufficient as it only works for small units (hours, minutes, seconds). It
     * does not work for any unit that has a variable duration (a month can be
     * 28, 30, or 31 days long). We also want to be able to support a
     * "step rate" (e.g. "truncate to minutes, to 5 minutes, to 15 minutes").
     *
     * @param time
     *            the time to truncate
     * @param unit
     *            the chrono unit on which the truncation will be based
     * @param stepRate
     *            the step rate (1, 5, 15, ....)
     * @return the truncated time
     */
    public static LocalTime truncate(LocalTime time, ChronoUnit unit,
            int stepRate) {

        switch (unit) {
        case HOURS:
            return adjustField(time, HOUR_OF_DAY, stepRate).truncatedTo(unit);
        case MINUTES:
            return adjustField(time, MINUTE_OF_HOUR, stepRate).truncatedTo(unit);
        case SECONDS:
            return adjustField(time, SECOND_OF_MINUTE, stepRate).truncatedTo(unit);
        case MILLIS:
            return adjustField(time, MILLI_OF_SECOND, stepRate).truncatedTo(unit);
        case MICROS:
            return adjustField(time, MICRO_OF_SECOND, stepRate).truncatedTo(unit);
        case NANOS:
            return adjustField(time, NANO_OF_SECOND, stepRate).truncatedTo(unit);
        default:
        }

        return time;
    }

    private static ZonedDateTime adjustField(ZonedDateTime time, ChronoField field, int stepRate) {
        return time.with(field, time.get(field) - time.get(field) % stepRate);
    }

    private static LocalTime adjustField(LocalTime time, ChronoField field, int stepRate) {
        return time.with(field, time.get(field) - time.get(field) % stepRate);
    }
}
