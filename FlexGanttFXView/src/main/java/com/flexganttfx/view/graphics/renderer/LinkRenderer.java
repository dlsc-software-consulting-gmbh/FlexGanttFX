/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
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
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 * The path builder is used to compute path nodes for instances of type
 * {@link ActivityLink}. It is only used by the {@link LinksCanvas}.
 *
 * @since 1.0
 */
public class LinkRenderer<T extends ActivityLink<?>> extends RendererBase {

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
         * Draws the arrow pointing up.
         *
         * @since 1.0
         */
        UP,

        /**
         * Draws the arrow pointing down.
         *
         * @since 1.0
         */
        DOWN,

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
     * Constructs a new path calculator.
     *
     * @since 1.0
     */
    public LinkRenderer(GraphicsBase<?> graphics, String name) {
        super(graphics, name);
    }

    protected TargetLocation calculateTargetLocation(double sx, double sy, double tx, double ty) {
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
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     * @return the result path
     * @since 1.0
     */
    protected void drawStartToStart(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect) {

        double offset = getOffset();
        double gap = getGap();
        double curve = getCurve();

        double sx = snapPositionX(sourceRect.getMinX());
        double sx1 = snapPositionX(sx - offset);

        double tx = snapPositionX(targetRect.getMinX());
        double tx1 = snapPositionX(tx - offset);

        double sy = snapPositionY(sourceRect.getMinY() + sourceRect.getHeight() / 2);
        double ty = snapPositionY(targetRect.getMinY() + targetRect.getHeight() / 2);

        TargetLocation targetLocation = calculateTargetLocation(sx1, sy, tx1, ty);

        /*
         * Some optimization in case the start and end are on the same y coordinate / same row
         */
        if (sy == ty && (targetLocation.equals(TargetLocation.RIGHT) || targetLocation.equals(TargetLocation.LEFT))) {
            TargetLocation targetLocationOriginalLocations = calculateTargetLocation(sx, sy, tx, ty);
            if (!targetLocation.equals(targetLocationOriginalLocations)) {
                // source and target rectangles are too close to each other. We can not use the offset.
                sx1 = sx;
                tx1 = tx;
                targetLocation = targetLocationOriginalLocations;
            }
        }

        gc.beginPath();
        gc.moveTo(sx, sy);

        switch (targetLocation) {
            case BELOW_RIGHT:
            case BELOW:
            case BELOW_LEFT:
                double x = Math.min(sx1, tx1);
                gc.lineTo(x + curve, sy);
                gc.quadraticCurveTo(x, sy, x, sy + curve);
                gc.lineTo(x, ty - curve);
                gc.quadraticCurveTo(x, ty, x + curve, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_RIGHT:
            case ABOVE_LEFT:
            case ABOVE:
                x = Math.min(sx1, tx1);
                gc.lineTo(x + curve, sy);
                gc.quadraticCurveTo(x, sy, x, sy - curve);
                gc.lineTo(x, ty + curve);
                gc.quadraticCurveTo(x, ty, x + curve, ty);
                gc.lineTo(tx, ty);
                break;
            case RIGHT:
                double my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
                gc.lineTo(sx1 + curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy + curve);
                gc.lineTo(sx1, my - curve);
                gc.quadraticCurveTo(sx1, my, sx1 + curve, my);
                gc.lineTo(tx1 - curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my - curve);
                gc.lineTo(tx1, ty + curve);
                gc.quadraticCurveTo(tx1, ty, tx1 + curve, ty);
                gc.lineTo(tx, ty);
                break;
            case LEFT:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1 + curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy - curve);
                gc.lineTo(sx1, my + curve);
                gc.quadraticCurveTo(sx1, my, sx1 - curve, my);
                gc.lineTo(tx1 + curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my + curve);
                gc.lineTo(tx1, ty - curve);
                gc.quadraticCurveTo(tx1, ty, tx + curve, ty);
                gc.lineTo(tx, ty);
                break;
            case SAME_LOCATION:
                break;
        }

        gc.stroke();
    }

    /**
     * Draws a path in the given graphics context from the end of the source
     * rectangle to the end of the target rectangle.
     *
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     * @return the result path
     * @since 1.0
     */
    protected void drawEndToEnd(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect) {

        double offset = getOffset();
        double gap = getGap();
        double curve = getCurve();

        double sx = sourceRect.getMinX() + sourceRect.getWidth();
        double sx1 = sx + offset;

        double tx = targetRect.getMinX() + targetRect.getWidth();
        double tx1 = tx + offset;

        double sy = sourceRect.getMinY() + sourceRect.getHeight() / 2;
        double ty = targetRect.getMinY() + targetRect.getHeight() / 2;

        Path path = new Path();

        TargetLocation targetLocation = calculateTargetLocation(sx1, sy, tx1, ty);

        /*
         * Some optimization in case the start and end are on the same y coordinate / same row
         */
        if (sy == ty && (targetLocation.equals(TargetLocation.RIGHT) || targetLocation.equals(TargetLocation.LEFT))) {
            TargetLocation targetLocationOriginalLocations = calculateTargetLocation(sx, sy, tx, ty);
            if (!targetLocation.equals(targetLocationOriginalLocations)) {
                // source and target rectangles are too close to each other. We can not use the offset.
                sx1 = sx;
                tx1 = tx;
                targetLocation = targetLocationOriginalLocations;
            }
        }

        ObservableList<PathElement> pathElements = path.getElements();
        pathElements.add(new MoveTo(sx, sy));

        gc.beginPath();
        gc.moveTo(sx, sy);

        switch (targetLocation) {
            case BELOW_RIGHT:
            case BELOW:
            case BELOW_LEFT:
                double x = Math.max(sx1, tx1);
                gc.lineTo(x - curve, sy);
                gc.quadraticCurveTo(x, sy, x, sy + curve);
                gc.lineTo(x, ty - curve);
                gc.quadraticCurveTo(x, ty, x - curve, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_RIGHT:
            case ABOVE_LEFT:
            case ABOVE:
                x = Math.max(sx1, tx1);
                gc.lineTo(x - curve, sy);
                gc.quadraticCurveTo(x, sy, x, sy - curve);
                gc.lineTo(x, ty + curve);
                gc.quadraticCurveTo(x, ty, x - curve, ty);
                gc.lineTo(tx, ty);
                break;
            case RIGHT:
                double my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1 - curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy - curve);
                gc.lineTo(sx1, my + curve);
                gc.quadraticCurveTo(sx1, my, sx1 + curve, my);
                gc.lineTo(tx1 - curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my + curve);
                gc.lineTo(tx1, ty - curve);
                gc.quadraticCurveTo(tx1, ty, tx1 - curve, ty);
                gc.lineTo(tx, ty);
                break;
            case LEFT:
                my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
                gc.lineTo(sx1 - curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy + curve);
                gc.lineTo(sx1, my - curve);
                gc.quadraticCurveTo(sx1, my, sx1 - curve, my);
                gc.lineTo(tx1 + curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my - curve);
                gc.lineTo(tx1, ty + curve);
                gc.quadraticCurveTo(tx1, ty, tx1 - curve, ty);
                gc.lineTo(tx, ty);
                break;
            case SAME_LOCATION:
                break;
        }

        gc.stroke();
    }

    /**
     * Draws a path in the given graphics context from the start of the source
     * rectangle to the end of the target rectangle.
     *
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     * @return the result path
     * @since 1.0
     */
    protected void drawStartToEnd(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect) {

        double offset = getOffset();
        double gap = getGap();
        double curve = getCurve();

        double sx = sourceRect.getMinX();
        double sx1 = sx - offset;

        double tx = targetRect.getMinX() + targetRect.getWidth();
        double tx1 = tx + offset;

        double sy = sourceRect.getMinY() + sourceRect.getHeight() / 2;
        double ty = targetRect.getMinY() + targetRect.getHeight() / 2;

        TargetLocation targetLocation = calculateTargetLocation(sx1, sy, tx1, ty);

        /*
         * Some optimization in case the start and end are on the same y coordinate / same row
         */
        if (sy == ty && (targetLocation.equals(TargetLocation.RIGHT) || targetLocation.equals(TargetLocation.LEFT))) {
            TargetLocation targetLocationOriginalLocations = calculateTargetLocation(sx, sy, tx, ty);
            if (!targetLocation.equals(targetLocationOriginalLocations)) {
                // source and target rectangles are too close to each other. We can not use the offset.
                sx1 = sx;
                tx1 = tx;
                targetLocation = targetLocationOriginalLocations;
            }
        }

        gc.beginPath();
        gc.moveTo(sx, sy);

        switch (targetLocation) {
            case BELOW:
            case BELOW_LEFT:
                gc.lineTo(sx1 + curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy + curve);
                gc.lineTo(sx1, ty - curve);
                gc.quadraticCurveTo(sx1, ty, sx1 - curve, ty);
                gc.lineTo(tx, ty);
                break;
            case BELOW_RIGHT:
                double my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
                gc.lineTo(sx1 + curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy + curve);
                gc.lineTo(sx1, my - curve);
                gc.quadraticCurveTo(sx1, my, sx1 + curve, my);
                gc.lineTo(tx1 - curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my + curve);
                gc.lineTo(tx1, ty - curve);
                gc.quadraticCurveTo(tx1, ty, tx1 - curve, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_RIGHT:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1 + curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy - curve);
                gc.lineTo(sx1, my + curve);
                gc.quadraticCurveTo(sx1, my, sx1 + curve, my);
                gc.lineTo(tx1 - curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my - curve);
                gc.lineTo(tx1, ty + curve);
                gc.quadraticCurveTo(tx1, ty, tx1 - curve, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_LEFT:
            case ABOVE:
                gc.lineTo(sx1 + curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy - curve);
                gc.lineTo(sx1, ty + curve);
                gc.quadraticCurveTo(sx1, ty, sx1 - curve, ty);
                gc.lineTo(tx, ty);
                break;
            case RIGHT:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1 + curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy - curve);
                gc.lineTo(sx1, my + curve);
                gc.quadraticCurveTo(sx1, my, sx1 + curve, my);
                gc.lineTo(tx1 - curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my + curve);
                gc.lineTo(tx1, ty - curve);
                gc.quadraticCurveTo(tx1, ty, tx1 - curve, ty);
                gc.lineTo(tx, ty);
                break;
            case LEFT:
                gc.lineTo(tx, ty);
                break;
            case SAME_LOCATION:
                break;
        }

        gc.stroke();
    }

    /**
     * Draws a path in the given graphics context from the end of the source
     * rectangle to the start of the target rectangle.
     *
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     * @return the result path
     * @since 1.0
     */
    protected void drawEndToStart(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect) {

        double offset = getOffset();
        double gap = getGap();
        double curve = getCurve();

        double sx = snapPositionX(sourceRect.getMinX() + sourceRect.getWidth());
        double sx1 = snapPositionX(sx + offset);

        double tx = snapPositionX(targetRect.getMinX()) + .5;
        double tx1 = snapPositionX(tx - offset);

        double sy = snapPositionY(sourceRect.getMinY() + sourceRect.getHeight() / 2);
        double ty = snapPositionY(targetRect.getMinY() + targetRect.getHeight() / 2);

        TargetLocation targetLocation = calculateTargetLocation(sx1, sy, tx1, ty);

        /*
         * Some optimization in case the start and end are on the same y coordinate / same row
         */
        if (sy == ty && (targetLocation.equals(TargetLocation.RIGHT) || targetLocation.equals(TargetLocation.LEFT))) {
            TargetLocation targetLocationOriginalLocations = calculateTargetLocation(sx, sy, tx, ty);
            if (!targetLocation.equals(targetLocationOriginalLocations)) {
                // source and target rectangles are too close to each other. We can not use the offset.
                sx1 = sx;
                tx1 = tx;
                targetLocation = targetLocationOriginalLocations;
            }
        }

        gc.beginPath();
        gc.moveTo(sx, sy);

        switch (targetLocation) {
            case BELOW_RIGHT:
                gc.lineTo(sx1 - curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy + curve);
                gc.lineTo(sx1, ty - curve);
                gc.quadraticCurveTo(sx1, ty, sx1 + curve, ty);
                gc.lineTo(tx, ty);
                break;
            case BELOW_LEFT:
            case BELOW:
                double my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
                gc.lineTo(sx1 - curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy + curve);
                gc.lineTo(sx1, my - curve);
                gc.quadraticCurveTo(sx1, my, sx1 - curve, my);
                gc.lineTo(tx1 + curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my + curve);
                gc.lineTo(tx1, ty - curve);
                gc.quadraticCurveTo(tx1, ty, tx1 + curve, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_RIGHT:
                gc.lineTo(sx1 - curve, sy);
                gc.quadraticCurveTo(sx1, sy, sx1, sy - curve);
                gc.lineTo(sx1, ty + curve);
                gc.quadraticCurveTo(sx1, ty, sx1 + curve, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_LEFT:
            case ABOVE:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1 - curve, sy);
                double delta = (sy - my) / 2 + 1;
                gc.quadraticCurveTo(sx1 - curve + delta, sy - delta, sx1 - curve, my);
                gc.lineTo(tx1 + curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my - curve);
                gc.lineTo(tx1, ty + curve);
                gc.quadraticCurveTo(tx1, ty, tx1 + curve, ty);
                gc.lineTo(tx, ty);
                break;
            case RIGHT:
                gc.lineTo(snapPositionX(tx), snapPositionY(ty));
                break;
            case LEFT:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1 - curve, sy);
                delta = (sy - my) / 2 + 1;
                gc.quadraticCurveTo(sx1 - curve + delta, sy - delta, sx1 - curve, my);
                gc.lineTo(tx1 + curve, my);
                gc.quadraticCurveTo(tx1, my, tx1, my + curve);
                gc.lineTo(tx1, ty - curve);
                gc.quadraticCurveTo(tx1, ty, tx1 + curve, ty);
                break;
            case SAME_LOCATION:
                break;
        }

        gc.stroke();

        drawArrowHead(ArrowDirection.RIGHT, gc, tx, ty);
    }

    protected void drawArrowHead(ArrowDirection direction, GraphicsContext gc, double x, double y) {
        final int s = getArrowSize();
        gc.setFill(getArrowHeadColor());
        gc.fillPolygon(new double[]{x, x - s * 1.5, x - s * 1.5}, new double[]{y, y - s, y + s}, 3);
        gc.strokePolygon(new double[]{x, x - s * 1.5, x - s * 1.5}, new double[]{y, y - s, y + s}, 3);
    }

    // STROKE WIDTH

    private final DoubleProperty strokeWidth = new SimpleDoubleProperty(this, "strokeWidth", 2);

    public final double getStrokeWidth() {
        return strokeWidth.get();
    }

    public final DoubleProperty strokeWidthProperty() {
        return strokeWidth;
    }

    public final void setStrokeWidth(double strokeWidth) {
        this.strokeWidth.set(strokeWidth);
    }

    // STROKE COLOR

    private final ObjectProperty<Color> strokeColor = new SimpleObjectProperty<>(this, "strokeColor", Color.BLACK);

    public final Color getStrokeColor() {
        return strokeColor.get();
    }

    public final ObjectProperty<Color> strokeColorProperty() {
        return strokeColor;
    }

    public final void setStrokeColor(Color strokeColor) {
        this.strokeColor.set(strokeColor);
    }

    // ARROW HEAD COLOR

    private final ObjectProperty<Color> arrowHeadColor = new SimpleObjectProperty<>(this, "arrowHeadColor", Color.BLACK);

    public final Color getArrowHeadColor() {
        return arrowHeadColor.get();
    }

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
     */
    public final DoubleProperty offsetProperty() {
        return offset;
    }

    /**
     * The offset determines the end location of the first segment of the
     * calculated path. The first segment is used to move away from the start or
     * end bounds before continuing to draw up or down.
     *
     * @return the offset
     * @since 1.0
     */
    public final double getOffset() {
        return offset.get();
    }

    /**
     * The offset determines the end location of the first segment of the
     * calculated path. The first segment is used to move away from the start or
     * end bounds before continuing to draw up or down.
     *
     * @param offset the offset in pixels
     * @since 1.0
     */
    public final void setOffset(double offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset can not be negative");
        }

        if (getCurve() > offset) {
            throw new IllegalArgumentException("curve can not be larger than the offset (requested offset = " + offset + ", current curve = " + getCurve() + ")");
        }

        this.offset.set(offset);
    }

