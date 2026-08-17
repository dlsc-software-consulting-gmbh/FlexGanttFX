This demo focuses on the standard `GanttChart` control with its combined tree-table and graphics area. It is a good starting point for understanding how rows, layers, activities, and the timeline work together in the full-featured chart variant.

```java
GanttChart gc = new GanttChart<>();

DemoRow root = new DemoRow("root");

Layer layer = new Layer("layer");
gc.getLayers().add(layer);
gc.setAutoHideScrollBar(false);

gc.getTimeline().getModel().setHorizonStartTime(ZonedDateTime.now().minusMonths(3).truncatedTo(ChronoUnit.DAYS).toInstant());
gc.getTimeline().getModel().setHorizonEndTime(ZonedDateTime.now().plusMonths(3).truncatedTo(ChronoUnit.DAYS).toInstant());

DemoActivity activity = new DemoActivity();
activity.setStartTime(Instant.now());
activity.setEndTime(Instant.now().plus(Duration.ofDays(7)));
root.addActivity(layer, activity);

for (int i = 0; i < 200; i++) {
    DemoRow row = new DemoRow("Row " + (i + 1));
    row.setHeight(20 + Math.random() * 100);
    root.getChildren().add(row);
}

gc.setRoot(root);
```
