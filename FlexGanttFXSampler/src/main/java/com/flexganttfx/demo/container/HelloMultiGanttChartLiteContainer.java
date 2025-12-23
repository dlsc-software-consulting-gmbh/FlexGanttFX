/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.container;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.container.MultiGanttChartLiteContainer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.stream.Collectors;

public class HelloMultiGanttChartLiteContainer extends FlexGanttFXSampleBase {

	class DemoRow extends Row<DemoRow, DemoRow, Activity> {
		public DemoRow(String name) {
			super(name);
		}
	}

	private MultiGanttChartLiteContainer multiGanttChart;
	private ListView<Entry> listView;
	private GanttChartLite<DemoRow> masterGC;

	@Override
	public void dispose() {
		super.dispose();
		multiGanttChart = null;
		listView = null;
		masterGC = null;
	}

	@Override
	public Node getPanel(Stage stage) {
		listView = new ListView<>();
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listView.getSelectionModel().getSelectedItems().addListener((InvalidationListener) observable -> updateContainer());
		listView.setCellFactory(createCellFactory());

		multiGanttChart = new MultiGanttChartLiteContainer();

		masterGC = new GanttChartLite<>();
		masterGC.getRows().add(new DemoRow("Row"));
		masterGC.getGraphics().setShowVerticalCursor(true);
		masterGC.setId("gantt-master");

		for (int i = 0; i < 3; i++) {
			DemoRow root = new DemoRow("Gantt #" + (i + 1));
			GanttChartLite<DemoRow> gc = new GanttChartLite<>();
			gc.getRows().add(new DemoRow("Row"));
			gc.setId("gantt-" + i);
			Entry entry = new Entry();
			entry.name = "Gantt #" + (i + 1);
			entry.gc = gc;
			listView.getItems().add(entry);
		}

		multiGanttChart.getGanttCharts().add(masterGC);
		multiGanttChart.resetDividerPositions();

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(multiGanttChart);
		borderPane.setTop(new GanttChartToolBar<>(masterGC));

		return borderPane;
	}

	class Entry {
		String name;
		GanttChartLite<DemoRow> gc;
	}

	@Override
	public Node getControlPanel() {
		return new StackPane(listView);
	}

	private Callback<ListView<Entry>, ListCell<Entry>> createCellFactory() {
		return new Callback<>() {

			@Override
			public ListCell<Entry> call(ListView<Entry> param) {
				ListCell<Entry> cell = new ListCell<>() {

					@Override
					protected void updateItem(Entry item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							setText(item.name);
						}
					}

					@Override
					public void updateIndex(int i) {
						super.updateIndex(i);

						if (i == -1) {
							setText("");
						}
					}
				};
				return cell;
			}
		};
	}

	private void updateContainer() {
		multiGanttChart.getGanttCharts().setAll(
				listView.getSelectionModel().getSelectedItems().stream().map(entry -> entry.gc)
						.collect(Collectors.toList()));
		multiGanttChart.getGanttCharts().add(0, masterGC);
		Platform.runLater(() -> multiGanttChart.resetDividerPositions());
	}

	@Override
	public String getSampleName() {
		return "Multi Lite";
	}

	@Override
	public String getSampleDescription() {
		return "The multi Gantt chart container class can be used to display an arbitrary number "
				+ "of Gantt charts. The selected charts in the list view below will be shown inside "
				+ "the Gantt chart container";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}