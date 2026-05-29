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
 * Top-level model classes required for creating a Gantt chart.
 * <ul>
 * <li><b>Activity:</b> an interface that needs to be implemented by objects that want to be displayed graphically.</li>
 * <li><b>Activity Link:</b> models a dependency between two activities (e.g. successor, predecessor relationships).</li>
 * <li><b>Activity Ref:</b> an exact reference to an activity including the layer and the row where the activity is shown.</li>
 * <li><b>Activity Repository:</b> the object used by rows to store activities.</li>
 * <li><b>Calendar:</b> a specialization of activity repository for calendar information that will be rendered in the background of a row.</li>
 * <li><b>Layer:</b> used for placing activities on different levels (z-ordering).</li>
 * <li><b>Layout:</b> controls the way activities are laid out inside their row or inner line.</li>
 * <li><b>Lines Manager:</b> used to manage the location, height, and individual line layouts.</li> 
 * <li><b>Row:</b> represents a row within the Gantt chart and stores activities inside a repository.</li>
 * </ul>
 * More information for each model type can be found inside the individual class documentation.
 *
 * @since 1.0
 */
package com.flexganttfx.model;

