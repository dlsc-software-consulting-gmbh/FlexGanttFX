/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.demo.showcase;

import com.flexganttfx.demo.DemoBase;
import com.jpro.webapi.WebAPI;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Shows a single demo: a header strip (name + description) at the top,
 * then stacks the optional control panel above the demo panel.
 */
public class DemoContentView extends BorderPane {

    private static final String ATLANTAFX_THEME_CLASS = "showcase-atlantafx-theme";
    private static final String ATLANTAFX_DARK_THEME_CLASS = "showcase-atlantafx-dark-theme";
    private static final String ATLANTAFX_LIGHT_THEME_CLASS = "showcase-atlantafx-light-theme";
    private static final String ROOT_LISTENER_KEY = "markdown-root-listener";
    private static final String ROOT_NODE_KEY = "markdown-root-node";

    private DemoBase currentDemo;
    private final Stage stage;

    public DemoContentView(Stage stage) {
        this.stage = stage;
        getStyleClass().add("demo-content-root");
    }

    /**
     * Shows the given demo.
     *
     * @param demo   the demo to show
     * @param category the category the demo belongs to, may be null (e.g. when a demo
     *                 gets launched standalone)
     */
    public void showDemo(DemoBase demo, DemoCategory category) {
        // Dispose previous demo
        if (currentDemo != null) {
            try {
                currentDemo.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        currentDemo = demo;

        String codeExample = loadCodeExample(demo);
        boolean hasCodeExample = codeExample != null;

        // Header
        VBox header = new VBox(4);
        header.getStyleClass().add("demo-header");

        Label nameLabel = new Label(demo.getName());
        nameLabel.getStyleClass().add("demo-header-title");

        VBox headerText = new VBox(4);
        headerText.getStyleClass().add("demo-header-text");

        if (category != null) {
            Label catLabel = new Label(category.getName().toUpperCase());
            catLabel.getStyleClass().add("demo-header-category");
            catLabel.setStyle("-fx-text-fill: " + category.getAccentColor() + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            headerText.getChildren().add(catLabel);
        }

        headerText.getChildren().add(nameLabel);

        HBox headerMain = new HBox(12);
        headerMain.getStyleClass().add("demo-header-main");
        headerMain.setAlignment(Pos.CENTER_LEFT);
        headerMain.getChildren().add(headerText);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        headerMain.getChildren().add(headerSpacer);

        HiddenSidesPane examplePane = createExamplePane(codeExample);
        if (hasCodeExample) {
            Button exampleCodeButton = new Button("Read More");
            exampleCodeButton.getStyleClass().add("demo-header-action");
            exampleCodeButton.setOnAction(evt -> toggleExamplePane(examplePane));
            headerMain.getChildren().add(exampleCodeButton);
        }

        header.getChildren().add(headerMain);

        String desc = demo.getDescription();
        if (desc != null && !desc.isBlank()) {
            Label descLabel = new Label(desc);
            descLabel.getStyleClass().add("demo-header-desc");
            descLabel.setWrapText(true);
            header.getChildren().add(descLabel);
        }

        setTop(header);

        // Main content
        Node demoPanel;
        try {
            demoPanel = demo.getPanel(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
            Label err = new Label("Error loading demo: " + ex.getMessage());
            err.setWrapText(true);
            err.setPadding(new Insets(20));
            setCenter(err);
            return;
        }

        Node controlPanel = demo.getControlPanel();
        Node mainContent;

        if (controlPanel == null) {
            // No control panel — demo fills the entire area
            if (demoPanel != null) {
                mainContent = demoPanel;
            } else {
                mainContent = null;
            }
        } else {
            VBox contentBox = new VBox();
            contentBox.getStyleClass().add("demo-content-box");

            // Control panel in a titled wrapper with scroll
            Label controlTitle = new Label("DEMO CONTROLS");
            controlTitle.getStyleClass().add("control-panel-title");

            VBox controlWrapper = new VBox(4);
            controlWrapper.getStyleClass().add("control-panel-wrapper");
            controlWrapper.getChildren().add(controlTitle);
            controlWrapper.getChildren().add(controlPanel);
            contentBox.getChildren().add(controlWrapper);

            if (demoPanel != null) {
                if (demoPanel instanceof Region) {
                    Region region = (Region) demoPanel;
                    region.setMaxHeight(Double.MAX_VALUE);
                }
                VBox.setVgrow(demoPanel, Priority.ALWAYS);
                contentBox.getChildren().add(demoPanel);
            }

            mainContent = contentBox;
        }

        examplePane.setContent(mainContent);
        setCenter(examplePane);
    }

    private String loadCodeExample(DemoBase demo) {
        String markdownFileName = demo.getClass().getSimpleName() + ".md";
        try (InputStream inputStream = demo.getClass().getResourceAsStream(markdownFileName)) {
            if (inputStream == null) {
                return null;
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load code example: " + markdownFileName, e);
        }
    }

    private HiddenSidesPane createExamplePane(String codeExample) {
        HiddenSidesPane examplePane = new HiddenSidesPane();
        examplePane.setTriggerDistance(0);
        examplePane.setAnimationDelay(Duration.ZERO);
        if (WebAPI.isBrowser()) {
            examplePane.setAnimationDuration(Duration.millis(1));
        } else {
            examplePane.setAnimationDuration(Duration.millis(200));
        }

        if (codeExample != null) {
            examplePane.setRight(createExampleTray(codeExample, examplePane));
        }

        return examplePane;
    }

    private Node createExampleTray(String codeExample, HiddenSidesPane examplePane) {
        BorderPane tray = new BorderPane();
        tray.getStyleClass().add("demo-example-tray");

        Label title = new Label("Example Code");
        title.getStyleClass().add("demo-example-title");

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("demo-example-close-button");
        closeButton.setOnAction(evt -> examplePane.hide());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox trayHeader = new HBox(10, title, spacer, closeButton);
        trayHeader.getStyleClass().add("demo-example-header");
        trayHeader.setAlignment(Pos.CENTER_LEFT);

        MarkdownView markdownView = new MarkdownView(codeExample) {
            @Override
            public Optional<String> getDefaultLanguage() {
                return Optional.of("java");
            }
        };
        markdownView.getStyleClass().add("demo-example-markdown");
        markdownView.setFillWidth(true);
        installThemeStyleSync(markdownView);

        ScrollPane scrollPane = new ScrollPane(markdownView);
        scrollPane.getStyleClass().add("demo-example-scroll-pane");
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
        if (currentDemo != null) {
            try {
                currentDemo.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            currentDemo = null;
        }
    }
}
