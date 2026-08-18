/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.demo.container;

import com.flexganttfx.demo.DemoBase;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiGanttChartLiteContainerDemo extends DemoBase {

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
	public String getName() {
		return "Multi Lite";
	}

	@Override
	public String getDescription() {
		return "The multi Gantt chart container class can be used to display an arbitrary number "
				+ "of Gantt charts. The selected charts in the toggle buttons below will be shown inside "
				+ "the Gantt chart container";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
