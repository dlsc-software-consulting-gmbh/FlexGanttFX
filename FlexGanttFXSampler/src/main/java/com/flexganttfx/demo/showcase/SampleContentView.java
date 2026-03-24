/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.showcase;

import fxsampler.Sample;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Shows a single sample: a header strip (name + description) at the top,
 * then a SplitPane with the sample panel on the left and the control panel
 * (if any) on the right.
 */
public class SampleContentView extends BorderPane {

    private Sample currentSample;
    private final Stage stage;

    public SampleContentView(Stage stage) {
        this.stage = stage;
        getStyleClass().add("sample-content-root");
    }

    public void showSample(Sample sample, SampleCategory category) {
        // Dispose previous sample
        if (currentSample != null) {
            try {
                currentSample.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        currentSample = sample;

        // Header
        VBox header = new VBox(4);
        header.getStyleClass().add("sample-header");

        Label catLabel = new Label(category.getName().toUpperCase());
        catLabel.getStyleClass().add("sample-header-category");
        catLabel.setStyle("-fx-text-fill: " + category.getAccentColor() + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label nameLabel = new Label(sample.getSampleName());
        nameLabel.getStyleClass().add("sample-header-title");

        header.getChildren().addAll(catLabel, nameLabel);

        String desc = sample.getSampleDescription();
        if (desc != null && !desc.isBlank()) {
            Label descLabel = new Label(desc);
            descLabel.getStyleClass().add("sample-header-desc");
            descLabel.setWrapText(true);
            header.getChildren().add(descLabel);
        }

        setTop(header);

        // Main content
        Node samplePanel;
        try {
            samplePanel = sample.getPanel(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
            Label err = new Label("Error loading sample: " + ex.getMessage());
            err.setWrapText(true);
            err.setPadding(new Insets(20));
            setCenter(err);
            return;
        }

        Node controlPanel = sample.getControlPanel();

        if (controlPanel == null) {
            // No control panel — sample fills the entire area
            if (samplePanel != null) {
                BorderPane.setMargin(samplePanel, new Insets(8));
                setCenter(samplePanel);
            }
        } else {
            SplitPane splitPane = new SplitPane();
            splitPane.setOrientation(Orientation.HORIZONTAL);
            splitPane.getStyleClass().add("sample-split-pane");

            if (samplePanel != null) {
                splitPane.getItems().add(samplePanel);
            }

            // Control panel in a titled wrapper with scroll
            VBox controlWrapper = new VBox(4);
            controlWrapper.getStyleClass().add("control-panel-wrapper");
            Label controlTitle = new Label("CONTROLS");
            controlTitle.getStyleClass().add("control-panel-title");
            controlWrapper.getChildren().add(controlTitle);

            ScrollPane controlScroll = new ScrollPane(controlPanel);
            controlScroll.setFitToWidth(true);
            controlScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            controlScroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
            VBox.setVgrow(controlScroll, Priority.ALWAYS);
            controlWrapper.getChildren().add(controlScroll);

            splitPane.getItems().add(controlWrapper);
            splitPane.setDividerPositions(sample.getControlPanelDividerPosition());

            setCenter(splitPane);
        }
    }

    public void dispose() {
        if (currentSample != null) {
            try {
                currentSample.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            currentSample = null;
        }
    }
}
