This sample focuses on creating a dashboard-style view with four synchronized Gantt charts. It demonstrates how **FlexGanttFX** containers can keep multiple charts aligned while still allowing layout-level configuration such as animation and lower-pane visibility.

```java
QuadGanttChartContainer quad = new QuadGanttChartContainer();

GanttChart gc1 = new GanttChart();
GanttChart gc2 = new GanttChart();
GanttChart gc3 = new GanttChart();
GanttChart gc4 = new GanttChart();

quad.setUpperLeftGanttChart(gc1);
quad.setLowerLeftGanttChart(gc2);
quad.setUpperRightGanttChart(gc3);
quad.setLowerRightGanttChart(gc4);
```
