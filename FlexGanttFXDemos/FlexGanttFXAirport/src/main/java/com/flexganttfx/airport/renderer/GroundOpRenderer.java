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
package com.flexganttfx.airport.renderer;

import com.flexganttfx.airport.model.GroundOp;
import com.flexganttfx.airport.model.OpType;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Colour-codes ground operation bars by their {@link OpType}.
 */
public class GroundOpRenderer extends ActivityBarRenderer<GroundOp> {

    public GroundOpRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Ground Op Renderer");
        setCornersRounded(true);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<GroundOp> activityRef,
                                       Position position, GraphicsContext gc,
                                       double x, double y, double w, double h,
                                       boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        OpType type = activityRef.getActivity().getUserObject();
        Color fill = colorForType(type);
        setFill(fill);
        setStroke(fill.darker());

        ActivityBounds bounds = super.drawActivity(activityRef, position, gc,
                x, y, w, h, selected, hover, highlighted, pressed);

        drawText(activityRef, activityRef.getActivity().getName(),
                TextPosition.CENTER, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        return bounds;
    }

    private Color colorForType(OpType type) {
        if (type == null) {
            return Color.GRAY;
        }
        switch (type) {
            case REFUELING:     return Color.DODGERBLUE;
            case CATERING:      return Color.DARKORANGE;
            case CLEANING:      return Color.MEDIUMSEAGREEN;
            case BAGGAGE_UNLOAD:
            case BAGGAGE_LOAD:  return Color.GOLD;
            case BOARDING:      return Color.MEDIUMPURPLE;
            case PUSHBACK:      return Color.TOMATO;
            case MAINTENANCE:
            default:            return Color.GRAY;
        }
    }
}
