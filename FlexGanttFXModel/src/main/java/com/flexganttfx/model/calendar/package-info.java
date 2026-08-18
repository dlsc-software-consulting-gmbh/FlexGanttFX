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
/**
 * Classes and interfaces related to calendars. A calendar is a 
 * specialized activity repository for displaying calendar activities in the background of the Gantt chart rows. 
 * A calendar has a name and can be shown or hidden. The activities returned by a calendar can not be manipulated 
 * by the user (e.g. dragging, changing start time, etc...).
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.model.calendar.CalendarBase} - the base class for custom
 * calendars.</li>
 * <li>{@link com.flexganttfx.model.calendar.WeekendCalendar} - a ready-to-use calendar
 * that returns an activity for every weekend day.</li>
 * <li>{@link com.flexganttfx.model.calendar.CalendarActivity} /
 * {@link com.flexganttfx.model.calendar.CalendarActivityBase} - the activities returned
 * by a calendar.</li>
 * </ul>
 *
 * @see com.flexganttfx.model.Calendar
 * @see com.flexganttfx.model.ActivityRepository
 *
 * @since 1.0
 */
package com.flexganttfx.model.calendar;

