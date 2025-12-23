/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class Renderer extends RendererBase {

	public Renderer(GraphicsBase<?> graphics, String name) {
		super(graphics, name);

		setFill(Color.LIGHTBLUE);
		setFillPressed(Color.LIGHTBLUE.darker());
		setFillHighlight(Color.YELLOW.deriveColor(1, 1, 1, .5));
		setFillSelected(Color.valueOf("#F21B1BBB"));
		setFillHover(Color.GREEN);

		redrawObservable(padding);
		redrawObservable(fill);
		redrawObservable(fillHighlight);
		redrawObservable(fillPressed);
		redrawObservable(fillSelected);
		redrawObservable(fillHover);
	}

	protected Paint getFill(boolean selected, boolean hover, boolean highlighted, boolean pressed) {
		if (pressed) {
			return getFillPressed();
		} else if (highlighted) {
			return getFillHighlight();
		} else if (hover) {
			return getFillHover();
		} else if (selected) {
			return getFillSelected();
		} else {
			return getFill();
		}
	}

	private final ObjectProperty<Paint> fill = new SimpleObjectProperty<>(this, "fill");
	private final ObjectProperty<Paint> fillPressed = new SimpleObjectProperty<>(this, "fillPressed");
	private final ObjectProperty<Paint> fillHighlight = new SimpleObjectProperty<>(this, "fillHighlight");
	private final ObjectProperty<Paint> fillSelected = new SimpleObjectProperty<>(this, "fillSelected");
	private final ObjectProperty<Paint> fillHover = new SimpleObjectProperty<>(this,"fillHover");

	public final ObjectProperty<Paint> fillProperty() { return fill; }
	public final ObjectProperty<Paint> fillPressedProperty() { return fillPressed; }
	public final ObjectProperty<Paint> fillHoverProperty() { return fillHover; }
	public final ObjectProperty<Paint> fillSelectedProperty() { return fillSelected; }
	public final ObjectProperty<Paint> fillHighlightProperty() { return fillHighlight; }

	public final Paint getFill() {
		return fill.get();
	}

	public final void setFill(Paint paint) {
		Objects.nonNull(paint);
		this.fill.set(paint);
	}

	public final Paint getFillPressed() {
		return fillPressed.get();
	}

	public final void setFillPressed(Paint paint) {
		Objects.nonNull(paint);
		this.fillPressed.set(paint);
	}

	public final Paint getFillHighlight() {
		return fillHighlight.get();
	}

	public final void setFillHighlight(Paint paint) {
		Objects.nonNull(paint);
		this.fillHighlight.set(paint);
	}

	public final Paint getFillSelected() {
		return fillSelected.get();
	}

	public final void setFillSelected(Paint paint) {
		Objects.nonNull(paint);
		this.fillSelected.set(paint);
	}

	public final Paint getFillHover() {
		return fillHover.get();
	}

	public final void setFillHover(Paint paint) {
		Objects.nonNull(paint);
		this.fillHover.set(paint);
	}

	// padding

	private final ObjectProperty<Insets> padding = new SimpleObjectProperty<>(this, "padding", Insets.EMPTY);

	public final ObjectProperty<Insets> paddingProperty() {
		return padding;
	}

	public final Insets getPadding() {
		return padding.get();
	}

	public final void setPadding(Insets insets) {
		requireNonNull(insets);
		padding.set(insets);
	}
}
