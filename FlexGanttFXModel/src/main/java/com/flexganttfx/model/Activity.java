/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model;

import java.time.Instant;

/**
 * Activities represent objects that will be displayed below the timeline in the
 * graphics view of the Gantt chart control. Activities can be added to a
 * specific layer on a row by calling {@link Row#addActivity(Layer, Activity)}.
 *
 * @since 1.0
 */
public interface Activity {

	/**
	 * The name of the activity, for example "Flight 3441".
	 *
	 * @return the name of the activity
	 * @since 1.0
	 */
	String getName();

	/**
	 * The unique id of the activity.
	 *
	 * @return the unique activity ID
	 * @since 1.0
	 */
	String getId();

	/**
	 * The start time of the activity.
	 *
	 * @return the activity start time
	 * @since 1.0
	 */
	Instant getStartTime();

	/**
	 * The end time of the activity.
	 *
	 * @return the activity end time
	 * @since 1.0
	 */
	Instant getEndTime();
}