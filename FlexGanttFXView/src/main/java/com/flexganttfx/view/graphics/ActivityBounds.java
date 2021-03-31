/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
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

	public final Activity getActivity() {
		return activityRef.getActivity();
	}

	public final Layer getLayer() {
		return activityRef.getLayer();
	}

	public final int getLineIndex() {
		return activityRef.getLineIndex();
	}

	public final Row<?, ?, ?> getRow() {
		return activityRef.getRow();
	}

	public final ActivityRef<?> getActivityRef() {
		return activityRef;
	}

	public final void setPosition(Position position) {
		this.position = position;
	}

	public final Position getPosition() {
		return position;
	}

	public final void setLayout(Layout layout) {
		this.layout = layout;
	}

	public final Layout getLayout() {
		return layout;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((activityRef == null) ? 0 : activityRef.hashCode());
		result = prime * result + ((layout == null) ? 0 : layout.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
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

	@Override
	public String toString() {
		return "ActivityBounds [bounds = " + super.toString()
				+ ", activityRef=" + activityRef + ", layout=" + layout
				+ ", position=" + position + "]";
	}
}
