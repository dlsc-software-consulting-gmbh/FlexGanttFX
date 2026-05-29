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
package com.flexganttfx.space.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.space.model.ScienceOp;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renders science operations as teal bars with a darker completion overlay
 * showing the percentage complete.
 */
public class ScienceOpRenderer extends ActivityBarRenderer<ScienceOp> {

    private static final Color BASE_COLOR       = Color.MEDIUMSEAGREEN;
    private static final Color COMPLETE_COLOR   = Color.TEAL;

    public ScienceOpRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Science Op");
        setFill(BASE_COLOR);
        setFillSelected(BASE_COLOR.darker());
        setFillHover(BASE_COLOR.brighter());
        setFillHighlight(BASE_COLOR.deriveColor(0, 1, 1.2, 0.8));
        setFillPressed(BASE_COLOR.darker().darker());
        setStroke(BASE_COLOR.darker());
        setTextFill(Color.WHITE);
        setTextFillSelected(Color.WHITE);
        setTextFillHover(Color.WHITE);
        setTextFillHighlight(Color.WHITE);
        setTextFillPressed(Color.WHITE);
    }

    @Override
    protected ActivityBounds drawActivity(ActivityRef<ScienceOp> activityRef,
                                          Position position, GraphicsContext gc,
                                          double x, double y, double w, double h,
                                          boolean selected, boolean hover,
                                          boolean highlighted, boolean pressed) {
        // Draw base bar
        ActivityBounds bounds = super.drawActivity(activityRef, position, gc,
                x, y, w, h, selected, hover, highlighted, pressed);

        // Overlay completion sub-bar
        double pct = activityRef.getActivity().getPercentageComplete() / 100.0;
        if (pct > 0) {
            gc.setFill(COMPLETE_COLOR.deriveColor(0, 1, 1, 0.55));
            gc.fillRect(x + 1, y + h * 0.6, (w - 2) * pct, h * 0.35);
        }

        return bounds;
    }
}
