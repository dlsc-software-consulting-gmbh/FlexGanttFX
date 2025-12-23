/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import com.flexganttfx.model.layout.ChartLayout;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * The base implementation of a chart activity, which defines an additional
 * chart value attribute that can be utilized by the {@link ChartLayout} to
 * create charts based on activities, e.g. for capacity usage / resource usage
 * profiles.
 *
 * @see ChartLayout#getMinValue()
 * @see ChartLayout#getMaxValue()
 * 
 * @param <T>
 *            the type of the optional user object
 * 
 * @since 1.0
 */
public class ChartActivityBase<T> extends ActivityBase<T> implements
		ChartActivity {

	/**
	 * Constructs a new chart activity. The initial start time will be set to
	 * {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}. The
	 * chart value will be equal to zero.
	 *
	 * @since 1.0
	 */
	public ChartActivityBase() {
		super();
	}

	/**
	 * Constructs a new chart activity with the given value. The initial start
	 * time will be set to {@link Instant#now()} and the end time will be equal
	 * to {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 *
	 * @param value
	 *            the chart value of the activity
	 * @since 1.0
	 */
	public ChartActivityBase(double value) {
		super();

		this.chartValue = value;
	}

	/**
	 * Constructs a new chart activity with the given value. The start and end
	 * time will be equal to the given time.
	 *
	 * @param value
	 *            the chart value of the activity
	 * @param time
	 *            the start and end time of the activity
	 * @since 1.0
	 */
	public ChartActivityBase(double value, Instant time) {
		super(time, time);

		requireNonNull(value);

		this.chartValue = value;
	}

	/**
	 * Constructs a new chart activity with the given value. The start time and
	 * end time will be equal to the given times.
	 *
	 * @param value
	 *            the chart value of the activity
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 * 
	 * @since 1.0
	 */
	public ChartActivityBase(double value, Instant startTime, Instant endTime) {
		super(startTime, endTime);

		requireNonNull(value);

		this.chartValue = value;
	}

	// Chart value support.

	protected double chartValue;

	@Override
	public double getChartValue() {
		return chartValue;
	}

	@Override
	public String toString() {
		return super.toString() + ", chart value = " + getChartValue();
	}
}
