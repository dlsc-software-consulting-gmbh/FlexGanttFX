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
package com.flexganttfx.model.dateline;

import com.flexganttfx.model.util.SimpleUnit;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * A virtual grid implementation for {@link SimpleUnit}.
 *
 * @since 1.1
 */
public final class SimpleUnitGrid extends VirtualGrid<SimpleUnit> {

    /**
     * Constructs a new grid.
     *
     * @param name      the grid name as shown in the UI (e.g. "50 Units")
     * @param shortName the short name (e.g. "50")
     * @param unit      the chrono unit on which the grid will be based (e.g. "TEN")
     * @param amount    the amount of the chrono unit (e.g. "5")
     * @since 1.0
     */
    public SimpleUnitGrid(String name, String shortName, SimpleUnit unit, int amount) {
        super(name, shortName, unit, amount);
    }

    /**
     * Constructs a new grid.
     *
     * @param name      the grid name as shown in the UI (e.g. "50")
     * @param unit      the chrono unit on which the grid will be based (e.g. "TEN")
     * @param amount    the amount of the chrono unit (e.g. "5")
     * @since 1.0
     */
    public SimpleUnitGrid(String name, SimpleUnit unit, int amount) {
        super(name, unit, amount);
    }

    @Override
    public Instant adjustTime(Instant instant, ZoneId zoneId, boolean roundUp, DayOfWeek firstDayOfWeek) {

        long millis = instant.toEpochMilli();

        millis -= millis % getUnit().getMillis();

        if (roundUp) {
            millis += getUnit().getMillis();
        }

        return Instant.ofEpochMilli(millis);
    }

    /**
     * {@inheritDoc}
     * <p>
     * A grid based on {@link SimpleUnit} operates on absolute millisecond values and
     * therefore has no notion of a local time. This operation is not supported.
     *
     * @throws UnsupportedOperationException always, as local times are not supported
     *             by this type of grid
     */
    @Override
    public LocalTime adjustTime(LocalTime time, boolean roundUp) {
        throw new UnsupportedOperationException("local time not supported by grid");
    }
}
