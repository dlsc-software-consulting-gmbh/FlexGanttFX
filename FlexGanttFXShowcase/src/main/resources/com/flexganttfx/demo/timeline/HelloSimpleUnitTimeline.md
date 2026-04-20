This sample focuses on the simple-unit timeline model that uses decimal-style units such as one, ten, hundred, and thousand. It demonstrates how **FlexGanttFX** can visualize timelines that are not tied to real calendar units.

```java
Timeline timeline = new Timeline();
timeline.getModel().setNow(Instant.ofEpochMilli(0));
timeline.setModel(new SimpleUnitTimelineModel());
timeline.getDateline().setModel(new SimpleUnitDatelineModel());
```
