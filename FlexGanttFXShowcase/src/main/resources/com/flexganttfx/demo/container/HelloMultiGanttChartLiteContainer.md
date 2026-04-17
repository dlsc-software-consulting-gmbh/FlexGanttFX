This sample focuses on the lite version of the multi-chart container. It demonstrates how several `GanttChartLite` views can be composed into a synchronized layout for lightweight multi-view scenarios.

```java
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
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HelloMultiGanttChartLiteContainer extends FlexGanttFXSampleBase {

	class DemoRow extends Row<DemoRow, DemoRow, Activity> {
		public DemoRow(String name) {
			super(name);
		}
	}

	private MultiGanttChartLiteContainer multiGanttChart;
	private final List<Entry> entries = new ArrayList<>();
	private GanttChartLite<DemoRow> masterGC;

	@Override
	public void dispose() {
		super.dispose();
		multiGanttChart = null;
		entries.clear();
		masterGC = null;
	}

	@Override
	public Node getPanel(Stage stage) {
		multiGanttChart = new MultiGanttChartLiteContainer();
		entries.clear();

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
			entries.add(entry);
		}

		multiGanttChart.getGanttCharts().add(masterGC);
		multiGanttChart.getGanttCharts().addAll(entries.stream().map(entry -> entry.gc).collect(Collectors.toList()));
		multiGanttChart.resetDividerPositions();

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(multiGanttChart);
		borderPane.setTop(new GanttChartToolBar<>(masterGC));

		return borderPane;
	}

	class Entry {
		String name;
		GanttChartLite<DemoRow> gc;
		ToggleButton toggleButton;
	}

	@Override
	public Node getControlPanel() {
		HBox box = new HBox(10);
		box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

		for (Entry entry : entries) {
			ToggleButton button = new ToggleButton(entry.name);
			button.setMaxWidth(Double.MAX_VALUE);
			button.setSelected(true);
			button.selectedProperty().addListener(it -> updateContainer());
			entry.toggleButton = button;
			box.getChildren().add(button);
		}

		return box;
	}

	private void updateContainer() {
		multiGanttChart.getGanttCharts().setAll(
				entries.stream()
						.filter(entry -> entry.toggleButton != null && entry.toggleButton.isSelected())
						.map(entry -> entry.gc)
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
				+ "of Gantt charts. The selected charts in the toggle buttons below will be shown inside "
				+ "the Gantt chart container";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
```
