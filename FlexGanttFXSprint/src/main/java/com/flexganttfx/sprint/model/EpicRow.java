/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;

public class EpicRow extends Row<TeamRow, StoryRow, Activity> {

    public EpicRow(String name) {
        super(name);
    }
}
