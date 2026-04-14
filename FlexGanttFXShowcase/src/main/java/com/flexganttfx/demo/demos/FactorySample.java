/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.factory.view.FactoryView;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Sampler wrapper for the standalone Factory scheduling demo.
 */
public class FactorySample extends FlexGanttFXSampleBase {

    @Override
    public String getSampleName() {
        return "Factory Scheduling";
    }

    @Override
    public String getSampleDescription() {
        return "A manufacturing floor demo showing production lines, machines, and jobs " +
               "with colour-coded status (Scheduled, In Progress, Done, Delayed). " +
               "Demonstrates GanttChart with a custom activity renderer.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new FactoryView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }
}
