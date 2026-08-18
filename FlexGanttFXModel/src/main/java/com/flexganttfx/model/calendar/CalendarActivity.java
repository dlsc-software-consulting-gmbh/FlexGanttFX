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
package com.flexganttfx.model.calendar;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Calendar;
import com.flexganttfx.model.Row;

/**
 * An extension of the {@link Activity} interface which marks an activity as
 * something that is being returned / managed by a {@link Calendar}. The user
 * can never directly interact with a calendar activity (they are always
 * read-only). They are used to represent calendar entries and are rendered in
 * the background of all or individual rows.
 * 
 * @see Row#getCalendars()
 * 
 * @since 1.0
 */
public interface CalendarActivity extends Activity {
}
