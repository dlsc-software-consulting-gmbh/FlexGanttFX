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
package com.flexganttfx.spacex.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class LaunchActivity extends MutableActivityBase<LaunchActivity> {

    private final int flightNumber;
    private final Boolean success;
    private final boolean upcoming;
    private final String rocketId;
    private final String launchpadId;

    public LaunchActivity(String missionName, Instant startTime, int flightNumber, Boolean success,
                          boolean upcoming, String rocketId, String launchpadId) {
        setName(missionName);
        setStartTime(startTime);
        setEndTime(startTime.plus(1, ChronoUnit.DAYS));
        this.flightNumber = flightNumber;
        this.success = success;
        this.upcoming = upcoming;
        this.rocketId = Objects.requireNonNullElse(rocketId, "");
        this.launchpadId = Objects.requireNonNullElse(launchpadId, "");
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public Boolean getSuccess() {
        return success;
    }

    public boolean isUpcoming() {
        return upcoming;
    }

    public String getRocketId() {
        return rocketId;
    }

    public String getLaunchpadId() {
        return launchpadId;
    }
}
