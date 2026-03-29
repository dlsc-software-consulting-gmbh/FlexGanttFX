/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

import com.flexganttfx.model.activity.MutableCompletableActivityBase;

import java.time.Instant;

/** A scientific observation activity with a percentage-complete indicator. */
public class ScienceOp extends MutableCompletableActivityBase<String> {

    public ScienceOp(String name, Instant start, Instant end, double percentComplete) {
        setName(name);
        setStartTime(start);
        setEndTime(end);
        setPercentageComplete(percentComplete);
    }
}
