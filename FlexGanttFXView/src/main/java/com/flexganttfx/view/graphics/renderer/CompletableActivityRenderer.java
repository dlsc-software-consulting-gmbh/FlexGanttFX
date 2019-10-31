/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.activity.CompletableActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.Position;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CompletableActivityRenderer<A extends CompletableActivity> extends
		ActivityBarRenderer<A> {

	public CompletableActivityRenderer(GraphicsBase<?> graphics, String name) {
		super(graphics, name);

		setFillCompletion(new Color(0, 0, 0, .3));
		setFillCompletionHover(new Color(0, 0, 0, .4));
		setFillCompletionPressed(new Color(0, 0, 0, .5));
		setFillCompletionSelected(new Color(0, 0, 0, .3));
		setFillCompletionHighlight(new Color(0, 0, 0, .2));

		redrawObservable(fillCompletionProperty());
		redrawObservable(fillCompletionHoverProperty());
		redrawObservable(fillCompletionPressedProperty());
		redrawObservable(fillCompletionSelectedProperty());
		redrawObservable(fillCompletionHighlightProperty());
	}

	@Override
	protected ActivityBounds drawActivity(ActivityRef<A> path,
			Position position, GraphicsContext gc, double x, double y,
			double w, double h, boolean selected, boolean hover,
			boolean highlighted, boolean pressed) {

		ActivityBounds bounds = super.drawActivity(path, position, gc, x, y, w,
				h, selected, hover, highlighted, pressed);

		drawCompletion(path, gc, x, y, w, h, selected, hover, highlighted,
				pressed);

		return bounds;
	}

	protected void drawCompletion(ActivityRef<A> activityRef,
			GraphicsContext gc, double x, double y, double w, double h,
			boolean selected, boolean hover, boolean highlighted,
			boolean pressed) {

		double percentage = activityRef.getActivity().getPercentageComplete();

		double pw = (percentage / 100) * w;
		double my = y + (h - getBarHeight()) / 2;

		gc.setFill(getFillCompletion(selected, hover, highlighted, pressed));

		if (isCornersRounded()) {
			gc.fillRoundRect(x, my, pw, getBarHeight(), getCornerRadius(),
					getCornerRadius());
		} else {
			gc.fillRect(x, my, pw, getBarHeight());
		}
	}

	protected Paint getFillCompletion(boolean selected, boolean hover,
			boolean highlighted, boolean pressed) {

		if (pressed) {
			return getFillCompletionPressed();
		} else if (highlighted) {
			return getFillCompletionHighlight();
		} else if (hover) {
			return getFillCompletionHover();
		} else if (selected) {
			return getFillCompletionSelected();
		} else {
			return getFillCompletion();
		}
	}

	private final ObjectProperty<Paint> fillCompletion = new SimpleObjectProperty<>(
			this, "fillCompletion");

	public final ObjectProperty<Paint> fillCompletionProperty() {
		return fillCompletion;
	}

	public final void setFillCompletion(Paint fill) {
		fillCompletionProperty().set(fill);
	}

	public final Paint getFillCompletion() {
		return fillCompletionProperty().get();
	}

	private final ObjectProperty<Paint> fillCompletionHover = new SimpleObjectProperty<>(
			this, "fillCompletionHover");

	public final ObjectProperty<Paint> fillCompletionHoverProperty() {
		return fillCompletionHover;
	}

	public final void setFillCompletionHover(Paint fill) {
		fillCompletionHoverProperty().set(fill);
	}

	public final Paint getFillCompletionHover() {
		return fillCompletionHoverProperty().get();
	}

	private final ObjectProperty<Paint> fillCompletionHighlight = new SimpleObjectProperty<>(
			this, "fillCompletionHighlight");

	public final ObjectProperty<Paint> fillCompletionHighlightProperty() {
		return fillCompletionHighlight;
	}

	public final void setFillCompletionHighlight(Paint fill) {
		fillCompletionHighlightProperty().set(fill);
	}

	public final Paint getFillCompletionHighlight() {
		return fillCompletionHighlightProperty().get();
	}

	private final ObjectProperty<Paint> fillCompletionSelected = new SimpleObjectProperty<>(
			this, "fillCompletionSelected");

	public final ObjectProperty<Paint> fillCompletionSelectedProperty() {
		return fillCompletionSelected;
	}

	public final void setFillCompletionSelected(Paint fill) {
		fillCompletionSelectedProperty().set(fill);
	}

	public final Paint getFillCompletionSelected() {
		return fillCompletionSelectedProperty().get();
	}

	private final ObjectProperty<Paint> fillCompletionPressed = new SimpleObjectProperty<>(
			this, "fillCompletionPressed");

	public final ObjectProperty<Paint> fillCompletionPressedProperty() {
		return fillCompletionPressed;
	}

	public final void setFillCompletionPressed(Paint fill) {
		fillCompletionPressedProperty().set(fill);
	}

	public final Paint getFillCompletionPressed() {
		return fillCompletionPressedProperty().get();
	}
}
