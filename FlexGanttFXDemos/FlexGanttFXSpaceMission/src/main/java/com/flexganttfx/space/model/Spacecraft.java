/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;

/**
 * Represents a single spacecraft. Self-referential generics allow
 * {@link SpaceFleet} (a subtype) to act as the invisible tree root inside
 * {@code GanttChart<Spacecraft>}.
 */
public class Spacecraft extends Row<Spacecraft, Spacecraft, Activity> {

    public Spacecraft(String name) {
        super(name);
    }
}
