/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.hospital.view.HospitalView;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Sampler wrapper for the standalone hospital operating room scheduling demo.
 */
public class HospitalSample extends FlexGanttFXSampleBase {

    @Override
    public String getSampleName() {
        return "Hospital OR Scheduler";
    }

    @Override
    public String getSampleDescription() {
        return "An operating room scheduling demo with linked room and resource charts, "
                + "a CalendarFX detailed day view, conflict resolution, and drag-and-resize editing "
                + "across a month-long surgery schedule.";
    }

    @Override
    public Node getPanel(Stage stage) {
        return new HospitalView();
    }

    @Override
    public Node getControlPanel() {
        return null;
    }
}
