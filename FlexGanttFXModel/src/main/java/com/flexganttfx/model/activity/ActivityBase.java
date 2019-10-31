/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import com.flexganttfx.model.Activity;

import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * The base implementation of the {@link Activity} interface.
 *
 * @param <T>
 *            the type of an optional user object
 * @since 1.0
 */
public class ActivityBase<T> implements Activity {

	public static final Duration DEFAULT_DURATION = Duration.ofDays(5);

	private static long ID_COUNTER = 1;

	/**
	 * Constructs a new activity. The initial start time will be set to
	 * {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 *
	 * @since 1.0
	 */
	public ActivityBase() {
		this(null);
	}

	/**
	 * Constructs a new activity with the given name. The initial start time
	 * will be set to {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 *
	 * @param name
	 *            the name of the activity
	 *
	 * @since 1.0
	 */
	public ActivityBase(String name) {
		this(name, Instant.now(), Instant.now().plus(DEFAULT_DURATION));
	}

	/**
	 * Constructs a new activity with the start time and end time.
	 *
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 *
	 * @since 1.0
	 */
	public ActivityBase(Instant startTime, Instant endTime) {
		this(null, startTime, endTime);
	}

	/**
	 * Constructs a new activity with the given name, start time, and end time.
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
	public ActivityBase(String name, Instant startTime, Instant endTime) {

		requireNonNull(startTime);
		requireNonNull(endTime);

		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	// Name support.

	protected String name;

	@Override
	public String getName() {
		return name;
	}

	// ID support.

	protected String id = Long.toString(createID());

	private static synchronized long createID() {
		return ID_COUNTER++;
	}

	@Override
	public String getId() {
		return id;
	}

	// User object support

	protected T userObject;

    public void setUserObject(T userObject) {
        this.userObject = userObject;
    }

	public T getUserObject() {
		return userObject;
	}

	// Start time support

	protected Instant startTime;

	@Override
	public Instant getStartTime() {
		return startTime;
	}

	// Start time support

	protected Instant endTime;

	@Override
	public Instant getEndTime() {
		return endTime;
	}

	@Override
	public String toString() {
		return getName() + " from " + getStartTime() + " until " + getEndTime() + ", user object = " + getUserObject();
	}
}
