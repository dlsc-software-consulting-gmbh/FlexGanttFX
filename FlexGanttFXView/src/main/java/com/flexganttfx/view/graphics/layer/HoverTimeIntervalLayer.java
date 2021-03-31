/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
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
 * Draws the hover time interval specified by the dateline. If the mouse cursor
 * hovers over a week in the dateline then the layer will fill the time interval
 * defined by this week with a highlighting color.
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
public class HoverTimeIntervalLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

    public HoverTimeIntervalLayer(GraphicsBase<R> graphics) {
        super("Hover Time Interval", graphics);

        setHoverTimeIntervalFill(Color.BEIGE.deriveColor(0, 1, 1, .5));

        redrawObservable(hoverTimeIntervalFillProperty());

        fadeInOutObservable(graphics.showHoverTimeIntervalLayerProperty());
    }

    private final ObjectProperty<Paint> hoverTimeIntervalFill = new SimpleObjectProperty<>(this, "hoverTimeIntervalFill");

    public final ObjectProperty<Paint> hoverTimeIntervalFillProperty() {
        return hoverTimeIntervalFill;
    }

    public final Paint getHoverTimeIntervalFill() {
        return hoverTimeIntervalFillProperty().get();
    }

    public final void setHoverTimeIntervalFill(Paint fill) {
        Objects.requireNonNull(fill);
        hoverTimeIntervalFillProperty().set(fill);
    }

    @Override
    public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
        GraphicsBase graphics = getGraphics();
        Timeline timeline = graphics.getTimeline();
        Dateline dateline = timeline.getDateline();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        TimeInterval hoverTimeInterval = dateline.getHoverTimeInterval();

        // draw the focused time interval
        if (hoverTimeInterval != null) {
            gc.setFill(getHoverTimeIntervalFill());

            double x1 = getLocation(hoverTimeInterval.getStartTime(), canvas);
            double x2 = getLocation(hoverTimeInterval.getEndTime(), canvas);

            gc.fillRect(x1, 0, x2 - x1, canvas.getHeight());
        }
    }
}
