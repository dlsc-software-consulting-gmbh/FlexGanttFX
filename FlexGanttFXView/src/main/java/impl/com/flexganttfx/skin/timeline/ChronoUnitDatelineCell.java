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
import com.flexganttfx.model.util.ChronoUnitUtils;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.DatelineCell;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoField.AMPM_OF_DAY;

/**
 * Dateline cell implementation for {@link java.time.temporal.ChronoUnit}-based resolutions.
 * It formats labels and boundaries for the represented time interval and updates itself from
 * the visible timeline range.
 */
public class ChronoUnitDatelineCell extends DatelineCell<ChronoUnit> {

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
	public void update(Instant startTime, Instant endTime, Resolution<ChronoUnit> resolution, Dateline dateline, Position scalePosition) {
		super.update(startTime, endTime, resolution, dateline, scalePosition);

		ChronoUnit temporalUnit = resolution.getTemporalUnit();
		ZoneId zoneId = dateline.getZoneId();
		setText(resolution.format(startTime, zoneId));
		ZonedDateTime zonedStartTime = ChronoUnitUtils.truncate(ZonedDateTime.ofInstant(startTime, zoneId), temporalUnit, 1, dateline.getFirstDayOfWeek());

		switch (resolution.getTemporalUnit()) {
		case DAYS:
			getStyleClass().add(zonedStartTime.getDayOfWeek().toString().toLowerCase());
			break;
		case HALF_DAYS:
			getStyleClass().add(zonedStartTime.get(AMPM_OF_DAY) == 0 ? "am" : "pm");
			break;
		case MONTHS:
			getStyleClass().add(zonedStartTime.getMonth().toString().toLowerCase());
			break;
		default:
			break;
		}

		// TODO: add support for calendars that are attached to the dateline
	}
}
