/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.renderer;

import com.flexganttfx.sprint.model.BugActivity;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.paint.Color;

public class BugRenderer extends ActivityBarRenderer<BugActivity> {

    public BugRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Bug");
        setFill(Color.TOMATO);
        setFillSelected(Color.TOMATO.darker());
        setFillHover(Color.TOMATO.brighter());
        setStroke(Color.DARKRED);
        setCornersRounded(true);
        setCornerRadius(3);
    }
}
