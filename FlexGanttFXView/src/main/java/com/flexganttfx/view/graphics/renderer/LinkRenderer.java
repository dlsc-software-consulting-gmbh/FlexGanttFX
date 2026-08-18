/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.graphics.LinksCanvas;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The base class for all link renderers. It is only used by the {@link LinksCanvas}.
 *
 * @see CurvedLinkRenderer
 * @see StraightLinkRenderer
 * @see GraphicsBase#setLinkRenderer(Class, LinkRenderer)
 *
 * @param <T> the type of the activity links drawn by this renderer
 * @since 1.0
 */
public abstract class LinkRenderer<T extends ActivityLink<?>> extends RendererBase {

    /**
     * An enumerator of possible locations that the target object can have
     * relative to the source object. If the target object is for example in a
     * row above the source object and the x-coordinate of its start time is
     * before the x-coordinate of the end time of the source object then it is
     * located {@link TargetLocation#ABOVE_LEFT}.
     *
     * @since 1.0
     */
    public enum TargetLocation {

        /**
         * A enumerator value indicating that the target object is located in a
         * row below the source object and that the x-coordinate of its start
         * time is larger than the x-coordinate of the end time of the source
         * object.
         *
         * @since 1.0
         */
        BELOW_RIGHT,

        /**
         * A enumerator value indicating that the target object is located in a
         * row below the source object and that the x-coordinate of its start
         * time is equal to the x-coordinate of the end time of the source
         * object.
         *
         * @since 1.0
         */
        BELOW,

        /**
         * A enumerator value indicating that the target object is located in a
         * row below the source object and that the x-coordinate of its start
         * time is smaller than the x-coordinate of the end time of the source
         * object.
         *
         * @since 1.0
         */
        BELOW_LEFT,

        /**
         * A enumerator value indicating that the target object is located in a
         * row above the source object and that the x-coordinate of its start
         * time is larger than the x-coordinate of the end time of the source
         * object.
         *
         * @since 1.0
         */
        ABOVE_RIGHT,

        /**
         * A enumerator value indicating that the target object is located in a
         * row above the source object and that the x-coordinate of its start
         * time is equal to the x-coordinate of the end time of the source
         * object.
         *
         * @since 1.0
         */
        ABOVE,

        /**
         * A enumerator value indicating that the target object is located in a
         * row above the source object and that the x-coordinate of its start
         * time is smaller than the x-coordinate of the end time of the source
         * object.
         *
         * @since 1.0
         */
        ABOVE_LEFT,

        /**
         * A enumerator value indicating that the target object is located in
         * the same row as the source object and that the x-coordinate of its
         * start time is smaller than the x-coordinate of the end time of the
         * source object.
         *
         * @since 1.0
         */
        LEFT,

        /**
         * A enumerator value indicating that the target object is located in
         * the same row as the source object and that the x-coordinate of its
         * start time is larger than the x-coordinate of the end time of the
         * source object.
         *
         * @since 1.0
         */
        RIGHT,

        /**
         * A enumerator value indicating that the target object is located in
         * the same row as the source object and that the x-coordinate of its
         * start time is equal to the x-coordinate of the end time of the source
         * object.
         *
         * @since 1.0
         */
        SAME_LOCATION
    }

    /**
     * An enum listing the various directions the arrow can be painted.
     *
     * @since 1.0
     */
    public enum ArrowDirection {

        /**
         * Draws the arrow pointing left.
         *
         * @since 1.0
         */
        LEFT,

        /**
         * Draws the arrow pointing right.
         *
         * @since 1.0
         */
        RIGHT
    }

    /**
     * Constructs a new link renderer.
     *
     * @param graphics the graphics view where the renderer will be used
     * @param name the name of the renderer, used for logging and debugging
     * @since 1.0
     */
    protected LinkRenderer(GraphicsBase<?> graphics, String name) {
        super(graphics, name);
    }

    /**
     * Determines the target location relative to the source coordinates.
     *
     * @param sx the source x coordinate
     * @param sy the source y coordinate
     * @param tx the target x coordinate
     * @param ty the target y coordinate
     * @return the relative target location
     */
    protected final TargetLocation calculateTargetLocation(double sx, double sy, double tx, double ty) {
        double xDelta = tx - sx;
        if (sy < ty) {
            if (xDelta > 0) {
                return TargetLocation.BELOW_RIGHT;
            } else if (xDelta < 0) {
                return TargetLocation.BELOW_LEFT;
            } else {
                return TargetLocation.BELOW;
            }
        } else if (sy > ty) {
            if (xDelta > 0) {
                return TargetLocation.ABOVE_RIGHT;
            } else if (xDelta < 0) {
                return TargetLocation.ABOVE_LEFT;
            } else {
                return TargetLocation.ABOVE;
            }
        } else {
            if (xDelta > 0) {
                return TargetLocation.RIGHT;
            } else if (xDelta < 0) {
                return TargetLocation.LEFT;
            } else {
                return TargetLocation.SAME_LOCATION;
            }
        }
    }

    /**
     * Draws the given link between the two activity bounds into the given graphics context.
     *
     * @param link         the link to draw
     * @param gc           the graphics context
     * @param sourceBounds the bounds of the source activity
     * @param targetBounds the bounds of the target activity
     */
    public void draw(T link, GraphicsContext gc, Rectangle2D sourceBounds, Rectangle2D targetBounds) {
        gc.setStroke(getStrokeColor());
        gc.setLineWidth(getStrokeWidth());

        switch (link.getType()) {
            case END_TO_END:
                drawEndToEnd(gc, sourceBounds, targetBounds);
                break;
            case END_TO_START:
                drawEndToStart(gc, sourceBounds, targetBounds);
                break;
            case START_TO_END:
                drawStartToEnd(gc, sourceBounds, targetBounds);
                break;
            case START_TO_START:
                drawStartToStart(gc, sourceBounds, targetBounds);
                break;
            default:
                break;
        }
    }

