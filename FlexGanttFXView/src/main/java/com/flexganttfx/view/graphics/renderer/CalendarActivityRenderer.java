/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.calendar.CalendarActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CalendarActivityRenderer<A extends CalendarActivity> extends ActivityRenderer<A> {

    public CalendarActivityRenderer(GraphicsBase<?> graphics, String name) {
        super(graphics, name);

        setFill(new Color(0, 0, 0, .1));
        setStroke(getFill());
        setCornersRounded(false);
    }

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
