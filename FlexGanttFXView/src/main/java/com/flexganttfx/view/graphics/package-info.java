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
 * The various graphics controls that are used for rendering the Gantt chart.
 *
 * <p>
 * The graphics view is the canvas-based area in which the activities of the
 * rows are drawn. It resolves the renderer for each activity based on the
 * activity type and the {@link com.flexganttfx.model.Layout} of the row.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.view.graphics.GraphicsBase} - the base class of
 * all graphics views; manages layers, renderers, system layers, selection,
 * editing and scrolling.</li>
 * <li>{@link com.flexganttfx.view.graphics.ListViewGraphics},
 * {@link com.flexganttfx.view.graphics.SingleRowGraphics},
 * {@link com.flexganttfx.view.graphics.SplitPaneGraphics},
 * {@link com.flexganttfx.view.graphics.VBoxGraphics} - concrete graphics
 * implementations for different use cases.</li>
 * <li>{@link com.flexganttfx.view.graphics.ActivityBounds} - the bounds
 * occupied by an activity after it has been drawn.</li>
 * <li>{@link com.flexganttfx.view.graphics.ActivityEvent},
 * {@link com.flexganttfx.view.graphics.LassoEvent} - events fired while
 * activities are edited or selected.</li>
 * </ul>
 *
 * @since 1.0
 */
package com.flexganttfx.view.graphics;

