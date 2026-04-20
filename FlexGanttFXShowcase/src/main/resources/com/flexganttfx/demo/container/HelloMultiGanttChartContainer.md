This sample focuses on a container with several synchronized Gantt charts. It shows how multiple related views can be shown or hidden while still behaving like one coordinated scheduling surface.

```java
MultiGanttChartContainer container = new MultiGanttChartContainer();

GanttChart masterGC = new GanttChart<>(new DemoRow("Master"));
GanttChart gc1 = new GanttChart<>(new DemoRow("Gantt 1"));
GanttChart gc2 = new GanttChart<>(new DemoRow("Gantt 2"));
GanttChart gc3 = new GanttChart<>(new DemoRow("Gantt 3"));
        
container.getGanttCharts().addAll(masterGC, gc1, gc2, gc3);
```
