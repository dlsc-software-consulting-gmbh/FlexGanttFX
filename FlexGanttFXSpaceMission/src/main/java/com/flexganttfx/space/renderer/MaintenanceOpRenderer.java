/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.renderer;

import com.flexganttfx.space.model.MaintenanceOp;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.paint.Color;

/** Renders maintenance operations as gray bars. */
public class MaintenanceOpRenderer extends ActivityBarRenderer<MaintenanceOp> {

    public MaintenanceOpRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Maintenance Op");
        setFill(Color.GRAY);
        setFillSelected(Color.DARKGRAY);
        setFillHover(Color.LIGHTGRAY);
        setFillHighlight(Color.GRAY.deriveColor(0, 1, 1.3, 0.8));
        setFillPressed(Color.DARKGRAY.darker());
        setStroke(Color.DIMGRAY);
        setTextFill(Color.WHITE);
        setTextFillSelected(Color.WHITE);
        setTextFillHover(Color.BLACK);
        setTextFillHighlight(Color.WHITE);
        setTextFillPressed(Color.WHITE);
    }
}
