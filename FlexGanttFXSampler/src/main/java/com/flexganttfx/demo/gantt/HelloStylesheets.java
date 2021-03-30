/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.io.FileNotFoundException;

public class HelloStylesheets extends FlexGanttFXSample {

	private GanttChart<HelloRow> gc;

	@Override
	public String getSampleName() {
		return "Stylesheets";
	}

	@Override
	public void dispose() {
		super.dispose();
		gc = null;
	}

	@Override
	protected GanttChart<?> createGanttChart() throws FileNotFoundException {
		gc = new GanttChart<>();

		HelloRow root = new HelloRow("root");
		for (int i = 0; i < 200; i++) {
			HelloRow row = new HelloRow("Row " + (i + 1));
			root.getChildren().add(row);
		}

		gc.setRoot(root);


		return gc;
	}

	@Override
	public Node getControlPanel() {
		ComboBox<String> box = new ComboBox<>();
		box.getItems().setAll("Modena", "Dark", "Red");
		box.setValue("Modena");
		box.valueProperty().addListener(it -> applyStylesheet(box.getValue()));
		return box;
	}

	private void applyStylesheet(String value) {
		switch (value) {
			case "Modena":
				gc.getStylesheets().clear();
				break;
			case "Dark":
				gc.getStylesheets().setAll(HelloStylesheets.class.getResource("dark.css").toExternalForm());
				break;
			case "Red":
				gc.getStylesheets().setAll(HelloStylesheets.class.getResource("red.css").toExternalForm());
				break;
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
