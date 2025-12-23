/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.util.ActivityHelper;

/**
 * A column is part of a {@link Cluster}. Each cluster can have one or more
 * columns. The column stores / displays a list of non-overlapping activities.
 *
 * @param <A>
 *            the type of the activities
 */
public final class Column<A extends Activity> {

	private List<A> activities;

	/**
	 * Adds the given activity to the cluster.
	 *
	 * @param activity
	 *            the activity to add
	 */
	public void add(A activity) {
		if (activities == null) {
			activities = new ArrayList<>();
		}

		activities.add(activity);
	}

	/**
	 * Checks whether the column has room for the given activity, meaning
	 * whether the time interval occupied by the activity is currently being
	 * used or not.
	 *
	 * @param activity
	 *            the activity to place
	 * @param filter
	 *            a filter used for determining if the given activity is
	 *            relevant for conflict checks or not
	 * @return true if the column has enough space for the given activity
	 */
	public boolean hasRoomFor(A activity, Predicate<A> filter) {
		if (activities == null) {
			return true;
		}

		if (filter != null && !filter.test(activity)) {
			return true;
		}

		for (A otherActivity : activities) {

			/*
			 * Only check for intersection / conflict if the other entry is
			 * relevant at all.
			 */
			if (filter == null || filter.test(otherActivity)) {
				if (ActivityHelper.intersect(activity.getStartTime(),
						activity.getEndTime(), otherActivity.getStartTime(),
						otherActivity.getEndTime())) {

					/*
					 * The two activities intersect, so we can not use this
					 * column for the passed activity.
					 */
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Returns all activities within the column.
	 *
	 * @return the column activities
	 */
	public List<A> getActivities() {
		return activities;
	}
}
