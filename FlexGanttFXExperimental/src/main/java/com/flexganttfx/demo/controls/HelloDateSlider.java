/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.controls;

import com.flexganttfx.experimental.DateSlider;
import com.flexganttfx.experimental.PopOverTitledPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HelloDateSlider extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		DateSlider slider = new DateSlider();

		stage.setScene(new Scene(slider));
		stage.setWidth(500);
		stage.setHeight(300);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
