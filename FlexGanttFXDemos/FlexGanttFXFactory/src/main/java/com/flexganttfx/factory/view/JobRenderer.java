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
package com.flexganttfx.factory.view;

import com.flexganttfx.factory.model.Job;
import com.flexganttfx.factory.model.JobStatus;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.CompletableActivityRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.EnumMap;

/**
 * Renders {@link Job} activities as colour-coded bars based on their
 * {@link JobStatus}. A semi-transparent overlay drawn by the parent
 * {@link CompletableActivityRenderer} shows the percentage complete. The job
 * name is drawn in the centre of the bar.
 */
public class JobRenderer extends CompletableActivityRenderer<Job> {

    private static final EnumMap<JobStatus, Color> FILL_MAP = new EnumMap<>(JobStatus.class);
    private static final EnumMap<JobStatus, Color> STROKE_MAP = new EnumMap<>(JobStatus.class);

    static {
        FILL_MAP.put(JobStatus.SCHEDULED,   Color.STEELBLUE);
        FILL_MAP.put(JobStatus.IN_PROGRESS, Color.DARKORANGE);
        FILL_MAP.put(JobStatus.DONE,        Color.MEDIUMSEAGREEN);
        FILL_MAP.put(JobStatus.DELAYED,     Color.CRIMSON);

        STROKE_MAP.put(JobStatus.SCHEDULED,   Color.STEELBLUE.darker());
        STROKE_MAP.put(JobStatus.IN_PROGRESS, Color.DARKORANGE.darker());
        STROKE_MAP.put(JobStatus.DONE,        Color.MEDIUMSEAGREEN.darker());
        STROKE_MAP.put(JobStatus.DELAYED,     Color.CRIMSON.darker());
    }

    public JobRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Job");
        setCornersRounded(true);
        setFillCompletion(new Color(0, 0, 0, 0.25));
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<Job> activityRef,
                                       Position position, GraphicsContext gc,
                                       double x, double y, double w, double h,
                                       boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {

        JobStatus status = activityRef.getActivity().getStatus();
        setFill(FILL_MAP.getOrDefault(status, Color.GRAY));
        setStroke(STROKE_MAP.getOrDefault(status, Color.DARKGRAY));

        ActivityBounds bounds = super.drawActivity(activityRef, position, gc,
                x, y, w, h, selected, hover, highlighted, pressed);

        drawText(activityRef, activityRef.getActivity().getName(),
                TextPosition.CENTER, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        return bounds;
    }
}
