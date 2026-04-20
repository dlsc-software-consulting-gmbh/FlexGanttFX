/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.model;

import com.flexganttfx.model.activity.MutableCompletableActivityBase;

import java.time.Instant;

public class UserStory extends MutableCompletableActivityBase<StoryData> {

    public UserStory(String name, Instant start, Instant end, String assignee, int points, double pctComplete) {
        setName(name);
        setStartTime(start);
        setEndTime(end);
        setUserObject(new StoryData(assignee, points));
        setPercentageComplete(pctComplete);
    }
}
