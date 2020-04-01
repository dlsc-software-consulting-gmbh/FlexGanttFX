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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 * A link renderer used to draw curved link lines between activities of type
 * {@link ActivityLink}. It is only used by the {@link LinksCanvas}.
 */
public class CurvedLinkRenderer<T extends ActivityLink<?>> extends LinkRenderer<T> {

    /**
     * Constructs a new link renderer.
     */
    public CurvedLinkRenderer(GraphicsBase<?> graphics, String name) {
        super(graphics, name);
    }

    /**
     * Draws a link in the given graphics context from the start of the source
     * rectangle to the start of the target rectangle.
     *
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
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

        drawArrowHead(ArrowDirection.RIGHT, gc, tx, ty);
    }

    /**
     * Draws a link in the given graphics context from the end of the source
     * rectangle to the end of the target rectangle.
     *
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
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

        drawArrowHead(ArrowDirection.LEFT, gc, tx, ty);
    }

    /**
     * Draws a path in the given graphics context from the start of the source
     * rectangle to the end of the target rectangle.
     *
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
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

        drawArrowHead(ArrowDirection.LEFT, gc, tx, ty);
    }

    /**
     * Draws a path in the given graphics context from the end of the source
     * rectangle to the start of the target rectangle.
     *
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
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

    // CURVE

    private final DoubleProperty curve = new SimpleDoubleProperty(this, "curve", 6);

    /**
     * Sets the radius for the curve. The radius can not be larger than the
     * offset (see {@link LinkRenderer#setOffset(double)}). Setting this value to 0 results
     * in corners instead of curves (but then you might consider using the {@link StraightLinkRenderer} as
     * it is more efficient).
     *
     * @param curve the radius of the curve
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
     */
    public final double getCurve() {
        return curve.get();
    }
}
