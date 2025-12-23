/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
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
