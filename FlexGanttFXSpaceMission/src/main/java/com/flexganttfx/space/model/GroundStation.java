/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;

/**
 * Represents a single ground station. Self-referential generics allow
 * {@link MissionControl} (a subtype) to act as the invisible tree root inside
 * {@code GanttChart<GroundStation>}.
 */
public class GroundStation extends Row<GroundStation, GroundStation, Activity> {

    public GroundStation(String name) {
        super(name);
    }
}
