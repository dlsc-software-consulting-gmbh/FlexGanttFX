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
package com.flexganttfx.hospital.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Duration;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

public class HospitalActivity extends MutableActivityBase<HospitalCase> {

    private final HospitalActivityRole role;
    private Duration preparationDuration;
    private Duration cleanupDuration;

    public HospitalActivity(HospitalCase hospitalCase, HospitalActivityRole role, Instant startTime, Instant endTime,
                            Duration preparationDuration, Duration cleanupDuration) {
        super(role.getShortName(), startTime, endTime);
        this.role = role;
        this.id = hospitalCase.getId() + "-" + role.name().toLowerCase();
        setPreparationDuration(preparationDuration);
        setCleanupDuration(cleanupDuration);
        setUserObject(hospitalCase);
    }

    public HospitalActivityRole getRole() {
        return role;
    }

    public Duration getPreparationDuration() {
        return preparationDuration;
    }

    public void setPreparationDuration(Duration preparationDuration) {
        this.preparationDuration = requireNonNull(preparationDuration);
    }

    public Duration getCleanupDuration() {
        return cleanupDuration;
    }

    public void setCleanupDuration(Duration cleanupDuration) {
        this.cleanupDuration = requireNonNull(cleanupDuration);
    }

    public Instant getProcedureStartTime() {
        return getStartTime().plus(preparationDuration);
    }

    public Instant getProcedureEndTime() {
        return getEndTime().minus(cleanupDuration);
    }

    public Duration getProcedureDuration() {
        return Duration.between(getProcedureStartTime(), getProcedureEndTime());
    }

    public boolean hasPhases() {
        return role.isRoomRole();
    }
}
