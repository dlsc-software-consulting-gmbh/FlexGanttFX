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
 * Renderer classes for visualizing activities, calendar entries, links, and for
 * adding custom background visuals based on row type.
 *
 * <p>
 * A renderer draws onto the {@link javafx.scene.canvas.Canvas} of a graphics
 * view. Renderers are registered per activity type and layout type via
 * {@link com.flexganttfx.view.graphics.GraphicsBase#setActivityRenderer(Class, Class, com.flexganttfx.view.graphics.renderer.ActivityRenderer)}.
 * The colors used by a renderer are set programmatically (fill, fillSelected,
 * fillHover, fillHighlight, fillPressed and the corresponding stroke
 * properties) and not via CSS.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.view.graphics.renderer.ActivityRenderer} - the
 * base class for all activity renderers.</li>
 * <li>{@link com.flexganttfx.view.graphics.renderer.ActivityBarRenderer},
 * {@link com.flexganttfx.view.graphics.renderer.CompletableActivityRenderer},
 * {@link com.flexganttfx.view.graphics.renderer.ChartActivityRenderer} -
 * ready-to-use activity renderers.</li>
 * <li>{@link com.flexganttfx.view.graphics.renderer.LinkRenderer},
 * {@link com.flexganttfx.view.graphics.renderer.StraightLinkRenderer},
 * {@link com.flexganttfx.view.graphics.renderer.CurvedLinkRenderer} - renderers
 * for {@link com.flexganttfx.model.ActivityLink} instances.</li>
 * <li>{@link com.flexganttfx.view.graphics.renderer.RowRenderer} - draws the
 * background of a row.</li>
 * <li>{@link com.flexganttfx.view.graphics.renderer.CalendarActivityRenderer},
 * {@link com.flexganttfx.view.graphics.renderer.WeekendCalendarActivityRenderer}
 * - renderers used by the calendar layer.</li>
 * </ul>
 *
 * @since 1.0
 */
package com.flexganttfx.view.graphics.renderer;

