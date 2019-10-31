/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.util.FlexGanttFXControl;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsBase;

import impl.com.flexganttfx.extras.skin.LayersViewSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

/**
 * A control used for displaying the list of layers used by the
 * {@link GraphicsBase}. The user can manipulate the order of the layers, the
 * opacity of the layers, and also delete layers.<br>
 * <img src="doc-files/layers-view.png" alt="Layers View">
 *
 * @see GraphicsBase#getLayers()
 *
 * @param <R>
 *            the type of the rows
 * @since 1.0
 */
public class LayersView<R extends Row<?, ?, ?>> extends FlexGanttFXControl {

	/**
	 * Constructs a new layer view.
	 */
	public LayersView() {
		getStylesheets().add(
				LayersView.class.getResource("layers-view.css")
						.toExternalForm());

		getStylesheets().add(
				GanttChart.class.getResource("icons16/icons.css")
						.toExternalForm());
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new LayersViewSkin<>(this);
	}

	private final ObjectProperty<GraphicsBase<R>> graphics = new SimpleObjectProperty<>(
			this, "graphics");

	/**
	 * A property used to store a reference to the graphics view for which the
	 * control is being used.
	 *
	 * @see GraphicsBase#getLayers()
	 *
	 * @return the property used to store the graphics view
	 * @since 1.0
	 */
	public final ObjectProperty<GraphicsBase<R>> graphicsProperty() {
		return graphics;
	}

	/**
	 * Returns the value of {@link #graphicsProperty()}.
	 *
	 * @return the graphics view used for this control
	 * @since 1.0
	 */
	public final GraphicsBase<R> getGraphics() {
		return graphics.get();
	}

	/**
	 * Sets the value of {@link #graphicsProperty()}.
	 *
	 * @param graphics
	 *            the graphics view used for this control
	 * @since 1.0
	 */
	public final void setGraphics(GraphicsBase<R> graphics) {
		graphicsProperty().set(graphics);
	}
}
