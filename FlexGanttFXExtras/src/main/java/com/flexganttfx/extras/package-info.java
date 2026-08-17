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
 * Supplementary FlexGanttFX controls that are not part of the core Gantt chart
 * control but are frequently needed when building an application around it.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.extras.GanttChartToolBar} - a ready-made toolbar
 * for zooming, scrolling and switching the layout of a Gantt chart.</li>
 * <li>{@link com.flexganttfx.extras.GanttChartStatusBar} - a status bar
 * showing the currently hovered activity and the active virtual grid.</li>
 * <li>{@link com.flexganttfx.extras.LayersView} - a control for reordering,
 * hiding and deleting the layers of a graphics view.</li>
 * <li>{@link com.flexganttfx.extras.RadarView} - a miniature overview of all
 * activities currently managed by a graphics view.</li>
 * <li>{@link com.flexganttfx.extras.VirtualGridControl} - a control for
 * selecting the virtual grid used for snapping while editing.</li>
 * <li>{@link com.flexganttfx.extras.RowControls} - a minimal example of the
 * controls that can be shown inside a row.</li>
 * </ul>
 *
 * <p>
 * All of these controls are designed for rapid prototyping. They observe a
 * {@link com.flexganttfx.view.GanttChartBase} or a
 * {@link com.flexganttfx.view.graphics.GraphicsBase} and can be added to any
 * JavaFX scene graph.
 *
 * @since 1.0
 */
package com.flexganttfx.extras;

