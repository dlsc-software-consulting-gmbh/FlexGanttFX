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
 * ControlsFX property-sheet support for inspecting and configuring FlexGanttFX
 * controls, layers, renderers and timelines at runtime.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.extras.properties.ItemProvider} - strategy
 * interface that turns a target object into a list of property sheet items.</li>
 * <li>{@link com.flexganttfx.extras.properties.ItemFactory} - registry that
 * looks up the item provider for a given target type.</li>
 * <li>{@link com.flexganttfx.extras.properties.GanttChartItemProvider},
 * {@link com.flexganttfx.extras.properties.GanttChartBaseItemProvider},
 * {@link com.flexganttfx.extras.properties.GraphicsBaseItemProvider} -
 * providers for the main chart and graphics controls.</li>
 * </ul>
 *
 * <p>
 * Sub-packages add providers for system layers, renderers and timeline
 * controls, plus views that display them.
 *
 * @since 1.0
 */
package com.flexganttfx.extras.properties;