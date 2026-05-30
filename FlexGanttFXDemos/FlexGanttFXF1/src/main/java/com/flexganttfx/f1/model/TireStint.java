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
package com.flexganttfx.f1.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

public class TireStint extends MutableActivityBase<TireStint> {

    private final TireCompound compound;
    private final int stintNumber;
    private final int lapStart;
    private final int lapEnd;

    public TireStint(TireCompound compound, int stintNumber, int lapStart, int lapEnd, Instant start, Instant end) {
        this.compound = compound;
        this.stintNumber = stintNumber;
        this.lapStart = lapStart;
        this.lapEnd = lapEnd;

        setName(compound.getDisplayName() + " Stint " + stintNumber);
        setStartTime(start);
        setEndTime(end);
    }

    public TireCompound getCompound() {
        return compound;
    }

    public int getStintNumber() {
        return stintNumber;
    }

    public int getLapStart() {
        return lapStart;
    }

    public int getLapEnd() {
        return lapEnd;
    }
}