    // CURVE

    private final DoubleProperty curve = new SimpleDoubleProperty(this, "curve", 6);

    /**
     * Sets the radius for the curve. The radius can not be larger than the
     * offset (see {@link #setOffset(double)}). Setting this value to 0 results
     * in corners instead of curves.
     *
     * @param curve the radius of the curve
     * @since 1.0
     */
    public final void setCurve(double curve) {
        if (curve < 0) {
            throw new IllegalArgumentException("curve can not be negative");
        }

        if (curve > getOffset()) {
            throw new IllegalArgumentException("curve can not be larger than the offset (current offset = " + getOffset() + ", requested curve = " + curve + ")");
        }

        this.curve.set(curve);
    }

    /**
     * Returns the radius of the curve.
     *
     * @return the curve radius
     * @since 1.0
     */
    public final double getCurve() {
        return curve.get();
    }

    // GAP

    private final DoubleProperty gap = new SimpleDoubleProperty(this, "gap", 4);

    /**
     * The gap determines how far the line is drawn away from the bounds of the
     * source or target timeline object.
     */
    public DoubleProperty gapProperty() {
        return gap;
    }

    /**
     * The gap determines how far the line is drawn away from the bounds of the
     * source or target timeline object.
     *
     * @return the gap between line and timeline objects
     * @since 1.0
     */
    public final double getGap() {
        return gap.get();
    }

    /**
     * The gap determines how far the line is drawn away from the bounds of the
     * source or target timeline object.
     *
     * @param gap the distance between line and timeline objects
     * @since 1.0
     */
    public final void setGap(double gap) {
        this.gap.set(gap);
    }
}
