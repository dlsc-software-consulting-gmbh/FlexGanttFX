/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A showcase application to show off the conflict resolution feature.
 */
public class AgendaGanttChartApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		AgendaGanttChart ganttChart = new AgendaGanttChart();
		Scene scene = new Scene(ganttChart);
		primaryStage.setScene(scene);
		primaryStage.setWidth(1200);
		primaryStage.setHeight(1000);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
