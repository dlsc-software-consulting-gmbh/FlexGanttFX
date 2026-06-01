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
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.calendar.CalendarActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renders calendar activities as row-spanning background fills.
 * It paints translucent blocks and borders for calendar entries without producing interactive activity bounds.
 */
public class CalendarActivityRenderer<A extends CalendarActivity> extends ActivityRenderer<A> {

    public CalendarActivityRenderer(GraphicsBase<?> graphics, String name) {
        super(graphics, name);

        setFill(new Color(0, 0, 0, .1));
        setStroke(getFill());
        setCornersRounded(false);
    }

    /**
     * Draws the calendar activity and returns the resulting bounds.
     *
     * @param activityRef the activity reference to render
     * @param position the activity position
     * @param gc the graphics context
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h the height
     * @param selected whether the activity is selected
     * @param hover whether the activity is hovered
     * @param highlighted whether the activity is highlighted
     * @param pressed whether the activity is pressed
     * @return the bounds of the rendered activity, or {@code null} for background fills
     */
    @Override
    protected ActivityBounds drawActivity(ActivityRef<A> activityRef,
                                          Position position, GraphicsContext gc, double x, double y, double w,
                                          double h, boolean selected, boolean hover, boolean highlighted,
                                          boolean pressed) {

        final GraphicsBase<?> graphics = getGraphics();
        final double alpha = gc.getGlobalAlpha();
        final boolean safeRendering = graphics.isSafeRendering();

        if (safeRendering) {
            gc.save();
        }

        gc.setGlobalAlpha(alpha * getAlpha());
        drawBackground(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);

        if (safeRendering) {
            gc.restore();
            gc.save();
        }

        gc.setGlobalAlpha(alpha * getAlpha());
        drawBorder(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);

        if (safeRendering) {
            gc.restore();
        }

        gc.setGlobalAlpha(alpha);

		/*
         * We do not need to return activity bounds for calendar entries.
		 * Entries are simply background fills.
		 */
        return null;
    }
}
