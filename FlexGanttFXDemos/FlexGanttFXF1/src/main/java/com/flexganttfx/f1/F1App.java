/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.f1;

import atlantafx.base.theme.PrimerDark;
import com.flexganttfx.f1.view.F1View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class F1App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        F1View view = new F1View();
        VBox root = new VBox(view);
        VBox.setVgrow(view, Priority.ALWAYS);

        Scene scene = new Scene(root, 1400, 900);
        scene.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage.setTitle("Formula 1 Race Strategy — FlexGanttFX Demo");
        stage.setScene(scene);
        stage.setWidth(1400);
        stage.setHeight(900);
        stage.centerOnScreen();
        stage.show();
    }
}
