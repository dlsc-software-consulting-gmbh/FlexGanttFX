/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras;

import impl.com.flexganttfx.extras.skin.RadarViewSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.util.FlexGanttFXControl;
import com.flexganttfx.view.graphics.GraphicsBase;

/**
 * A control used for rendering an overview of all activities within a Gantt
 * chart or to be more precise a {@link GraphicsBase}.<br>
 * <img src="doc-files/radar-view.png" alt="Radar View">
 *
 * @param <R>
 *            the type of the rows
 * @since 1.0
 */
public class RadarView<R extends Row<?, ?, ?>> extends FlexGanttFXControl {

	/**
	 * Constructs a new control.
	 *
	 * @since 1.0
	 */
	public RadarView() {
		getStylesheets().add(
				RadarView.class.getResource("radar-view.css").toExternalForm());
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new RadarViewSkin<>(this);
	}

	private final ObjectProperty<GraphicsBase<R>> graphics = new SimpleObjectProperty<>(
			this, "graphics");

	/**
	 * A property used to store the {@link GraphicsBase} for which the radar
	 * will be used. The radar will display the information provided by the rows
	 * returned by the graphics control.
	 *
	 * @see GraphicsBase#getRows()
	 *
	 * @return the property used for referencing the graphics control
	 * @since 1.0
	 */
	public final ObjectProperty<GraphicsBase<R>> graphicsProperty() {
		return graphics;
	}

	/**
	 * Returns the value of {@link #graphicsProperty()}.
	 *
	 * @return the graphics control
	 * @since 1.0
	 */
	public final GraphicsBase<R> getGraphics() {
		return graphics.get();
	}

	/**
	 * Sets the value of {@link #graphicsProperty()}.
	 *
	 * @param graphics
	 *            the graphics control for which the radar will be used
	 * @since 1.0
	 */
	public final void setGraphics(GraphicsBase<R> graphics) {
		graphicsProperty().set(graphics);
	}

	// width support

	private final DoubleProperty radarWidth = new SimpleDoubleProperty(this,
			"width", 300);

	/**
	 * The property used to store the width of the radar canvas inside the
	 * control.
	 *
	 * @return the property used to store the radar canvas width
	 * @since 1.0
	 */
	public final DoubleProperty radarWidthProperty() {
		return radarWidth;
	}

	/**
	 * Returns the value of {@link #radarWidthProperty()}.
	 *
	 * @return the width of the radar canvas
	 * @since 1.0
	 */
	public final double getRadarWidth() {
		return radarWidth.get();
	}

	/**
	 * Sets the value of {@link #radarWidthProperty()}.
	 *
	 * @param width
	 *            the width used for the radar canvas
	 * @since 1.0
	 */
	public final void setRadarWidth(double width) {
		if (width <= 0) {
			throw new IllegalArgumentException(
					"width must be larger than 0 but was " + width);
		}
		radarWidth.set(width);
	}

	// height support

	private final DoubleProperty radarHeight = new SimpleDoubleProperty(this,
			"height", 200);

	/**
	 * The property used to store the height of the radar canvas inside the
	 * control.
	 *
	 * @return the property used to store the radar canvas height
	 * @since 1.0
	 */
	public final DoubleProperty radarHeightProperty() {
		return radarHeight;
	}

	/**
	 * Returns the value of {@link #radarHeightProperty()}.
	 *
	 * @return the height of the radar canvas
	 * @since 1.0
	 */
	public final double getRadarHeight() {
		return radarHeight.get();
	}

	/**
	 * Sets the value of {@link #radarHeightProperty()}.
	 *
	 * @param height
	 *            the height used for the radar canvas
	 * @since 1.0
	 */
	public final void setRadarHeight(double height) {
		if (height <= 0) {
			throw new IllegalArgumentException(
					"height must be larger than 0 but was " + height);
		}
		radarHeight.set(height);
	}
}
