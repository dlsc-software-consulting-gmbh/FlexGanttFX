/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * The base implementation of a mutable high / low activity.
 *
 * @param <T>
 *            the type of the optional user object
 * @since 1.0
 */
public class MutableHighLowChartActivityBase<T> extends HighLowChartActivityBase<T> implements MutableHighLowChartActivity {

	/**
	 * Constructs a new high-low activity. The initial high and low values will
	 * both be equal to zero. The start time will be equal to
	 * {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus {@link ActivityBase#DEFAULT_DURATION}.
	 *
	 * @since 1.0
	 */
	public MutableHighLowChartActivityBase() {
		super();
	}

	/**
	 * Constructs a new high-low activity with the given initial high and low
	 * values and the given start and end time.
	 *
	 * @param low
	 *            the low value of the activity
	 * @param high
	 *            the high value of the activity
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 *
	 * @throws IllegalArgumentException
	 *             if low is larger than high
	 *
	 * @since 1.0
	 */
	public MutableHighLowChartActivityBase(double low, double high,
			Instant startTime, Instant endTime) {
		super(low, high, startTime, endTime);
	}

	/**
	 * Constructs a new high-low activity with the given initial high and low
	 * values and the start and end time set to the given time.
	 *
	 * @param low
	 *            the low value of the activity
	 * @param high
	 *            the high value of the activity
	 * @param time
	 *            the start and end time of the activity
	 *
	 * @throws IllegalArgumentException
	 *             if low is larger than high
	 *
	 * @since 1.0
	 */
	public MutableHighLowChartActivityBase(double low, double high, Instant time) {
		super(low, high, time);
	}

	// Name support.

	@Override
	public void setName(String name) {
		this.name = name;
	}

	// User object support

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

	@Override
	public void setHigh(double high) {
		this.high = high;
	}

	@Override
	public void setLow(double low) {
		this.low = low;
	}

	@Override
	public String toString() {
		return super.toString() + ", low = " + getLow() + ", high = " + getHigh();
	}
}
