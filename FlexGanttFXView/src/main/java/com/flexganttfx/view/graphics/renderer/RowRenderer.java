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

import static javafx.scene.paint.Color.TRANSPARENT;
import static javafx.scene.paint.Color.YELLOW;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;

/**
 * Renders the background area of a row.
 * It fills the row bounds with the state-dependent paint configured on the renderer while respecting padding.
 */
public class RowRenderer<R extends Row<?, ?, ?>> extends Renderer {

	public RowRenderer(GraphicsBase<?> graphics, String name) {
		super(graphics, name);

		setFill(TRANSPARENT);
		setFillPressed(TRANSPARENT);
		setFillHighlight(YELLOW);
		setFillSelected(TRANSPARENT);
		setFillHover(TRANSPARENT);
	}

	/**
	 * Draws the given row.
	 *
	 * @param row the row to render
	 * @param gc the graphics context
	 * @param w the width
	 * @param h the height
	 * @param selected whether the row is selected
	 * @param hover whether the row is hovered
	 * @param highlighted whether the row is highlighted
	 * @param pressed whether the row is pressed
	 */
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

	/**
	 * Draws the row contents into the graphics context.
	 *
	 * @param row the row to render
	 * @param gc the graphics context
	 * @param w the width
	 * @param h the height
	 * @param selected whether the row is selected
	 * @param hover whether the row is hovered
	 * @param highlighted whether the row is highlighted
	 * @param pressed whether the row is pressed
	 */
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
