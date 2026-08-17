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
 * System layers are used to draw various pieces of information into the
 * background or foreground of a row (time now, grid lines, marked time
 * interval, chart and agenda lines, etc...).
 *
 * <p>
 * In contrast to a {@link com.flexganttfx.model.Layer}, which groups
 * activities, a {@link com.flexganttfx.view.graphics.layer.SystemLayer} is a
 * pure rendering concern. Every system layer can be looked up, configured and
 * switched on or off individually.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.view.graphics.layer.SystemLayer} - the base class
 * of all system layers.</li>
 * <li>{@link com.flexganttfx.view.graphics.layer.NowLineLayer},
 * {@link com.flexganttfx.view.graphics.layer.GridLinesLayer},
 * {@link com.flexganttfx.view.graphics.layer.CalendarLayer},
 * {@link com.flexganttfx.view.graphics.layer.DSTLineLayer} - background
 * decorations.</li>
 * <li>{@link com.flexganttfx.view.graphics.layer.LayoutLayer},
 * {@link com.flexganttfx.view.graphics.layer.RowLayer},
 * {@link com.flexganttfx.view.graphics.layer.InnerLinesLayer},
 * {@link com.flexganttfx.view.graphics.layer.ChartLinesLayer},
 * {@link com.flexganttfx.view.graphics.layer.AgendaLinesLayer} - layers that
 * depend on the layout of a row.</li>
 * <li>{@link com.flexganttfx.view.graphics.layer.HoverTimeIntervalLayer},
 * {@link com.flexganttfx.view.graphics.layer.SelectedTimeIntervalsLayer},
 * {@link com.flexganttfx.view.graphics.layer.ZoomTimeIntervalLayer} - layers
 * that visualize user interaction.</li>
 * </ul>
 *
 * @see com.flexganttfx.view.graphics.GraphicsBase#getSystemLayer(Class)
 * @see com.flexganttfx.view.graphics.GraphicsBase#getForegroundSystemLayers()
 * @see com.flexganttfx.view.graphics.GraphicsBase#getBackgroundSystemLayers()
 *
 * @since 1.0
 */
package com.flexganttfx.view.graphics.layer;

