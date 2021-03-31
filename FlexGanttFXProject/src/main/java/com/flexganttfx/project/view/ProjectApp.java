/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.project.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.flexganttfx.view.container.DualGanttChartContainer;

public class ProjectApp extends Application {

	public ProjectApp() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		TaskGanttChart taskGanttChart = new TaskGanttChart();
		ResourceGanttChart resourceGanttChart = new ResourceGanttChart();
		DualGanttChartContainer dual = new DualGanttChartContainer(taskGanttChart, resourceGanttChart);
		dual.setShowSecondary(false);
		Platform.runLater(() -> {
			taskGanttChart.getTimeline().showNow(true);
		});
		Scene scene = new Scene(dual);
		scene.getStylesheets().add(ProjectApp.class.getResource("project.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
