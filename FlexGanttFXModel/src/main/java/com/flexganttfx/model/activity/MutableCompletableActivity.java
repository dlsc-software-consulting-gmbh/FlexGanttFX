/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.activity;

/**
 * An add-on interface for completable activities where the user should be able
 * to interactively edit the percentage complete value.
 *
 * @since 1.0
 */
public interface MutableCompletableActivity extends MutableActivity, CompletableActivity {

	/**
	 * Returns the percentage complete value of the activity.
	 *
	 * @param complete
	 *            the new percentage complete value (must be between 0 and 100).
	 * @since 1.0
	 */
	void setPercentageComplete(double complete);
}
