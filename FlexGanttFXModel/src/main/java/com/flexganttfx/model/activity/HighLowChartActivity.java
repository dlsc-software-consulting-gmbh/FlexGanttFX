/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.layout.ChartLayout;

/**
 * An add-on interface for {@link Activity} used for activities that want to be
 * shown as "High Low Sticks" (e.g. Stock Charts) in a {@link ChartLayout}.
 *
 * @since 1.0
 */
public interface HighLowChartActivity extends Activity {

	/**
	 * Returns the low value of the activity.
	 *
	 * @return the low value
	 * @since 1.0
	 */
	double getLow();

	/**
	 * Returns the high value of the activity.
	 *
	 * @return the high value
	 * @since 1.0
	 */
	double getHigh();
}