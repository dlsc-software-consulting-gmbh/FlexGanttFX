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
package impl.com.flexganttfx.skin.treetable;

import java.io.Serializable;

import com.flexganttfx.model.Row;

import javafx.scene.control.TreeTableRow;

/**
 * Custom {@link javafx.scene.control.TreeTableRow} used by the Gantt chart tree table. It
 * derives its height from the backing row when possible and otherwise falls back to the
 * default row sizing.
 */
public class GanttChartTreeTableRow<R extends Row<?, ?, ?>> extends
		TreeTableRow<R> implements Serializable {

	private static final long serialVersionUID = -2245080748276282382L;

	/**
	 * Constructs a new tree table row.
	 */
	public GanttChartTreeTableRow() {
	}

	/**
	 * Computes the minimum height for the given width.
	 *
	 * @param width the width
	 *
	 * @return the minimum height
	 */
	@Override
	protected double computeMinHeight(double width) {
		if (getItem() != null) {
			return getItem().getMinHeight();
		}

		return Row.DEFAULT_ROW_HEIGHT;
	}

	/**
	 * Computes the preferred height for the given width.
	 *
	 * @param width the width
	 *
	 * @return the preferred height
	 */
	@Override
	protected double computePrefHeight(double width) {
		if (getItem() != null) {
			return getItem().getHeight();
		}

		return Row.DEFAULT_ROW_HEIGHT;
	}

	/**
	 * Computes the maximum height for the given width.
	 *
	 * @param width the width
	 *
	 * @return the maximum height
	 */
	@Override
	protected double computeMaxHeight(double width) {
		if (getItem() != null) {
			return getItem().getMaxHeight();
		}

		return Row.DEFAULT_ROW_HEIGHT;
	}
}
