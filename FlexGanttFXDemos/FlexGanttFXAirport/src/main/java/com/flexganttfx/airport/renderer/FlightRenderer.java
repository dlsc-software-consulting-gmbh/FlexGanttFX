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
package com.flexganttfx.airport.renderer;

import com.flexganttfx.airport.model.Flight;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renders flight blocks as dark navy bars with the flight number centred.
 */
public class FlightRenderer extends ActivityBarRenderer<Flight> {

    public FlightRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Flight Renderer");
        setFill(Color.DARKSLATEBLUE);
        setFillSelected(Color.SLATEBLUE);
        setFillHover(Color.MEDIUMSLATEBLUE);
        setStroke(Color.DARKSLATEBLUE.darker());
        setCornersRounded(false);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<Flight> activityRef,
                                       Position position, GraphicsContext gc,
                                       double x, double y, double w, double h,
                                       boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {
        ActivityBounds bounds = super.drawActivity(activityRef, position, gc,
                x, y, w, h, selected, hover, highlighted, pressed);

        drawText(activityRef, activityRef.getActivity().getName(),
                TextPosition.CENTER, gc, x, y, w, h,
                selected, hover, highlighted, pressed);

        return bounds;
    }
}
