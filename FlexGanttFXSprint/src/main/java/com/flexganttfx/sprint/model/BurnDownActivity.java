/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.model;

import com.flexganttfx.model.activity.ChartActivityBase;

import java.time.Instant;

public class BurnDownActivity extends ChartActivityBase<String> {

    public BurnDownActivity(String name, Instant start, Instant end, double remaining) {
        super(remaining, start, end);
        // ChartActivityBase extends ActivityBase (immutable name field set via parent constructor chain)
        setUserObject(name);
    }
}
