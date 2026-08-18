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
import com.flexganttfx.sprint.model.TaskActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TaskRenderer extends ActivityBarRenderer<TaskActivity> {

    public TaskRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Task");
        setFill(Color.MEDIUMPURPLE);
        setFillSelected(Color.MEDIUMPURPLE.darker());
        setFillHover(Color.MEDIUMPURPLE.brighter());
        setStroke(Color.PURPLE.darker());
        setCornersRounded(true);
        setCornerRadius(3);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<TaskActivity> activityRef,
                                       Position position, GraphicsContext gc,
                                       double x, double y, double w, double h,
                                       boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        ActivityBounds bounds = super.drawActivity(activityRef, position, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        TaskActivity task = activityRef.getActivity();
        String assignee = task.getUserObject();
        String label = assignee == null || assignee.isBlank() ? task.getName() : task.getName() + " - " + assignee;
        drawText(activityRef, label, TextPosition.CENTER, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        return bounds;
    }
}
