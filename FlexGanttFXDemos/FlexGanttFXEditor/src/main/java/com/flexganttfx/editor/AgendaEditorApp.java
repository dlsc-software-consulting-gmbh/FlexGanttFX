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
package com.flexganttfx.editor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The application class used to show the agenda editor.
 */
public class AgendaEditorApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		AgendaEditor editor = new AgendaEditor();
		Scene scene = new Scene(editor);
		scene.getStylesheets().add(
				AgendaEditorApp.class.getResource("editor.css")
						.toExternalForm());
		primaryStage.setTitle("Curriculum Editor");
		primaryStage.setWidth(1200);
		primaryStage.setHeight(1000);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
