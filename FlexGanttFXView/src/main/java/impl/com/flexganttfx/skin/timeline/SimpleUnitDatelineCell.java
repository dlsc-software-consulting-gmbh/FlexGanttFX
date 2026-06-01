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
package impl.com.flexganttfx.skin.timeline;

import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.dateline.Resolution.Position;
import com.flexganttfx.model.util.SimpleUnit;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.DatelineCell;

import java.time.Instant;

/**
 * Dateline cell implementation for {@link com.flexganttfx.model.util.SimpleUnit}-based
 * resolutions. It updates its label and boundaries from the visible timeline interval.
 */
public class SimpleUnitDatelineCell extends DatelineCell<SimpleUnit> {

	/**
	 * Updates the dateline cell.
	 *
	 * @param startTime the start time
	 * @param endTime the end time
	 * @param resolution the resolution
	 * @param dateline the dateline
	 * @param scalePosition the scale position
	 */
	@Override
	public void update(Instant startTime, Instant endTime, Resolution<SimpleUnit> resolution, Dateline dateline, Position scalePosition) {
		super.update(startTime, endTime, resolution, dateline, scalePosition);
		setText(resolution.format(startTime, dateline.getZoneId()));
		getStyleClass().add("dateline-cell-simple");
	}
}
