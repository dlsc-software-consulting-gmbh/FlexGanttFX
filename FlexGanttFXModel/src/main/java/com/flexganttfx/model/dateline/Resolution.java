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
package com.flexganttfx.model.dateline;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * A resolution represents the visual representation of a temporal unit. The unit will
 * be displayed in the given format, step rate, and in the given positions.
 *
 * @param <T> the type of the temporal unit
 * @since 1.0
 */
public abstract class Resolution<T extends TemporalUnit> {

    public enum Position {
        TOP, MIDDLE, BOTTOM, ONLY
    }

    private final Set<Position> supportedPositions = new HashSet<>();

    private final T temporalUnit;
    private final String format;
    private final int stepRate;

    /**
     * Constructs a new resolution for the given temporal unit, in the
     * given format, step rate, and for the given supported positions.
     *
     * @param temporalUnit       the temporal unit (e.g. DAYS)
     * @param format             the display format (e.g. HH:MM)
     * @param stepRate           the step rate (e.g. 1, 5, 10, 15, 30)
     * @param supportedPositions the supported positions (top, middle, bottom) inside the dateline
     * @since 1.0
     */
    public Resolution(T temporalUnit, String format, int stepRate, Position... supportedPositions) {

        requireNonNull(temporalUnit);
        requireNonNull(format);
        requireNonNull(supportedPositions);

        if (stepRate <= 0) {
            throw new IllegalArgumentException("step rate must be larger than 0, but was " + stepRate);
        }
        if (supportedPositions.length == 0) {
            throw new IllegalArgumentException("at least one position must be specified for a resolution, but the passed array was empty");
        }

        this.temporalUnit = temporalUnit;
        this.format = format;
        this.stepRate = stepRate;
        this.supportedPositions.addAll(Arrays.asList(supportedPositions));
    }

    /**
     * Constructs a new resolution for the given temporal unit, in the
     * given format, step rate, for all positions.
     *
     * @param temporalUnit the temporal unit (e.g. DAYS)
     * @param format       the display format (e.g. HH:MM)
     * @param stepRate     the step rate (e.g. 1, 5, 10, 15, 30)
     * @since 1.0
     */
    public Resolution(T temporalUnit, String format, int stepRate) {
        this(temporalUnit, format, stepRate, Position.values());
    }

    /**
     * Determines if the given position is supported by this resolution.
     *
     * @param position the position
     * @return true if the position is supported
     * @since 1.0
     */
    public final boolean isSupportingPosition(Position position) {
        return supportedPositions.contains(position);
    }

    /**
     * Returns the temporal unit represented by this resolution.
     *
     * @return the temporal unit
     * @since 1.0
     */
    public final T getTemporalUnit() {
        return temporalUnit;
    }

    /**
     * Returns the format in which the resolution will be displayed.
     *
     * @return the format string for the date time formatter
     * @see java.time.format.DateTimeFormatter#format(TemporalAccessor)
     * @since 1.0
     */
    public final String getFormat() {
        return format;
    }

    /**
     * Returns the step rate of this resolution, e.g. "5" for "5 Minutes". Normally
     * values are 1, 5, 10, 15, or 30.
     *
     * @return the step rate
     * @since 1.0
     */
    public final int getStepRate() {
        return stepRate;
    }

    @Override
    public String toString() {
        return "Resolution [supportedPositions=" + supportedPositions
                + ", temporalUnit=" + temporalUnit + ", format=" + format
                + ", stepRate=" + stepRate + "]";
    }

    /**
     * Formats the given time / instant for the given time zone based on the
     * settings of this resolution.
     *
     * @param instant the time used for the formatting
     * @param zoneId  the time zone
     * @return the formatted time
     * @since 1.0
     */
    public abstract String format(Instant instant, ZoneId zoneId);

    /**
     * Truncates the given time based on the temporal unit represented by this
     * resolution. Example: a date located on a Wednesday will be adjusted to
     * a date on the previous Monday (if Monday is the first day of the week in
     * the given time zone).
     *
     * @param instant        the time to truncate
     * @param zoneId         the time zone
     * @param firstDayOfWeek the weekday that is considered to be the first
     *                       day of the week
     * @return the truncated time
     * @since 1.0
     */
    public abstract Instant truncate(Instant instant, ZoneId zoneId, DayOfWeek firstDayOfWeek);

    /**
     * Increments the given time based on the temporal unit represented by this
     * resolution. Example: if the temporal unit is DAY and the given time is
     * located on Monday, then the incremented time will be Tuesday. If the temporal
     * unit is WEEK then the incremented time will be the Monday of the next week.
     * This function is very important for building the cells in the dateline
     * control.
     *
     * @param instant the time to increment
     * @param zoneId  the time zone
     * @return the incremented time
     * @since 1.0
     */
    public abstract Instant increment(Instant instant, ZoneId zoneId);

    /**
     * Decrements the given time based on the temporal unit represented by this
     * resolution. Example: if the temporal unit is DAY and the given time is
     * located on Monday, then the decremented time will be Sunday. If the temporal
     * unit is WEEK then the decremented time will be the Monday of the previous week.
     * This function is very important for building the cells in the dateline
     * control.
     *
     * @param instant the time to increment
     * @param zoneId  the time zone
     * @return the incremented time
     */
    public abstract Instant decrement(Instant instant, ZoneId zoneId);

    /**
     * Creates a virtual grid for editing operations based on the settings of this
     * resolution.
     *
     * @return the virtual grid
     * @since 1.1
     */
    public abstract VirtualGrid<T> createGrid();
}
