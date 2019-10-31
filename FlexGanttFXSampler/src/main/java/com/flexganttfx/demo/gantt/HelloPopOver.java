/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import static org.controlsfx.control.PopOver.ArrowLocation.TOP_CENTER;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.controlsfx.control.PopOver;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.ListViewGraphics;

import javafx.application.Application;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class HelloPopOver extends FlexGanttFXSample {

	private static final Layer layer = new Layer("Flights");

	private GanttChart<HelloRow> gc;

	@Override
	protected GanttChart<?> createGanttChart() throws FileNotFoundException {
		gc = new GanttChart<>();

		gc.getLayers().add(layer);

		HelloRow row = new HelloRow("Row");

		HelloActivity activity1 = new HelloActivity("Item 1");
		HelloActivity activity2 = new HelloActivity("Item 2");
		HelloActivity activity3 = new HelloActivity("Item 3");

		activity1.setStartTime(Instant.now().plus(2, ChronoUnit.DAYS));
		activity1.setEndTime(Instant.now().plus(3, ChronoUnit.DAYS));
		activity2.setStartTime(Instant.now().plus(5, ChronoUnit.DAYS));
		activity2.setEndTime(Instant.now().plus(8, ChronoUnit.DAYS));
		activity3.setStartTime(Instant.now().plus(10, ChronoUnit.DAYS));
		activity3.setEndTime(Instant.now().plus(12, ChronoUnit.DAYS));

		row.addActivity(layer, activity1);
		row.addActivity(layer, activity2);
		row.addActivity(layer, activity3);

		gc.getTimeline().showTime(Instant.now().plus(1, ChronoUnit.DAYS), false);
		gc.setRoot(row);

		ListViewGraphics<HelloRow> graphics = gc.getGraphics();
		graphics.getListView().addEventHandler(MouseEvent.MOUSE_MOVED, evt -> mouseMoved(evt));
		return gc;
	}

	private PopOver popOver;

	private void mouseMoved(MouseEvent evt) {
		ActivityRef<?> ref = gc.getGraphics().getActivityRefAt(evt.getX(), evt.getY());
		if (ref != null) {
			if (popOver == null || popOver.isDetached()) {
				popOver = new PopOver();
				popOver.setArrowLocation(TOP_CENTER);
				popOver.getContentNode().setMouseTransparent(true);
			}

			double x = evt.getScreenX();
			double y = evt.getScreenY();

			if (!popOver.isShowing()) {
				popOver.setTitle(ref.getActivity().getName());
				popOver.show(gc.getGraphics(), x, y, Duration.ONE);
			}
		} else {
			if (popOver != null && !popOver.isDetached()) {
				popOver.hide();
			}
		}
	}

	@Override
	public String getSampleName() {
		return "PopOvers";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
