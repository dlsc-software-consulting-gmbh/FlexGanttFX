/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.RendererBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
 * @param <R> the type of the rows
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public abstract class SystemLayer<R extends Row<?, ?, ?>> extends RendererBase {

	private final ChangeListener<Boolean> fadeInOutListener = (observable, oldVisibility, newVisibility) -> {
		if (newVisibility) {
			fade(1);
		} else {
			fade(0);
		}
	};

	private final ChangeListener<Boolean> weakFadeInOutListener = new WeakChangeListener<>(fadeInOutListener);

	public SystemLayer(String name, GraphicsBase<R> graphicsView) {
		super(graphicsView, name);

		redrawObservable(visibleProperty());
		redrawObservable(opacityProperty());
	}

	protected void fadeInOutObservable(ObservableBooleanValue observable) {
		requireNonNull(observable);
		observable.addListener(weakFadeInOutListener);

		if (!observable.get()) {
			opacity.set(0);
		}
	}

	private void fade(double opacityTarget) {
		if (getGraphics().isFadeInOutVisibilityChanges()) {
			KeyValue keyValue = new KeyValue(opacity, opacityTarget);
			KeyFrame keyFrame = new KeyFrame(Duration.millis(getGraphics().getFadeInOutVisibilityChangesDuration()), keyValue);
			Timeline timeline = new Timeline(keyFrame);
			timeline.play();
		} else {
			opacity.set(opacityTarget);
		}
	}

	private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible", true);

	public final BooleanProperty visibleProperty() {
		return visible;
	}

	public final boolean isVisible() {
		return visibleProperty().get();
	}

	public final void setVisible(boolean visible) {
		visibleProperty().set(visible);
	}

	private final ReadOnlyDoubleWrapper opacity = new ReadOnlyDoubleWrapper(this, "opacity", 1);

	public final ReadOnlyDoubleProperty opacityProperty() {
		return opacity.getReadOnlyProperty();
	}

	public final double getOpacity() {
		return opacityProperty().get();
	}

	public abstract void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime);
}
