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
package com.flexganttfx.sprint.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.sprint.model.MilestoneActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MilestoneRenderer extends ActivityBarRenderer<MilestoneActivity> {

    public MilestoneRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Milestone");
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<MilestoneActivity> activityRef,
                                       Position position, GraphicsContext gc,
                                       double x, double y, double w, double h,
                                       boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        double cx = x;
        double cy = y + h / 2;
        double size = Math.min(h / 2.0, 10);

        gc.save();
        gc.setFill(selected ? Color.ORANGE : Color.GOLD);
        gc.setStroke(Color.DARKGOLDENROD);
        gc.setLineWidth(1.5);
        gc.fillPolygon(
            new double[]{cx, cx + size, cx, cx - size},
            new double[]{cy - size, cy, cy + size, cy},
            4);
        gc.strokePolygon(
            new double[]{cx, cx + size, cx, cx - size},
            new double[]{cy - size, cy, cy + size, cy},
            4);
        gc.restore();

        drawText(activityRef, activityRef.getActivity().getName(),
                TextPosition.TRAILING, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        return new ActivityBounds(activityRef, cx - size, cy - size, size * 2, size * 2);
    }
}
