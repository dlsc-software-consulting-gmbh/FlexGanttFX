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
 * Timeline model class and the two specializations for the temporal
 * units ChronoUnit and SimpleUnit.
 * <p>
 * The timeline model stores everything that is needed for navigating in time: the
 * current time ("now"), the start time, the horizon, the zoom level (millis per pixel),
 * and the supported zoom range. It is also responsible for converting a point in time
 * into a pixel location and vice versa.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.model.timeline.TimelineModel} - the base model.</li>
 * <li>{@link com.flexganttfx.model.timeline.ChronoUnitTimelineModel} - the default model,
 * based on calendar units ranging from milliseconds to millennia.</li>
 * <li>{@link com.flexganttfx.model.timeline.SimpleUnitTimelineModel} - a model based on
 * fixed-length units, useful when the chart does not follow a calendar.</li>
 * </ul>
 *
 * @see com.flexganttfx.model.dateline.DatelineModel
 * @see com.flexganttfx.model.util.SimpleUnit
 *
 * @since 1.0
 */
package com.flexganttfx.model.timeline;

