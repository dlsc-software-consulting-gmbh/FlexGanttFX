/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

/** An orbital maneuver / engine burn. */
public class Maneuver extends MutableActivityBase<String> {

    public Maneuver(String name, Instant start, Instant end) {
        setName(name);
        setStartTime(start);
        setEndTime(end);
    }
}
