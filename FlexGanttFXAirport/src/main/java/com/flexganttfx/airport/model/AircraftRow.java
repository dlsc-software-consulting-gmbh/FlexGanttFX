/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.airport.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;

/**
 * Represents an individual aircraft in the FRA fleet. Acts as both the root
 * type and child type so that a single {@code GanttChart<AircraftRow>} can
 * hold a flat list under a virtual "FRA Fleet" root.
 */
public class AircraftRow extends Row<AircraftRow, AircraftRow, Activity> {

    public AircraftRow(String name) {
        super(name);
    }
}
