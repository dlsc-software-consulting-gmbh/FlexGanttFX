/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class Renderer {

	private final String name;
	private GraphicsBase<?> graphics;

	public Renderer(GraphicsBase<?> graphics, String name) {
		requireNonNull(name);
		requireNonNull(graphics);

		this.name = name;
		this.graphics = graphics;

		setFill(Color.LIGHTBLUE);
		setFillPressed(Color.LIGHTBLUE.darker());
		setFillHighlight(Color.YELLOW.deriveColor(1, 1, 1, .5));
		setFillSelected(Color.valueOf("#F21B1BBB"));
		setFillHover(Color.GREEN);

		// Listener support / Redraw

		redrawObservable(enabled);
		redrawObservable(snapToPixel);
		redrawObservable(padding);
		redrawObservable(alpha);
		redrawObservable(fill);
		redrawObservable(fillHighlight);
		redrawObservable(fillPressed);
		redrawObservable(fillSelected);
		redrawObservable(fillHover);
	}

	protected double snapPosition(double value) {
		return snapPosition(value, isSnapToPixel());
	}

	protected double snapSpace(double value) {
		return snapSpace(value, isSnapToPixel());
	}

	protected double snapSize(double value) {
		return snapSize(value, isSnapToPixel());
	}

	/**
	 * If snapToPixel is true, then the value is rounded using Math.round.
	 * Otherwise, the value is simply returned.
	 *
	 * @param value
	 *            The value that needs to be snapped
	 * @param snapToPixel
	 *            Whether to snap to pixel
	 * @return value either as passed in or rounded based on snapToPixel
	 */
	private double snapSpace(double value, boolean snapToPixel) {
		return snapToPixel ? Math.round(value) : value;
	}

	/**
	 * If snapToPixel is true, then the value is ceil'd using Math.ceil.
	 * Otherwise, the value is simply returned.
	 *
	 * @param value
	 *            The value that needs to be snapped
	 * @param snapToPixel
	 *            Whether to snap to pixel
	 * @return value either as passed in or ceil'd based on snapToPixel
	 */
	private double snapSize(double value, boolean snapToPixel) {
		return snapToPixel ? Math.ceil(value) : value;
	}

	/**
	 * If snapToPixel is true, then the value is rounded using Math.round.
	 * Otherwise, the value is simply returned.
	 *
	 * @param value
	 *            The value that needs to be snapped
	 * @param snapToPixel
	 *            Whether to snap to pixel
	 * @return value either as passed in or rounded based on snapToPixel
	 */
	private double snapPosition(double value, boolean snapToPixel) {
		return snapToPixel ? Math.round(value) + .5 : value;
	}

	private boolean drawingInProgress;

	protected final void disableRedrawAfterPropertyChange() {
		drawingInProgress = true;
	}

	protected final void enableRedrawAfterPropertyChange() {
		drawingInProgress = false;
	}

	private final InvalidationListener redrawListener = observable -> {
		if (!drawingInProgress) {
			graphics.redraw();
		}
	};

	protected void redrawObservable(Observable observable) {
		requireNonNull(observable);
		observable.addListener(redrawListener);
	}

	public final String getName() {
		return name;
	}

	public final GraphicsBase<?> getGraphics() {
		return graphics;
	}

	/**
	 * Calculates the x coordinate for the given time. This method only returns
	 * valid results when the renderers is used in a layout with horizontal
	 * orientation. It will not work in {@link AgendaLayout}.
	 *
	 * @param time
	 *            the time for which to calculate the x coordinate
	 * @return the location of the given time point
	 * @see TimelineModel#calculateLocationForTime(Instant)
	 * @since 1.0
	 */
	protected final double getLocation(Instant time) {
		Timeline timeline = getGraphics().getTimeline();
		TimelineModel<?> timelineModel = timeline.getModel();
		return timelineModel.calculateLocationForTime(time);
	}

	/**
	 * Calculates the time at the given x coordinate. This method only returns
	 * valid results when the renderers is used in a layout with horizontal
	 * orientation. It will not work in {@link AgendaLayout}.
	 *
	 * @param location
	 *            the location for which to return the time
	 * @return the time at the given x coordinate
	 * @see TimelineModel#calculateTimeForLocation(double)
	 * @since 1.0
	 */
	protected final Instant getTimeAt(double location) {
		Timeline timeline = getGraphics().getTimeline();
		TimelineModel<?> timelineModel = timeline.getModel();
		return timelineModel.calculateTimeForLocation(location);
	}

	protected Paint getFill(boolean selected, boolean hover,
			boolean highlighted, boolean pressed) {
		if (pressed) {
			return getFillPressed();
		} else if (highlighted) {
			return getFillHighlight();
		} else if (hover) {
			return getFillHover();
		} else if (selected) {
			return getFillSelected();
		} else {
			return getFill();
		}
	}

	// @formatter:off
	private final BooleanProperty enabled = new SimpleBooleanProperty(this, "enabled", true);
	private final BooleanProperty snapToPixel = new SimpleBooleanProperty(this, "snapToPixel", true);

	private final ObjectProperty<Insets> padding = new SimpleObjectProperty<>(this, "padding", Insets.EMPTY);

	private final ObjectProperty<Paint> fill = new SimpleObjectProperty<>(this, "fill");
	private final ObjectProperty<Paint> fillPressed = new SimpleObjectProperty<>(this, "fillPressed");
	private final ObjectProperty<Paint> fillHighlight = new SimpleObjectProperty<>(this, "fillHighlight");
	private final ObjectProperty<Paint> fillSelected = new SimpleObjectProperty<>(this, "fillSelected");
	private final ObjectProperty<Paint> fillHover = new SimpleObjectProperty<>(this,"fillHover");
	private final DoubleProperty alpha = new SimpleDoubleProperty(this, "alpha", 1);

	public final BooleanProperty enabledProperty() { return enabled; }
	public final BooleanProperty snapToPixelProperty() { return snapToPixel; }
	public final ObjectProperty<Insets> paddingProperty() { return padding; }
	public final DoubleProperty alphaProperty() { return alpha; }
	public final ObjectProperty<Paint> fillProperty() { return fill; }
	public final ObjectProperty<Paint> fillPressedProperty() { return fillPressed; }
	public final ObjectProperty<Paint> fillHoverProperty() { return fillHover; }
	public final ObjectProperty<Paint> fillSelectedProperty() { return fillSelected; }
	public final ObjectProperty<Paint> fillHighlightProperty() { return fillHighlight; }
	// @formatter:on

	public final boolean isEnabled() {
		return enabled.get();
	}

	public final void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	public final void setSnapToPixel(boolean snap) {
		snapToPixel.set(snap);
	}

	public final boolean isSnapToPixel() {
		return snapToPixel.get();
	}

	public final Insets getPadding() {
		return padding.get();
	}

	public final void setPadding(Insets insets) {
		requireNonNull(insets);
		padding.set(insets);
	}

	public final Paint getFill() {
		return fill.get();
	}

	public final void setFill(Paint paint) {
		Objects.nonNull(paint);
		this.fill.set(paint);
	}

	public final Paint getFillPressed() {
		return fillPressed.get();
	}

	public final void setFillPressed(Paint paint) {
		Objects.nonNull(paint);
		this.fillPressed.set(paint);
	}

	public final Paint getFillHighlight() {
		return fillHighlight.get();
	}

	public final void setFillHighlight(Paint paint) {
		Objects.nonNull(paint);
		this.fillHighlight.set(paint);
	}

	public final Paint getFillSelected() {
		return fillSelected.get();
	}

	public final void setFillSelected(Paint paint) {
		Objects.nonNull(paint);
		this.fillSelected.set(paint);
	}

	public final Paint getFillHover() {
		return fillHover.get();
	}

	public final void setFillHover(Paint paint) {
		Objects.nonNull(paint);
		this.fillHover.set(paint);
	}

	public final void setAlpha(double alpha) {
		this.alpha.set(alpha);
	}

	public final double getAlpha() {
		return alpha.get();
	}
}
