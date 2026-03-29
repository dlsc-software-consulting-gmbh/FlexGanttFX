/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.airport.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;

/**
 * Represents a terminal in the gate hierarchy. Using {@code Terminal} as both
 * parent and child type allows the chart hierarchy to nest terminal groups and
 * individual gates (represented by {@link Gate}, a subclass) under a common
 * root.
 */
public class Terminal extends Row<Terminal, Terminal, Activity> {

    public Terminal(String name) {
        super(name);
    }
}
