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
package com.flexganttfx.earthquake;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Theme;
import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.earthquake.view.EarthquakeView;
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

public class EarthquakeApp extends Application {

    private static final String TITLE = "USGS Earthquake Monitor — FlexGanttFX Demo";
    private static final List<Theme> THEMES = List.of(new CupertinoLight(), new CupertinoDark());
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(EarthquakeApp.class);
    private static final String THEME_KEY = "theme";

    @Override
    public void init() {
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("6Xtb60ORFtkUaEHIR2lo7uCnlqMq+X2WXZWYlZ6NlOlvLpF9nIW+WSPEnF6vCEfoNmr7i+ObhCYT0XolNhzY0v2VAUlnz1Z9GT6bOOOl1DMgR2Y4EWXJhyKC5zGuwjYVn71wLCNTyBMxk/4OTwfQee7hEUwpnKExYcbfNRLKxjYgY2k4SeOSkqP8wlHf5Jx2f+3Mk1ClnG6xnYieO6pkXoXcApj3B5yzlpcIk+/rr93dM5LBnEhmG+GxK2EPlb7kIod8eZt+cyR8fs2Tyrr5b7N4ZZ4DJVk6j73jlQ1GA4h6lZYYoQovgloIEEiy8StqKj6izVF2naQD8nQsnFvuDwN4Iw==");
        }
    }

    @Override
    public void start(Stage stage) {
        EarthquakeView view = new EarthquakeView();
        VBox.setVgrow(view, Priority.ALWAYS);

        VBox root = new VBox(createMenuBar(stage), view);
        Scene scene = new Scene(root, 1440, 900);
        scene.setUserAgentStylesheet(resolvePersistedTheme().getUserAgentStylesheet());

        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(evt -> Platform.exit());
        fileMenu.getItems().add(exitItem);

        Menu themeMenu = new Menu("Theme");
        ToggleGroup themeGroup = new ToggleGroup();
        Theme activeTheme = resolvePersistedTheme();
        for (Theme theme : THEMES) {
            RadioMenuItem item = new RadioMenuItem(theme.getName());
            item.setToggleGroup(themeGroup);
            item.setSelected(theme.getName().equals(activeTheme.getName()));
            item.setOnAction(evt -> {
                stage.getScene().setUserAgentStylesheet(theme.getUserAgentStylesheet());
                PREFERENCES.put(THEME_KEY, theme.getName());
            });
            themeMenu.getItems().add(item);
        }

        menuBar.getMenus().addAll(fileMenu, themeMenu);
        return menuBar;
    }

    private Theme resolvePersistedTheme() {
        String savedTheme = PREFERENCES.get(THEME_KEY, null);
        if (savedTheme != null) {
            for (Theme theme : THEMES) {
                if (theme.getName().equals(savedTheme)) {
                    return theme;
                }
            }
        }

        return THEMES.get(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
