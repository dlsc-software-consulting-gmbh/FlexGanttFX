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
 * The timeline control, which consists of the dateline and the eventline child
 * controls.
 *
 * <p>
 * The timeline defines which time interval is currently visible and how time is
 * mapped to pixel coordinates. Its behaviour is driven by the models found in
 * {@link com.flexganttfx.model.timeline} and
 * {@link com.flexganttfx.model.dateline}.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.view.timeline.Timeline} - the container control;
 * defines the zoom mode and the visible time interval.</li>
 * <li>{@link com.flexganttfx.view.timeline.Dateline} - displays the time scales
 * (days, weeks, months, years, ...).</li>
 * <li>{@link com.flexganttfx.view.timeline.DatelineCell} - the cell used for
 * rendering a single entry of a dateline scale.</li>
 * <li>{@link com.flexganttfx.view.timeline.Eventline} - displays time markers
 * and events.</li>
 * <li>{@link com.flexganttfx.view.timeline.TimeTracker} - keeps track of the
 * current time so that time-dependent visuals stay up-to-date.</li>
 * </ul>
 *
 * @since 1.0
 */
package com.flexganttfx.view.timeline;

