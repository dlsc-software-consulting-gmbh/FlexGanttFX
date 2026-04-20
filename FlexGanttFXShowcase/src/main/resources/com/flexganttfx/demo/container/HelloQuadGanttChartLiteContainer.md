This sample focuses on the lite quad-chart container. It shows how four `GanttChartLite` instances can be arranged into a synchronized grid when an application needs several coordinated chart views at once.

```java
QuadGanttChartLiteContainer quad = new QuadGanttChartLiteContainer();

GanttChartLite gc1 = new GanttChartLite();
GanttChartLite gc2 = new GanttChartLite();
GanttChartLite gc3 = new GanttChartLite();
GanttChartLite gc4 = new GanttChartLite();

quad.setUpperLeftGanttChart(gc1);
quad.setLowerLeftGanttChart(gc2);
quad.setUpperRightGanttChart(gc3);
quad.setLowerRightGanttChart(gc4);
```