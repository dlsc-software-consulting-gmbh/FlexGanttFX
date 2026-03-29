/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.airport.model;

/**
 * Represents a single departure/arrival gate. Extends {@link Terminal} so it
 * can be added as a child of a {@link Terminal} row in the gate chart hierarchy.
 */
public class Gate extends Terminal {

    public Gate(String name) {
        super(name);
    }
}
