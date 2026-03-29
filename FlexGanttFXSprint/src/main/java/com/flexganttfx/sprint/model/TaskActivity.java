/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

public class TaskActivity extends MutableActivityBase<String> {

    public TaskActivity(String name, Instant start, Instant end, String assignee) {
        setName(name);
        setStartTime(start);
        setEndTime(end);
        setUserObject(assignee);
    }
}
