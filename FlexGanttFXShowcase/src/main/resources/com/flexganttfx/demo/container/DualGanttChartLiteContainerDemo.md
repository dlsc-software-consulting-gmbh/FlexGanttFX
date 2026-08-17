This demo focuses on the lite dual-chart container. It demonstrates how two synchronized `GanttChartLite` instances can be combined in a compact view while keeping their timeline behavior coordinated.


```java
GanttChartLite<DemoRow> gc1 = new GanttChartLite<>();
GanttChartLite<DemoRow> gc2 = new GanttChartLite<>();
DualGanttChartLiteContainer dual = new DualGanttChartLiteContainer(gc1, gc2);
```
