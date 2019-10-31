/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.model;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityLink.LinkType;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.time.Instant;

public class HelloLinks extends FlexGanttFXSample {

	private static final long ONE_DAY = 1 * 24 * 60 * 60 * 1000;

	private HelloRow root;
	private GanttChart<HelloRow> gantt;
	private Layer layer;

	private ActivityLink<HelloActivity> link;

	class HelloRow extends Row<HelloRow, HelloRow, HelloActivity> {
		public HelloRow(String name) {
			super(name);
		}
	}

    public HelloLinks() {
		root = new HelloRow("Initial Root");
		root.setExpanded(true);
	}

	@Override
	protected GanttChart<?> createGanttChart() throws Exception {
		gantt = new GanttChart<>(root);

		// renderer
		ActivityBarRenderer<HelloActivity> renderer = new ActivityBarRenderer<>(gantt.getGraphics(), "My Renderer");
		renderer.setCornersRounded(false);
		gantt.getGraphics().setActivityRenderer(HelloActivity.class, GanttLayout.class, renderer);

		gantt.getGraphics().setActivityEditingCallback(HelloActivity.class, param -> true);
		gantt.getGraphics().setRowDragAndDropCallback(HelloRow.class, param -> true);

		// layer
		layer = new Layer("Default");
		gantt.getLayers().add(layer);

		createActivities();

		return gantt;
	}

	private void createActivities() {
		HelloRow row1 = new HelloRow("Adjacent Activities");
		HelloActivity activity11 = new HelloActivity("Activity 11");
		HelloActivity activity12 = new HelloActivity("Activity 12");

		activity11.setStartTime(Instant.now().plusMillis(ONE_DAY));
		activity11.setEndTime(Instant.now().plusMillis(5 * ONE_DAY));

		activity12.setStartTime(Instant.now().plusMillis(7 * ONE_DAY));
		activity12.setEndTime(Instant.now().plusMillis(13 * ONE_DAY));

		row1.addActivity(layer, activity11);
		row1.addActivity(layer, activity12);

		ActivityRef<HelloActivity> ref11 = new ActivityRef<>(row1, layer, activity11);
		ActivityRef<HelloActivity> ref12 = new ActivityRef<>(row1, layer, activity12);

		link = new ActivityLink<>(ref11, ref12);
		gantt.getGraphics().getLinks().add(link);

		root.getChildren().add(row1);

		// row 2
		HelloRow row2 = new HelloRow("Row 2");
		root.getChildren().add(row2);

		// row 3
		HelloRow row3 = new HelloRow("Row 3");
		root.getChildren().add(row3);
	}

	@Override
	public Node getControlPanel() {
		ComboBox<LinkType> box = new ComboBox<>();
		box.getItems().addAll(LinkType.values());
		box.setValue(LinkType.END_TO_START);
		box.valueProperty().addListener(it -> {
			link.setType(box.getValue());
			gantt.getGraphics().redraw();
		});
		return box;
	}

	@Override
	public String getSampleName() {
		return "Links";
	}

	@Override
	public String getSampleDescription() {
	    return "A sample to test the four different link types (E->S, S->E, E->E, S->S)";
	}

	@Override
	public String getJavaDocURL() {
		return super.getJavaDocBase() + "com/flexganttfx/model/ActivityLink.html";
	}

	public static void main(String[] args) {
		launch(args);
	}
}
