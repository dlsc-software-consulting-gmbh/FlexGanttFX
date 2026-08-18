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
package com.flexganttfx.demo.model;

import com.flexganttfx.demo.DemoActivity;
import com.flexganttfx.demo.GanttChartDemoBase;
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
import javafx.util.StringConverter;

import java.time.Instant;

public class LinksDemo extends GanttChartDemoBase {

	private static final long ONE_DAY = 24 * 60 * 60 * 1000;

	private final DemoRow root;
	private GanttChart<DemoRow> gantt;
	private Layer layer;

	private ActivityLink<DemoActivity> link;

	@Override
	public void dispose() {
		super.dispose();
		gantt = null;
	}

	class DemoRow extends Row<DemoRow, DemoRow, DemoActivity> {
		public DemoRow(String name) {
			super(name);
		}
	}

    public LinksDemo() {
		root = new DemoRow("Initial Root");
		root.setExpanded(true);
	}

	@Override
	protected GanttChart<?> createGanttChart() throws Exception {
		gantt = new GanttChart<>(root);

		// renderer
		ActivityBarRenderer<DemoActivity> renderer = new ActivityBarRenderer<>(gantt.getGraphics(), "My Renderer");
		renderer.setCornersRounded(false);
		gantt.getGraphics().setActivityRenderer(DemoActivity.class, GanttLayout.class, renderer);

		gantt.getGraphics().setActivityEditingCallback(DemoActivity.class, param -> true);
		gantt.getGraphics().setRowDragAndDropCallback(DemoRow.class, param -> true);

		// layer
		layer = new Layer("Default");
		gantt.getLayers().add(layer);

		createActivities();

		return gantt;
	}

	private void createActivities() {
		DemoRow row1 = new DemoRow("Adjacent Activities");
		DemoRow row2 = new DemoRow("Row 2");
		DemoRow row3 = new DemoRow("Row 3");

		root.getChildren().addAll(row1, row2, row3);

		DemoActivity activity11 = new DemoActivity("Activity 11");
		DemoActivity activity12 = new DemoActivity("Activity 12");

		activity11.setStartTime(Instant.now().plusMillis(ONE_DAY));
		activity11.setEndTime(Instant.now().plusMillis(5 * ONE_DAY));

		activity12.setStartTime(Instant.now().plusMillis(7 * ONE_DAY));
		activity12.setEndTime(Instant.now().plusMillis(13 * ONE_DAY));

		row1.addActivity(layer, activity11);
		row3.addActivity(layer, activity12);

		ActivityRef<DemoActivity> ref11 = new ActivityRef<>(row1, layer, activity11);
		ActivityRef<DemoActivity> ref12 = new ActivityRef<>(row3, layer, activity12);

		link = new ActivityLink<>(ref11, ref12);
		link.setType(LinkType.END_TO_START);
		gantt.getGraphics().getLinks().add(link);
	}

	@Override
	public Node getControlPanel() {
		ComboBox<LinkType> box = new ComboBox<>();
		box.getItems().addAll(LinkType.values());
		box.setValue(link.getType());
		box.setConverter(new StringConverter<LinkType>() {
			@Override
			public String toString(LinkType object) {
				switch (object) {
					case END_TO_START: return "End to Start";
					case START_TO_END: return "Start to End";
					case START_TO_START: return "Start to Start";
					case END_TO_END: return "End to End";
					default: return "";
				}
			}

			@Override
			public LinkType fromString(String string) {
				return null;
			}
		});
		box.valueProperty().addListener(it -> {
			link.setType(box.getValue());
			gantt.getGraphics().redraw();
		});
		return box;
	}

	@Override
	public String getName() {
		return "Links";
	}

	@Override
	public String getDescription() {
	    return "A demo to test the four different link types (end to start, start to end, start to start, end to end)";
	}

	public static void main(String[] args) {
		launch(args);
	}
}
