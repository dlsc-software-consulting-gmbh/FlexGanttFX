/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.timeline;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.view.timeline.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloChronoUnitTimeline extends FlexGanttFXSampleBase {

	private Timeline timeline;

	@Override
	public Node getPanel(Stage stage) {
		GridPane gridPane = new GridPane();

		ColumnConstraints cc = new ColumnConstraints();
		cc.setPercentWidth(100);

		RowConstraints rc = new RowConstraints();
		rc.setPercentHeight(100);

		gridPane.getColumnConstraints().add(cc);
		gridPane.getRowConstraints().add(rc);

		timeline = new Timeline();
		timeline.setMinSize(100, 80);

		StackPane stackPane = new StackPane();
		stackPane.setPadding(new Insets(20));
		stackPane.getChildren().add(timeline);
		stackPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
		GridPane.setFillWidth(stackPane, true);
		GridPane.setFillHeight(stackPane, false);
		GridPane.setMargin(stackPane, new Insets(20));

		GridPane.setValignment(stackPane, VPos.CENTER);
		GridPane.setHgrow(stackPane, Priority.ALWAYS);

		gridPane.add(stackPane, 0, 0);

		return gridPane;
	}

	@Override
	public Node getControlPanel() {
		BorderPane pane = new BorderPane();
		pane.setCenter(new TimelineControlPanel(timeline));
		return pane;
	}

	@Override
	public String getSampleName() {
		return "Chrono Unit";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
