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
package com.flexganttfx.model.layout;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;

import java.util.Objects;

/**
 * A base class for new {@link LinesManager} types.
 *
 * @param <A>
 *            the type of the activities
 * @since 1.0
 */
public abstract class LinesManagerBase<A extends Activity> implements LinesManager<A> {

	private final Row<?, ?, ?> row;

	/**
	 * Constructs a new lines manager.
	 *
	 * @param row
	 *            the row that will be managed by this manager class
	 * @since 1.0
	 */
	public LinesManagerBase(Row<?, ?, ?> row) {
		Objects.requireNonNull(row);

		this.row = row;
	}

	/**
	 * Returns the row managed by this manager.
	 *
	 * @return the managed row
	 * @since 1.0
	 */
	public Row<?, ?, ?> getRow() {
		return row;
	}
}
