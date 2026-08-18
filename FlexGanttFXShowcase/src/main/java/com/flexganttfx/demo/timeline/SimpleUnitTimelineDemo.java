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
package com.flexganttfx.demo.timeline;

import com.flexganttfx.demo.DemoBase;
import com.flexganttfx.model.dateline.SimpleUnitDatelineModel;
import com.flexganttfx.model.timeline.SimpleUnitTimelineModel;
import com.flexganttfx.view.timeline.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.Instant;

public class SimpleUnitTimelineDemo extends DemoBase {

	private Timeline timeline;

	@Override
	public void dispose() {
		super.dispose();
		timeline = null;
	}

	@Override
	public Node getPanel(Stage stage) {
		GridPane gridPane = new GridPane();

		ColumnConstraints cc = new ColumnConstraints();
		cc.setPercentWidth(100);

		RowConstraints rc = new RowConstraints();
		rc.setPercentHeight(100);

		gridPane.getColumnConstraints().add(cc);
		gridPane.getRowConstraints().add(rc);

		timeline = new Timeline();
		timeline.getEventline().setVisible(false);
		timeline.getModel().setNow(Instant.ofEpochMilli(0));
		timeline.setModel(new SimpleUnitTimelineModel());
		timeline.getDateline().setModel(new SimpleUnitDatelineModel());

		StackPane stackPane = new StackPane();
		stackPane.setPadding(new Insets(20));
		stackPane.getChildren().add(timeline);
		GridPane.setFillWidth(stackPane, true);
		GridPane.setFillHeight(stackPane, false);
		GridPane.setMargin(stackPane, new Insets(20));

		GridPane.setValignment(stackPane, VPos.CENTER);
		GridPane.setHgrow(stackPane, Priority.ALWAYS);

		gridPane.add(stackPane, 0, 0);

		return gridPane;
	}

	@Override
	public Node getControlPanel() {
		BorderPane pane = new BorderPane();
		pane.setCenter(new TimelineControlPanel(timeline));
		return pane;
	}

	@Override
	public String getName() {
		return "Simple Unit";
	}

	@Override
	public String getDescription() {
		return "A timeline with simple decimal unit. The dateline model supports the following units: " +
				"one, ten, hundreds, thousands, etc...";
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
