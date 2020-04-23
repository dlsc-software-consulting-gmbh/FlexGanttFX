/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.repository.ListActivityRepository;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HelloSystemLayers extends FlexGanttFXSample {

    private static final Layer layer = new Layer("Flights");

    private GanttChart<HelloRow> gc;

    @Override
    protected GanttChart<?> createGanttChart() throws FileNotFoundException {
        gc = new GanttChart<>();

        gc.getLayers().add(layer);

        gc.getTimeline().getModel().setHorizonStartTime(Instant.now().truncatedTo(ChronoUnit.DAYS).minus(2, ChronoUnit.DAYS));

        HelloRow row = new HelloRow("Row");
        row.setRepository(new ListActivityRepository<>(ListActivityRepository.IteratorType.SIMPLE_ITERATOR));

        HelloActivity activity1 = new HelloActivity("Item 1");
        HelloActivity activity2 = new HelloActivity("Item 2");
        HelloActivity activity3 = new HelloActivity("Item 3");

        activity1.setStartTime(Instant.now().plus(2, ChronoUnit.DAYS));
        activity1.setEndTime(Instant.now().plus(3, ChronoUnit.DAYS));
        activity2.setStartTime(Instant.now().plus(5, ChronoUnit.DAYS));
        activity2.setEndTime(Instant.now().plus(8, ChronoUnit.DAYS));
        activity3.setStartTime(Instant.now().plus(10, ChronoUnit.DAYS));
        activity3.setEndTime(Instant.now().plus(12, ChronoUnit.DAYS));

        row.addActivity(layer, activity1);
        row.addActivity(layer, activity2);
        row.addActivity(layer, activity3);

        gc.getGraphics().getBackgroundSystemLayers().add(new CustomLinksLayer(gc.getGraphics()));
        gc.getTimeline().showTime(Instant.now().plus(1, ChronoUnit.DAYS), false);
        gc.setRoot(row);

        return gc;
    }

    class CustomLinksLayer extends SystemLayer<HelloRow> {
        private Map<String, Rectangle2D> boundsMap = new HashMap<>();

        public CustomLinksLayer(GraphicsBase<HelloRow> graphicsView) {
            super("Links Layer", graphicsView);
        }

        @Override
        public void drawLayer(RowCanvas<HelloRow> canvas, Instant startTime, Instant endTime) {
            HelloRow row = canvas.getRow();
            if (row != null) {
                boundsMap.clear();

                GraphicsBase<HelloRow> graphics = canvas.getGraphics();
                Timeline timeline = graphics.getTimeline();
                Dateline dateline = timeline.getDateline();

                /*
                 * This is the height used by the default ActivityBarRenderer of
                 * FlexGanttFX. You will need to set this according to the
                 * height of your flights.
                 */
                int barHeight = 10;

                ActivityRepository<HelloActivity> repository = row.getRepository();
                TemporalUnit primaryTemporalUnit = dateline.getPrimaryTemporalUnit();

                /*
                 * I am only iterating over one (staticly defined) layer. In
                 * your code you will most likely have another loop here so that
                 * you find all activities on all layers.
                 */
                Iterator<HelloActivity> activities = repository.getActivities(layer, startTime, endTime, primaryTemporalUnit, row.getZoneId());
                while (activities.hasNext()) {
                    HelloActivity activity = activities.next();

                    String name = activity.getName();

                    double x1 = getLocation(activity.getStartTime(), canvas);
                    double x2 = getLocation(activity.getEndTime(), canvas);
                    double y1 = (canvas.getHeight() - barHeight) / 2;
                    double y2 = y1 + barHeight;

                    boundsMap.put(name, new Rectangle2D(x1, y1, x2 - x1, y2 - y1));
                }

                Rectangle2D bounds1 = boundsMap.get("Item 1");
                Rectangle2D bounds2 = boundsMap.get("Item 2");
                Rectangle2D bounds3 = boundsMap.get("Item 3");

                GraphicsContext gc = canvas.getGraphicsContext2D();

                if (bounds1 != null && bounds2 != null) {
                    gc.setStroke(Color.RED);
                    gc.strokeLine(bounds1.getMaxX(), bounds1.getMinY(), bounds2.getMinX(), bounds2.getMinY());
                }

                if (bounds2 != null && bounds3 != null) {
                    gc.setStroke(Color.BLUE);
                    gc.strokeLine(bounds2.getMaxX(), bounds2.getMaxY(), bounds3.getMinX(), bounds3.getMaxY());
                }
            }
        }
    }

    @Override
    public String getSampleName() {
        return "System Layers";
    }

    @Override
    public String getJavaDocURL() {
        return getJavaDocBase() + "com/flexganttfx/view/graphics/layer/SystemLayer.html";
    }

    @Override
    public String getSampleDescription() {
        return "This sample shows how to create a custom system layer for drawing "
                + "connecting lines between activities.";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
