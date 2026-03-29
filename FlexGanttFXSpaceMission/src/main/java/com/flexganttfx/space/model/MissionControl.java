/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

/**
 * Root grouping row for the ground station Gantt chart. Extends
 * {@link GroundStation} so that it fits the self-referential
 * {@code Row<GroundStation,GroundStation,Activity>} hierarchy and can serve
 * as the hidden tree root.
 */
public class MissionControl extends GroundStation {

    public MissionControl(String name) {
        super(name);
    }
}
