/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.container;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.container.MultiGanttChartContainerBase;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.ListSelectionView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HelloMultiGanttChartContainer extends FlexGanttFXSampleBase {

	class DemoRow extends Row<DemoRow, DemoRow, Activity> {
		public DemoRow(String name) {
			super(name);
		}
	}

	private MultiGanttChartContainerBase multiGanttChart;
	private List<Entry> availableList;
	private List<Entry> selectedList;
	private ListSelectionView<Entry> listSV;
	private GanttChart<DemoRow> masterGC;

	@Override
	public Node getPanel(Stage stage) {
		multiGanttChart = new MultiGanttChartContainerBase();

		masterGC = new GanttChart<>(new DemoRow("Master"));
		masterGC.getGraphics().setShowVerticalCursor(true);
		masterGC.setId("gantt-master");

		availableList = new ArrayList<>();
		selectedList = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			DemoRow root = new DemoRow("Gantt #" + (i + 1));
			GanttChart<DemoRow> gc = new GanttChart<>(root);
			gc.setId("gantt-" + i);
			Entry entry = new Entry();
			entry.name = "Gantt #" + (i + 1);
			entry.gc = gc;
			availableList.add(entry);
		}

		multiGanttChart.getGanttCharts().add(masterGC);

		for (int i = 0; i < 2; i++) {
			multiGanttChart.getGanttCharts().add(availableList.get(i).gc);
			selectedList.add(availableList.remove(i));
		}

		multiGanttChart.resetDividerPositions();


		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(multiGanttChart);
		borderPane.setTop(new GanttChartToolBar<>(masterGC));

		return borderPane;
	}

	class Entry {
		String name;
		GanttChart<DemoRow> gc;
	}

	@Override
	public Node getControlPanel() {
		listSV = new ListSelectionView<>();
		listSV.setPadding(new Insets(10));
		listSV.setOrientation(Orientation.VERTICAL);
		listSV.setCellFactory(new Callback<ListView<Entry>, ListCell<Entry>>() {

			@Override
			public ListCell<Entry> call(ListView<Entry> param) {
				ListCell<Entry> cell = new ListCell<Entry>() {
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
		});

		listSV.getSourceItems().addAll(availableList);
		listSV.getTargetItems().addAll(selectedList);

		listSV.getTargetItems().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				updateContainer();
			}
		});

		return listSV;
	}

	private void updateContainer() {
		multiGanttChart.getGanttCharts().setAll(
				listSV.getTargetItems().stream().map(entry -> entry.gc)
						.collect(Collectors.toList()));
		multiGanttChart.getGanttCharts().add(0, masterGC);
		Platform.runLater(() -> multiGanttChart.resetDividerPositions());
	}

	@Override
	public String getSampleName() {
		return "Multi";
	}

	@Override
	public String getSampleDescription() {
		return "The multi Gantt chart container class can be used to display an arbitrary number "
				+ "of Gantt charts. The list selection view below allows you to manipulate the "
				+ "list of charts shown.";
	}

	@Override
	public String getJavaDocURL() {
		return getJavaDocBase() + "com/flexganttfx/view/container/MultiGanttChartContainer.html";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
