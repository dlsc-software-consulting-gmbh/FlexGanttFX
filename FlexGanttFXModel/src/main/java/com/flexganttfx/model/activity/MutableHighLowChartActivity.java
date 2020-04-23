/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

/**
 * An add-on interface for high-low activities which allows the user to
 * interactively edit the high and low value of the activity.
 * 
 * @since 1.0
 */
public interface MutableHighLowChartActivity extends MutableActivity, HighLowChartActivity {

	/**
	 * Sets the low value of the activity.
	 * 
	 * @param low
	 *            the new low value
	 * @since 1.0
	 */
	void setLow(double low);

	/**
	 * Sets the high value of the activity.
	 * 
	 * @param high
	 *            the new high value
	 * @since 1.0
	 */
	void setHigh(double high);
}