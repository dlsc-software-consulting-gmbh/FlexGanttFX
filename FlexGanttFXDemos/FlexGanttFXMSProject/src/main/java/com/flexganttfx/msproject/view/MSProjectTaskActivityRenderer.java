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
package com.flexganttfx.msproject.view;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.msproject.model.MSProjectTaskActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.CompletableActivityRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.sf.mpxj.Task;

public class MSProjectTaskActivityRenderer extends CompletableActivityRenderer<MSProjectTaskActivity> {

	public MSProjectTaskActivityRenderer(GraphicsBase<?> graphics) {
		super(graphics, "MSProject Task");
		setCornersRounded(true);
	}

	@Override
	public ActivityBounds drawActivity(ActivityRef<MSProjectTaskActivity> ref,
			Position position, GraphicsContext gc, double x, double y,
			double width, double height, boolean selected, boolean focused,
			boolean highlighted, boolean pressed) {

		if (ref.getRow().getChildren().isEmpty()) {
			setFill(Color.CORAL);
			setStroke(Color.CORAL.darker());
		} else {
			setFill(Color.CADETBLUE);
			setStroke(Color.CADETBLUE.darker());
		}

		ActivityBounds bounds = super.drawActivity(ref, position, gc, x, y, width, height, selected, focused, highlighted, pressed);

		MSProjectTaskActivity taskActivity = ref.getActivity();
		Task task = taskActivity.getUserObject().getTask();
		String resourceNames = task.getResourceNames();

		if (getGraphics().isDarkTheme()) {
			setTextFill(Color.WHITE);
			setTextFillHover(Color.WHITE);
			setTextFillHighlight(Color.YELLOW);
			setTextFillPressed(Color.WHITE);
			setTextFillSelected(Color.WHITE);
		} else {
			setTextFill(Color.BLACK);
			setTextFillHover(Color.BLACK);
			setTextFillHighlight(Color.BLACK);
			setTextFillPressed(Color.BLACK);
			setTextFillSelected(Color.BLACK);
		}

		drawText(ref, taskActivity.getName(), TextPosition.TRAILING, gc, x, y, width, height, selected, focused, highlighted, pressed);
		drawText(ref, resourceNames, TextPosition.LEADING, gc, x, y, width, height, selected, focused, highlighted, pressed);

		return bounds;
	}
}
