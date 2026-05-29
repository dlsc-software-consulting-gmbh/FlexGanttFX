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

import static java.util.Objects.requireNonNull;

import com.flexganttfx.model.Activity;

/**
 * A data structure to store the placement of an activity. A placement is
 * defined by the column index and the total column count. For example: an
 * activity can be shown in the second column of a {@link Cluster} with a total
 * of 5 columns.
 *
 * @param <A>
 *            the type of the activities
 */
public final class Placement<A extends Activity> {

	private final int columnIndex;

	private final int columnCount;

	private final A activity;

	/**
	 * Constructs a new placement.
	 *
	 * @param activity
	 *            the activity to be placed
	 * @param columnIndex
	 *            the column where the activity will be shown
	 * @param columnCount
	 *            the total number of columns in the cluster
	 */
	public Placement(A activity, int columnIndex, int columnCount) {
		this.activity = requireNonNull(activity);
		this.columnIndex = columnIndex;
		this.columnCount = columnCount;
	}

	/**
	 * Returns the activity.
	 *
	 * @return the activity
	 */
	public A getActivity() {
		return activity;
	}

	/**
	 * Returns the index of the column where the activity will be shown within
	 * its cluster.
	 *
	 * @return the column index
	 */
	public int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * Returns the total number of columns used for the cluster where the
	 * activity is being shown.
	 *
	 * @return the total number of columns
	 */
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public String toString() {
		return "Placement [columnIndex=" + columnIndex + ", columnCount="
				+ columnCount + ", activity=" + activity + "]";
	}
}
