/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.renderer;

import com.flexganttfx.space.model.Maneuver;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.paint.Color;

/** Renders orbital maneuvers as orange bars with a darker stroke. */
public class ManeuverRenderer extends ActivityBarRenderer<Maneuver> {

    public ManeuverRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Maneuver");
        setFill(Color.ORANGE);
        setFillSelected(Color.DARKORANGE);
        setFillHover(Color.ORANGE.brighter());
        setFillHighlight(Color.ORANGE.deriveColor(0, 1, 1.2, 0.8));
        setFillPressed(Color.DARKORANGE.darker());
        setStroke(Color.DARKORANGE.darker());
        setTextFill(Color.BLACK);
        setTextFillSelected(Color.BLACK);
        setTextFillHover(Color.BLACK);
        setTextFillHighlight(Color.BLACK);
        setTextFillPressed(Color.BLACK);
    }
}
