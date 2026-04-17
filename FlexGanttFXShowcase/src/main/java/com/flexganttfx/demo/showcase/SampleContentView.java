/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.showcase;

import com.flexganttfx.demo.Sample;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import one.jpro.platform.mdfx.MarkdownView;
import org.controlsfx.control.HiddenSidesPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shows a single sample: a header strip (name + description) at the top,
 * then stacks the optional control panel above the sample panel.
 */
public class SampleContentView extends BorderPane {

    private static final String ATLANTAFX_THEME_CLASS = "showcase-atlantafx-theme";
    private static final String ATLANTAFX_DARK_THEME_CLASS = "showcase-atlantafx-dark-theme";
    private static final String ATLANTAFX_LIGHT_THEME_CLASS = "showcase-atlantafx-light-theme";
    private static final String ROOT_LISTENER_KEY = "markdown-root-listener";
    private static final String ROOT_NODE_KEY = "markdown-root-node";

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

        String codeExample = sample.getCodeExample();
        boolean hasCodeExample = codeExample != null;

        // Header
        VBox header = new VBox(4);
        header.getStyleClass().add("sample-header");

        Label catLabel = new Label(category.getName().toUpperCase());
        catLabel.getStyleClass().add("sample-header-category");
        catLabel.setStyle("-fx-text-fill: " + category.getAccentColor() + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label nameLabel = new Label(sample.getSampleName());
        nameLabel.getStyleClass().add("sample-header-title");

        VBox headerText = new VBox(4, catLabel, nameLabel);
        headerText.getStyleClass().add("sample-header-text");

        HBox headerMain = new HBox(12);
        headerMain.getStyleClass().add("sample-header-main");
        headerMain.setAlignment(Pos.CENTER_LEFT);
        headerMain.getChildren().add(headerText);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        headerMain.getChildren().add(headerSpacer);

        HiddenSidesPane examplePane = createExamplePane(codeExample);
        if (hasCodeExample) {
            Button exampleCodeButton = new Button("Example Code");
            exampleCodeButton.getStyleClass().add("sample-header-action");
            exampleCodeButton.setOnAction(evt -> toggleExamplePane(examplePane));
            headerMain.getChildren().add(exampleCodeButton);
        }

        header.getChildren().add(headerMain);

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
        Node mainContent;

        if (controlPanel == null) {
            // No control panel — sample fills the entire area
            if (samplePanel != null) {
                mainContent = samplePanel;
            } else {
                mainContent = null;
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
            controlWrapper.getChildren().add(controlPanel);
            contentBox.getChildren().add(controlWrapper);

            if (samplePanel != null) {
                if (samplePanel instanceof Region) {
                    Region region = (Region) samplePanel;
                    region.setMaxHeight(Double.MAX_VALUE);
                }
                VBox.setVgrow(samplePanel, Priority.ALWAYS);
                contentBox.getChildren().add(samplePanel);
            }

            mainContent = contentBox;
        }

        examplePane.setContent(mainContent);
        setCenter(examplePane);
    }

    private HiddenSidesPane createExamplePane(String codeExample) {
        HiddenSidesPane examplePane = new HiddenSidesPane();
        examplePane.setTriggerDistance(0);
        examplePane.setAnimationDelay(Duration.ZERO);
        examplePane.setAnimationDuration(Duration.millis(200));

        if (codeExample != null) {
            examplePane.setRight(createExampleTray(codeExample, examplePane));
        }

        return examplePane;
    }

    private Node createExampleTray(String codeExample, HiddenSidesPane examplePane) {
        BorderPane tray = new BorderPane();
        tray.getStyleClass().add("sample-example-tray");

        Label title = new Label("Example Code");
        title.getStyleClass().add("sample-example-title");

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("sample-example-close-button");
        closeButton.setOnAction(evt -> examplePane.hide());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox trayHeader = new HBox(10, title, spacer, closeButton);
        trayHeader.getStyleClass().add("sample-example-header");
        trayHeader.setAlignment(Pos.CENTER_LEFT);

        MarkdownView markdownView = new MarkdownView(codeExample) {
            @Override
            public Optional<String> getDefaultLanguage() {
                return Optional.of("java");
            }
        };
        markdownView.getStyleClass().add("sample-example-markdown");
        markdownView.setFillWidth(true);
        installThemeStyleSync(markdownView);

        ScrollPane scrollPane = new ScrollPane(markdownView);
        scrollPane.getStyleClass().add("sample-example-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        tray.setTop(trayHeader);
        tray.setCenter(scrollPane);
        return tray;
    }

    private void toggleExamplePane(HiddenSidesPane examplePane) {
        if (examplePane.getPinnedSide() == Side.RIGHT) {
            examplePane.hide();
        } else {
            examplePane.show(Side.RIGHT);
        }
    }

    private void installThemeStyleSync(MarkdownView markdownView) {
        markdownView.sceneProperty().addListener((obs, oldScene, newScene) -> bindMarkdownThemeSync(markdownView));
        markdownView.parentProperty().addListener((obs, oldParent, newParent) -> bindMarkdownThemeSync(markdownView));
        bindMarkdownThemeSync(markdownView);
    }

    @SuppressWarnings("unchecked")
    private void bindMarkdownThemeSync(MarkdownView markdownView) {
        Parent oldRoot = (Parent) markdownView.getProperties().remove(ROOT_NODE_KEY);
        ListChangeListener<String> oldListener = (ListChangeListener<String>) markdownView.getProperties().remove(ROOT_LISTENER_KEY);
        if (oldRoot != null && oldListener != null) {
            oldRoot.getStyleClass().removeListener(oldListener);
        }

        Parent root = markdownView.getScene() == null ? null : markdownView.getScene().getRoot();
        if (root != null) {
            ListChangeListener<String> listener = change -> syncMarkdownThemeClasses(markdownView);
            root.getStyleClass().addListener(listener);
            markdownView.getProperties().put(ROOT_NODE_KEY, root);
            markdownView.getProperties().put(ROOT_LISTENER_KEY, listener);
        }

        syncMarkdownThemeClasses(markdownView);
    }

    private void syncMarkdownThemeClasses(MarkdownView markdownView) {
        markdownView.getStyleClass().removeAll("atlantafx", "dark", "light");

        Parent root = markdownView.getScene() == null ? null : markdownView.getScene().getRoot();
        if (root == null) {
            return;
        }

        List<String> rootStyleClasses = root.getStyleClass();
        if (!rootStyleClasses.contains(ATLANTAFX_THEME_CLASS)) {
            return;
        }

        markdownView.getStyleClass().add("atlantafx");
        if (rootStyleClasses.contains(ATLANTAFX_DARK_THEME_CLASS)) {
            markdownView.getStyleClass().add("dark");
        } else if (rootStyleClasses.contains(ATLANTAFX_LIGHT_THEME_CLASS)) {
            markdownView.getStyleClass().add("light");
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
