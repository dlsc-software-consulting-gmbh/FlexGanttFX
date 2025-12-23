/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.RowRenderer;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.canvas.GraphicsContext;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * Draws the background of each row. The layer can be configured with pluggable
 * renderers that are mapped to the type of the row. Applications can register
 * their own renderers by calling {@link #setRowRenderer(Class, RowRenderer)}.
 * 
 * @param <R>
 *            the type of the rows
 * 
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 * 
 * @since 1.0
 */
public class RowLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public RowLayer(GraphicsBase<R> graphics) {
		super("Row", graphics);

		setRowRenderer(Row.class, new RowRenderer<>(graphics, "Rows"));

		fadeInOutObservable(graphics.showRowLayerProperty());

		redrawObservable(rowRendererMap);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {

		R row = canvas.getRow();
		if (row != null) {
			RowRenderer renderer = getRowRenderer(row.getClass());

			if (renderer != null) {
				GraphicsBase graphics = getGraphics();
				GraphicsContext gc = canvas.getGraphicsContext2D();

				boolean selected = false;
				boolean hover = canvas.isHover();
				boolean highlight = graphics.getHighlightedRows().contains(row);
				boolean pressed = canvas.isPressed();

				renderer.draw(row, gc, canvas.getWidth(), canvas.getHeight(), selected, hover, highlight, pressed);
			}
		}
	}

	// Row renderers support.

	private final ObservableMap<Class<?>, RowRenderer<?>> rowRendererMap = FXCollections.observableHashMap();

	private final ObservableMap<Class<?>, RowRenderer<?>> rendererCache = FXCollections.observableHashMap();

	public final void setRowRenderer(Class<Row> clazz, RowRenderer<?> renderer) {
		rendererCache.clear();

		if (renderer != null) {
			LoggingDomain.CONFIG.fine("class = " + clazz + ", renderer = "
					+ renderer.getClass().getName());
		} else {
			LoggingDomain.CONFIG.fine("class = " + clazz + ", renderer = null");
		}

		requireNonNull(clazz);

		rowRendererMap.put(clazz, renderer);
	}

	@SuppressWarnings("unchecked")
	public final <RT extends Row<?, ?, ?>> RowRenderer<RT> getRowRenderer(Class<RT> clazz) {
		RowRenderer<RT> cachedRenderer = (RowRenderer<RT>) rendererCache.get(clazz);
		if (cachedRenderer != null) {
			return cachedRenderer;
		}

		RowRenderer<RT> renderer = (RowRenderer<RT>) doGetRowRenderer(clazz);
		rendererCache.put(clazz, renderer);
		return renderer;
	}

	private RowRenderer<?> doGetRowRenderer(Class<?> clazz) {
		if (clazz != null) {
			RowRenderer<?> renderer = rowRendererMap.get(clazz);
			if (renderer == null) {
				return doGetRowRenderer(clazz.getSuperclass());
			}

			return renderer;
		}

		return null;
	}
}
