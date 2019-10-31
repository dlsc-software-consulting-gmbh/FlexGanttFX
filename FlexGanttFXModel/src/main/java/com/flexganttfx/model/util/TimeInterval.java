/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * A convenience class for storing a pair of instants that define a time
 * interval.
 */
public final class TimeInterval {

	private Instant startTime;

	private Instant endTime;

	/**
	 * Constructs a new time interval.
	 *
	 * @param startTime the start time of the interval
	 * @param endTime the end time of the interval
	 */
	public TimeInterval(Instant startTime, Instant endTime) {
		requireNonNull(startTime);
		requireNonNull(endTime);

		if (startTime.isAfter(endTime)) {
			throw new IllegalArgumentException(
					"start time can not be after end time, start = "
							+ startTime + ", end = " + endTime);
		}

		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * Returns the start time of the interval.
	 *
	 * @return the start time
	 */
	public Instant getStartTime() {
		return startTime;
	}

    /**
     * Returns the end time of the interval.
     *
     * @return the end time
     */
	public Instant getEndTime() {
		return endTime;
	}

    /**
     * Returns the duration of the interval.
     *
     * @return the duration
     */
	public Duration getDuration() {
		return Duration.between(startTime, endTime);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeInterval other = (TimeInterval) obj;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (startTime == null) {
            return other.startTime == null;
		} else return startTime.equals(other.startTime);
    }

	@Override
	public String toString() {
		return "TimeInterval [startTime=" + startTime + ", endTime=" + endTime + "]";
	}
}