    /**
     * Draws a path in the given graphics context from the start of the source
     * rectangle to the start of the target rectangle.
     *
     * @param gc the graphics context to draw on
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     * @since 1.0
     */
    protected abstract void drawStartToStart(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect);

    /**
     * Draws a path in the given graphics context from the end of the source
     * rectangle to the end of the target rectangle.
     *
     * @param gc the graphics context to draw on
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     * @since 1.0
     */
    protected abstract void drawEndToEnd(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect);

    /**
     * Draws a path in the given graphics context from the start of the source
     * rectangle to the end of the target rectangle.
     *
     * @param gc the graphics context to draw on
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     * @since 1.0
     */
    protected abstract void drawStartToEnd(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect);

    /**
     * Draws a path in the given graphics context from the end of the source
     * rectangle to the start of the target rectangle.
     *
     * @param gc the graphics context to draw on
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     * @since 1.0
     */
    protected abstract void drawEndToStart(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect);

    /**
     * Draws an arrow head at the specified location.
     *
     * @param direction the arrow direction
     * @param gc the graphics context
     * @param x the x coordinate
     * @param y the y coordinate
     */
    protected void drawArrowHead(ArrowDirection direction, GraphicsContext gc, double x, double y) {
        final int s = getArrowSize();

        gc.setStroke(getArrowHeadColor());
        gc.setFill(getArrowHeadColor());

        switch (direction) {
            case LEFT:
                gc.fillPolygon(new double[]{x, x + s * 1.5, x + s * 1.5}, new double[]{y, y - s, y + s}, 3);
                gc.strokePolygon(new double[]{x, x + s * 1.5, x + s * 1.5}, new double[]{y, y - s, y + s}, 3);
                break;
            case RIGHT:
                gc.fillPolygon(new double[]{x, x - s * 1.5, x - s * 1.5}, new double[]{y, y - s, y + s}, 3);
                gc.strokePolygon(new double[]{x, x - s * 1.5, x - s * 1.5}, new double[]{y, y - s, y + s}, 3);
                break;
        }
    }

    // STROKE WIDTH

    private final DoubleProperty strokeWidth = new SimpleDoubleProperty(this, "strokeWidth", 2);

    public final double getStrokeWidth() {
        return strokeWidth.get();
    }

    /**
     * The strokeWidth property. Controls the line width used to draw links.
     *
     * @return the strokeWidth property
     */
    public final DoubleProperty strokeWidthProperty() {
        return strokeWidth;
    }

    public final void setStrokeWidth(double strokeWidth) {
        this.strokeWidth.set(strokeWidth);
    }

    // STROKE COLOR

    private final ObjectProperty<Color> strokeColor = new SimpleObjectProperty<>(this, "strokeColor", Color.ROSYBROWN);

    public final Color getStrokeColor() {
        return strokeColor.get();
    }

    /**
     * The strokeColor property. Defines the stroke color used to draw links.
     *
     * @return the strokeColor property
     */
    public final ObjectProperty<Color> strokeColorProperty() {
        return strokeColor;
    }

    public final void setStrokeColor(Color strokeColor) {
        this.strokeColor.set(strokeColor);
    }

    // ARROW HEAD COLOR

    private final ObjectProperty<Color> arrowHeadColor = new SimpleObjectProperty<>(this, "arrowHeadColor", Color.ROSYBROWN);

    public final Color getArrowHeadColor() {
        return arrowHeadColor.get();
    }

    /**
     * The arrowHeadColor property. Defines the fill and stroke color used to draw arrow heads.
     *
     * @return the arrowHeadColor property
     */
    public final ObjectProperty<Color> arrowHeadColorProperty() {
        return arrowHeadColor;
    }

    public final void setArrowHeadColor(Color arrowHeadColor) {
        this.arrowHeadColor.set(arrowHeadColor);
    }

    // ARROW SIZE

    private final IntegerProperty arrowSize = new SimpleIntegerProperty(this, "arrowSize", 3);

    /**
     * The arrow size determines how big the arrow head will be drawn.
     *
     * @return the arrow head size
     */
    public final IntegerProperty arrowSizeProperty() {
        return arrowSize;
    }

    public final int getArrowSize() {
        return arrowSize.get();
    }

    public final void setArrowSize(int arrowSize) {
        this.arrowSize.set(arrowSize);
    }

    // OFFSET

    private final DoubleProperty offset = new SimpleDoubleProperty(this, "offset", 8);

    /**
     * The offset determines the end location of the first segment of the
     * calculated path. The first segment is used to move away from the start or
     * end bounds before continuing to draw up or down.
     *
     * <p>
     * Setting a negative offset will cause an {@link IllegalArgumentException}.
     *
     * @return the offset property
     * @since 1.0
     */
    public final DoubleProperty offsetProperty() {
        return offset;
    }

    public final double getOffset() {
        return offset.get();
    }

    public final void setOffset(double offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset can not be negative");
        }

        this.offset.set(offset);
    }

    // GAP

    private final DoubleProperty gap = new SimpleDoubleProperty(this, "gap", 4);

    /**
     * The gap determines how far the line is drawn away from the bounds of the
     * source or target timeline object.
     *
     * @return the gap property
     * @since 1.0
     */
    public final DoubleProperty gapProperty() {
        return gap;
    }

    public final double getGap() {
        return gap.get();
    }

    public final void setGap(double gap) {
        this.gap.set(gap);
    }
}
