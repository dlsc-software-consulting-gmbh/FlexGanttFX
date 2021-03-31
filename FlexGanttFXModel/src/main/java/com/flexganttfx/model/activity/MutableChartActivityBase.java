/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * The base implementation of a mutable chart activity.
 *
 * @param <T>
 *            the type of the optional user object
 * @since 1.0
 */
public class MutableChartActivityBase<T> extends ChartActivityBase<T> implements MutableChartActivity {

	/**
	 * Constructs a new mutable chart activity. The initial start time will be
	 * set to {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}. The
	 * chart value will be equal to zero.
	 *
	 * @since 1.0
	 */
	public MutableChartActivityBase() {
		super();
	}

	/**
	 * Constructs a new mutable chart activity with the given value. The initial
	 * start time will be set to {@link Instant#now()} and the end time will be
	 * equal to {@link Instant#now()} plus the value of
	 * {@link #DEFAULT_DURATION}.
	 *
	 * @param value
	 *            the chart value of the activity
	 * @since 1.0
	 */
	public MutableChartActivityBase(double value) {
		super(value);
	}

	/**
	 * Constructs a new mutable chart activity with the given value. The start
	 * and end time will be equal to the given time.
	 *
	 * @param value
	 *            the chart value of the activity
	 * @param time
	 *            the start and end time of the activity
	 * @since 1.0
	 */
	public MutableChartActivityBase(double value, Instant time) {
		super(value, time);
	}

	/**
	 * Constructs a new mutable chart activity with the given value.
	 *
	 * @param value
	 *            the chart value of the activity
	 * @param startTime
	 *            the start and end time of the activity
	 * @param endTime
	 *            the start and end time of the activity
	 * @since 1.5
	 */
	public MutableChartActivityBase(double value, Instant startTime, Instant endTime) {
		super(value, startTime, endTime);
	}

	// Name support.

	@Override
	public void setName(String name) {
		this.name = name;
	}

	// User object support

	@Override
	public void setUserObject(T userObject) {
		this.userObject = userObject;
	}

	// Start Time support

	@Override
	public void setStartTime(Instant time) {
		requireNonNull(time);
		this.startTime = time;
	}

	// End Time support

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

	// Chart value support.

	@Override
	public void setChartValue(double value) {
		this.chartValue = value;
	}

	@Override
	public String toString() {
		return super.toString() + ", chart value = " + getChartValue();
	}
}
