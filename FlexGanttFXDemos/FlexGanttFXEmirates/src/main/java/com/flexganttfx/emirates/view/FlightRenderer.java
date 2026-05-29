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
package com.flexganttfx.emirates.view;

import com.flexganttfx.emirates.model.Flight;
import com.flexganttfx.emirates.model.Flight.ServiceType;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.EnumMap;

public class FlightRenderer extends ActivityBarRenderer<Flight> {

    private final EnumMap<ServiceType, Color> servicePaintMap = new EnumMap<>(
            ServiceType.class);

    public FlightRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Flight");

        servicePaintMap.put(ServiceType.E, Color.ALICEBLUE);
        servicePaintMap.put(ServiceType.J, Color.BURLYWOOD);
        servicePaintMap.put(ServiceType.N, Color.BLANCHEDALMOND);
        servicePaintMap.put(ServiceType.Y, Color.CORNSILK);
        servicePaintMap.put(ServiceType.Z, Color.CRIMSON);

        setCornersRounded(false);
    }

    @Override
    public ActivityBounds drawActivity(ActivityRef<Flight> activityRef,
                                       Position position, GraphicsContext gc, double x, double y,
                                       double w, double h, boolean selected, boolean hover,
                                       boolean highlighted, boolean pressed) {

        Color color = servicePaintMap.get(activityRef.getActivity().getServiceType());

        setFill(color);
        setStroke(color.darker());

        ActivityBounds bounds = super.drawActivity(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);

        Flight flight = activityRef.getActivity();

        drawText(activityRef, flight.getFlightNo(), TextPosition.CENTER, gc, x, y, w, h, selected, hover, highlighted, pressed);

        if (h >= 40) {
            drawText(activityRef, flight.getServiceType().toString(),
                    TextPosition.ABOVE_LEFT, gc, x, y, w, h, selected, hover,
                    highlighted, pressed);

            if (w >= 50) {
                drawText(activityRef, flight.getDepartureAirport(),
                        TextPosition.BELOW_LEFT, gc, x, y, w, h, selected,
                        hover, highlighted, pressed);

                drawText(activityRef, flight.getArrivalAirport(),
                        TextPosition.BELOW_RIGHT, gc, x, y, w, h, selected,
                        hover, highlighted, pressed);
            }
        }

        return bounds;
    }
}
