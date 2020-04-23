/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.zone.ZoneOffsetTransition;
import java.util.Objects;

/**
 * Draws a vertical line at the location of the next daylight savings time change.
 *
 * @param <R>
 *            the type of the rows
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 8.8
 */
public class DSTLineLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public DSTLineLayer(GraphicsBase<R> graphics) {
		super("DST Line", graphics);

		setStroke(Color.DARKORANGE);
		setLineWidth(2.5);

		redrawObservable(strokeProperty());
		redrawObservable(lineWidthProperty());

		fadeInOutObservable(graphics.showDSTLineLayerProperty());
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

		ZoneId zoneId;

		final Timeline timeline = canvas.getGraphics().getTimeline();
		final Dateline dateline = timeline.getDateline();
		final TemporalUnit unit = dateline.getPrimaryTemporalUnit();

		if (unit != null && (unit.equals(ChronoUnit.HOURS) || unit.equals(ChronoUnit.MINUTES))) {
			R row = canvas.getRow();
			if (row != null) {
				zoneId = row.getZoneId();
			} else {
				zoneId = dateline.getZoneId();
			}

			if (zoneId != null) {
				final ZoneOffsetTransition transition = zoneId.getRules().nextTransition(startTime);
				if (transition != null) {
					double location = getLocation(transition.getInstant(), canvas);
					gc.strokeLine(location, 0, location, canvas.getHeight());
				}
			}
		}
	}
}
