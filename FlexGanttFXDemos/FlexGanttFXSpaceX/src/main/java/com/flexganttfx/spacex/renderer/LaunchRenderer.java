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
package com.flexganttfx.spacex.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.spacex.model.LaunchActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LaunchRenderer extends ActivityBarRenderer<LaunchActivity> {

    public LaunchRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Launch");
        setCornersRounded(false);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<LaunchActivity> ref, Position pos,
                                       GraphicsContext gc, double x, double y, double w, double h,
                                       boolean selected, boolean hover, boolean highlighted, boolean pressed) {
        Color color = launchColor(ref.getActivity().getSuccess());
        setFill(color);
        setFillHover(color.brighter());
        setFillSelected(color.darker());
        setStroke(color.darker());
        setTextFill(Color.WHITE);

        ActivityBounds bounds = super.drawActivity(ref, pos, gc, x, y, w, h,
                selected, hover, highlighted, pressed);
        drawText(ref, "#" + ref.getActivity().getFlightNumber(), TextPosition.CENTER,
                gc, x, y, w, h, selected, hover, highlighted, pressed);
        return bounds;
    }

    private Color launchColor(Boolean success) {
        if (Boolean.TRUE.equals(success)) {
            return Color.web("#4CAF50");
        }
        if (Boolean.FALSE.equals(success)) {
            return Color.web("#F44336");
        }
        return Color.web("#78909C");
    }
}
