/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

/**
 * An add-on interface for chart activities where it should be possible to
 * change the chart value at a later time. This turns a chart activity into a
 * mutable activity and allows for the user to edit the value interactively.
 *
 * @since 1.0
 */
public interface MutableChartActivity extends MutableActivity, ChartActivity {

	/**
	 * Sets a new chart value.
	 *
	 * @param value
	 *            the new value
	 * @since 1.0
	 */
	void setChartValue(double value);
}