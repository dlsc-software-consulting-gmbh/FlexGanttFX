/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.sprint.view.SprintView;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Sampler wrapper for the standalone Agile Sprint Planner demo.
 */
public class SprintSample extends FlexGanttFXSampleBase {

    @Override
    public String getSampleName() {
        return "Agile Sprint Planner";
    }

    @Override
    public String getSampleDescription() {
        return "Agile sprint planning board showing tasks, bugs, milestones, and tech-debt items "
            + "assigned to engineers across multiple sprints. "
            + "Demonstrates GanttChart with multiple activity types and layers, "
            + "ChartLayout for burn-down visualisation, activity links, and a LayersView panel.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new SprintView();
    }
}
