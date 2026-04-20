/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.airport.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

/**
 * Represents a single ground operation performed on an aircraft at a gate.
 * The {@link OpType} is stored as the user object and drives colour-coding
 * in {@link com.flexganttfx.airport.renderer.GroundOpRenderer}.
 */
public class GroundOp extends MutableActivityBase<OpType> {

    public GroundOp(String name, OpType type, Instant start, Instant end) {
        setName(name);
        setUserObject(type);
        setStartTime(start);
        setEndTime(end);
    }
}
