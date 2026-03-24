/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.showcase;

import atlantafx.base.theme.*;
import com.flexganttfx.core.FlexGanttFX;
import devtoolsfx.gui.GUI;
import fxsampler.Sample;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

/**
 * Root layout: top bar + sidebar + content area.
 */
public class ShowcaseView extends BorderPane {

    /** Sentinel theme that restores JavaFX's built-in Modena stylesheet. */
    private static final Theme MODENA = Theme.of("Modena", Application.STYLESHEET_MODENA, false);

    private static final List<Theme> THEMES = List.of(
        new PrimerDark(),
        new PrimerLight(),
        new NordDark(),
        new NordLight(),
        new CupertinoDark(),
        new CupertinoLight(),
        new Dracula(),
        MODENA
    );

    private static final Preferences PREFS = Preferences.userNodeForPackage(ShowcaseView.class);
    private static final String PREF_THEME = "theme";

    /** Applies the persisted theme (or PrimerDark) before the scene is created. */
    public static void applyPersistedTheme() {
        Application.setUserAgentStylesheet(resolvePersistedTheme().getUserAgentStylesheet());
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
        return THEMES.get(0); // default: PrimerDark
    }

    private final SampleContentView contentView;
    private final WelcomeView welcomeView;
    private final List<Label> allSampleRows = new ArrayList<>();

    // Currently selected label (for deselection)
    private Label selectedLabel = null;

    public ShowcaseView(Stage stage, HostServices hostServices) {
        contentView = new SampleContentView(stage);
        welcomeView = new WelcomeView(this::selectFirstSample);

        setTop(buildTopBar(stage, hostServices));
        setLeft(buildSidebar());
        setCenter(welcomeView);
    }

    // ── Top bar ──────────────────────────────────────────────────────────

    private HBox buildTopBar(Stage stage, HostServices hostServices) {
        HBox bar = new HBox();
        bar.getStyleClass().add("showcase-top-bar");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setSpacing(10);

        FontIcon logoIcon = new FontIcon(MaterialDesign.MDI_CHART_GANTT);
        logoIcon.setIconColor(Color.web("#4A90D9"));
        logoIcon.setIconSize(24);

        Label logoLabel = new Label("FlexGanttFX");
        logoLabel.getStyleClass().add("showcase-logo-label");

        Label badge = new Label("v" + FlexGanttFX.getVersion());
        badge.getStyleClass().add("showcase-version-badge");

        Label tagline = new Label("Feature Showcase");
        tagline.getStyleClass().add("showcase-tagline");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ── Theme switcher ────────────────────────────────────────────────
        Label themeLabel = new Label("Theme:");
        themeLabel.getStyleClass().add("showcase-tagline");

        ComboBox<Theme> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll(THEMES);
        themeCombo.setValue(resolvePersistedTheme());
        themeCombo.getStyleClass().add("showcase-theme-combo");
        themeCombo.setPrefWidth(160);
        themeCombo.setCellFactory(lv -> new ThemeCell());
        themeCombo.setButtonCell(new ThemeCell());
        themeCombo.valueProperty().addListener((obs, oldTheme, newTheme) -> {
            if (newTheme != null) {
                Application.setUserAgentStylesheet(newTheme.getUserAgentStylesheet());
                PREFS.put(PREF_THEME, newTheme.getName());
            }
        });

        Button scenicViewBtn = new Button("DevToolsFX");
        scenicViewBtn.getStyleClass().add("showcase-website-btn");
        scenicViewBtn.setOnAction(e -> GUI.openToolStage(stage, hostServices));

        Button websiteBtn = new Button("flexganttfx.com  ↗");
        websiteBtn.getStyleClass().add("showcase-website-btn");
        websiteBtn.setOnAction(e -> {
            if (hostServices != null) {
                hostServices.showDocument("https://www.flexganttfx.com");
            }
        });

        bar.getChildren().addAll(logoIcon, logoLabel, badge, tagline, spacer, themeLabel, themeCombo, scenicViewBtn, websiteBtn);
        return bar;
    }

    /** Simple ListCell that shows the theme name. */
    private static class ThemeCell extends ListCell<Theme> {
        @Override
        protected void updateItem(Theme theme, boolean empty) {
            super.updateItem(theme, empty);
            setText(empty || theme == null ? null : theme.getName());
        }
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
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-background: -color-bg-subtle;");
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
            try { tempInstance.dispose(); } catch (Exception ignored) {}

            Label row = new Label(sampleName);
            row.getStyleClass().add("sidebar-sample-row");
            row.setMaxWidth(Double.MAX_VALUE);
            row.setPrefWidth(250);
            row.setUserData(new SampleEntry(supplier, category, sampleName));
            allSampleRows.add(row);

            row.setOnMouseClicked(e -> handleSampleClick(row));
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

    private void filterSamples(String filter) {
        String lower = filter == null ? "" : filter.toLowerCase();
        for (Label row : allSampleRows) {
            SampleEntry entry = (SampleEntry) row.getUserData();
            boolean matches = lower.isBlank() || entry.name().toLowerCase().contains(lower);
            row.setVisible(matches);
            row.setManaged(matches);
        }
    }

    private void selectFirstSample() {
        if (!allSampleRows.isEmpty()) {
            handleSampleClick(allSampleRows.get(0));
        }
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

        Supplier<Sample> supplier() { return supplier; }
        SampleCategory category()   { return category; }
        String name()               { return name; }
    }
}
