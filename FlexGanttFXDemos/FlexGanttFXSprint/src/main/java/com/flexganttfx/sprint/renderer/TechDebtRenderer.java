/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.renderer;

import com.flexganttfx.sprint.model.TechDebtActivity;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.paint.Color;

public class TechDebtRenderer extends ActivityBarRenderer<TechDebtActivity> {

    public TechDebtRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Tech Debt");
        setFill(Color.GOLDENROD);
        setFillSelected(Color.GOLDENROD.darker());
        setFillHover(Color.GOLDENROD.brighter());
        setStroke(Color.DARKGOLDENROD);
        setCornersRounded(true);
        setCornerRadius(3);
    }
}
