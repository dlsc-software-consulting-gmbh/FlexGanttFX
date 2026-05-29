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
package com.flexganttfx.demo.controls;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.flexganttfx.experimental.PopOverTitledPane;

public class HelloDualTitledPane extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Label summary = new Label("Summary Label");
		TextField details = new TextField("Details");

		PopOverTitledPane pane = new PopOverTitledPane("Title", summary,
				details);

		stage.setScene(new Scene(pane));
		stage.setWidth(500);
		stage.setHeight(300);
		stage.show();
	}

//	@Override
//	public String getSampleName() {
//		return "PopOver Titled Pane";
//	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
