/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
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
