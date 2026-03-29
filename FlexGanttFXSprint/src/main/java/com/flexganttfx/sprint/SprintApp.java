/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.sprint.view.SprintView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.prefs.Preferences;

public class SprintApp extends Application {

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

    private static final Preferences PREFS = Preferences.userNodeForPackage(SprintApp.class);
    private static final String PREF_THEME = "theme";

    public static void main(String[] args) {
        launch(args);
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
        return THEMES.get(0);
    }

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(resolvePersistedTheme().getUserAgentStylesheet());

        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
        }

        stage.setTitle("Agile Sprint Planner — FlexGanttFX Demo");

        SprintView sprintView = new SprintView();
        VBox.setVgrow(sprintView, Priority.ALWAYS);

        MenuBar menuBar = createMenuBar(sprintView);

        VBox root = new VBox(menuBar, sprintView);
        VBox.setVgrow(sprintView, Priority.ALWAYS);

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setWidth(1400);
        stage.setHeight(900);
        stage.centerOnScreen();
        stage.show();
    }

    private MenuBar createMenuBar(SprintView sprintView) {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(evt -> Platform.exit());
        fileMenu.getItems().add(exitItem);
        menuBar.getMenus().add(fileMenu);

        Menu themeMenu = new Menu("Theme");
        ToggleGroup themeGroup = new ToggleGroup();
        Theme activeTheme = resolvePersistedTheme();
        for (Theme t : THEMES) {
            RadioMenuItem item = new RadioMenuItem(t.getName());
            item.setToggleGroup(themeGroup);
            item.setSelected(t.getName().equals(activeTheme.getName()));
            item.setOnAction(evt -> {
                Application.setUserAgentStylesheet(t.getUserAgentStylesheet());
                PREFS.put(PREF_THEME, t.getName());
            });
            themeMenu.getItems().add(item);
        }
        menuBar.getMenus().add(themeMenu);

        return menuBar;
    }
}
