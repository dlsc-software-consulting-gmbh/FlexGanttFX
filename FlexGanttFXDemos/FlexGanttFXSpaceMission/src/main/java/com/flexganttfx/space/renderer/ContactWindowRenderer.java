/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.renderer;

import com.flexganttfx.space.model.ContactWindow;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.paint.Color;

/** Renders contact windows as steel-blue bars. */
public class ContactWindowRenderer extends ActivityBarRenderer<ContactWindow> {

    public ContactWindowRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Contact Window");
        setFill(Color.STEELBLUE);
        setFillSelected(Color.STEELBLUE.darker());
        setFillHover(Color.STEELBLUE.brighter());
        setFillHighlight(Color.STEELBLUE.deriveColor(0, 1, 1.3, 0.8));
        setFillPressed(Color.STEELBLUE.darker().darker());
        setStroke(Color.STEELBLUE.darker());
        setTextFill(Color.WHITE);
        setTextFillSelected(Color.WHITE);
        setTextFillHover(Color.WHITE);
        setTextFillHighlight(Color.WHITE);
        setTextFillPressed(Color.WHITE);
    }
}
