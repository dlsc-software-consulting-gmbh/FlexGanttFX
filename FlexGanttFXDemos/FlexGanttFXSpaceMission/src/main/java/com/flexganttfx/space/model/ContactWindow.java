/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

/** A communication pass between a spacecraft and a ground station. */
public class ContactWindow extends MutableActivityBase<String> {

    public ContactWindow(String name, Instant start, Instant end) {
        setName(name);
        setStartTime(start);
        setEndTime(end);
    }
}
