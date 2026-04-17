/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * A convenient base class for showcase samples. Extend this class and implement
 * {@link #getPanel(Stage)} at minimum. Optionally override {@link #getControlPanel()}
 * to provide an interactive settings panel.
 */
public abstract class SampleBase extends Application implements Sample {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(getSampleName());
        primaryStage.show();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Node getControlPanel() {
        return null;
    }

    @Override
    public double getControlPanelDividerPosition() {
        return 0.75;
    }

    @Override
    public String getSampleDescription() {
        return "";
    }

    @Override
    public String getProjectName() {
        return "FlexGanttFX";
    }

    @Override
    public String getProjectVersion() {
        return "";
    }

    @Override
    public String getJavaDocURL() {
        return "https://www.flexganttfx.com/api/index.html";
    }

    @Override
    public String getControlStylesheetURL() {
        return null;
    }

    @Override
    public String getSampleSourceURL() {
        return null;
    }

    @Override
    public String getCodeExample() {
        return null;
    }

    @Override
    public void dispose() {
    }

    public String code(String code) {
        return "```" + code + "```";
    }
}
