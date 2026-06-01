/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
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

    /**
     * Constructs a new inner lines layer.
     *
     * @param graphics the graphics view that owns the layer
     */
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

    /**
     * The stroke property. Stores the paint used for inner divider lines.
     *
     * @return the stroke property
     */
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

    /**
     * The lineWidth property. Stores the stroke width of the inner divider lines.
     *
     * @return the lineWidth property
     */
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

    /**
     * The drawLastDividerLine property. Controls whether the last divider line is drawn.
     *
     * @return the drawLastDividerLine property
     */
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

    /**
     * The lineDashes property. Stores the dash pattern used for inner divider lines.
     *
     * @return the lineDashes property
     */
    public final ObjectProperty<double[]> lineDashesProperty() {
        return lineDashes;
    }

    public final void setLineDashes(double... dashes) {
        lineDashesProperty().set(dashes);
    }

    public final double[] getLineDashes() {
        return lineDashesProperty().get();
    }

    /**
     * Draws divider lines between the visible inner lines of the current row.
     *
     * @param canvas the canvas to draw on
     * @param startTime the visible start time
     * @param endTime the visible end time
     */
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
