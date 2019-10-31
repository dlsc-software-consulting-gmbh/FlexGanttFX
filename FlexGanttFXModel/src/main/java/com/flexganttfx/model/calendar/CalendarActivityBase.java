/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.calendar;

import com.flexganttfx.model.activity.ActivityBase;

import java.time.Instant;

/**
 * The base implementation of a calendar activity.
 * 
 * @param <T>
 *            the type of an optional user object
 * @since 1.0
 */
public class CalendarActivityBase<T> extends ActivityBase<T> implements CalendarActivity {

	/**
	 * Constructs a new calendar activity. The initial start time will be set to
	 * {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 *
	 * @since 1.0
	 */
	public CalendarActivityBase() {
		super();
	}

	/**
	 * Constructs a new calendar activity with the given name. The initial start
	 * time will be set to {@link Instant#now()} and the end time will be equal
	 * to {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 *
	 * @param name
	 *            the name of the activity
	 *
	 * @since 1.0
	 */
	public CalendarActivityBase(String name) {
		super(name);
	}

	/**
	 * Constructs a new calendar activity with the start time and end time.
	 *
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 *
	 * @since 1.0
	 */
	public CalendarActivityBase(Instant startTime, Instant endTime) {
		super(startTime, endTime);
	}

	/**
	 * Constructs a new calendar activity with the given name, start time, and
	 * end time.
	 *
	 * @param name
	 *            the name of the activity
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 *
	 * @since 1.0
	 */
	public CalendarActivityBase(String name, Instant startTime, Instant endTime) {
		super(name, startTime, endTime);
	}
}
