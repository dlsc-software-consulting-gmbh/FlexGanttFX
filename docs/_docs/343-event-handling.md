# 3.4.3 Event Handling

- [Introduction](#introduction)
- [Activity Events](#activity-events)
- [Activity Events Hierarchy](#activity-events-hierarchy)
- [Activity Event Properties](#activity-event-properties)
- [Lasso Events](#lasso-events)
- [Lasso Event Hierarchy](#lasso-event-hierarchy)
- [Lasso Info](#lasso-info)
- [Links / Further Reading](#links-further-reading)

<a id="introduction"></a>

## Introduction

The [graphics view](https://flexgantt.atlassian.net/wiki/spaces/FFXMAN/pages/491612/3.5+GraphicsBase) fires standard JavaFX events in order to let applications react to change. The concepts used for event handler support in ***FlexGanttFX*** are the same as the ones found in the standard JavaFX controls. 

<a id="activity-events"></a>

## Activity Events

Activity events are fired whenever the user deletes or edits an activity. To receive an activity event simply register an event handler with the [graphics view](https://flexgantt.atlassian.net/wiki/spaces/FFXMAN/pages/491612/3.5+GraphicsBase) via one of the convenience methods.

**Single Activity Event Handler**

```
GraphicsBase<?> graphics = ganttChart.getGraphics();
graphics.setOnActivityChangeFinished(evt -> System.out.println("An activity has changed"));
```

If you need to register more than one handler for a specific event type then use this approach:

**Multiple Activity Event Handlers**

```
GraphicsBase<?> graphics = ganttChart.getGraphics();
graphics.addEventHandler(ActivityEvent.ACTIVITY_CHANGE_FINISHED, evt -> System.out.println("Listener 1"));
graphics.addEventHandler(ActivityEvent.ACTIVITY_CHANGE_FINISHED, evt -> System.out.println("Listener 2"));
```

The following table lists all supported activity event types and the convenience setter methods of the [graphics view](https://flexgantt.atlassian.net/wiki/spaces/FFXMAN/pages/491612/3.5+GraphicsBase). These methods are used to quickly register an event handler for the given event type.

| Event Types | Methods | Description |
| --- | --- | --- |
| ```<br>ACTIVITY_DELETED<br>``` | ```<br>setOnActivityDeleted()<br>``` | Fired whenever the user deletes an activity via the backspace key. |
| ```<br>ACTIVITY_CHANGE<br>``` | ```<br>setOnActivityChanged()<br>``` | The parent event type of all activity changes. Can be used to to receive a notification for any kind of activity change. |
| ```<br>ACTIVITY_CHANGE_STARTED<br>```<br>```<br>ACTIVITY_CHANGE_ONGOING<br>```<br>```<br>ACTIVITY_CHANGE_FINISHED<br>``` | ```<br>setOnActivityChangeStarted()<br>```<br>```<br>setOnActivityChangeOngoing()<br>```<br>```<br>setOnActivityChangeFinished()<br>``` | Fired whenever an activity change has started, is ongoing, or has finished. |
| ```<br>CHART_HIGH_VALUE_CHANGE_STARTED<br>```<br>```<br>CHART_HIGH_VALUE_CHANGE_ONGOING<br>```<br>```<br>CHART_HIGH_VALUE_CHANGE_FINISHED<br>``` | ```<br>setOnChartHighValueChangeStarted();<br>```<br>```<br>setOnChartHighValueChangeOngoing();<br>```<br>```<br>setOnChartHighValueChangeFinished();<br>``` | Fired whenever the user has started editing, is in the process of editing, or has finished editing the "high" value of a high / low chart activity. |
| ```<br>CHART_LOW_VALUE_CHANGE_STARTED<br>```<br>```<br>CHART_LOW_VALUE_CHANGE_ONGOING<br>```<br>```<br>CHART_LOW_VALUE_CHANGE_FINISHED<br>``` | ```<br>setOnChartLowValueChangeStarted();<br>```<br>```<br>setOnChartLowValueChangeOngoing();<br>```<br>```<br>setOnChartLowValueChangeFinished();<br>``` | Fired whenever the user has started editing, is in the process of editing, or has finished editing the "low" value of a high / low chart activity. |
| ```<br>CHART_VALUE_CHANGE_STARTED<br>```<br>```<br>CHART_VALUE_CHANGE_ONGOING<br>```<br>```<br>CHART_VALUE_CHANGE_FINISHED<br>``` | ```<br>setOnChartValueChangeStarted();<br>```<br>```<br>setOnChartValueChangeOngoing();<br>```<br>```<br>setOnChartValueChangeFinished();<br>``` | Fired whenever the user has started editing, is in the process of editing, or has finished editing a chart value of a chart activity. |
| ```<br>DRAG_STARTED<br>```<br>```<br>DRAG_ONGOING<br>```<br>```<br>DRAG_FINISHED<br>``` | ```<br>setOnActivityDragStarted();<br>```<br>```<br>setOnActivityDragOngoing();<br>```<br>```<br>setOnActivityDragFinished();<br>``` | Fired whenever the user has started dragging, is in the process of dragging, or has finished dragging an activity via platform-provided drag & drop. This event type is used when the user can freely move the activity around, vertically and horizontally. |
| ```<br>END_TIME_CHANGE_STARTED<br>```<br>```<br>END_TIME_CHANGE_ONGOING<br>```<br>```<br>END_TIME_CHANGE_FINISHED<br>``` | ```<br>setOnActivityEndTimeChangeStarted();<br>```<br>```<br>setOnActivityEndTimeChangeOngoing();<br>```<br>```<br>setOnActivityEndTimeChangeFinished();<br>``` | Fired whenever the user has started changing, is in the process of changing, or has finished changing the end time of an activity. |
| ```<br>HORIZONTAL_DRAG_STARTED<br>```<br>```<br>HORIZONTAL_DRAG_ONGOING<br>```<br>```<br>HORIZONTAL_DRAG_FINISHED<br>``` | ```<br>setOnActivityHorizontalDragStarted();<br>```<br>```<br>setOnActivityHorizontalDragOngoing();<br>```<br>```<br>setOnActivityHorizontalDragFinished();<br>``` | Fired whenever the user has started changing, is in the process of changing, or has finished changing the time interval (start and end time) of an activity. Changing this time interval makes the activity move horizontally, either to the right (future) or the left (past). |
| ```<br>PERCENTAGE_CHANGE_STARTED<br>```<br>```<br>PERCENTAGE_CHANGE_ONGOING<br>```<br>```<br>PERCENTAGE_CHANGE_FINISHED<br>``` | ```<br>setOnActivityPercentageChangeStarted();<br>```<br>```<br>setOnActivityPercentageChangeOngoing();<br>```<br>```<br>setOnActivityPercentageChangeFinished();<br>``` | Fired whenever the user has started changing, is in the process of changing, or has finished changing the "percentage complete" value of an activity. |
| ```<br>START_TIME_CHANGE_STARTED<br>```<br>```<br>START_TIME_CHANGE_ONGOING<br>```<br>```<br>START_TIME_CHANGE_FINISHED<br>``` | ```<br>setOnActivityStartTimeChangeStarted();<br>```<br>```<br>setOnActivityStartTimeChangeOngoing();<br>```<br>```<br>setOnActivityStartTimeChangeFinished();<br>``` | Fired whenever the user has started changing, is in the process of changing, or has finished changing the start time of an activity. |
| ```<br>VERTICAL_DRAG_STARTED<br>```<br>```<br>VERTICAL_DRAG_ONGOING<br>```<br>```<br>VERTICAL_DRAG_FINISHED<br>``` | ```<br>setOnActivityVerticalDragStarted();<br>```<br>```<br>setOnActivityVerticalDragOngoing();<br>```<br>```<br>setOnActivityVerticalDragFinished();<br>``` | Fired whenever the user has started dragging, is in the process of dragging, or has finished dragging an activity via platform-provided drag & drop. This event type is used when the user can only drag the activity vertically (reassign an activity to a different row). |

<a id="activity-events-hierarchy"></a>

## Activity Events Hierarchy

The event types defined in the **ActivityEvent** class are defining an event hierarchy. All events are input events (InputEvent.ANY) and they change the activity. Some of them get fired when the user starts the change, some while the change is ongoing, and some when the change is finished. 

- ```
InputEvent.ANY
```
  - ```
ACTIVITY_CHANGE
```
    - ```
ACTIVITY_DELETED
```
    - ```
ACTIVITY_CHANGE_STARTED  // All event types that signal "start"
```
      - ```
CHART_VALUE_CHANGE_STARTED
```
        - ```
CHART_HIGH_VALUE_CHANGE_STARTED
```
        - ```
CHART_LOW_VALUE_CHANGE_STARTED
```
      - ```
DRAG_STARTED
```
      - ```
END_TIME_CHANGE_STARTED
```
      - ```
HORIZONTAL_DRAG_STARTED
```
      - ```
PERCENTAGE_CHANGE_STARTED
```
      - ```
START_TIME_CHANGE_STARTED
```
      - ```
VERTICAL_DRAG_STARTED
```
    - ```
ACTIVITY_CHANGE_ONGOING // All event types that signal "ongoing"
```
      - ```
CHART_VALUE_CHANGE_ONGOING
```
        - ```
CHART_HIGH_VALUE_CHANGE_ONGOING
```
        - ```
CHART_LOW_VALUE_CHANGE_ONGOING
```
      - ```
DRAG_ONGOING
```
      - ```
END_TIME_CHANGE_ONGOING
```
      - ```
HORIZONTAL_DRAG_ONGOING
```
      - ```
PERCENTAGE_CHANGE_ONGOING
```
      - ```
START_TIME_CHANGE_ONGOING
```
      - ```
VERTICAL_DRAG_ONGOING
```
    - ```
ACTIVITY_CHANGE_FINISHED // All event types that signal "finished"
```
      - ```
CHART_VALUE_CHANGE_FINISHED
```
        - ```
CHART_HIGH_VALUE_CHANGE_FINISHED
```
        - ```
CHART_LOW_VALUE_CHANGE_FINISHED
```
      - ```
DRAG_FINISHED
```
      - ```
END_TIME_CHANGE_FINISHED
```
      - ```
HORIZONTAL_DRAG_FINISHED
```
      - ```
PERCENTAGE_CHANGE_FINISHED
```
      - ```
START_TIME_CHANGE_FINISHED
```
      - ```
VERTICAL_DRAG_FINISHED
```

<a id="activity-event-properties"></a>

## Activity Event Properties

Applications are obviously interested in the attributes of an activity. Not only the new values of these attributes (for example the new start time) but also the old values (start time before the change). The new values are already available on the activity as they are being set while the user performs the change. The old values are stored on the event object. The following table lists the methods on **ActivityEvent** to retrieve these values.

| Method | Description | Event Types |
| --- | --- | --- |
| ```<br>getOldTime()<br>``` | Returns the old start or end time of the activity. | ```<br>END_TIME_CHANGE_<br>```<br>```<br>START_TIME_CHANGE_<br>``` |
| ```<br>getOldTimeInterval()<br>``` | Returns the old start and end time of the activity. | ```<br>DRAG_<br>```<br>```<br>HORIZONTAL_DRAG_<br>```<br>```<br>VERTICAL_DRAG_<br>``` |
| ```<br>getOldRow()<br>``` | Returns the old row where the activity was located before. | ```<br>DRAG_<br>```<br>```<br>VERTICAL_DRAG_<br>``` |
| ```<br>getOldValue()<br>``` | Returns the old value of "percentage complete" or "chart value". | ```<br>CHART_VALUE_CHANGE_<br>```<br>```<br>CHART_HIGH_VALUE_<br>```<br>```<br>CHART_LOW_VALUE_<br>```<br>```<br>PERCENTAGE_CHANGE_<br>``` |

<a id="lasso-events"></a>

## Lasso Events

The user can use a lasso to select activities. Events are fired when this happens. To receive a lasso event simply register an event handler [with the graphics view](https://flexgantt.atlassian.net/wiki/spaces/FFXMAN/pages/491612/3.5+GraphicsBase) via one of the convenience methods.

**Singe Lasso Event Handler**

```
GraphicsBase<?> graphics = ganttChart.getGraphics();
graphics.setOnLassoFinished(evt -> System.out.println("The lasso was used"));
```

If you need to register more than one handler for a specific event type then use this approach:

**Multiple Lasso Event Handlers**

```
GraphicsBase<?> graphics = ganttChart.getGraphics();
graphics.addEventHandler(LassoEvent.SELECTION_FINISHED, evt -> System.out.println("Listener 1"));
graphics.addEventHandler(LassoEvent.SELECTION_FINISHED, evt -> System.out.println("Listener 2"));
```

The following table lists the event types and the convenience setter methods of the [graphics view](https://flexgantt.atlassian.net/wiki/spaces/FFXMAN/pages/491612/3.5+GraphicsBase).

| Event Type | Method | Description |
| --- | --- | --- |
| ```<br>ALL<br>``` | ```<br>setOnLassoSelection()<br>``` | Any lasso operation (start, ongoing, finished). |
| ```<br>SELECTION_STARTED<br>``` | ```<br>setOnLassoSelectionStarted()<br>``` | The user has pressed the mouse button and started a drag. The lasso has become visible. |
| ```<br>SELECTION_ONGOING<br>``` | ```<br>setOnLassoSelectionOngoing()<br>``` | The user is changing the size of the lasso. |
| ```<br>SELECTION_FINISHED<br>``` | ```<br>setOnLassoSelectionFinished()<br>``` | The user has finished the lasso selection. The lasso is no longer visible. |

<a id="lasso-event-hierarchy"></a>

## Lasso Event Hierarchy

 The event types defined in the **LassoEvent** class are defining an event hierarchy. All events are input events (InputEvent.ANY). 

- ```
InputEvent.ANY
```
  - ```
LassoEvent.ALL
```
    - ```
LassoEvent.SELECTION_STARTED
```
    - ```
LassoEvent.SELECTION_ONGOING
```
    - ```
LassoEvent.SELECTION_FINISHED
```

<a id="lasso-info"></a>

## Lasso Info

The lasso automatically performs selections of activities but sometimes we might want to know more about the exact nature of this selection or we want to use the lasso for another use case (e.g. for creating new activities). For this reason instances of **LassoEvent** also provide an object of type **LassoInfo**, which carries many attributes that the application can use to react accordingly. The lasso information can be retrieved by calling **LassoEvent.getInfo()**. The following table lists the attributes of **LassoInfo**.

| Method | Description |
| --- | --- |
| ```<br>List<ActivityRef<?>> getActivities();<br>``` | Returns all activities that were selected by the lasso. |
| ```<br>Instant getStartTime();<br>```<br>```<br>Instant getEndTime();<br>``` | Returns the start and end time of the lasso according to the location of the left and right edge of the lasso. |
| ```<br>LocalTime getLocalStartTime();<br>```<br>```<br>LocalTime getLocalEndTime();<br>``` | Returns the local start and end time. These values are only provided if the upper or lower edge of the lasso is located in an area that uses the [AgendaLayout](https://flexgantt.atlassian.net/wiki/spaces/FFXMAN/pages/492047/4.8.2+Agenda+Layout). |
| ```<br>List<Row<?,?,?>> getRows();<br>``` | Returns the rows that were touched by the lasso. |

<a id="links-further-reading"></a>

## Links / Further Reading

- [Oracle JavaFX documentation](http://docs.oracle.com/javase/8/javafx/events-tutorial/events.htm#jfxed117)
- [Event handling examples](http://code.makery.ch/blog/javafx-8-event-handling-examples/)