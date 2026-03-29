/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

/** An on-board maintenance task. */
public class MaintenanceOp extends MutableActivityBase<String> {

    public MaintenanceOp(String name, Instant start, Instant end) {
        setName(name);
        setStartTime(start);
        setEndTime(end);
    }
}
