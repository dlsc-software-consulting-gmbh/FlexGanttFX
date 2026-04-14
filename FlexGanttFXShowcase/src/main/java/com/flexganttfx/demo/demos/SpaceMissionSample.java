/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.space.view.SpaceMissionView;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Sampler wrapper for the standalone Space Mission Control Center demo.
 */
public class SpaceMissionSample extends FlexGanttFXSampleBase {

    @Override
    public String getSampleName() {
        return "Space Mission Control";
    }

    @Override
    public String getSampleDescription() {
        return "A dual-chart mission control demo showing spacecraft manoeuvres, contact windows, "
            + "and maintenance operations in one chart alongside ground-station scheduling in a second. "
            + "Demonstrates DualGanttChartContainer, multiple layers, ChartLayout for burn-delta "
            + "visualisation, and a RadarView / LayersView side panel.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new SpaceMissionView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }
}
