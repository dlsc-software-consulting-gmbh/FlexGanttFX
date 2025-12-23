/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

import com.flexganttfx.model.Activity;

/**
 * An add-on interface for any {@link Activity} that wants to visualize a
 * percentage complete value. This is often done by filling a section of the
 * activity bar depending on the value represented.
 *
 * @since 1.0
 */
public interface CompletableActivity extends Activity {

	/**
	 * Returns the "percentage complete" value of the activity. Must be a value
	 * between 0 and 100%.
	 *
	 * @return the percentage complete value [0, 100];
	 */
	double getPercentageComplete();
}
