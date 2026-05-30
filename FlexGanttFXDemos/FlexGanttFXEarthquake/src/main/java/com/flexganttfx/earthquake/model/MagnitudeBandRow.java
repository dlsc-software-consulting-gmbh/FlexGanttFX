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
package com.flexganttfx.earthquake.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;

public class MagnitudeBandRow extends Row<MagnitudeBandRow, MagnitudeBandRow, Activity> {

    private final double minMag;
    private final double maxMag;

    public MagnitudeBandRow(String name, double minMag) {
        super(name);
        this.minMag = minMag;
        this.maxMag = determineMaxMag(minMag);
        setExpanded(true);
    }

    private static double determineMaxMag(double minMag) {
        if (Double.compare(minMag, 5.0) == 0) {
            return 5.9;
        }
        if (Double.compare(minMag, 6.0) == 0) {
            return 6.9;
        }
        if (Double.compare(minMag, 7.0) == 0) {
            return 7.9;
        }
        return -1;
    }

    public double getMinMag() {
        return minMag;
    }

    public double getMaxMag() {
        return maxMag;
    }

    public static MagnitudeBandRow band5() {
        return new MagnitudeBandRow("M 5.0 – 5.9", 5.0);
    }

    public static MagnitudeBandRow band6() {
        return new MagnitudeBandRow("M 6.0 – 6.9", 6.0);
    }

    public static MagnitudeBandRow band7() {
        return new MagnitudeBandRow("M 7.0 – 7.9", 7.0);
    }

    public static MagnitudeBandRow band8plus() {
        return new MagnitudeBandRow("M 8.0 +", 8.0);
    }
}
