/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
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
