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
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 * The straight link renderer is used to draw any link of type
 * {@link ActivityLink}. It is only used by the {@link LinksCanvas}.
 *
 * @param <T> the type of the activity links drawn by this renderer
 */
public class StraightLinkRenderer<T extends ActivityLink<?>> extends LinkRenderer<T> {

    /**
     * Constructs a new link renderer.
     *
     * @param graphics the graphics view where the renderer will be used
     * @param name the name of the renderer, used for logging and debugging
     */
    public StraightLinkRenderer(GraphicsBase<?> graphics, String name) {
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
                gc.lineTo(x, sy);
                gc.lineTo(x, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_RIGHT:
            case ABOVE_LEFT:
            case ABOVE:
                x = Math.min(sx1, tx1);
                gc.lineTo(x, sy);
                gc.lineTo(x, ty);
                gc.lineTo(tx, ty);
                break;
            case RIGHT:
                double my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, my);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
                gc.lineTo(tx, ty);
                break;
            case LEFT:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, my);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
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
                gc.lineTo(x, sy);
                gc.lineTo(x, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_RIGHT:
            case ABOVE_LEFT:
            case ABOVE:
                x = Math.max(sx1, tx1);
                gc.lineTo(x, sy);
                gc.lineTo(x, ty);
                gc.lineTo(tx, ty);
                break;
            case RIGHT:
                double my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, my);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
                gc.lineTo(tx, ty);
                break;
            case LEFT:
                my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, my);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
                gc.lineTo(tx, ty);
                break;
            case SAME_LOCATION:
                break;
        }

        gc.stroke();

        drawArrowHead(ArrowDirection.LEFT, gc, tx, ty);
    }

    /**
     * Draws a link in the given graphics context from the start of the source
     * rectangle to the end of the target rectangle.
     *
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     */
    protected void drawStartToEnd(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect) {

        double offset = getOffset();
        double gap = getGap();

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
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, ty);
                gc.lineTo(tx, ty);
                break;
            case BELOW_RIGHT:
                double my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, my);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_RIGHT:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, my);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_LEFT:
            case ABOVE:
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, ty);
                gc.lineTo(tx, ty);
                break;
            case RIGHT:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, my);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
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
     * Draws a link in the given graphics context from the end of the source
     * rectangle to the start of the target rectangle.
     *
     * @param sourceRect the source rectangle
     * @param targetRect the target rectangle
     */
    protected void drawEndToStart(GraphicsContext gc, Rectangle2D sourceRect, Rectangle2D targetRect) {

        double offset = getOffset();
        double gap = getGap();

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
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, ty);
                gc.lineTo(tx, ty);
                break;
            case BELOW_LEFT:
            case BELOW:
                double my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, my);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_RIGHT:
                gc.lineTo(sx1, sy);
                gc.lineTo(sx1, ty);
                gc.lineTo(tx, ty);
                break;
            case ABOVE_LEFT:
            case ABOVE:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
                gc.lineTo(tx, ty);
                break;
            case RIGHT:
                gc.lineTo(snapPositionX(tx), snapPositionY(ty));
                break;
            case LEFT:
                my = sourceRect.getMinY() - gap;
                gc.lineTo(sx1, sy);
                gc.lineTo(tx1, my);
                gc.lineTo(tx1, ty);
                break;
            case SAME_LOCATION:
                break;
        }

        gc.stroke();

        drawArrowHead(ArrowDirection.RIGHT, gc, tx, ty);
    }
}
