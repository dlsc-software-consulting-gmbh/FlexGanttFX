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
package com.flexganttfx.earthquake.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class EarthquakeActivity extends MutableActivityBase<EarthquakeActivity> {

    private final double magnitude;
    private final double depth;
    private final String place;
    private final String detailUrl;

    public EarthquakeActivity(String name, Instant startTime, double magnitude, double depth, String place, String detailUrl) {
        setName(name);
        setStartTime(startTime);
        setEndTime(startTime.plus(6, ChronoUnit.HOURS));
        this.magnitude = magnitude;
        this.depth = depth;
        this.place = Objects.requireNonNullElse(place, "");
        this.detailUrl = Objects.requireNonNullElse(detailUrl, "");
    }

    public double getMagnitude() {
        return magnitude;
    }

    public double getDepth() {
        return depth;
    }

    public String getPlace() {
        return place;
    }

    public String getDetailUrl() {
        return detailUrl;
    }
}
