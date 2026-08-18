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
package com.flexganttfx.f1.renderer;

import com.flexganttfx.f1.model.TireStint;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TireStintRenderer extends ActivityBarRenderer<TireStint> {

    public TireStintRenderer(GraphicsBase<?> graphics) {
        super(graphics, "TireStint");
        setCornersRounded(true);
        setTextFill(Color.BLACK);
        setTextFillHover(Color.BLACK);
        setTextFillSelected(Color.BLACK);
        setTextFillHighlight(Color.BLACK);
        setTextFillPressed(Color.BLACK);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<TireStint> ref,
                                       Position pos,
                                       GraphicsContext gc,
                                       double x,
                                       double y,
                                       double w,
                                       double h,
                                       boolean selected,
                                       boolean hover,
                                       boolean highlighted,
                                       boolean pressed) {
        Color compoundColor = ref.getActivity().getCompound().getColor();
        setFill(compoundColor);
        setFillHover(compoundColor.deriveColor(0, 1, 0.92, 1));
        setFillSelected(compoundColor.deriveColor(0, 1, 0.82, 1));
        setFillHighlight(compoundColor.deriveColor(0, 1, 1.1, 1));
        setFillPressed(compoundColor.darker());
        setStroke(compoundColor.darker());
        setStrokeHover(compoundColor.darker());
        setStrokeSelected(compoundColor.darker().darker());
        setStrokeHighlight(compoundColor.darker());
        setStrokePressed(compoundColor.darker().darker());

        ActivityBounds bounds = super.drawActivity(ref, pos, gc, x, y, w, h, selected, hover, highlighted, pressed);
        drawText(ref, ref.getActivity().getCompound().getDisplayName(), TextPosition.CENTER, gc, x, y, w, h, selected, hover, highlighted, pressed);
        return bounds;
    }
}
