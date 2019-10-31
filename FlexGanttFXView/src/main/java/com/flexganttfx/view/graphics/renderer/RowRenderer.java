/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import static javafx.scene.paint.Color.TRANSPARENT;
import static javafx.scene.paint.Color.YELLOW;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;

public class RowRenderer<R extends Row<?, ?, ?>> extends Renderer {

	public RowRenderer(GraphicsBase<?> graphics, String name) {
		super(graphics, name);

		setFill(TRANSPARENT);
		setFillPressed(TRANSPARENT);
		setFillHighlight(YELLOW);
		setFillSelected(TRANSPARENT);
		setFillHover(TRANSPARENT);
	}

	public final void draw(R row, GraphicsContext gc, double w, double h,
			boolean selected, boolean hover, boolean highlighted,
			boolean pressed) {

		disableRedrawAfterPropertyChange();

		try {
			drawRow(row, gc, w, h, selected, hover, highlighted, pressed);
		} finally {
			enableRedrawAfterPropertyChange();
		}
	}

	protected void drawRow(R row, GraphicsContext gc, double w, double h,
			boolean selected, boolean hover, boolean highlighted,
			boolean pressed) {

		Insets padding = getPadding();

		double x = padding.getLeft();
		double y = padding.getTop();

		w -= (padding.getLeft() + padding.getRight());
		h -= (padding.getTop() + padding.getBottom());

		gc.setFill(getFill(selected, hover, highlighted, pressed));
		gc.fillRect(x, y, w, h);
	}
}
