This sample focuses on the `DualGanttChartContainer` and shows how a view with two synchronized Gantt charts can be created easily. It is useful for scenarios where two related schedules should scroll and zoom together while still being presented as separate charts.

```java
GanttChart<HelloRow> gc1 = new GanttChart<>();
GanttChart<HelloRow> gc2 = new GanttChart<>();
DualGanttChartContainer dual = new DualGanttChartContainer(gc1, gc2);
```
