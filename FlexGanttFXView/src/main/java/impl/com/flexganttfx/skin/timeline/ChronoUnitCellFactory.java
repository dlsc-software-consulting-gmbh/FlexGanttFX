/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package impl.com.flexganttfx.skin.timeline;

import javafx.util.Callback;

import java.time.temporal.ChronoUnit;

/**
 * Factory for creating {@link ChronoUnitDatelineCell} instances. It binds a dateline scale to
 * a specific {@link java.time.temporal.ChronoUnit} resolution.
 */
public class ChronoUnitCellFactory
		implements Callback<ChronoUnit, ChronoUnitDatelineCell> {

	/**
	 * Constructs a new cell factory.
	 */
	public ChronoUnitCellFactory() {
	}

	/**
	 * Creates a dateline cell for the given unit.
	 *
	 * @param unit the unit
	 *
	 * @return the dateline cell
	 */
	@Override
	public ChronoUnitDatelineCell call(ChronoUnit unit) {
		return new ChronoUnitDatelineCell();
	}
}
