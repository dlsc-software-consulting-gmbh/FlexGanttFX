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
/**
 * Various utility classes for comparing, sorting, and storing activities.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.model.util.IntervalTree} - a red-black tree optimized for
 * time interval queries. Used by the default activity repository.</li>
 * <li>{@link com.flexganttfx.model.util.TimeInterval} - an immutable start / end time
 * pair.</li>
 * <li>{@link com.flexganttfx.model.util.ActivityComparator} - compares activities based
 * on their start time.</li>
 * <li>{@link com.flexganttfx.model.util.ActivityHelper} - convenience methods for working
 * with activities and time intervals.</li>
 * <li>{@link com.flexganttfx.model.util.ChronoUnitUtils} - convenience methods for working
 * with {@link java.time.temporal.ChronoUnit}.</li>
 * <li>{@link com.flexganttfx.model.util.SimpleUnit} - a temporal unit of fixed length,
 * an alternative to the calendar-based chrono units.</li>
 * </ul>
 *
 * @since 1.0
 */
package com.flexganttfx.model.util;

