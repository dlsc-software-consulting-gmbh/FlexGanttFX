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
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.calendar.WeekendCalendarActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WeekendCalendarActivityRenderer<A extends WeekendCalendarActivity>
		extends CalendarActivityRenderer<A> {

	public WeekendCalendarActivityRenderer(GraphicsBase<?> graphics, String name) {
		super(graphics, name);
		fillProperty().bindBidirectional(graphics.weekendColorProperty());
		alphaProperty().bindBidirectional(graphics.weekendOpacityProperty());
		setStroke(Color.TRANSPARENT);
	}
}
