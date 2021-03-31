/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.util.Objects;

/**
 * Draws a vertical line at the location of the current time / now time. The
 * current time is defined in the timeline model.
 *
 * @param <R>
 *            the type of the rows
 *
 * @see TimelineModel#getNow()
 * @see TimelineModel#getNowLocation()
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public class NowLineLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public NowLineLayer(GraphicsBase<R> graphics) {
		super("Now Line", graphics);

		strokeProperty().bindBidirectional(graphics.timeNowColorProperty());

		setLineWidth(2.5);

		redrawObservable(strokeProperty());
		redrawObservable(lineWidthProperty());

		fadeInOutObservable(graphics.showNowLineLayerProperty());
	}

	private final ObjectProperty<Paint> stroke = new SimpleObjectProperty<>(this, "stroke");

	public final ObjectProperty<Paint> strokeProperty() {
		return stroke;
	}

	public final Paint getStroke() {
		return strokeProperty().get();
	}

	public final void setStroke(Paint stroke) {
		Objects.requireNonNull(stroke);
		strokeProperty().set(stroke);
	}

	private final DoubleProperty lineWidth = new SimpleDoubleProperty(this, "lineWidth");

	public final DoubleProperty lineWidthProperty() {
		return lineWidth;
	}

	public final double getLineWidth() {
		return lineWidthProperty().get();
	}

	public final void setLineWidth(double lineWidth) {
		lineWidthProperty().set(lineWidth);
	}

	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setStroke(getStroke());
		gc.setLineWidth(getLineWidth());

		TimelineModel<?> model = canvas.getTimelineModel();
		Instant now = model.getNow();

		double nowLocation = getLocation(now, canvas);

		gc.strokeLine(nowLocation, 0, nowLocation, canvas.getHeight());
	}
}
