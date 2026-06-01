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

    /**
     * Constructs a new hover time-interval layer.
     *
     * @param graphics the graphics view that owns the layer
     */
    public HoverTimeIntervalLayer(GraphicsBase<R> graphics) {
        super("Hover Time Interval", graphics);

        setHoverTimeIntervalFill(Color.BEIGE.deriveColor(0, 1, 1, .5));

        redrawObservable(hoverTimeIntervalFillProperty());

        fadeInOutObservable(graphics.showHoverTimeIntervalLayerProperty());
    }

    private final ObjectProperty<Paint> hoverTimeIntervalFill = new SimpleObjectProperty<>(this, "hoverTimeIntervalFill");

    /**
     * The hoverTimeIntervalFill property. Stores the fill used for the hovered time interval.
     *
     * @return the hoverTimeIntervalFill property
     */
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

    /**
     * Draws the time interval currently hovered in the dateline.
     *
     * @param canvas the canvas to draw on
     * @param startTime the visible start time
     * @param endTime the visible end time
     */
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
