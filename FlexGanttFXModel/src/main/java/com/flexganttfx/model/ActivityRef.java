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
package com.flexganttfx.model;

import static java.util.Objects.requireNonNull;

/**
 * An activity reference is used to precisely identify the location of an
 * activity where the location is a combination of row, layer, and the activity
 * itself. As the same activity can be located on multiple rows and or multiple
 * layers at the same time it is often necessary to work with an activity
 * reference instead of only the activity.
 *
 * @param <A>
 *            the type of the referenced activity
 * @since 1.0
 */
public final class ActivityRef<A extends Activity> {

	private final Row<?, ?, A> row;
	private final A activity;
	private final Layer layer;

	/**
	 * Constructs a new activity reference.
	 *
	 * @param row
	 *            the row where the activity is shown
	 * @param layer
	 *            the model layer where the activity is located (optional)
	 * @param activity
	 *            the referenced activity
	 * @throws NullPointerException if the given activity is {@code null}
	 * @since 1.0
	 */
	public ActivityRef(Row<?, ?, A> row, Layer layer, A activity) {
		requireNonNull(activity);

		this.row = row;
		this.layer = layer;
		this.activity = activity;
	}

	/**
	 * Determines if the entire row path to the activity is currently expanded
	 * or not. An activity will not be shown if the tree path to it is not
	 * expanded.
	 *
	 * @return true if the row path to the activity is completely expanded
	 * @since 1.0
	 */
	public boolean isPathExpanded() {
		return isPathExpanded(row);
	}

	private boolean isPathExpanded(Row<?, ?, ?> row) {
		Row<?, ?, ?> parent = row.getParent();
		return parent == null || (parent.isExpanded() && isPathExpanded(parent));
	}

	/**
	 * Returns the row where the activity is located.
	 *
	 * @return the row where the activity is located
	 * @since 1.0
	 */
	public Row<?, ?, A> getRow() {
		return row;
	}

	/**
	 * The layer on which the activity is shown.
	 *
	 * @return the layer that is displaying the activity
	 * @since 1.0
	 */
	public Layer getLayer() {
		return layer;
	}

	/**
	 * Returns the activity itself.
	 *
	 * @return the activity
	 */
	public A getActivity() {
		return activity;
	}

	/**
	 * The line index of the activity. This is a convenience method delegating
	 * to {@link Row#getLineIndex(Activity)} which then delegates to
	 * {@link LinesManager#getLineIndex(Activity)}.
	 *
	 * @return the line index of the activity
	 * @since 1.0
	 */
	public int getLineIndex() {
		return row.getLineIndex(activity);
	}

	/**
	 * Removes the activity from the row where it is currently shown.
	 *
	 * @see #detachFromRow()
	 * @since 1.0
	 */
	public void detachFromRow() {
		row.removeActivity(layer, activity);
	}

	/**
	 * Attaches the activity to its row.
	 *
	 * @see Row#addActivity(Layer, Activity)
	 * @since 1.0
	 */
	public void attachToRow() {
		row.addActivity(layer, activity);
	}

	@Override
	public String toString() {
		return "ActivityRef [row=" + row + ", activity=" + activity
				+ ", layer=" + layer + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activity == null) ? 0 : activity.hashCode());
		result = prime * result + ((layer == null) ? 0 : layer.hashCode());
		result = prime * result + ((row == null) ? 0 : row.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityRef other = (ActivityRef) obj;
		if (activity == null) {
			if (other.activity != null)
				return false;
		} else if (!activity.equals(other.activity))
			return false;
		if (layer == null) {
			if (other.layer != null)
				return false;
		} else if (!layer.equals(other.layer))
			return false;
		if (row == null) {
            return other.row == null;
		} else return row.equals(other.row);
    }
}
