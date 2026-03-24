/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.factory.model;

/**
 * Represents a production line in the factory. A production line acts as a
 * grouping row and extends {@link Machine} so it fits into the self-referential
 * {@code Row<Machine, Machine, Job>} hierarchy.
 */
public class ProductionLine extends Machine {

    public ProductionLine(String name) {
        super(name);
    }
}
