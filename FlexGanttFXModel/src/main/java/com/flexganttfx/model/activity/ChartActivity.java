/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.layout.ChartLayout;

/**
 * An add-on interface for any activity that is managed by the
 * {@link ChartLayout}.
 *
 * @since 1.0
 */
public interface ChartActivity extends Activity {

	/**
	 * Returns the chart value of the activity. The value can be positive or
	 * negative.
	 *
	 * @return the chart value
	 */
	double getChartValue();
}