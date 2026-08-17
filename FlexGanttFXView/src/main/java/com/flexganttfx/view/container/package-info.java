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
 * Containers for the synchronized display of two or more Gantt charts.
 *
 * <p>
 * All containers keep the layout (table width, timeline) and the scrolling and
 * zooming behaviour of the charts they manage in sync.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.view.container.DualGanttChartContainer} /
 * {@link com.flexganttfx.view.container.DualGanttChartLiteContainer} - two
 * charts, the second one shown inside a detail pane.</li>
 * <li>{@link com.flexganttfx.view.container.QuadGanttChartContainer} /
 * {@link com.flexganttfx.view.container.QuadGanttChartLiteContainer} - four
 * charts arranged in a two-by-two grid.</li>
 * <li>{@link com.flexganttfx.view.container.MultiGanttChartContainer} /
 * {@link com.flexganttfx.view.container.MultiGanttChartLiteContainer} - an
 * arbitrary number of charts stacked on top of each other.</li>
 * <li>{@link com.flexganttfx.view.container.ContainerBase} - the common base
 * class of all containers.</li>
 * </ul>
 *
 * <p>
 * When a chart is placed inside a container its
 * {@link com.flexganttfx.view.util.Position} determines whether it shows a
 * timeline or a graphics header.
 *
 * @since 1.6
 */
package com.flexganttfx.view.container;

