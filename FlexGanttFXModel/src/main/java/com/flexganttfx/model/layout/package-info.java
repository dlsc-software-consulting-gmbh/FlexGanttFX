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
 * Different layout and line manager implementations used to control the layout of activities.
 * <p>
 * A layout is assigned to a row or to an individual line within a row. It influences the
 * rendering of the activities, the behaviour during editing, and the appearance of the
 * row background (system layers).
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.model.layout.GanttLayout} - the default layout. Activities
 * are laid out horizontally below the timeline.</li>
 * <li>{@link com.flexganttfx.model.layout.AgendaLayout} - activities are laid out like
 * appointments in a calendar, next to a vertical scale showing the time of day.</li>
 * <li>{@link com.flexganttfx.model.layout.ChartLayout} - activities are laid out as chart
 * bars, for example to form a capacity profile.</li>
 * <li>{@link com.flexganttfx.model.layout.EqualLinesManager} - the default lines manager.
 * Distributes the height of a row equally across all of its lines.</li>
 * <li>{@link com.flexganttfx.model.layout.LinesManagerBase} - the base class for custom
 * lines managers.</li>
 * </ul>
 *
 * @see com.flexganttfx.model.Layout
 * @see com.flexganttfx.model.LinesManager
 * @see com.flexganttfx.model.Row#setLayout(com.flexganttfx.model.Layout)
 *
 * @since 1.0
 */
package com.flexganttfx.model.layout;