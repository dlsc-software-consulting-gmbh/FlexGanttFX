/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.util.Objects;

/**
 * Draws the zoom interval as defined by the timeline property
 * {@link Dateline#selectedTimeIntervalProperty()}. The zoom interval gets
 * created by the user via the help of the timeline lasso.
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
public class ZoomTimeIntervalLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public ZoomTimeIntervalLayer(GraphicsBase<R> graphics) {
		super("Zoom Time Interval", graphics);

		// -fx-accent plus 99 opacity
		setZoomTimeIntervalFill(Color.valueOf("0096C999"));

		redrawObservable(zoomTimeIntervalFillProperty());

		fadeInOutObservable(graphics.showZoomTimeIntervalLayerProperty());
	}

	private final ObjectProperty<Paint> zoomTimeIntervalFill = new SimpleObjectProperty<>(this, "zoomTimeIntervalFill");

	public final ObjectProperty<Paint> zoomTimeIntervalFillProperty() {
		return zoomTimeIntervalFill;
	}

	public final Paint getZoomTimeIntervalFill() {
		return zoomTimeIntervalFillProperty().get();
	}

	public final void setZoomTimeIntervalFill(Paint fill) {
		Objects.requireNonNull(fill);
		zoomTimeIntervalFillProperty().set(fill);
	}

	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
		GraphicsBase graphics = getGraphics();
		Timeline timeline = graphics.getTimeline();
		Dateline dateline = timeline.getDateline();
		TimeInterval selectedTimeInterval = dateline.getSelectedTimeInterval();

		if (selectedTimeInterval != null) {

			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setFill(getZoomTimeIntervalFill());

			double x1 = getLocation(selectedTimeInterval.getStartTime(), canvas);
			double x2 = getLocation(selectedTimeInterval.getEndTime(), canvas);

			gc.fillRect(x1, 0, x2 - x1, canvas.getHeight());
		}
	}
}
