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
package com.flexganttfx.view.graphics;

import com.flexganttfx.model.*;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.util.Position;
import javafx.geometry.Rectangle2D;

import static java.util.Objects.requireNonNull;

/**
 * Activity bounds contain the visual bounds of and the reference to an
 * activity. They are the result of a call to an activity renderer (see
 * {@link ActivityRenderer}). They are needed for hitpoint detection, so that
 * activities can be located based on x and y coordinates (e.g. mouse event
 * coordinates).
 *
 * @since 1.0
 */
public final class ActivityBounds extends Rectangle2D {

	private final ActivityRef<?> activityRef;

	private Layout layout;

	private Position position;

	public ActivityBounds(ActivityRef<?> activityRef, double x, double y,
			double width, double height) {

		super(x, y, Math.max(0, width), Math.max(0, height));

		this.activityRef = requireNonNull(activityRef);
	}

	/**
	 * Returns the activity stored inside these bounds.
	 *
	 * @return the activity
	 */
	public Activity getActivity() {
		return activityRef.getActivity();
	}

	/**
	 * Returns the layer that contains the activity.
	 *
	 * @return the activity layer
	 */
	public Layer getLayer() {
		return activityRef.getLayer();
	}

	/**
	 * Returns the line index of the activity.
	 *
	 * @return the line index
	 */
	public int getLineIndex() {
		return activityRef.getLineIndex();
	}

	/**
	 * Returns the row that contains the activity.
	 *
	 * @return the parent row
	 */
	public Row<?, ?, ?> getRow() {
		return activityRef.getRow();
	}

	/**
	 * Returns the activity reference stored inside these bounds.
	 *
	 * @return the activity reference
	 */
	public ActivityRef<?> getActivityRef() {
		return activityRef;
	}

	/**
	 * Sets the position of the activity within a sequence of activities.
	 *
	 * @param position the position to store
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * Returns the position of the activity within a sequence of activities.
	 *
	 * @return the position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Sets the layout associated with these bounds.
	 *
	 * @param layout the layout to store
	 */
	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	/**
	 * Returns the layout associated with these bounds.
	 *
	 * @return the layout
	 */
	public Layout getLayout() {
		return layout;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((activityRef == null) ? 0 : activityRef.hashCode());
		result = prime * result + ((layout == null) ? 0 : layout.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityBounds other = (ActivityBounds) obj;
		if (activityRef == null) {
			if (other.activityRef != null)
				return false;
		} else if (!activityRef.equals(other.activityRef))
			return false;
		if (layout == null) {
			if (other.layout != null)
				return false;
		} else if (!layout.equals(other.layout))
			return false;
		return position == other.position;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "ActivityBounds [bounds = " + super.toString()
				+ ", activityRef=" + activityRef + ", layout=" + layout
				+ ", position=" + position + "]";
	}
}
