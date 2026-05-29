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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.flexganttfx.model.Activity;

/**
 * A cluster is a group of activities placed on multiple columns. The activities
 * can overlap each other within the cluster, but not within a single column.
 *
 * @param <A>
 *            the type of the activities
 *
 * @see Resolver#resolve(List, Predicate)
 */
public final class Cluster<A extends Activity> {

	private List<A> activities;

	private Instant startTime;

	private Instant endTime;

	private List<Column<A>> columns;

	/**
	 * Returns the number of columns that are needed by the cluster in order to
	 * display all activities without any overlaps. This value is only valid
	 * after the {@link #resolve(Predicate)} method has been called.
	 *
	 * @return the number of columns inside the cluster
	 */
	public int getColumnCount() {
		if (columns == null || columns.isEmpty()) {
			return -1;
		}

		return columns.size();
	}

	/**
	 * Adds an activity to the cluster.
	 *
	 * @param activity
	 *            the activity
	 */
	public void add(A activity) {
		if (activities == null) {
			activities = new ArrayList<>();
		}

		activities.add(activity);

		Instant activityStartTime = activity.getStartTime();
		Instant activityEndTime = activity.getEndTime();

		if (startTime == null || activityStartTime.isBefore(startTime)) {
			startTime = activityStartTime;
		}

		if (endTime == null || activityEndTime.isAfter(endTime)) {
			endTime = activityEndTime;
		}
	}

	/**
	 * Returns all activities within the cluster.
	 *
	 * @return the cluster activities
	 */
	public List<A> getActivities() {
		return activities;
	}

	/**
	 * Determines if the given activity intersects with the current time bounds
	 * of the cluster. The first activity always intersects with the cluster so
	 * that the cluster gets initialized.
	 *
	 * @param activity
	 *            the activity to check
	 * @return true if the time bounds of the activity intersect with the time
	 *         bounds of the cluster
	 */
	public boolean intersects(A activity) {
		if (startTime == null) {
			/*
			 * The first added activity initializes the cluster.
			 */
			return true;
		}

		Instant activityStartTime = activity.getStartTime();
		Instant activityEndTime = activity.getEndTime();

		return activityStartTime.isBefore(endTime)
				&& activityEndTime.isAfter(startTime);

	}

	/**
	 * Resolves the conflicts within this cluster by placing the activites in
	 * different columns. The filter allows the application to ignore some
	 * activities when resolving the conflicts.
	 *
	 * @param filter
	 *            a filter used to ignore some of the activities
	 * @return a map structure containing the placement for each activity within
	 *         the cluster
	 */
	public Map<A, Placement<A>> resolve(Predicate<A> filter) {
		if (activities == null || activities.isEmpty()) {
			return Collections.emptyMap();
		}

		columns = new ArrayList<>();
		columns.add(new Column<>());

		for (A activity : activities) {

			boolean added = false;

			// Try to add the activity to an existing column.
			for (Column<A> column : columns) {
				if (column.hasRoomFor(activity, filter)) {
					column.add(activity);
					added = true;
					break;
				}
			}

			// No column found, create a new column.
			if (!added) {
				Column<A> column = new Column<>();
				columns.add(column);
				column.add(activity);
			}
		}

		final Map<A, Placement<A>> placements = new HashMap<>();
		final int colCount = columns.size();

		for (int col = 0; col < columns.size(); col++) {
			Column<A> column = columns.get(col);
			for (A activity : column.getActivities()) {
				placements.put(activity,
						new Placement<>(activity, col, colCount));
			}
		}

		return placements;
	}

	/**
	 * Returns the list of columns. This list is only valid after the method
	 * {@link #resolve(Predicate)} has been called.
	 *
	 * @return the list of columns
	 */
	public List<Column<A>> getColumns() {
		return columns;
	}
}
