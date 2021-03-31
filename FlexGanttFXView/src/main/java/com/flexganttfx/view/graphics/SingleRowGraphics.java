/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics;

import impl.com.flexganttfx.skin.graphics.SingleRowGraphicsSkin;
import javafx.scene.control.Skin;

import com.flexganttfx.model.Row;

/**
 * A specialization of {@link GraphicsBase} that displays exactly one row. The
 * row will be the first element in the rows list (see
 * {@link GraphicsBase#getRows()}.
 * 
 * @param <R>
 *            the type of the row
 * @since 1.0
 */
public class SingleRowGraphics<R extends Row<?, ?, ?>> extends GraphicsBase<R> {

	public SingleRowGraphics() {
		getStyleClass().add("single-row-graphics");
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new SingleRowGraphicsSkin<>(this);
	}
}
