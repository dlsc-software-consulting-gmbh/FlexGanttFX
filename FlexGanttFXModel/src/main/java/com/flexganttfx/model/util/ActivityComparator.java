/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.model.util;

import com.flexganttfx.model.Activity;

import java.util.Comparator;

/**
 * A specialized comparator used for sorting activities.
 */
public final class ActivityComparator implements Comparator<Activity> {

	/**
	 * Constructs a new comparator. Applications normally use the singleton
	 * returned by {@link #getInstance()} instead.
	 */
	public ActivityComparator() {
	}

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
