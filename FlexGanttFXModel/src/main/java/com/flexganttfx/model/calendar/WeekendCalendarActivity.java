/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
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
