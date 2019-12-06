/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.emirates.model.DataModel;
import com.flexganttfx.emirates.model.DataModel.DataSet;
import com.flexganttfx.emirates.view.EmiratesAircraftGanttChart;
import com.flexganttfx.emirates.view.IntroPane;
import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

public class EmiratesApp extends Application {

	private EmiratesAircraftGanttChart gantt;
	private Stage stage;
	private Node ganttNode;
	private Node introNode;
	private GanttChartStatusBar<?> statusBar;

	@Override
	public void start(Stage stage) {
		FlexGanttFX.setLicenseKey("LIC=SYSKRON;VEN=DLSC;VER=11;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02147E12D2B805F802CB2D639934A1E7F757361A3CE3021424E1BBC023F20B6C29E8BDAEF61B7D9D95E5005F");

		this.stage = stage;
		this.stage.setTitle("Emirates Aircraft Scheduling");

		StackPane stack = new StackPane();
		stack.getChildren().add(ganttNode = createGanttChart());
		stack.getChildren().add(introNode = createIntro());

		Scene scene = new Scene(stack);
		scene.getStylesheets().add(EmiratesApp.class.getResource("emirates.css").toExternalForm());

		stage.setScene(scene);
		stage.setWidth(1400);
		stage.setHeight(900);
		stage.centerOnScreen();
		stage.show();
	}

	public void load(final DataSet dataSet) {
		KeyValue keyValue = new KeyValue(introNode.opacityProperty(), 0);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), keyValue);
		Timeline timeline = new Timeline(keyFrame);
		timeline.setOnFinished(finishedEvent -> {

			ganttNode.toFront();
			gantt.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 25);

			final Task<DataModel> task = new Task<DataModel>() {
				@Override
				protected DataModel call() throws Exception {
					return new DataModel(dataSet, statusBar);
				}
			};

			task.setOnSucceeded(succeededEvent -> Platform.runLater(() -> {
				try {
					statusBar.setProgress(0);
					DataModel model = task.get();
					gantt.getRows().setAll(model.getRows());
					gantt.getLayers().setAll(model.getLayers());
					Platform.runLater(() -> gantt.getGraphics()
							.showEarliestActivities());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}));

			Thread thread = new Thread(task);
			thread.start();
		});
		timeline.play();
	}

	private Region createIntro() {
		HBox placeholder = new HBox();
		placeholder.getStyleClass().add("cover");
		IntroPane introPane = new IntroPane(this);
		HBox.setMargin(introPane, new Insets(0, 0, 40, 40));
		placeholder.getChildren().add(introPane);
		return placeholder;
	}

	private Region createGanttChart() {
		gantt = new EmiratesAircraftGanttChart();
		gantt.setPrefSize(1300, 1100);
		VBox.setVgrow(gantt, Priority.ALWAYS);

		VBox vbox = new VBox(0);

		MenuBar menuBar = createMenuBar();
		vbox.getChildren().add(menuBar);

		GanttChartToolBar<?> toolBar = new GanttChartToolBar<>(gantt);
		vbox.getChildren().add(toolBar);

		vbox.getChildren().add(gantt);

		statusBar = new GanttChartStatusBar<>(gantt);
		vbox.getChildren().add(statusBar);
		return vbox;
	}

	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");

		Menu bufferMenu = new Menu("Canvas Buffer");

		MenuItem bufferOff = new MenuItem("Off");
		bufferOff.setOnAction(evt -> gantt.getGraphics().setCanvasBuffer(0));
		MenuItem buffer100 = new MenuItem("100 Pixel");
		buffer100.setOnAction(evt -> gantt.getGraphics().setCanvasBuffer(100));
		MenuItem buffer200 = new MenuItem("200 Pixel");
		buffer200.setOnAction(evt -> gantt.getGraphics().setCanvasBuffer(200));
		MenuItem buffer500 = new MenuItem("500 Pixel");
		buffer500.setOnAction(evt -> gantt.getGraphics().setCanvasBuffer(500));
		bufferMenu.getItems().setAll(bufferOff, buffer100, buffer200, buffer500);
		fileMenu.getItems().add(bufferMenu);

		Menu loadMenu = new Menu("Load");
		for (final DataSet data : DataSet.values()) {
			MenuItem openDataSetItem = new MenuItem(data.getDisplayName());
			openDataSetItem.setOnAction(event -> load(data));
			loadMenu.getItems().add(openDataSetItem);
		}

		fileMenu.getItems().add(loadMenu);

		MenuItem redraw = new MenuItem("Redraw");
		redraw.setOnAction(evt -> {
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				if (i % 1000 == 0) {
					System.out.println("repaints done: " + i);
				}
				gantt.getGraphics().redraw();
			}
			System.out.println("time in ms: " + (System.currentTimeMillis() - startTime));
		});

		fileMenu.getItems().add(redraw);

		CheckMenuItem safeItem = new CheckMenuItem("Safe Rendering");
		safeItem.selectedProperty().bindBidirectional(gantt.getGraphics().safeRenderingProperty());
		fileMenu.getItems().add(safeItem);

		menuBar.getMenus().add(fileMenu);

		return menuBar;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
