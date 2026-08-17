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
package com.flexganttfx.demo.timeline;

import com.flexganttfx.view.timeline.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;

public class TimelineControlPanel extends HBox {

	public TimelineControlPanel(Timeline timeline) {
		setSpacing(10);

		Button zoomIn = new Button("Zoom In");
		zoomIn.setOnAction(evt -> timeline.zoomIn());
		getChildren().add(zoomIn);

		Button zoomOut = new Button("Zoom Out");
		zoomOut.setOnAction(evt -> timeline.zoomOut());
		getChildren().add(zoomOut);

		Button gotoToday = new Button("Now (center)");
		gotoToday.setOnAction(evt -> timeline.showNow());
		getChildren().add(gotoToday);

		Button gotoTodayLeft = new Button("Now (Left)");
		gotoTodayLeft.setOnAction(evt -> timeline.showNow(false));
		getChildren().add(gotoTodayLeft);

		for (Node node : getChildren()) {
			Control control = (Control) node;
			control.setMaxWidth(Double.MAX_VALUE);
		}
	}
}
