/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.exception.IllegalLineIndexException;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.util.Objects;

/**
 * Draws separator lines between inner lines. By default the line width is set
 * to 0 and the lines will not be drawn. To change this use
 * {@link #setLineWidth(double)} and specify a value greater than 0.
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
public class InnerLinesLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

    public InnerLinesLayer(GraphicsBase<R> graphics) {
        super("Inner Lines", graphics);

        strokeProperty().bindBidirectional(graphics.innerLinesColorProperty());

        setLineWidth(.5);

        redrawObservable(strokeProperty());
        redrawObservable(lineWidthProperty());
        redrawObservable(drawLastDividerLineProperty());

        fadeInOutObservable(graphics.showInnerLinesLayerProperty());
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

    private final BooleanProperty drawLastDividerLine = new SimpleBooleanProperty(
            this, "drawLastDividerLine", false);

    public final BooleanProperty drawLastDividerLineProperty() {
        return drawLastDividerLine;
    }

    public final void setDrawLastDividerLine(boolean draw) {
        drawLastDividerLineProperty().set(draw);
    }

    public final boolean isDrawLastDividerLine() {
        return drawLastDividerLine.get();
    }

    private final ObjectProperty<double[]> lineDashes = new SimpleObjectProperty<>(this, "lineDashes");

    public final ObjectProperty<double[]> lineDashesProperty() {
        return lineDashes;
    }

    public final void setLineDashes(double... dashes) {
        lineDashesProperty().set(dashes);
    }

    public final double[] getLineDashes() {
        return lineDashesProperty().get();
    }

    @Override
    public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {

        R row = canvas.getRow();
        double lineWidth = getLineWidth();

        if (row != null && lineWidth > 0) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setStroke(getStroke());
            gc.setLineWidth(getLineWidth());

            int lastLineIndex = row.getLineCount() - 1;
            if (!isDrawLastDividerLine()) {
                lastLineIndex--;
            }

            gc.setLineDashes(getLineDashes());

            for (int lineIndex = 0; lineIndex <= lastLineIndex; lineIndex++) {
                try {
                    double y = row.getLineLocation(lineIndex)
                            + row.getLineHeight(lineIndex);
                    gc.strokeLine(0, y, canvas.getWidth(), y);
                } catch (IllegalLineIndexException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
