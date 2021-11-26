/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.tutorial;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.timeline.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TutorialAircraftFlight extends Application {

	/*
	 * Plain data object storing dummy flight information.
	 */
	public static class FlightData {

		String flightNo;
		Instant departureTime = Instant.now();
		Instant arrivalTime = Instant.now().plus(Duration.ofHours(6));

		public FlightData(String flightNo, int day) {
			this.flightNo = flightNo;
			departureTime = departureTime.plus(Duration.ofDays(day));
			arrivalTime = arrivalTime.plus(Duration.ofDays(day));
		}
	}

	/*
	 * The activity representing the flight. This object will be rendered as a
	 * bar in the graphics view of the Gantt chart. The flight is mutable, so
	 * the user will be able to interact with it.
	 */
	public static class Flight extends MutableActivityBase<FlightData> {
		public Flight(FlightData data) {
			setUserObject(data);
			setName(data.flightNo);
			setStartTime(data.departureTime);
			setEndTime(data.arrivalTime);
		}
	}

	/*
	 * Each row represents an aircraft in this example. The activities shown on
	 * the row are of type Flight.
	 */
	public static class Aircraft extends Row<Aircraft, Aircraft, Flight> {
		public Aircraft(String name) {
			super(name);
		}
	}

	public void start(Stage stage) {

		// Create the Gantt chart
		GanttChart<Aircraft> gantt = new GanttChart<Aircraft>(new Aircraft(
				"ROOT"));

		Layer layer = new Layer("Flights");
		gantt.getLayers().add(layer);

		Aircraft b747 = new Aircraft("B747");
		b747.addActivity(layer, new Flight(new FlightData("flight1", 1)));
		b747.addActivity(layer, new Flight(new FlightData("flight2", 2)));
		b747.addActivity(layer, new Flight(new FlightData("flight3", 3)));

		Aircraft a380 = new Aircraft("A380");
		a380.addActivity(layer, new Flight(new FlightData("flight1", 1)));
		a380.addActivity(layer, new Flight(new FlightData("flight2", 2)));
		a380.addActivity(layer, new Flight(new FlightData("flight3", 3)));

		gantt.getRoot().getChildren().setAll(b747, a380);

		Timeline timeline = gantt.getTimeline();
		timeline.showTemporalUnit(ChronoUnit.HOURS, 10);

		GraphicsBase<Aircraft> graphics = gantt.getGraphics();
		graphics.setActivityRenderer(Flight.class, GanttLayout.class,
				new ActivityBarRenderer<>(graphics, "Flight Renderer"));
		graphics.showEarliestActivities();

		Scene scene = new Scene(gantt);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.centerOnScreen();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}