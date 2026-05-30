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
package com.flexganttfx.spacex;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Theme;
import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.spacex.view.SpaceXView;
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

public class SpaceXApp extends Application {

    private static final String TITLE = "SpaceX Launch History — FlexGanttFX Demo";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(SpaceXApp.class);
    private static final String THEME_KEY = "theme";
    private static final List<Theme> THEMES = List.of(new CupertinoLight(), new CupertinoDark());

    @Override
    public void init() {
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
        }
    }

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(resolveSavedTheme().getUserAgentStylesheet());

        SpaceXView view = new SpaceXView();
        VBox.setVgrow(view, Priority.ALWAYS);

        VBox root = new VBox(createMenuBar(), view);
        Scene scene = new Scene(root, 1440, 900);

        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private MenuBar createMenuBar() {
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(evt -> Platform.exit());

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(exitItem);

        ToggleGroup group = new ToggleGroup();
        Menu themeMenu = new Menu("Theme");
        Theme selectedTheme = resolveSavedTheme();

        for (Theme theme : THEMES) {
            RadioMenuItem item = new RadioMenuItem(theme.getName());
            item.setToggleGroup(group);
            item.setSelected(theme.getName().equals(selectedTheme.getName()));
            item.setOnAction(evt -> {
                Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
                PREFERENCES.put(THEME_KEY, theme.getName());
            });
            themeMenu.getItems().add(item);
        }

        return new MenuBar(fileMenu, themeMenu);
    }

    private Theme resolveSavedTheme() {
        String themeName = PREFERENCES.get(THEME_KEY, THEMES.get(0).getName());
        for (Theme theme : THEMES) {
            if (theme.getName().equals(themeName)) {
                return theme;
            }
        }
        return THEMES.get(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
