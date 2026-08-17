This demo focuses on tooltip support for chart content. It demonstrates how activities can surface richer contextual information on hover to make complex schedules easier to inspect.

```java
public class ToolTipDemo extends GanttChartDemoBase {

	private static final Layer layer = new Layer("Flights");

	private GanttChart<DemoRow> gc;

	private Tooltip tooltip;
    
	@Override
	protected GanttChart<?> createGanttChart() {
		gc = new GanttChart<>();

		tooltip = new Tooltip("");

		gc.getGraphics().getListView().setTooltip(tooltip);

		gc.getLayers().add(layer);

		DemoRow row = new DemoRow("Row");

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

		gc.getTimeline().showTime(Instant.now().plus(1, ChronoUnit.DAYS), false);
		gc.setRoot(row);

		ListViewGraphics<DemoRow> graphics = gc.getGraphics();
		graphics.getListView().addEventHandler(MouseEvent.MOUSE_MOVED, this::mouseMoved);
		return gc;
	}

	private void mouseMoved(MouseEvent evt) {
		ActivityRef<?> ref = gc.getGraphics().getActivityRefAt(evt.getX(), evt.getY());
		if (ref != null) {
			tooltip.setText(ref.getActivity().getName());
		}
	}
}
```
