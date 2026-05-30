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

import java.time.Instant;

public class RaceSession {

    private final int sessionKey;
    private final String sessionName;
    private final String location;
    private final String countryName;
    private final int year;
    private final Instant dateStart;
    private final Instant dateEnd;

    public RaceSession(int sessionKey, String sessionName, String location, String countryName, int year, Instant dateStart, Instant dateEnd) {
        this.sessionKey = sessionKey;
        this.sessionName = sessionName;
        this.location = location;
        this.countryName = countryName;
        this.year = year;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public int getSessionKey() {
        return sessionKey;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getLocation() {
        return location;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getYear() {
        return year;
    }

    public Instant getDateStart() {
        return dateStart;
    }

    public Instant getDateEnd() {
        return dateEnd;
    }

    @Override
    public String toString() {
        return year + " — " + location + " (" + countryName + ")";
    }
}
