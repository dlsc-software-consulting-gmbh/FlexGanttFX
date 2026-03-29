/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.model;

public class StoryData {

    public final String assignee;
    public final int storyPoints;

    public StoryData(String assignee, int storyPoints) {
        this.assignee = assignee;
        this.storyPoints = storyPoints;
    }
}
