/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;

import static java.util.Objects.requireNonNull;

/**
 * A utility class for supporting an invisible grid for editing operations on
 * activities. When a grid is set the start and end times of activities will
 * "snap" to locations defined by the grid.
 *
 * @param <T>
 *            the type of the temporal unit (e.g. ChronoUnit)
 *
 * @since 1.0
 */
public abstract class VirtualGrid<T extends TemporalUnit> {

	private String name;
	private String shortName;
	private T unit;
	private int amount;

	/**
	 * Constructs a new grid.
	 *
	 * @param name
	 *            a name that can be shown in the user interface (e.g.
	 *            "15 Minutes")
	 * @param shortName
	 *            a short name that can be shown in the user interface (e.g.
	 *            "15 Min.")
	 * @param unit
	 *            the temporal unit of the grid (e.g. MINUTES)
	 * @param amount
	 *            the amount of the temporal unit (e.g. "15")
	 *
	 * @since 1.1
	 */
	public VirtualGrid(String name, String shortName, T unit, int amount) {
		this.name = requireNonNull(name);
		this.unit = requireNonNull(unit);
		this.shortName = requireNonNull(shortName);

		if (amount <= 0) {
			throw new IllegalArgumentException("grid amount must be larger than 0 but was " + amount);
		}

		this.amount = amount;
	}

	/**
	 * Constructs a new grid.
	 *
	 * @param name
	 *            a name that can be shown in the user interface (e.g.
	 *            "15 Minutes"), will also be used as the short name
	 * @param unit
	 *            the temporal unit of the grid (e.g. MINUTES)
	 * @param amount
	 *            the amount of the temporal unit (e.g. "15")
	 *
	 * @since 1.0
	 */
	public VirtualGrid(String name, T unit, int amount) {
		this(name, name, unit, amount);
	}

	/**
	 * Returns the grid name that can be used for grid selection controls.
	 *
	 * @return the name of the grid settings
	 * @since 1.0
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the grid short name that can be used for grid selection controls.
	 *
	 * @return the short name of the grid settings
	 * @since 1.1
	 */
	public final String getShortName() {
		return shortName;
	}

	/**
	 * The temporal unit used for the grid.
	 *
	 * @return the temporal unit of the grid
	 * @since 1.0
	 */
	public final T getUnit() {
		return unit;
	}

	/**
	 * The number of units used for the grid.
	 *
	 * @return the number of units
	 * @since 1.0
	 */
	public final int getAmount() {
		return amount;
	}

	/**
	 * Adjusts the given instant so that the returned instant will snap to the
	 * position defined by the grid settings.
	 *
	 * @param instant
	 *            the time to adjust to a grid location
	 * @param zoneId
	 *            the time zone for which the adjustment is performed (can be
	 *            different from row to row)
	 * @param roundUp
	 *            a flag signaling whether we want the adjusted time to snap to
	 *            an earlier or later time (start time or end time)
	 * @param firstDayOfWeek
	 *            the weekday that is considered to be the first day of the week
	 *            (mostly Monday or Sunday)
	 * @return the grid adjusted time
	 * @since 1.0
	 */
	public abstract Instant adjustTime(Instant instant, ZoneId zoneId, boolean roundUp, DayOfWeek firstDayOfWeek);

	/**
	 * Adjusts the given local time so that the returned time will snap to the
	 * position defined by the grid settings.
	 *
	 * @param time
	 *            the time to adjust to a grid location
	 * @param roundUp
	 *            a flag signaling whether we want the adjusted time to snap to
	 *            an earlier or later time (start time or end time)
	 * @return the grid adjusted local time
	 * @since 1.0
	 */
	public abstract LocalTime adjustTime(LocalTime time, boolean roundUp);

	@Override
	public String toString() {
		return getName();
	}
}
