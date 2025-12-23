/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.dateline.ChronoUnitResolution;
import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Draws the vertical grid lines based on the scale resolutions currently
 * present in the dateline. The layer can be configured to display 0 to 3 grid
 * line levels (see {@link GraphicsBase#setMaxGridLevel(int)}). If the dateline
 * is, for example, showing days and weeks then a level of 2 would cause the
 * layer to draw grid lines for days and weeks, while a grid line level of 1
 * would only render grid lines for days.
 *
 * @param <R>
 *            the type of the rows
 *
 * @see Dateline#getScaleResolutions()
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public class GridLinesLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

    public GridLinesLayer(GraphicsBase<R> graphics) {
        super("Grid Lines", graphics);

        lineStroke1.bindBidirectional(graphics.gridLineColor1Property());
        lineStroke2.bindBidirectional(graphics.gridLineColor2Property());
        lineStroke3.bindBidirectional(graphics.gridLineColor3Property());


        // TODO: add styleable property
        setLineWidth1(0.5);
        // TODO: add styleable property
        setLineWidth2(0.5);
        // TODO: add styleable property
        setLineWidth3(0.5);

        redrawObservable(lineStroke1);
        redrawObservable(lineStroke2);
        redrawObservable(lineStroke3);
        redrawObservable(lineWidth1);
        redrawObservable(lineWidth2);
        redrawObservable(lineWidth3);

        fadeInOutObservable(graphics.showGridLineLayerProperty());
    }

    private final ObjectProperty<Paint> lineStroke1 = new SimpleObjectProperty<>(
            this, "lineStroke1");

    public final ObjectProperty<Paint> lineStroke1Property() {
        return lineStroke1;
    }

    public final Paint getLineStroke1() {
        return lineStroke1.get();
    }

    public final void setLineStroke1(Paint stroke) {
        Objects.requireNonNull(stroke);
        lineStroke1.set(stroke);
    }

    private final ObjectProperty<Paint> lineStroke2 = new SimpleObjectProperty<>(this, "lineStroke2");

    public final ObjectProperty<Paint> lineStroke2Property() {
        return lineStroke2;
    }

    public final Paint getLineStroke2() {
        return lineStroke2.get();
    }

    public final void setLineStroke2(Paint stroke) {
        Objects.requireNonNull(stroke);
        lineStroke2.set(stroke);
    }

    private final ObjectProperty<Paint> lineStroke3 = new SimpleObjectProperty<>(this, "lineStroke3");

    public final ObjectProperty<Paint> lineStroke3Property() {
        return lineStroke3;
    }

    public final Paint getLineStroke3() {
        return lineStroke3.get();
    }

    public final void setLineStroke3(Paint stroke) {
        Objects.requireNonNull(stroke);
        lineStroke3.set(stroke);
    }

    private final DoubleProperty lineWidth1 = new SimpleDoubleProperty(this, "lineWidth1");

    public final DoubleProperty lineWidth1Property() {
        return lineWidth1;
    }

    public final double getLineWidth1() {
        return lineWidth1.get();
    }

    public final void setLineWidth1(double width) {
        lineWidth1.set(width);
    }

    private final DoubleProperty lineWidth2 = new SimpleDoubleProperty(this,
            "lineWidth2");

    public final DoubleProperty lineWidth2Property() {
        return lineWidth2;
    }

    public final double getLineWidth2() {
        return lineWidth2.get();
    }

    public final void setLineWidth2(double width) {
        lineWidth2.set(width);
    }

    private final DoubleProperty lineWidth3 = new SimpleDoubleProperty(this, "lineWidth3");

    public final DoubleProperty lineWidth3Property() {
        return lineWidth3;
    }

    public final double getLineWidth3() {
        return lineWidth3.get();
    }

    public final void setLineWidth3(double width) {
        lineWidth3.set(width);
    }

    @Override
    public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
        Dateline dateline = canvas.getGraphics().getTimeline().getDateline();
        DayOfWeek firstDayOfWeek = dateline.getFirstDayOfWeek();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        ZoneId zoneId = dateline.getZoneId();

        R row = canvas.getRow();
        if (row != null) {
            zoneId = row.getZoneId();
        }

        double height = canvas.getHeight();
        double width = canvas.getWidth();

        int maxGridLevel = canvas.getGraphics().getMaxGridLevel();

        int counter = 0;
        for (Resolution<?> resolution : getGraphics().getTimeline().getDateline().getScaleResolutions()) {

            switch (counter) {
                case 0:
                    gc.setStroke(getLineStroke1());
                    gc.setLineWidth(getLineWidth1());
                    break;
                case 1:
                    gc.setStroke(getLineStroke2());
                    gc.setLineWidth(getLineWidth2());
                    break;
                case 2:
                default:
                    gc.setStroke(getLineStroke3());
                    gc.setLineWidth(getLineWidth3());
                    break;
            }

            Instant time = resolution.truncate(startTime, zoneId, firstDayOfWeek);

            double x = getLocation(time, canvas);

            do {
                gc.strokeLine(x, 0, x, height);
                time = resolution.increment(time, zoneId);
                if (resolution instanceof ChronoUnitResolution) {
                    ChronoUnitResolution chrono = (ChronoUnitResolution) resolution;

                    /*
                     * While displaying HOUR granularity we have to check whether a DST start or end
                     * was crossed and we have to adjust the time accordingly so that the grid lines
                     * will align with the dateline cells above. We do not perform this check if the
                     * step rate is equal to 1 as then this would turn into an infinite loop.
                     */
                    if (chrono.getTemporalUnit().equals(ChronoUnit.HOURS) && chrono.getStepRate() > 1) {
                        if (chrono.isDSTStartIncrement()) {
                            time = time.minus(1, ChronoUnit.HOURS);
                        } else if (chrono.isDSTEndIncrement()) {
                            time = time.plus(1, ChronoUnit.HOURS);
                        }
                    }
                }
                x = getLocation(time, canvas);
            } while (x < width);
            counter++;

            if (counter >= maxGridLevel) {
                break;
            }
        }
    }
}
