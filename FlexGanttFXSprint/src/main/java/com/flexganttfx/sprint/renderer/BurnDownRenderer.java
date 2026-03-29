/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.renderer;

import com.flexganttfx.sprint.model.BurnDownActivity;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.paint.Color;

public class BurnDownRenderer extends ActivityBarRenderer<BurnDownActivity> {

    public BurnDownRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Burn-Down");
        setFill(Color.LIGHTCORAL.deriveColor(0, 1, 1, 0.7));
        setFillSelected(Color.LIGHTCORAL.darker());
        setFillHover(Color.LIGHTCORAL.brighter());
        setStroke(Color.INDIANRED);
        setCornersRounded(false);
    }
}
