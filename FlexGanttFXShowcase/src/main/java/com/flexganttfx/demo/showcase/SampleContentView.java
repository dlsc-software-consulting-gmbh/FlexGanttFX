/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.showcase;

import com.flexganttfx.demo.Sample;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a single sample: a header strip (name + description) at the top,
 * then stacks the optional control panel above the sample panel.
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
                setCenter(samplePanel);
            }
        } else {
            VBox contentBox = new VBox();
            contentBox.getStyleClass().add("sample-content-box");

            // Control panel in a titled wrapper with scroll
            Label controlTitle = new Label("SAMPLE CONTROLS");
            controlTitle.getStyleClass().add("control-panel-title");

            VBox controlWrapper = new VBox(4);
            controlWrapper.getStyleClass().add("control-panel-wrapper");
            controlWrapper.getChildren().add(controlTitle);
            controlWrapper.getChildren().add(createHorizontalControlPanel(controlPanel));
            contentBox.getChildren().add(controlWrapper);

            if (samplePanel != null) {
                if (samplePanel instanceof Region) {
                    Region region = (Region) samplePanel;
                    region.setMaxHeight(Double.MAX_VALUE);
                }
                VBox.setVgrow(samplePanel, Priority.ALWAYS);
                contentBox.getChildren().add(samplePanel);
            }

            setCenter(contentBox);
        }
    }

    private Node createHorizontalControlPanel(Node controlPanel) {
        if (!(controlPanel instanceof VBox)) {
            return controlPanel;
        }

        VBox verticalControlPanel = (VBox) controlPanel;

        HBox horizontalControlPanel = new HBox(verticalControlPanel.getSpacing());
        horizontalControlPanel.setAlignment(verticalControlPanel.getAlignment());
        horizontalControlPanel.setPadding(verticalControlPanel.getPadding());
        horizontalControlPanel.getStyleClass().addAll(verticalControlPanel.getStyleClass());
        horizontalControlPanel.setId(verticalControlPanel.getId());
        horizontalControlPanel.setStyle(verticalControlPanel.getStyle());
        horizontalControlPanel.setFillHeight(false);

        List<Node> children = new ArrayList<>(verticalControlPanel.getChildren());
        for (Node child : children) {
            Insets margin = VBox.getMargin(child);
            if (margin != null) {
                HBox.setMargin(child, margin);
            }
        }

        verticalControlPanel.getChildren().clear();
        horizontalControlPanel.getChildren().addAll(children);
        return horizontalControlPanel;
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
