/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

/**
 * Root grouping row for the spacecraft Gantt chart. Extends {@link Spacecraft}
 * so that it fits the self-referential {@code Row<Spacecraft,Spacecraft,Activity>}
 * hierarchy and can serve as the hidden tree root.
 */
public class SpaceFleet extends Spacecraft {

    public SpaceFleet(String name) {
        super(name);
    }
}
