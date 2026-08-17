/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package impl.com.flexganttfx.skin.util;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.util.ActivityHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A column is part of a {@link Cluster}. Each cluster can have one or more
 * columns. The column stores / displays a list of non-overlapping activities.
 *
 * @param <A>
 *            the type of the activities
 */
public final class Column<A extends Activity> {

	/**
	 * Constructs a new empty column.
	 */
	public Column() {
	}

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
