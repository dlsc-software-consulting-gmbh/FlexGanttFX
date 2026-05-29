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
package com.flexganttfx.view.graphics;

import impl.com.flexganttfx.skin.graphics.RowPane;
import impl.com.flexganttfx.skin.graphics.SingleRowGraphicsSkin;
import javafx.scene.control.Skin;

import com.flexganttfx.model.Row;

import java.util.ArrayList;
import java.util.List;

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

	private final List<RowPane<R>> rowPanes = new ArrayList<>();

	@Override
	public List<RowPane<R>> getRowPanes() {
		return rowPanes;
	}
}
