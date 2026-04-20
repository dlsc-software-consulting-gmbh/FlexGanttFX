This sample focuses on custom system layers. It shows how additional drawing logic can be placed on top of the normal activity rendering pipeline, here by drawing custom connecting lines between activities.

```java
public class HelloSystemLayers extends FlexGanttFXSample {

    private static final Layer layer = new Layer("Flights");

    private GanttChart<HelloRow> gc;
    
    @Override
    protected GanttChart<?> createGanttChart() {
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

    static class CustomLinksLayer extends SystemLayer<HelloRow> {

        private final Map<String, Rectangle2D> boundsMap = new HashMap<>();

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
                 * I am only iterating over one (statically defined) layer. In
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
}
```
