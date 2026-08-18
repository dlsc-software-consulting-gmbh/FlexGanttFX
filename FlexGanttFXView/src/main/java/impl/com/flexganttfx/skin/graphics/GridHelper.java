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
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Utility methods for snapping times to the current or virtual grid. The graphics skins and
 * row canvas behaviors use this helper when positioning or resizing activities.
 */
public final class GridHelper {

	private GridHelper() {
	}

	/**
	 * Returns the nearest grid-aligned instant for the given time.
	 *
	 * @param graphics the graphics control
	 * @param time the time
	 *
	 * @return the grid-aligned instant
	 */
	public static Instant grid(GraphicsBase<?> graphics, Instant time) {
		Instant time1 = GridHelper.grid(graphics, time, false);
		Instant time2 = GridHelper.grid(graphics, time, true);

		Duration diff1 = Duration.between(time, time1);
		Duration diff2 = Duration.between(time, time2);

		if (diff1.abs().compareTo(diff2.abs()) < 0) {
			return time1;
		} else {
			return time2;
		}
	}

	/**
	 * Returns the grid-aligned instant for the given time.
	 *
	 * @param graphics the graphics control
	 * @param time the time
	 * @param roundUp whether the result should be rounded up
	 *
	 * @return the grid-aligned instant
	 */
	public static Instant grid(GraphicsBase<?> graphics, Instant time, boolean roundUp) {

		VirtualGrid<?> grid;

		if (graphics.isAutoGridEnabled()) {
			Timeline timeline = graphics.getTimeline();
			Dateline dateline = timeline.getDateline();
			Resolution<?> resolution = dateline.getScaleResolutions().get(0);
			grid = resolution.createGrid();
		} else {
			grid = graphics.getVirtualGrid();
		}

		if (grid != null) {
			Timeline timeline = graphics.getTimeline();
			Dateline dateline = timeline.getDateline();
			DayOfWeek firstDayOfWeek = dateline.getFirstDayOfWeek();
			ZoneId zoneId = dateline.getZoneId();
			time = grid.adjustTime(time, zoneId, roundUp, firstDayOfWeek);
		}

		return time;
	}

	/**
	 * Returns the grid-aligned local time for the given time.
	 *
	 * @param graphics the graphics control
	 * @param time the time
	 * @param roundUp whether the result should be rounded up
	 *
	 * @return the grid-aligned local time
	 */
	public static LocalTime grid(GraphicsBase<?> graphics, LocalTime time, boolean roundUp) {
		/*
		 * There is no auto-grid for vertical local time calculations.
		 */
		VirtualGrid<?> grid = graphics.getVirtualGrid();

		if (grid != null) {
			time = grid.adjustTime(time, roundUp);
		}

		return time;
	}
}
