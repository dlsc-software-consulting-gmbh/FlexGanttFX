/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
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
