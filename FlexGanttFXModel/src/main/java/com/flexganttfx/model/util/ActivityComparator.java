/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import com.flexganttfx.model.Activity;

import java.util.Comparator;

/**
 * A specialized comparator used for sorting activities.
 */
public final class ActivityComparator implements Comparator<Activity> {

	private static ActivityComparator instance;

	/**
	 * Returns the singleton instance of the comparator.
	 *
	 * @return the comparator singleton
	 */
	public static synchronized ActivityComparator getInstance() {
		if (instance == null) {
			instance = new ActivityComparator();
		}

		return instance;
	}

	@Override
	public int compare(Activity activity1, Activity activity2) {

		/*
		 * We never return 0, as this would be considered "equal" in which case
		 * the insert operations in a list sorted by start would not work.
		 */

	    if (activity1.getStartTime().equals(activity2.getStartTime())) {
			if (!activity1.getId().equals(activity2.getId())) {

				/*
				 * Using the ID allows us to maintain the sorting order even
				 * when activities start at the same time.
				 */

				return activity1.getId().compareTo(activity2.getId());
			}
		}
		if (activity1.getStartTime().isBefore(activity2.getStartTime())) {
			return -1;
		}

		return +1;
	}
}
