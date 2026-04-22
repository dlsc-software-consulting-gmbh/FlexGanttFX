/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.hospital.model;

public enum HospitalActivityRole {
    SURGERY("Surgery"),
    SURGEON("Surgeon"),
    ANESTHESIA("Anesthesia"),
    EQUIPMENT("Equipment");

    private final String shortName;

    HospitalActivityRole(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean isRoomRole() {
        return this == SURGERY;
    }

    public boolean isResourceRole() {
        return !isRoomRole();
    }
}
