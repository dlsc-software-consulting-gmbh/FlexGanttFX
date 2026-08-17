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

import atlantafx.base.theme.*;
import com.dlsc.atlantafx.themes.ArmyDark;
import com.dlsc.atlantafx.themes.ArmyLight;
import com.dlsc.atlantafx.themes.Autumn;
import com.dlsc.atlantafx.themes.Blacky;
import com.dlsc.atlantafx.themes.BlueDark;
import com.dlsc.atlantafx.themes.BlueLight;
import com.dlsc.atlantafx.themes.Browny;
import com.dlsc.atlantafx.themes.FallDark;
import com.dlsc.atlantafx.themes.FallLight;
import com.dlsc.atlantafx.themes.GithubSoftDark;
import com.dlsc.atlantafx.themes.NavyDark;
import com.dlsc.atlantafx.themes.NavyLight;
import com.dlsc.atlantafx.themes.News;
import com.dlsc.atlantafx.themes.SpringDark;
import com.dlsc.atlantafx.themes.SpringLight;
import com.dlsc.atlantafx.themes.SummerDark;
import com.dlsc.atlantafx.themes.SummerLight;
import com.dlsc.atlantafx.themes.WinterDark;
import com.dlsc.atlantafx.themes.WinterLight;
import com.dlsc.atlantafx.themes.Yacht;
import com.jpro.webapi.WebAPI;
import devtoolsfx.gui.GUI;
import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.demo.Sample;
import devtoolsfx.gui.ToolPane;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.prefs.Preferences;

/**
 * Root layout: top bar + sidebar + content area.
 */
public class ShowcaseView extends BorderPane {

    private static final String ATLANTAFX_THEME_CLASS = "showcase-atlantafx-theme";
    private static final String ATLANTAFX_DARK_THEME_CLASS = "showcase-atlantafx-dark-theme";
    private static final String ATLANTAFX_LIGHT_THEME_CLASS = "showcase-atlantafx-light-theme";
    private static final String MODENA_THEME_CLASS = "showcase-modena-theme";

    /**
     * Sentinel theme that restores JavaFX's built-in Modena stylesheet.
     */
    private static final Theme MODENA = Theme.of("Modena", Application.STYLESHEET_MODENA, false);

    private static final List<Theme> THEMES = List.of(
            new PrimerDark(),
            new PrimerLight(),
            new NordDark(),
            new NordLight(),
            new CupertinoDark(),
            new CupertinoLight(),
            new Dracula(),
            new Browny(),
            new NavyLight(),
            new NavyDark(),
            new ArmyLight(),
            new ArmyDark(),
            new Autumn(),
            new Blacky(),
            new BlueDark(),
            new BlueLight(),
            new News(),
            new Yacht(),
            new GithubSoftDark(),
            new SpringLight(),
            new SpringDark(),
            new SummerLight(),
            new SummerDark(),
            new FallLight(),
            new FallDark(),
            new WinterLight(),
            new WinterDark(),
            MODENA
    );

    private static final Preferences PREFS = Preferences.userNodeForPackage(ShowcaseView.class);
    private static final String PREF_THEME = "theme";
    private static final String CONTROLSFX_ATLANTAFX_CSS = Objects.requireNonNull(ShowcaseView.class.getResource("controlsfx-atlantafx.css")).toExternalForm();

    /**
     * Applies the persisted theme (or PrimerDark) before the scene is created.
     */
    public static void applyPersistedTheme(Scene scene) {
        Theme theme = resolvePersistedTheme();
        String uas = theme.equals(MODENA) ? null : theme.getUserAgentStylesheet();
        scene.setUserAgentStylesheet(uas);
    }

