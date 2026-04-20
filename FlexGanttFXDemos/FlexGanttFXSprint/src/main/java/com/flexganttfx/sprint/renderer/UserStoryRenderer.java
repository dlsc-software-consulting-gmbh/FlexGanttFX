/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.sprint.model.UserStory;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UserStoryRenderer extends ActivityBarRenderer<UserStory> {

    public UserStoryRenderer(GraphicsBase<?> graphics) {
        super(graphics, "User Story");
        setFill(Color.STEELBLUE);
        setFillSelected(Color.STEELBLUE.darker());
        setFillHover(Color.STEELBLUE.brighter());
        setStroke(Color.STEELBLUE.darker());
        setCornersRounded(true);
        setCornerRadius(4);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<UserStory> activityRef,
                                       Position position, GraphicsContext gc,
                                       double x, double y, double w, double h,
                                       boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        ActivityBounds bounds = super.drawActivity(activityRef, position, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        // Overlay green completion bar
        UserStory story = activityRef.getActivity();
        double pct = story.getPercentageComplete() / 100.0;
        double barH = getBarHeight();
        double my = y + (h - barH) / 2;
        double completionWidth = w * pct;

        gc.save();
        gc.setFill(Color.LIMEGREEN.deriveColor(0, 1, 1, 0.55));
        if (isCornersRounded()) {
            gc.fillRoundRect(x, my, completionWidth, barH, getCornerRadius(), getCornerRadius());
        } else {
            gc.fillRect(x, my, completionWidth, barH);
        }
        gc.restore();

        // Label: name + story points
        int points = (story.getUserObject() != null) ? story.getUserObject().storyPoints : 0;
        String label = story.getName() + " (" + points + " pts)";
        drawText(activityRef, label, TextPosition.CENTER, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        return bounds;
    }
}
