/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.airport.renderer;

import com.flexganttfx.airport.model.Flight;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renders flight blocks as dark navy bars with the flight number centred.
 */
public class FlightRenderer extends ActivityBarRenderer<Flight> {

    public FlightRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Flight Renderer");
        setFill(Color.DARKSLATEBLUE);
        setFillSelected(Color.SLATEBLUE);
        setFillHover(Color.MEDIUMSLATEBLUE);
        setStroke(Color.DARKSLATEBLUE.darker());
        setCornersRounded(false);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<Flight> activityRef,
                                       Position position, GraphicsContext gc,
                                       double x, double y, double w, double h,
                                       boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        ActivityBounds bounds = super.drawActivity(activityRef, position, gc,
                x, y, w, h, selected, hover, highlighted, pressed);

        drawText(activityRef, activityRef.getActivity().getName(),
                TextPosition.CENTER, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        return bounds;
    }
}
