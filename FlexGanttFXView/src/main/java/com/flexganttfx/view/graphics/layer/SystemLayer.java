/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.WeakChangeListener;
import javafx.util.Duration;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * System layers are used in the background and foreground of each row. A
 * background layer gets drawn <u>before</u> the activities are drawn while a
 * foreground layer gets drawn <u>after</u> the activities are drawn. Each layer
 * is specialized on drawing one type of information: current time, selected
 * time intervals, grid lines, and so on. The graphics view manages the layers
 * in two lists and provides convenience methods to easily look them up.
 *
 * @param <R>
 *            the type of the rows
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public abstract class SystemLayer<R extends Row<?, ?, ?>> {

	private final String name;

	private final GraphicsBase<R> graphicsView;

	private final InvalidationListener redrawListener = observable -> redraw();

	private final InvalidationListener weakRedrawListener = new WeakInvalidationListener(
			redrawListener);

	private final ChangeListener<Boolean> fadeInOutListener = (observable,
			oldVisibility, newVisibility) -> {
		if (newVisibility) {
			fade(1);
		} else {
			fade(0);
		}
	};

	private final ChangeListener<Boolean> weakFadeInOutListener = new WeakChangeListener<>(
			fadeInOutListener);

	public SystemLayer(String name, GraphicsBase<R> graphicsView) {
		requireNonNull(name);
		requireNonNull(graphicsView);

		this.name = name;
		this.graphicsView = graphicsView;

		redrawObservable(visibleProperty());
		redrawObservable(opacityProperty());
	}

	protected void redrawObservable(Observable observable) {
		requireNonNull(observable);
		observable.addListener(weakRedrawListener);
	}

	protected void fadeInOutObservable(ObservableBooleanValue observable) {
		requireNonNull(observable);
		observable.addListener(weakFadeInOutListener);

		if (!observable.get()) {
			opacity.set(0);
		}
	}

	public final GraphicsBase<R> getGraphics() {
		return graphicsView;
	}

	private void fade(double opacityTarget) {
		if (getGraphics().isFadeInOutVisibilityChanges()) {
			KeyValue keyValue = new KeyValue(opacity, opacityTarget);
			KeyFrame keyFrame = new KeyFrame(
					Duration.millis(getGraphics()
							.getFadeInOutVisibilityChangesDuration()),
					keyValue);
			Timeline timeline = new Timeline(keyFrame);
			timeline.play();
		} else {
			opacity.set(opacityTarget);
		}
	}

	public String getName() {
		return name;
	}

	private final BooleanProperty snapToPixel = new SimpleBooleanProperty(this,
			"snapToPixel", true);

	public final BooleanProperty snapToPixelProperty() {
		return snapToPixel;
	}

	public final void setSnapToPixel(boolean snap) {
		snapToPixel.set(snap);
	}

	public final boolean isSnapToPixel() {
		return snapToPixel.get();
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

	private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible",
			true);

	public final BooleanProperty visibleProperty() {
		return visible;
	}

	public final boolean isVisible() {
		return visibleProperty().get();
	}

	public final void setVisible(boolean visible) {
		visibleProperty().set(visible);
	}

	public final void redraw() {
		graphicsView.redraw();
	}

	private final ReadOnlyDoubleWrapper opacity = new ReadOnlyDoubleWrapper(this,
			"opacity", 1);

	public final ReadOnlyDoubleProperty opacityProperty() {
		return opacity.getReadOnlyProperty();
	}

	public final double getOpacity() {
		return opacityProperty().get();
	}

	public abstract void drawLayer(RowCanvas<R> canvas, Instant startTime,
			Instant endTime);
}
