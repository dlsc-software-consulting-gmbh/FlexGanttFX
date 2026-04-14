/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.airport.view.AirportView;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Sampler wrapper for the standalone Frankfurt Airport Ground Operations demo.
 */
public class AirportSample extends FlexGanttFXSampleBase {

    @Override
    public String getSampleName() {
        return "Airport Ground Operations";
    }

    @Override
    public String getSampleDescription() {
        return "Frankfurt Airport ground operations scheduling: aircraft movements, gate assignments, "
            + "and turnaround ground ops shown in a DualGanttChartContainer. "
            + "Demonstrates custom flight and ground-op renderers, terminal grouping, "
            + "and linked timelines across two independent charts.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new AirportView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }
}
