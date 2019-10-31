/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.container;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.container.MultiGanttChartLiteContainer;
import javafx.application.Application;
import javafx.application.Platform;
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

public class HelloMultiGanttChartLiteContainer extends FlexGanttFXSampleBase {

	class DemoRow extends Row<DemoRow, DemoRow, Activity> {
		public DemoRow(String name) {
			super(name);
		}
	}

	private MultiGanttChartLiteContainer multiGanttChartContainer;
	private List<Entry> availableList;
	private List<Entry> selectedList;
	private ListSelectionView<Entry> listSV;
	private GanttChartLite<DemoRow> masterGC;

	@Override
	public Node getPanel(Stage stage) {
		multiGanttChartContainer = new MultiGanttChartLiteContainer();

		masterGC = new GanttChartLite<>();
		masterGC.getGraphics().setShowVerticalCursor(true);
		masterGC.setId("gantt-master");
		masterGC.getRows().add(new DemoRow("Master"));

		availableList = new ArrayList<>();
		selectedList = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			DemoRow row = new DemoRow("Chart #" + (i + 1));
			GanttChartLite<DemoRow> gc = new GanttChartLite<>();
			gc.getRows().add(row);
			gc.setId("chart-" + i);
			Entry entry = new Entry();
			entry.name = "Chart #" + (i + 1);
			entry.gc = gc;
			availableList.add(entry);
		}

		multiGanttChartContainer.getGanttCharts().add(masterGC);

		for (int i = 0; i < 2; i++) {
			multiGanttChartContainer.getGanttCharts().add(availableList.get(i).gc);
			selectedList.add(availableList.remove(i));
		}

		multiGanttChartContainer.resetDividerPositions();

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(multiGanttChartContainer);

		return borderPane;
	}
	class Entry {
		String name;
		GanttChartLite<DemoRow> gc;
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

		listSV.getTargetItems().addListener((Observable observable) -> {
            updateContainer();
        });

		return listSV;
	}

	private void updateContainer() {
		multiGanttChartContainer.getGanttCharts().setAll(
				listSV.getTargetItems().stream().map(entry -> entry.gc)
						.collect(Collectors.toList()));
		multiGanttChartContainer.getGanttCharts().add(0, masterGC);
		Platform.runLater(() -> multiGanttChartContainer.resetDividerPositions());
	}

	@Override
	public String getSampleName() {
		return "Multi Lite";
	}

	@Override
	public String getSampleDescription() {
		return "The multi Gantt chart container class can be used to display an arbitrary number "
				+ "of Gantt charts. The list selection view below allows you to manipulate the "
				+ "list of charts shown.";
	}

	@Override
	public String getJavaDocURL() {
		return getJavaDocBase() + "com/flexganttfx/view/container/MultiGanttChartLiteContainer.html";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
