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
 * Classes and interfaces related to activities.
 * <ul>
 * <li>Chart activity: adds capabilities to an activity to display it as a chart value (vertical bar).</li>
 * <li>High / low chart activity: adds capabilities to an activity to display it as a chart value (vertical bar).</li>
 * <li>CompletableActivity: adds capabilities to an activity to visualize a "percentage complete" value inside the activity bar.</li>
 * </ul>
 * <p>
 * For each one of these basic interfaces the package includes a mutable version and a base implementation.
 * </p>
 * <ul>
 * <li>ActivityBase / MutableActivity / MutableActivityBase</li>
 * <li>ChartActivity / ChartActivityBase / MutableChartActivity / MutableChartActivityBase</li>
 * <li>HighLowChartActivity / HighLowChartActivityBase / HighLowMutableChartActivity / HighLowMutableChartActivityBase</li>
 * <li>CompletableActivity / CompletableActivityBase / MutableCompletableActivity / MutableCompletableActivityBase</li>
 * </ul>
 * <p>
 * All base implementations store an optional user object, which is the link between the
 * activity displayed in the Gantt chart and the business object of the application. The
 * mutable variants add setters for the name, the start time, and the end time and are the
 * ones to use whenever the user should be able to edit activities interactively.
 *
 * @see com.flexganttfx.model.ActivityRef
 * @see com.flexganttfx.model.ActivityRepository
 *
 * @since 1.0
 */
package com.flexganttfx.model.activity;

