/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.timeline;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.VBox;

import com.flexganttfx.view.timeline.Timeline;

public class TimelineControlPanel extends VBox {

	public TimelineControlPanel(Timeline timeline) {

		setFillWidth(true);
		setPadding(new Insets(10, 10, 10, 10));
		setSpacing(10);

		Button zoomIn = new Button("Zoom In");
		zoomIn.setOnAction(evt -> timeline.zoomIn());
		getChildren().add(zoomIn);

		Button zoomOut = new Button("Zoom Out");
		zoomOut.setOnAction(evt -> timeline.zoomOut());
		getChildren().add(zoomOut);

		Button gotoToday = new Button("Now (center)");
		gotoToday.setOnAction(evt -> timeline.showNow());
		getChildren().add(gotoToday);

		Button gotoTodayLeft = new Button("Now (Left)");
		gotoTodayLeft.setOnAction(evt -> timeline.showNow(false));
		getChildren().add(gotoTodayLeft);

		for (Node node : getChildren()) {
			Control control = (Control) node;
			control.setMaxWidth(Double.MAX_VALUE);
		}
	}
}
