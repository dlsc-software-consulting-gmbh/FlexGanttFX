/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

/** Zero-duration activity: start == end. */
public class MilestoneActivity extends MutableActivityBase<String> {

    public MilestoneActivity(String name, Instant when) {
        setName(name);
        setStartTime(when);
        setEndTime(when);
    }
}