    /**
     * Applies the persisted theme to a scene that is not managed by the showcase
     * application, e.g. when a single sample gets launched standalone. In addition to
     * the user agent stylesheet this also installs the theme style classes on the scene
     * root and the ControlsFX companion stylesheet.
     */
    public static void applyStandaloneTheme(Scene scene) {
        applyPersistedTheme(scene);

        Theme theme = resolvePersistedTheme();

        Parent root = scene.getRoot();
        root.getStyleClass().removeAll(ATLANTAFX_THEME_CLASS, ATLANTAFX_DARK_THEME_CLASS, ATLANTAFX_LIGHT_THEME_CLASS, MODENA_THEME_CLASS);

        if (isModenaTheme(theme)) {
            root.getStyleClass().add(MODENA_THEME_CLASS);
            scene.getStylesheets().remove(CONTROLSFX_ATLANTAFX_CSS);
        } else {
            root.getStyleClass().addAll(
                    ATLANTAFX_THEME_CLASS,
                    theme.isDarkMode() ? ATLANTAFX_DARK_THEME_CLASS : ATLANTAFX_LIGHT_THEME_CLASS
            );
            if (!scene.getStylesheets().contains(CONTROLSFX_ATLANTAFX_CSS)) {
                scene.getStylesheets().add(CONTROLSFX_ATLANTAFX_CSS);
            }
        }
    }

    private static Theme resolvePersistedTheme() {
        String saved = PREFS.get(PREF_THEME, null);
        if (saved != null) {
            for (Theme t : THEMES) {
                if (t.getName().equals(saved)) {
                    return t;
                }
            }
        }
        return THEMES.get(3); // default: Nord Light
    }

    private final SampleContentView contentView;
    private final WelcomeView welcomeView;
    private final List<Label> allSampleRows = new ArrayList<>();
    private final List<Label> atlantafxOnlyRows = new ArrayList<>();
    private final Stage stage;
    private final HostServices hostServices;
    private MenuButton themeMenu;
    private Theme currentTheme = resolvePersistedTheme();

    // Currently selected label (for deselection)
    private Label selectedLabel = null;

