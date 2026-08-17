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
package com.flexganttfx.sprint;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
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

        stage.setTitle("Agile Sprint Planner — FlexGanttFX Demo");

        SprintView sprintView = new SprintView();
        VBox.setVgrow(sprintView, Priority.ALWAYS);

        MenuBar menuBar = createMenuBar(sprintView, stage);

        VBox root = new VBox(menuBar, sprintView);
        VBox.setVgrow(sprintView, Priority.ALWAYS);

        Scene scene = new Scene(root);
        scene.setUserAgentStylesheet(resolvePersistedTheme().getUserAgentStylesheet());

        stage.setScene(scene);
        stage.setWidth(1400);
        stage.setHeight(900);
        stage.centerOnScreen();
        stage.show();
    }

    private MenuBar createMenuBar(SprintView sprintView, Stage stage) {
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
                stage.getScene().setUserAgentStylesheet(t.getUserAgentStylesheet());
                PREFS.put(PREF_THEME, t.getName());
            });
            themeMenu.getItems().add(item);
        }
        menuBar.getMenus().add(themeMenu);

        return menuBar;
    }
}
