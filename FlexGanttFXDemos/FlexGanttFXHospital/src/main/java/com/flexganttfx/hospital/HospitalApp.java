/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.hospital;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.hospital.view.HospitalView;
import com.flexganttfx.view.util.ThemingUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;
import java.util.prefs.Preferences;

public class HospitalApp extends Application {

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

    private static final Preferences PREFS = Preferences.userNodeForPackage(HospitalApp.class);
    private static final String PREF_THEME = "theme";
    private static final String DEFAULT_STYLESHEET = "hospital.css";
    private static final String ATLANTAFX_STYLESHEET = "hospital-atlantafx.css";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
        }
    }

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(resolvePersistedTheme().getUserAgentStylesheet());

        HospitalView hospitalView = new HospitalView();
        VBox.setVgrow(hospitalView, Priority.ALWAYS);

        VBox root = new VBox();
        root.getStyleClass().add("hospital-app");
        Scene scene = new Scene(root, 1560, 960);
        root.getChildren().addAll(createMenuBar(scene, hospitalView), hospitalView);
        applyThemeStylesheet(scene);
        hospitalView.applyThemeStylesheet();
        stage.setScene(scene);
        stage.setTitle("FlexGanttFX - Operating Room Scheduler");
        stage.centerOnScreen();
        stage.show();
    }

    private MenuBar createMenuBar(Scene scene, HospitalView hospitalView) {
        MenuBar menuBar = new MenuBar();

        Menu themeMenu = new Menu("Theme");
        ToggleGroup themeGroup = new ToggleGroup();
        Theme activeTheme = resolvePersistedTheme();
        for (Theme theme : THEMES) {
            RadioMenuItem item = new RadioMenuItem(theme.getName());
            item.setToggleGroup(themeGroup);
            item.setSelected(theme.getName().equals(activeTheme.getName()));
            item.setOnAction(evt -> {
                Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
                applyThemeStylesheet(scene);
                hospitalView.applyThemeStylesheet();
                PREFS.put(PREF_THEME, theme.getName());
            });
            themeMenu.getItems().add(item);
        }
        menuBar.getMenus().add(themeMenu);
        return menuBar;
    }

    private void applyThemeStylesheet(Scene scene) {
        String stylesheet = ThemingUtil.isAtlantaFXActive(scene) ? ATLANTAFX_STYLESHEET : DEFAULT_STYLESHEET;
        scene.getStylesheets().setAll(Objects.requireNonNull(HospitalApp.class.getResource(stylesheet)).toExternalForm());
    }

    private Theme resolvePersistedTheme() {
        String saved = PREFS.get(PREF_THEME, null);
        if (saved != null) {
            for (Theme theme : THEMES) {
                if (theme.getName().equals(saved)) {
                    return theme;
                }
            }
        }
        return THEMES.get(0);
    }
}
