/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.demo.model;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
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

public class HelloLinks extends FlexGanttFXSample {

	private static final long ONE_DAY = 24 * 60 * 60 * 1000;

	private final HelloRow root;
	private GanttChart<HelloRow> gantt;
	private Layer layer;

	private ActivityLink<HelloActivity> link;

	@Override
	public void dispose() {
		super.dispose();
		gantt = null;
	}

	class HelloRow extends Row<HelloRow, HelloRow, HelloActivity> {
		public HelloRow(String name) {
			super(name);
		}
	}

    public HelloLinks() {
		root = new HelloRow("Initial Root");
		root.setExpanded(true);
	}

	@Override
	protected GanttChart<?> createGanttChart() throws Exception {
		gantt = new GanttChart<>(root);

		// renderer
		ActivityBarRenderer<HelloActivity> renderer = new ActivityBarRenderer<>(gantt.getGraphics(), "My Renderer");
		renderer.setCornersRounded(false);
		gantt.getGraphics().setActivityRenderer(HelloActivity.class, GanttLayout.class, renderer);

		gantt.getGraphics().setActivityEditingCallback(HelloActivity.class, param -> true);
		gantt.getGraphics().setRowDragAndDropCallback(HelloRow.class, param -> true);

		// layer
		layer = new Layer("Default");
		gantt.getLayers().add(layer);

		createActivities();

		return gantt;
	}

	private void createActivities() {
		HelloRow row1 = new HelloRow("Adjacent Activities");
		HelloRow row2 = new HelloRow("Row 2");
		HelloRow row3 = new HelloRow("Row 3");

		root.getChildren().addAll(row1, row2, row3);

		HelloActivity activity11 = new HelloActivity("Activity 11");
		HelloActivity activity12 = new HelloActivity("Activity 12");

		activity11.setStartTime(Instant.now().plusMillis(ONE_DAY));
		activity11.setEndTime(Instant.now().plusMillis(5 * ONE_DAY));

		activity12.setStartTime(Instant.now().plusMillis(7 * ONE_DAY));
		activity12.setEndTime(Instant.now().plusMillis(13 * ONE_DAY));

		row1.addActivity(layer, activity11);
		row3.addActivity(layer, activity12);

		ActivityRef<HelloActivity> ref11 = new ActivityRef<>(row1, layer, activity11);
		ActivityRef<HelloActivity> ref12 = new ActivityRef<>(row3, layer, activity12);

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
	public String getSampleName() {
		return "Links";
	}

	@Override
	public String getSampleDescription() {
	    return "A sample to test the four different link types (end to start, start to end, start to start, end to end)";
	}

	public static void main(String[] args) {
		launch(args);
	}
}
