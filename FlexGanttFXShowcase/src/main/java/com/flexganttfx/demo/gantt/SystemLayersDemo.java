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
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.GanttChartDemoBase;
import com.flexganttfx.demo.DemoActivity;
import com.flexganttfx.demo.DemoRow;
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

public class SystemLayersDemo extends GanttChartDemoBase {

    private static final Layer layer = new Layer("Flights");

    private GanttChart<DemoRow> gc;

    @Override
    public void dispose() {
        super.dispose();
        gc = null;
    }

    @Override
    protected GanttChart<?> createGanttChart() throws FileNotFoundException {
        gc = new GanttChart<>();

        gc.getLayers().add(layer);

        gc.getTimeline().getModel().setHorizonStartTime(Instant.now().truncatedTo(ChronoUnit.DAYS).minus(2, ChronoUnit.DAYS));

        DemoRow row = new DemoRow("Row");
        row.setRepository(new ListActivityRepository<>(ListActivityRepository.IteratorType.SIMPLE_ITERATOR));

        DemoActivity activity1 = new DemoActivity("Item 1");
        DemoActivity activity2 = new DemoActivity("Item 2");
        DemoActivity activity3 = new DemoActivity("Item 3");

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

    static class CustomLinksLayer extends SystemLayer<DemoRow> {

        private final Map<String, Rectangle2D> boundsMap = new HashMap<>();

        public CustomLinksLayer(GraphicsBase<DemoRow> graphicsView) {
            super("Links Layer", graphicsView);
        }

        @Override
        public void drawLayer(RowCanvas<DemoRow> canvas, Instant startTime, Instant endTime) {
            DemoRow row = canvas.getRow();
            if (row != null) {
                boundsMap.clear();

                GraphicsBase<DemoRow> graphics = canvas.getGraphics();
                Timeline timeline = graphics.getTimeline();
                Dateline dateline = timeline.getDateline();

                /*
                 * This is the height used by the default ActivityBarRenderer of
                 * FlexGanttFX. You will need to set this according to the
                 * height of your flights.
                 */
                int barHeight = 10;

                ActivityRepository<DemoActivity> repository = row.getRepository();
                TemporalUnit primaryTemporalUnit = dateline.getPrimaryTemporalUnit();

                /*
                 * I am only iterating over one (statically defined) layer. In
                 * your code you will most likely have another loop here so that
                 * you find all activities on all layers.
                 */
                Iterator<DemoActivity> activities = repository.getActivities(layer, startTime, endTime, primaryTemporalUnit, row.getZoneId());
                while (activities.hasNext()) {
                    DemoActivity activity = activities.next();

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
    public String getName() {
        return "System Layers";
    }

    @Override
    public String getDescription() {
        return "This demo shows how to create a custom system layer for drawing "
                + "connecting lines between activities.";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
