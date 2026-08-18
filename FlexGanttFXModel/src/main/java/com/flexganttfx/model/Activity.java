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
package com.flexganttfx.model;

import java.time.Instant;

/**
 * Activities represent objects that will be displayed below the timeline in the
 * graphics view of the Gantt chart control. Activities can be added to a
 * specific layer on a row by calling {@link Row#addActivity(Layer, Activity)}.
 * <p>
 * Applications rarely implement this interface directly. Instead they extend one of
 * the base classes found in the {@code com.flexganttfx.model.activity} package, as
 * those classes already support the storage of a user object, which is the link
 * between the activity and the business object of the application.
 *
 * <h2>Code Example</h2>
 *
 * <pre>
 * public class Flight extends MutableActivityBase&lt;FlightData&gt; {
 *
 *     public Flight(FlightData data) {
 *         setUserObject(data);
 *         setName(data.flightNo);
 *         setStartTime(data.departureTime);
 *         setEndTime(data.arrivalTime);
 *     }
 * }
 *
 * Layer layer = new Layer("Flights");
 * Aircraft aircraft = new Aircraft("D-ABCD");
 * aircraft.addActivity(layer, new Flight(flightData));
 * </pre>
 *
 * @see Row#addActivity(Layer, Activity)
 * @see ActivityRef
 * @see ActivityRepository
 * @see com.flexganttfx.model.activity.ActivityBase
 * @see com.flexganttfx.model.activity.MutableActivityBase
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