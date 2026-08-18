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

import com.flexganttfx.model.calendar.CalendarActivity;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

/**
 * A calendar is an extension of an activity repository with the additions of a
 * name and a visibility property. Calendars can be added to the whole Gantt
 * chart or to individual rows within the Gantt chart.
 *
 * @see Row#getCalendars()
 *
 * @param <A>
 *            the type of the calendar activities shown by the calendar
 * @since 1.0
 */
public interface Calendar<A extends CalendarActivity> extends
		ActivityRepository<A> {

	/**
	 * Returns the property used to store the name of the calendar. The name
	 * might be displayed by the UI (e.g. in a context menu).
	 *
	 * @return the name of the calendar, for example ("Weekends")
	 * @since 1.0
	 */
	StringProperty nameProperty();

	/**
	 * Returns the property used to store the visibility flag of the calendar.
	 * Calendars can be shown / hidden by the user.
	 *
	 * @return the calendar's visibility
	 * @since 1.0
	 */
	BooleanProperty visibleProperty();
}
