/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.airport.renderer;

import com.flexganttfx.airport.model.GroundOp;
import com.flexganttfx.airport.model.OpType;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Colour-codes ground operation bars by their {@link OpType}.
 */
public class GroundOpRenderer extends ActivityBarRenderer<GroundOp> {

    public GroundOpRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Ground Op Renderer");
        setCornersRounded(true);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<GroundOp> activityRef,
                                       Position position, GraphicsContext gc,
                                       double x, double y, double w, double h,
                                       boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        OpType type = activityRef.getActivity().getUserObject();
        Color fill = colorForType(type);
        setFill(fill);
        setStroke(fill.darker());

        ActivityBounds bounds = super.drawActivity(activityRef, position, gc,
                x, y, w, h, selected, hover, highlighted, pressed);

        drawText(activityRef, activityRef.getActivity().getName(),
                TextPosition.CENTER, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        return bounds;
    }

    private Color colorForType(OpType type) {
        if (type == null) {
            return Color.GRAY;
        }
        switch (type) {
            case REFUELING:     return Color.DODGERBLUE;
            case CATERING:      return Color.DARKORANGE;
            case CLEANING:      return Color.MEDIUMSEAGREEN;
            case BAGGAGE_UNLOAD:
            case BAGGAGE_LOAD:  return Color.GOLD;
            case BOARDING:      return Color.MEDIUMPURPLE;
            case PUSHBACK:      return Color.TOMATO;
            case MAINTENANCE:
            default:            return Color.GRAY;
        }
    }
}
