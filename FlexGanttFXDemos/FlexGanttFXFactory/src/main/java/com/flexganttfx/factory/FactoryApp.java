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
package com.flexganttfx.factory;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.factory.view.FactoryView;
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
import java.util.prefs.Preferences;

/**
 * Entry point for the FlexGanttFX Factory scheduling demo.
 */
public class FactoryApp extends Application {

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

    private static final Preferences PREFS = Preferences.userNodeForPackage(FactoryApp.class);
    private static final String PREF_THEME = "theme";

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

    @Override
    public void init() throws Exception {
        super.init();
        if (!FlexGanttFX.isLicenseKeySet()) {
            FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
        }
    }

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(resolvePersistedTheme().getUserAgentStylesheet());

        FactoryView factoryView = new FactoryView();
        VBox.setVgrow(factoryView, Priority.ALWAYS);

        VBox root = new VBox(createMenuBar(), factoryView);
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("FlexGanttFX – Factory Scheduling Demo");
        stage.centerOnScreen();
        stage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

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

    public static void main(String[] args) {
        launch(args);
    }
}
