/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.Instant;

/**
 * The base implementation of a mutable activity. An activity is considered
 * mutable if it implements the {@link MutableActivity} interface. By doing so
 * we now have access to various setter methods to alter the state of the
 * activity.
 *
 * @param <T>
 *            the type of the optional user object
 *
 * @since 1.0
 */
public class MutableActivityBase<T> extends ActivityBase<T> implements
		MutableActivity {

	/**
	 * Constructs a new mutable activity. The initial start time will be set to
	 * {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 *
	 * @since 1.0
	 */
	public MutableActivityBase() {
		super();
	}

	/**
	 * Constructs a new mutable activity with the given name. The initial start
	 * time will be set to {@link Instant#now()} and the end time will be equal
	 * to {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 *
	 * @param name
	 *            the name of the activity
	 *
	 * @since 1.0
	 */
	public MutableActivityBase(String name) {
		super(name);
	}

	/**
	 * Constructs a new mutable activity with the start time and end time.
	 *
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 *
	 * @since 1.0
	 */
	public MutableActivityBase(Instant startTime, Instant endTime) {
		super(startTime, endTime);
	}

	/**
	 * Constructs a new mutable activity with the given name, start time, and
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
	public MutableActivityBase(String name, Instant startTime, Instant endTime) {
		super(name, startTime, endTime);
	}

	// Name support.

	@Override
	public void setName(String name) {
		this.name = name;
	}

	// Start time support

	@Override
	public void setStartTime(Instant time) {
		requireNonNull(time);
		this.startTime = time;
	}

	// End time support

	@Override
	public void setEndTime(Instant time) {
		requireNonNull(time);
		this.endTime = time;
	}

	// Duration support.

	/**
	 * Convenience method to determine a new end time based on a duration that
	 * will be added to the current start time of the activity.
	 *
	 * @param duration
	 *            the duration of the activity
	 * @since 1.0
	 */
	public void setDuration(Duration duration) {
		requireNonNull(duration);
		setEndTime(getStartTime().plus(duration));
	}

	/**
	 * Convenience method to determine the duration between the start and the
	 * end time of the activity.
	 *
	 * @return the duration of the activity
	 */
	public Duration getDuration() {
		return Duration.between(getStartTime(), getEndTime());
	}

	/**
	 * Convenience method to determine a new end time based on a duration that
	 * will be added once to the current end time of the activity.
	 *
	 * @param duration
	 *            the duration of the activity
	 * @since 1.0
	 */
	public void addDuration(Duration duration) {
		requireNonNull(duration);
		addDuration(duration, 1);
	}

	/**
	 * Convenience method to determine a new end time based on a duration that
	 * will be added several times to the current end time of the activity.
	 *
	 * @param duration
	 *            the duration of the activity
	 * @param multipliedBy
	 *            the number of times that the duration will be added
	 * @since 1.0
	 */
	public void addDuration(Duration duration, long multipliedBy) {
		requireNonNull(duration);
		setEndTime(getEndTime().plus(duration.multipliedBy(multipliedBy)));
	}
}
