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
package com.flexganttfx.model;

import java.time.Instant;

/**
 * Activities represent objects that will be displayed below the timeline in the
 * graphics view of the Gantt chart control. Activities can be added to a
 * specific layer on a row by calling {@link Row#addActivity(Layer, Activity)}.
 *
 * @since 1.0
 */
public interface Activity {

	/**
	 * The name of the activity, for example "Flight 3441".
	 *
	 * @return the name of the activity
	 * @since 1.0
	 */
	String getName();

	/**
	 * The unique id of the activity.
	 *
	 * @return the unique activity ID
	 * @since 1.0
	 */
	String getId();

	/**
	 * The start time of the activity.
	 *
	 * @return the activity start time
	 * @since 1.0
	 */
	Instant getStartTime();

	/**
	 * The end time of the activity.
	 *
	 * @return the activity end time
	 * @since 1.0
	 */
	Instant getEndTime();
}