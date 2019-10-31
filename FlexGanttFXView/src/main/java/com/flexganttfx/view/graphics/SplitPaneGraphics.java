/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics;

import impl.com.flexganttfx.skin.graphics.SplitPaneGraphicsSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;
import javafx.util.Callback;

import com.flexganttfx.model.Row;

/**
 * A specialization of {@link GraphicsBase} that displays several rows inside a
 * {@link SplitPane}. This way each row can be easily resized via the dividers
 * provided by the {@link SplitPane}.
 *
 * @param <R>
 *            the type of the rows
 * @since 1.0
 */
public class SplitPaneGraphics<R extends Row<?, ?, ?>> extends GraphicsBase<R> {

	public SplitPaneGraphics() {
		splitPane = createSplitPane();
		getStyleClass().add("split-pane-graphics");
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new SplitPaneGraphicsSkin<>(this);
	}

	private final SplitPane splitPane;

	/**
	 * Returns the {@link SplitPane} instance used by this graphics view.
	 *
	 * @return the splitpane
	 * @since 1.0
	 */
	public final SplitPane getSplitPane() {
		return splitPane;
	}

	/**
	 * Creates the {@link SplitPane} used by this view. This method can be
	 * overridden to provide an application-specific specialization of
	 * {@link SplitPane}.
	 *
	 * @return the split pane control used by the view
	 * @since 1.0
	 */
	protected SplitPane createSplitPane() {
		return new SplitPane();
	}

	private final ObjectProperty<Callback<R, Boolean>> resizableCallback = new SimpleObjectProperty<>(this, "resizableCallback");

	/**
	 * Returns the property used to store a callback that provides the resizing
	 * behaviour of each row (see
	 * {@link SplitPane#setResizableWithParent(javafx.scene.Node, Boolean)}).
	 *
	 * @return the property used to store the resizable callback
	 * @since 1.0
	 */
	public final ObjectProperty<Callback<R, Boolean>> resizableCallbackProperty() {
		return resizableCallback;
	}

	/**
	 * Sets the value of the {@link #resizableCallbackProperty()}.
	 *
	 * @param callback
	 *            the callback to be used for looking up resize behaviour for
	 *            each row
	 * @since 1.0
	 */
	public final void setResizableCallback(Callback<R, Boolean> callback) {
		resizableCallbackProperty().set(callback);
	}

	/**
	 * Returns the value of the {@link #resizableCallbackProperty()}.
	 *
	 * @return the callback
	 * @since 1.0
	 */
	public final Callback<R, Boolean> getResizableCallback() {
		return resizableCallbackProperty().get();
	}
}
