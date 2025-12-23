/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.activity.ChartActivity;
import com.flexganttfx.view.graphics.GraphicsBase;

import static javafx.scene.paint.Color.CADETBLUE;
import static javafx.scene.paint.Color.TRANSPARENT;

public class ChartActivityRenderer<A extends ChartActivity> extends
		ActivityRenderer<A> {

	public ChartActivityRenderer(GraphicsBase<?> graphics, String name) {
		super(graphics, name);

		setFill(CADETBLUE);
		setStroke(TRANSPARENT);
		setLineWidth(0);
		setCornersRounded(false);
		setSnapToPixel(false);
		setAlpha(.66);
	}
}
