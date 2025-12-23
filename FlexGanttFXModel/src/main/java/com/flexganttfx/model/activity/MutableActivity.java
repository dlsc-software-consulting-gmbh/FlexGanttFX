/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import java.time.Instant;

import com.flexganttfx.model.Activity;

/**
 * An add-on interface that needs to be implemented by any activity that can be
 * edited interactively by the user.
 * 
 * @since 1.0
 */
public interface MutableActivity extends Activity {

	/**
	 * Sets the name of the activity.
	 * 
	 * @param name
	 *            the new name of the activity
	 * @since 1.0
	 */
	void setName(String name);

	/**
	 * Sets a new start time on the activity.
	 * 
	 * @param time
	 *            the new start time
	 * @since 1.0
	 */
	void setStartTime(Instant time);

	/**
	 * Sets a new end time on the activity.
	 * 
	 * @param time
	 *            the new end time
	 * @since 1.0
	 */
	void setEndTime(Instant time);
}