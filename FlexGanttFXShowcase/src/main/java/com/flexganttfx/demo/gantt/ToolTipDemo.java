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
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.DemoActivity;
import com.flexganttfx.demo.DemoRow;
import com.flexganttfx.demo.GanttChartDemoBase;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.ListViewGraphics;
import javafx.application.Application;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ToolTipDemo extends GanttChartDemoBase {

	private static final Layer layer = new Layer("Flights");

	private GanttChart<DemoRow> gc;

	private Tooltip tooltip;

	@Override
	public void dispose() {
		super.dispose();
		gc = null;
	}

	@Override
	protected GanttChart<?> createGanttChart() throws FileNotFoundException {
		gc = new GanttChart<>();

		tooltip = new Tooltip("");

		gc.getGraphics().getListView().setTooltip(tooltip);

		gc.getLayers().add(layer);

		DemoRow row = new DemoRow("Row");

		DemoActivity activity1 = new DemoActivity("Item 1");
		DemoActivity activity2 = new DemoActivity("Item 2");
		DemoActivity activity3 = new DemoActivity("Item 3");

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

		ListViewGraphics<DemoRow> graphics = gc.getGraphics();
		graphics.getListView().addEventHandler(MouseEvent.MOUSE_MOVED, this::mouseMoved);
		return gc;
	}

	@Override
	public String getDescription() {
		return "Shows how to add tooltip support to the Gantt Chart.";
	}

	private void mouseMoved(MouseEvent evt) {
		ActivityRef<?> ref = gc.getGraphics().getActivityRefAt(evt.getX(), evt.getY());
		if (ref != null) {
			tooltip.setText(ref.getActivity().getName());
		}
	}

	@Override
	public String getName() {
		return "Tooltips";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
