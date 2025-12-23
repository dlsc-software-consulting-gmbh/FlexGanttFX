/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model;

import com.flexganttfx.model.repository.IntervalTreeActivityRepository;
import com.flexganttfx.model.repository.RepositoryEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;

/**
 * Activity repositories are used by rows to store and lookup activities. Each
 * row by default owns an {@link IntervalTreeActivityRepository}. This default
 * repository can be replaced with a custom one, for example if your application
 * requires a lazy loading strategy.
 *
 * @see Row#setRepository(ActivityRepository)
 *
 * @param <A>
 *            the type of activities stored in the repository
 * @since 1.0
 */
public interface ActivityRepository<A extends Activity> extends EventTarget {

	/**
	 * Returns an iterator for iterating over all activities found for the given
	 * layer and time interval. This method has to return very fast as it gets
	 * called many times during rendering of the chart. A slow implementation
	 * will have a direct impact on scrolling / rendering performance.
	 *
	 * @param layer
	 *            the layer for which to return the activities
	 * @param startTime
	 *            the start time of the time interval for which to return the
	 *            activities
	 * @param endTime
	 *            the end time of the time interval for which to return the
	 *            activities
	 * @param temporalUnit
	 *            the temporal unit currently displayed in the dateline
	 * @param zoneId
	 *            the timezone currently displayed in the dateline
	 * @return the activities on the given layer and in the given time interval
	 * @since 1.0
	 */
	Iterator<A> getActivities(Layer layer, Instant startTime, Instant endTime, TemporalUnit temporalUnit, ZoneId zoneId);

	/**
	 * Returns the earliest time used by the activities stored in this
	 * repository / on this row. This method gets used for navigation (e.g.
	 * "scroll to earliest time used in the Gantt chart",
	 * "zoom out to show all activities").
	 *
	 * @return the earliest time used by the activities in this repository / row
	 *         (null if no activities found)
	 * @since 1.0
	 */
	Instant getEarliestTimeUsed();

	/**
	 * Returns the latest time used by the activities stored in this repository
	 * / on this row. This method gets used for navigation (e.g.
	 * "scroll to latest time used in the Gantt chart",
	 * "zoom out to show all activities").
	 *
	 * @return the latest time used by the activities in this repository / row
	 *         (null if no activities found)
	 * @since 1.0
	 */
	Instant getLatestTimeUsed();

	/**
	 * Adds an event handler for receiving repository events. A repository will
	 * fire events if its state changes (e.g. activities added / removed).
	 *
	 * @param handler
	 *            the event handler that will be added to the repository
	 * @since 1.0
	 */
	void addEventHandler(EventHandler<RepositoryEvent> handler);

	/**
	 * Removes the given event handler from the repository.
	 *
	 * @param handler
	 *            the event handler that will be removed from the repository
	 * @since 1.0
	 */
	void removeEventHandler(EventHandler<RepositoryEvent> handler);
}
