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
package com.flexganttfx.model.timeline;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * A specialized timeline model for the {@link ChronoUnit} temporal unit. This
 * model is the default model used by the Gantt chart.
 *
 * @since 1.0
 */
public class ChronoUnitTimelineModel extends TimelineModel<ChronoUnit> {

    /**
     * Constructs a new model with the lowest unit set to minutes and the
     * highest unit set to years. The start time gets set to
     * {@link Instant#now()}.
     *
     * @since 1.0
     */
    public ChronoUnitTimelineModel() {
        setStartTime(Instant.now());
        setZoomRange(ChronoUnit.MINUTES, 5, 100, ChronoUnit.MONTHS, 1, 50);
    }
}