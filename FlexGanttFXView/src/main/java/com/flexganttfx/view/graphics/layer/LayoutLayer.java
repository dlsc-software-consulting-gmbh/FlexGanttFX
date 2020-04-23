/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Layout;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * Draws the layout padding areas. Each layout may have some padding added to
 * its top and bottom. This layer fills the padding area with a solid color.
 *
 * @param <R>
 *            the type of the rows
 *
 * @see Layout#getPadding()
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public class LayoutLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public LayoutLayer(GraphicsBase<R> graphics) {
		super("Layout", graphics);

		redrawObservable(paddingFill);

		fadeInOutObservable(graphics.showLayoutLayerProperty());
	}

	private final ObjectProperty<Paint> paddingFill = new SimpleObjectProperty<>(
			this, "fill", new Color(0, 0, 0, .1));

	public final ObjectProperty<Paint> paddingFillProperty() {
		return paddingFill;
	}

	public final Paint getPaddingFill() {
		return paddingFill.get();
	}

	public final void setPaddingFill(Paint fill) {
		requireNonNull("fill can not be null");
		paddingFillProperty().set(fill);
	}

	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime,
			Instant endTime) {

		Row<?, ?, ?> row = canvas.getRow();
		if (row != null) {

			double y = 0;
			double w = canvas.getWidth();
			double h = canvas.getHeight();

			Layout layout = row.getLayout();
			if (layout == null) {
				throw new IllegalArgumentException(
						"no layout returned for row " + row.getName());
			}

			drawLayout(layout, canvas, y, w, h);

			int lineCount = row.getLineCount();
			if (lineCount > 0) {
				for (int i = 0; i < lineCount; i++) {
					y = row.getLineLocation(i);
					h = row.getLineHeight(i);
					layout = row.getLineLayout(i);

					if (layout == null) {
						throw new IllegalArgumentException(
								"no layout returned for line " + i + " of row "
										+ row.getName() + ", lines manager = "
										+ row.getLinesManager().getClass());
					}

					drawLayout(layout, canvas, y, w, h);
				}
			}
		}
	}

	private void drawLayout(Layout layout, RowCanvas<R> canvas, double y,
			double w, double h) {
		double padding = layout.getPadding();
		if (padding > 0 && h >= 2 * padding) {
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setFill(getPaddingFill());
			gc.fillRect(0, y, w, padding);
			gc.fillRect(0, y + h - padding + 1, w, padding);
		}
	}
}
