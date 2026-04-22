/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
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
