/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.paint.Paint;

import static java.util.Objects.requireNonNull;

/**
 * Abstract base class for renderers that paint filled shapes with optional padding.
 * It centralizes fill configuration for the normal, hover, selected, highlighted, and pressed states.
 */
public abstract class Renderer extends RendererBase {

	/**
	 * Constructs a renderer and registers its fill and padding properties for redraws.
	 *
	 * @param graphics the graphics control that uses this renderer
	 * @param name the renderer name
	 */
	public Renderer(GraphicsBase<?> graphics, String name) {
		super(graphics, name);
		redrawObservable(padding);
		redrawObservable(fill);
		redrawObservable(fillHighlight);
		redrawObservable(fillPressed);
		redrawObservable(fillSelected);
		redrawObservable(fillHover);
	}

	/**
	 * Returns the fill paint for the given renderer state.
	 *
	 * @param selected whether the item is selected
	 * @param hover whether the item is hovered
	 * @param highlighted whether the item is highlighted
	 * @param pressed whether the item is pressed
	 * @return the fill paint to use
	 */
	protected Paint getFill(boolean selected, boolean hover, boolean highlighted, boolean pressed) {
		GraphicsBase<?> g = getGraphics();
		if (pressed) {
			Paint c = getFillPressed();
			return c != null ? c : g.getActivityPressed();
		} else if (highlighted) {
			Paint c = getFillHighlight();
			return c != null ? c : g.getActivityHighlight();
		} else if (hover) {
			Paint c = getFillHover();
			return c != null ? c : g.getActivityHover();
		} else if (selected) {
			Paint c = getFillSelected();
			return c != null ? c : g.getActivitySelected();
		} else {
			Paint c = getFill();
			return c != null ? c : g.getActivityFill();
		}
	}

	private final ObjectProperty<Paint> fill = new SimpleObjectProperty<>(this, "fill");
	private final ObjectProperty<Paint> fillPressed = new SimpleObjectProperty<>(this, "fillPressed");
	private final ObjectProperty<Paint> fillHighlight = new SimpleObjectProperty<>(this, "fillHighlight");
	private final ObjectProperty<Paint> fillSelected = new SimpleObjectProperty<>(this, "fillSelected");
	private final ObjectProperty<Paint> fillHover = new SimpleObjectProperty<>(this,"fillHover");

	/**
	 * The fill property. Defines the default fill paint used by this renderer.
	 *
	 * @return the fill property
	 */
	public final ObjectProperty<Paint> fillProperty() { return fill; }
	/**
	 * The fillPressed property. Defines the fill paint used while the rendered item is pressed.
	 *
	 * @return the fillPressed property
	 */
	public final ObjectProperty<Paint> fillPressedProperty() { return fillPressed; }
	/**
	 * The fillHover property. Defines the fill paint used while the rendered item is hovered.
	 *
	 * @return the fillHover property
	 */
	public final ObjectProperty<Paint> fillHoverProperty() { return fillHover; }
	/**
	 * The fillSelected property. Defines the fill paint used while the rendered item is selected.
	 *
	 * @return the fillSelected property
	 */
	public final ObjectProperty<Paint> fillSelectedProperty() { return fillSelected; }
	/**
	 * The fillHighlight property. Defines the fill paint used while the rendered item is highlighted.
	 *
	 * @return the fillHighlight property
	 */
	public final ObjectProperty<Paint> fillHighlightProperty() { return fillHighlight; }

	public final Paint getFill() {
		return fill.get();
	}

	public final void setFill(Paint paint) {
		this.fill.set(paint);
	}

	public final Paint getFillPressed() {
		return fillPressed.get();
	}

	public final void setFillPressed(Paint paint) {
		this.fillPressed.set(paint);
	}

	public final Paint getFillHighlight() {
		return fillHighlight.get();
	}

	public final void setFillHighlight(Paint paint) {
		this.fillHighlight.set(paint);
	}

	public final Paint getFillSelected() {
		return fillSelected.get();
	}

	public final void setFillSelected(Paint paint) {
		this.fillSelected.set(paint);
	}

	public final Paint getFillHover() {
		return fillHover.get();
	}

	public final void setFillHover(Paint paint) {
		this.fillHover.set(paint);
	}

	// padding

	private final ObjectProperty<Insets> padding = new SimpleObjectProperty<>(this, "padding", Insets.EMPTY);

	/**
	 * The padding property. Controls the insets applied before shapes are drawn.
	 *
	 * @return the padding property
	 */
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
