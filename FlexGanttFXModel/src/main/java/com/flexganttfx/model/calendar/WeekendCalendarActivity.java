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
package com.flexganttfx.model.calendar;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.Instant;

/**
 * A specialized calendar activity used to represent weekend days (e.g.
 * Saturday, Sunday).
 * 
 * @since 1.0
 */
public class WeekendCalendarActivity extends CalendarActivityBase<Object> {

	private final DayOfWeek dayOfWeek;

	/**
	 * Constructs a new weekend calendar activity. The initial start time will
	 * be set to {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 * 
	 * @param day
	 *            the day of week that this activity is representing
	 *
	 * @since 1.0
	 */
	public WeekendCalendarActivity(DayOfWeek day) {
		super();

		requireNonNull(day);

		this.dayOfWeek = day;
	}

	/**
	 * Constructs a new weekend calendar activity with the given name. The
	 * initial start time will be set to {@link Instant#now()} and the end time
	 * will be equal to {@link Instant#now()} plus the value of
	 * {@link #DEFAULT_DURATION}.
	 *
	 * @param name
	 *            the name of the activity
	 * @param day
	 *            the day of week that this activity is representing
	 *
	 * @since 1.0
	 */
	public WeekendCalendarActivity(String name, DayOfWeek day) {
		super(name);

		requireNonNull(day);

		this.dayOfWeek = day;
	}

	/**
	 * Constructs a new weekend calendar activity with the start time and end
	 * time.
	 *
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 * @param day
	 *            the day of week that this activity is representing
	 *
	 * @since 1.0
	 */
	public WeekendCalendarActivity(Instant startTime, Instant endTime,
			DayOfWeek day) {
		super(startTime, endTime);

		requireNonNull(day);

		this.dayOfWeek = day;
	}

	/**
	 * Constructs a new weekend calendar activity with the given name, start
	 * time, and end time.
	 *
	 * @param name
	 *            the name of the activity
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 * @param day
	 *            the day of week that this activity is representing
	 *
	 * @since 1.0
	 */
	public WeekendCalendarActivity(String name, Instant startTime,
			Instant endTime, DayOfWeek day) {
		super(name, startTime, endTime);

		requireNonNull(day);

		this.dayOfWeek = day;
	}

	/**
	 * Returns the week day that this activity is representing, e.g.
	 * {@link DayOfWeek#SUNDAY}.
	 * 
	 * @return the day of week represented by this activity
	 * @since 1.0
	 */
	public final DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}
}