    @SuppressWarnings("deprecation")
    public ShowcaseView(Stage stage, HostServices hostServices) {
        this.stage = stage;
        this.hostServices = hostServices;
        if (!WebAPI.isBrowser()) {
            stage.initStyle(StageStyle.EXTENDED);
        }
        getStyleClass().add("showcase-root");
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            updateThemeStyleClass();
            updateControlsFXStylesheet(newScene);
        });

        contentView = new SampleContentView(stage);
        welcomeView = new WelcomeView(this::selectFirstSample);

        rebuildTopBar();
        setLeft(buildSidebar());
        setCenter(welcomeView);
        if (WebAPI.isBrowser()) {
            setBottom(buildBrowserFooter());
        }
    }

    // ── Top bar ──────────────────────────────────────────────────────────

    @SuppressWarnings("deprecation")
    private HeaderBar buildTopBar(Stage stage, HostServices hostServices) {
        FontIcon logoIcon = new FontIcon(MaterialDesign.MDI_CHART_GANTT);
        logoIcon.getStyleClass().add("showcase-logo-icon");

        Label logoLabel = new Label("FlexGanttFX");
        logoLabel.getStyleClass().add("showcase-logo-label");

        Label badge = new Label("v" + FlexGanttFX.getVersion());
        badge.getStyleClass().add("showcase-version-badge");

        HBox logoGroup = new HBox(8, logoIcon, logoLabel, badge);
        logoGroup.setAlignment(Pos.CENTER_LEFT);
        logoGroup.setCursor(Cursor.HAND);
        logoGroup.getStyleClass().add("showcase-logo-group");
        logoGroup.setOnMouseClicked(e -> showWelcome());

        // ── Theme switcher ────────────────────────────────────────────────
        themeMenu = new MenuButton();
        updateThemeMenuText();
        themeMenu.getStyleClass().add("showcase-website-btn");
        for (Theme theme : THEMES) {
            MenuItem item = new MenuItem(theme.getName());
            item.setOnAction(e -> applyTheme(theme));
            themeMenu.getItems().add(item);
        }

        Button devToolsButton = new Button("DevToolsFX");
        devToolsButton.getStyleClass().add("showcase-website-btn");
        devToolsButton.setOnAction(e -> {
            if (WebAPI.isBrowser()) {
                WebAPI webAPI = WebAPI.getWebAPI(this.getScene());
                Stage stage2 = new Stage();
                ToolPane toolPane = GUI.createToolPane(stage, hostServices);
                stage2.setScene(new Scene(toolPane));
                stage2.setOnShown((e2) -> toolPane.getConnector().start());
                stage2.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (event) -> toolPane.getConnector().stop());
                webAPI.openStageAsPopup(stage2);
            } else {
                GUI.openToolStage(stage, hostServices);
            }
        });

        Button websiteBtn = new Button("flexganttfx.com  ↗");
        websiteBtn.getStyleClass().add("showcase-website-btn");
        websiteBtn.setOnAction(e -> {
            if (hostServices != null) {
                hostServices.showDocument("https://www.flexganttfx.com");
            }
        });

        ImageView dlscLogo = new ImageView(new Image(Objects.requireNonNull(ShowcaseView.class.getResourceAsStream("/com/flexganttfx/demo/showcase/dlsc-logo-small.png"))));
        dlscLogo.setFitHeight(28);
        dlscLogo.setPreserveRatio(true);
        dlscLogo.setSmooth(true);
        dlscLogo.setCursor(Cursor.HAND);
        dlscLogo.setOnMouseClicked(e -> {
            if (hostServices != null) {
                hostServices.showDocument("https://dlsc.com");
            }
        });

        HBox rightItems = new HBox(10, dlscLogo, themeMenu);
        rightItems.setAlignment(Pos.CENTER_RIGHT);
        HBox.setMargin(dlscLogo, new Insets(0, 8, 0, 0));
        if (!WebAPI.isBrowser()) {
            rightItems.getChildren().add(devToolsButton);
        }
        rightItems.getChildren().add(websiteBtn);
        HBox.setMargin(websiteBtn, new Insets(0, 4, 0, 0));

        HeaderBar headerBar = new HeaderBar();
        headerBar.getStyleClass().add("showcase-top-bar");
        headerBar.setLeading(logoGroup);
        headerBar.setTrailing(rightItems);
        return headerBar;
    }

    private void rebuildTopBar() {
        setTop(buildTopBar(stage, hostServices));
    }

    private HBox buildBrowserFooter() {
        HBox footer = new HBox(4);
        footer.getStyleClass().add("showcase-footer");
        footer.setAlignment(Pos.CENTER);

        Label poweredByLabel = new Label("Powered by JPro");
        poweredByLabel.getStyleClass().add("showcase-footer-link");
        poweredByLabel.setCursor(Cursor.HAND);
        poweredByLabel.setOnMouseClicked(evt -> {
            if (hostServices != null) {
                hostServices.showDocument("https://www.jpro.one");
            }
        });

        footer.getChildren().add(poweredByLabel);
        return footer;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("showcase-sidebar");

        // Search field
        TextField searchField = new TextField();
        searchField.getStyleClass().add("sidebar-search-field");
        searchField.setPromptText("Filter samples…");
        searchField.setPrefWidth(230);
        VBox searchBox = new VBox(searchField);
        searchBox.setPadding(new Insets(10, 10, 6, 10));

        sidebar.getChildren().add(searchBox);

        // Category sections
        VBox categoriesBox = new VBox(0);
        for (SampleCategory category : SampleRegistry.CATEGORIES) {
            categoriesBox.getChildren().add(buildCategorySection(category));
        }

        ScrollPane scrollPane = new ScrollPane(categoriesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("showcase-sidebar-scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        sidebar.getChildren().add(scrollPane);

        // Wire up search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterSamples(newVal));

        return sidebar;
    }

    private VBox buildCategorySection(SampleCategory category) {
        VBox section = new VBox(0);

        // Category header
        HBox header = new HBox(8);
        header.getStyleClass().add("sidebar-category-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 12, 4, 12));

        FontIcon catIcon = new FontIcon(category.getIcon());
        catIcon.getStyleClass().add("sidebar-category-icon");
        catIcon.setIconColor(Color.web(category.getAccentColor()));
        catIcon.setIconSize(14);

        Label catLabel = new Label(category.getName().toUpperCase());
        catLabel.setStyle("-fx-text-fill: " + category.getAccentColor() + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        FontIcon expandIcon = new FontIcon(MaterialDesign.MDI_CHEVRON_DOWN);
        expandIcon.setIconColor(Color.web("#6B6B6B"));
        expandIcon.setIconSize(12);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        header.getChildren().addAll(catIcon, catLabel, headerSpacer, expandIcon);

        // Sample rows container
        VBox samplesBox = new VBox(0);
        samplesBox.setUserData(category);

        for (Supplier<Sample> supplier : category.getSampleSuppliers()) {
            Sample tempInstance;
            try {
                tempInstance = supplier.get();
            } catch (Exception ex) {
                continue;
            }
            String sampleName = tempInstance.getSampleName();
            try {
                tempInstance.dispose();
            } catch (Exception ignored) {
            }

            Label row = new Label(sampleName);
            row.getStyleClass().add("sidebar-sample-row");
            row.setMaxWidth(Double.MAX_VALUE);
            row.setPrefWidth(250);
            row.setFocusTraversable(true);
            row.setUserData(new SampleEntry(supplier, category, sampleName));
            allSampleRows.add(row);

            if (tempInstance.requiresAtlantaFX()) {
                atlantafxOnlyRows.add(row);
                boolean atlantafxActive = !isModenaTheme(currentTheme);
                row.setVisible(atlantafxActive);
                row.setManaged(atlantafxActive);
            }

            row.setOnMouseClicked(e -> {
                row.requestFocus();
                handleSampleClick(row);
            });
            row.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.UP) {
                    selectAdjacentSample(row, -1);
                    e.consume();
                } else if (e.getCode() == KeyCode.DOWN) {
                    selectAdjacentSample(row, 1);
                    e.consume();
                } else if (e.getCode() == KeyCode.HOME) {
                    selectBoundarySample(true);
                    e.consume();
                } else if (e.getCode() == KeyCode.END) {
                    selectBoundarySample(false);
                    e.consume();
                }
            });
            samplesBox.getChildren().add(row);
        }

        // Toggle collapse on header click
        header.setOnMouseClicked(e -> {
            boolean visible = samplesBox.isVisible();
            samplesBox.setVisible(!visible);
            samplesBox.setManaged(!visible);
            expandIcon.setIconCode(visible ? MaterialDesign.MDI_CHEVRON_RIGHT : MaterialDesign.MDI_CHEVRON_DOWN);
        });

        section.getChildren().addAll(header, samplesBox);
        return section;
    }

    private void handleSampleClick(Label row) {
        SampleEntry entry = (SampleEntry) row.getUserData();

        // Deselect previous
        if (selectedLabel != null) {
            selectedLabel.getStyleClass().remove("selected");
        }
        row.getStyleClass().add("selected");
        selectedLabel = row;

        // Create and show sample
        try {
            Sample sample = entry.supplier().get();
            contentView.showSample(sample, entry.category());
            setCenter(contentView);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void selectAdjacentSample(Label currentRow, int delta) {
        List<Label> visibleRows = getVisibleSampleRows();
        int currentIndex = visibleRows.indexOf(currentRow);
        if (currentIndex < 0) {
            return;
        }

        int targetIndex = currentIndex + delta;
        if (targetIndex < 0 || targetIndex >= visibleRows.size()) {
            return;
        }

        Label targetRow = visibleRows.get(targetIndex);
        targetRow.requestFocus();
        handleSampleClick(targetRow);
    }

    private void selectBoundarySample(boolean first) {
        List<Label> visibleRows = getVisibleSampleRows();
        if (visibleRows.isEmpty()) {
            return;
        }

        Label targetRow = first ? visibleRows.get(0) : visibleRows.get(visibleRows.size() - 1);
        targetRow.requestFocus();
        handleSampleClick(targetRow);
    }

    private List<Label> getVisibleSampleRows() {
        return allSampleRows.stream()
                .filter(Label::isVisible)
                .collect(Collectors.toList());
    }

    private void filterSamples(String filter) {
        String lower = filter == null ? "" : filter.toLowerCase();
        for (Label row : allSampleRows) {
            SampleEntry entry = (SampleEntry) row.getUserData();
            boolean matches = lower.isBlank() || entry.name().toLowerCase().contains(lower);
            row.setVisible(matches);
            row.setManaged(matches);
        }
    }

    private void showWelcome() {
        if (selectedLabel != null) {
            selectedLabel.getStyleClass().remove("selected");
            selectedLabel = null;
        }
        setCenter(welcomeView);
    }

    private void selectFirstSample() {
        selectBoundarySample(true);
    }

    private void applyTheme(Theme theme) {
        currentTheme = theme;
        String uas = theme.equals(MODENA) ? null : theme.getUserAgentStylesheet();
        getScene().setUserAgentStylesheet(uas);
        if (!WebAPI.isBrowser()) {
            PREFS.put(PREF_THEME, theme.getName());
        }
        rebuildTopBar();
        updateThemeStyleClass();
        updateControlsFXStylesheet(getScene());
        updateAtlantafxOnlyRowVisibility();

        if (selectedLabel != null && selectedLabel.isVisible()) {
            handleSampleClick(selectedLabel);
        } else if (selectedLabel != null && !selectedLabel.isVisible()) {
            selectedLabel.getStyleClass().remove("selected");
            selectedLabel = null;
            setCenter(welcomeView);
        }
    }

    private void updateAtlantafxOnlyRowVisibility() {
        boolean atlantafxActive = !isModenaTheme(currentTheme);
        for (Label row : atlantafxOnlyRows) {
            row.setVisible(atlantafxActive);
            row.setManaged(atlantafxActive);
        }
    }

    private void updateThemeMenuText() {
        if (themeMenu != null) {
            themeMenu.setText("Theme: " + currentTheme.getName());
        }
    }

    private void updateThemeStyleClass() {
        getStyleClass().removeAll(ATLANTAFX_THEME_CLASS, ATLANTAFX_DARK_THEME_CLASS, ATLANTAFX_LIGHT_THEME_CLASS, MODENA_THEME_CLASS);
        if (isModenaTheme(currentTheme)) {
            getStyleClass().add(MODENA_THEME_CLASS);
        } else {
            getStyleClass().addAll(
                    ATLANTAFX_THEME_CLASS,
                    currentTheme.isDarkMode() ? ATLANTAFX_DARK_THEME_CLASS : ATLANTAFX_LIGHT_THEME_CLASS
            );
        }
    }

    private void updateControlsFXStylesheet(Scene scene) {
        if (scene == null) {
            return;
        }
        ObservableList<String> sheets = scene.getStylesheets();
        if (!isModenaTheme(currentTheme)) {
            if (!sheets.contains(CONTROLSFX_ATLANTAFX_CSS)) {
                sheets.add(CONTROLSFX_ATLANTAFX_CSS);
            }
        } else {
            sheets.remove(CONTROLSFX_ATLANTAFX_CSS);
        }
    }

    private static boolean isModenaTheme(Theme theme) {
        return MODENA.getName().equals(theme.getName());
    }

    // ── Inner types ───────────────────────────────────────────────────────

    private static final class SampleEntry {
        private final Supplier<Sample> supplier;
        private final SampleCategory category;
        private final String name;

        SampleEntry(Supplier<Sample> supplier, SampleCategory category, String name) {
            this.supplier = supplier;
            this.category = category;
            this.name = name;
        }

        Supplier<Sample> supplier() {
            return supplier;
        }

        SampleCategory category() {
            return category;
        }

        String name() {
            return name;
        }
    }
}
