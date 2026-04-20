This sample focuses on the lite version of the multi-chart container. It demonstrates how several `GanttChartLite` views can be composed into a synchronized layout for lightweight multi-view scenarios.

```java
MultiGanttChartLiteContainer container = new MultiGanttChartLiteContainer();

GanttChartLite masterGC = new GanttChartLite<>(new DemoRow("Master"));
GanttChartLite gc1 = new GanttChartLite<>(new DemoRow("Gantt 1"));
GanttChartLite gc2 = new GanttChartLite<>(new DemoRow("Gantt 2"));
GanttChartLite gc3 = new GanttChartLite<>(new DemoRow("Gantt 3"));
        
container.getGanttCharts().addAll(masterGC, gc1, gc2, gc3);
```
