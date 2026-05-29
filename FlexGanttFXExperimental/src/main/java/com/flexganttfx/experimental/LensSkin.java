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
/**
 *
 */
package com.flexganttfx.experimental;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.VBoxGraphics;
import com.flexganttfx.view.timeline.Timeline;

public class LensSkin<R extends Row<?, ?, ?>> extends SkinBase<Lens<R>> {

	private final VBoxGraphics<R> graphics;

    public LensSkin(Lens<R> lens) {
		super(lens);

		InvalidationListener updateListener = it -> updateView();

		lens.startIndexProperty().addListener(updateListener);
		lens.getRows().addListener(updateListener);

        Timeline timeline = new Timeline();
		timeline.setModel(lens.getGraphics().getTimeline().getModel());

		graphics = new VBoxGraphics<>();
		graphics.setTimeline(timeline);
		graphics.setPriorityCallback(row -> Priority.NEVER);
		Bindings.bindContent(graphics.getLayers(), lens.getGraphics()
				.getLayers());

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(timeline);
		borderPane.setCenter(graphics);

		getChildren().add(borderPane);

		updateView();
	}

	private void updateView() {
		List<R> rows = getSkinnable().getRows();
		int startIndex = getSkinnable().getStartIndex();

		List<R> result = new ArrayList<>();

		// DoubleProperty heightProperty = new SimpleDoubleProperty();
		// NumberBinding binding = Bindings.add(0,
		// timeline.prefHeightProperty());

		for (int i = 0; i < getSkinnable().getRowCount(); i++) {
			int index = startIndex + i;
			if (index >= rows.size()) {
				break;
			}

			R row = rows.get(index);
			row.getProperties().put("com.flexganttfx.row.showing", true);
			// binding = Bindings.add(binding, row.heightProperty());
			result.add(row);
		}

		graphics.getRows().setAll(result);
	}
}
