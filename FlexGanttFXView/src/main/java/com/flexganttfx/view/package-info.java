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
 * The main Gantt chart controls.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.view.GanttChart} - the full-featured Gantt chart
 * control with a tree table on the left-hand side and a graphics area on the
 * right-hand side.</li>
 * <li>{@link com.flexganttfx.view.GanttChartLite} - a light-weight variant
 * without the tree table.</li>
 * <li>{@link com.flexganttfx.view.GanttChartBase} - the common base class of
 * both controls, defining the timeline, the graphics view, the layers and the
 * activity links.</li>
 * </ul>
 *
 * <p>
 * The data shown by these controls is defined by the model types found in
 * {@link com.flexganttfx.model}, in particular
 * {@link com.flexganttfx.model.Row}, {@link com.flexganttfx.model.Activity} and
 * {@link com.flexganttfx.model.Layer}. The rendering itself is performed by the
 * renderers in {@link com.flexganttfx.view.graphics.renderer}.
 *
 * @since 1.0
 */
package com.flexganttfx.view;

