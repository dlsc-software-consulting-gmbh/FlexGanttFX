/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.factory.model;

import com.flexganttfx.model.Row;

/**
 * Represents a single machine on a production line. Each machine can have
 * multiple {@link Job} activities scheduled on it.
 * <p>
 * The self-referential generics allow {@link ProductionLine} (a subtype of
 * Machine) to appear as children of a root Machine, enabling a two-level tree
 * inside {@code GanttChart<Machine>}.
 */
public class Machine extends Row<Machine, Machine, Job> {

    public Machine(String name) {
        super(name);
    }
}
