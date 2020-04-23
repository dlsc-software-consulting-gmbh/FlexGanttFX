/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableChartActivity;
import com.flexganttfx.model.activity.MutableCompletableActivity;
import com.flexganttfx.model.activity.MutableHighLowChartActivity;
import com.flexganttfx.model.util.TimeInterval;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * Activity events are being fired whenever the user makes a change to an
 * activity in the graphics view. Event handlers for the event types listed in
 * this class can be registered on the {@link GraphicsBase} control.
 *
 * <h2>Code Example 1</h2>
 *
 * <pre>
 * GanttChart gantt = new GanttChart();
 * GraphicsView graphics = gantt.getGraphics();
 * graphics.setOnActivityChanged(evt -&gt; handleChange(evt));
 * </pre>
 *
 * <h2>Code Example 2</h2>
 *
 * <pre>
 * GanttChart gantt = new GanttChart();
 * GraphicsView graphics = gantt.getGraphics();
 * graphics.addEventHandler(ActivityEvent.ACTIVITY_CHANGED,
 * 		evt -&gt; handleChange(evt));
 * </pre>
 *
 * @see GraphicsBase#setOnActivityChange(javafx.event.EventHandler)
 *
 * @since 1.0
 */
public class ActivityEvent extends InputEvent {

	private static final long serialVersionUID = 6875000631749792169L;

	/**
	 * The parent event type of all other activity event types. Gets fired
	 * whenever anything changes.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> ACTIVITY_CHANGE = new EventType<>(InputEvent.ANY, "ACTIVITY_CHANGE");

	/**
	 * The parent event type of all STARTED activity event types. Gets fired
	 * whenever a change type is starting, e.g. DRAG_STARTED.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> ACTIVITY_CHANGE_STARTED = new EventType<>(ACTIVITY_CHANGE, "ACTIVITY_CHANGE");

	/**
	 * The parent event type of all ONGOING activity event types. Gets fired
	 * whenever a change type is ongoing, e.g. DRAG_ONGOING.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> ACTIVITY_CHANGE_ONGOING = new EventType<>(ACTIVITY_CHANGE, "ACTIVITY_CHANGE_ONGOING");

	/**
	 * The parent event type of all FINISHED activity event types. Gets fired
	 * whenever a change type is finishing, e.g. DRAG_FINISHED.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> ACTIVITY_CHANGE_FINISHED = new EventType<>(ACTIVITY_CHANGE, "ACTIVITY_CHANGE_FINISHED");

	/**
	 * An event type used when the user pressed the delete shortcut to delete an
	 * activity.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> ACTIVITY_DELETED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE, "ACTIVITY_DELETED");

	// --------- START TIME CHANGES --------- //

	/**
	 * An event type used when the user is about to change the start time of an
	 * activity.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> START_TIME_CHANGE_STARTED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_STARTED, "START_TIME_CHANGE_STARTED");

	/**
	 * An event type used when the start time of an activity has finished
	 * changing.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> START_TIME_CHANGE_FINISHED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_FINISHED, "START_TIME_CHANGE_FINISHED");

	/**
	 * An event type used when the start time of an activity is currently being
	 * changed.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> START_TIME_CHANGE_ONGOING = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_ONGOING, "START_TIME_CHANGE_ONGOING");

	// --------- END TIME CHANGES --------- //

	/**
	 * An event type used when the user starts changing the end time of an
	 * activity.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> END_TIME_CHANGE_STARTED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_STARTED, "END_TIME_CHANGE_STARTED");

	/**
	 * An event type used when the end time of an activity has finished
	 * changing.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> END_TIME_CHANGE_FINISHED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_FINISHED, "END_TIME_CHANGE_FINISHED");

	/**
	 * An event type used when the end time of an activity is currently being
	 * changed.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> END_TIME_CHANGE_ONGOING = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_ONGOING, "END_TIME_CHANGE_ONGOING");

	// --------- HORIZONTAL DRAG (within same row) --------- //

	/**
	 * An event type being used when the user has started dragging an activity
	 * within its row, which means that the start and end time will both change
	 * at the same time.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> HORIZONTAL_DRAG_STARTED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_STARTED, "HORIZONTAL_DRAG_STARTED");

	/**
	 * An event type that gets used when the user is in the process of dragging
	 * the activity within its row, changing the start and end time at the same
	 * time.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> HORIZONTAL_DRAG_ONGOING = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_ONGOING, "HORIZONTAL_DRAG_ONGOING");

	/**
	 * An event type being used when the user has finished dragging an activity
	 * within its row, which means that the start and end time have both changed
	 * at the same time.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> HORIZONTAL_DRAG_FINISHED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_FINISHED, "HORIZONTAL_DRAG_FINISHED");

	// --------- VERTICAL DRAG (from row to row) --------- //

	/**
	 * An event type being used when the user has started dragging an activity
	 * from one row to another while preserving the start and end time of the
	 * activity.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> VERTICAL_DRAG_STARTED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_STARTED, "VERTICAL_DRAG_STARTED");

	/**
	 * An event type that gets used when the user is in the process of dragging
	 * the activity from one row to another while preserving the start and end
	 * time of the activity.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> VERTICAL_DRAG_ONGOING = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_ONGOING, "VERTICAL_DRAG_ONGOING");

	/**
	 * An event type being used when the user has finished dragging an activity
	 * from one row to another while preserving the start and end time of the
	 * activity.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> VERTICAL_DRAG_FINISHED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_FINISHED, "VERTICAL_DRAG_FINISHED");

	/**
	 * An event type being used when the user has finished dragging an activity.
	 * This event does not necessarily mean that the activity was dropped onto
	 * another row. The drop might have ended outside the Gantt chart control
	 * and maybe even outside of JavaFX / the JVM.
	 *
	 * @since 1.2
	 */
	public static final EventType<ActivityEvent> VERTICAL_DRAG_DONE = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_FINISHED, "VERTICAL_DRAG_DONE");

	// --------- DRAG (from row to row) --------- //

	/**
	 * An event type being used when the user has started dragging an activity
	 * from one row to another and changing its start time at the same time
	 * (diagonal drag).
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> DRAG_STARTED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_STARTED, "DRAG_STARTED");

	/**
	 * An event type being used when the user is in the process of dragging an
	 * activity from one row to another and changing its start time at the same
	 * time (diagonal drag).
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> DRAG_ONGOING = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_ONGOING, "DRAG_ONGOING");

	/**
	 * An event type being used when the user has finished dragging an activity
	 * from one row to another and changing its start time at the same time
	 * (diagonal drag).
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> DRAG_FINISHED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_FINISHED, "DRAG_FINISHED");

	/**
	 * An event type being used when the user has finished dragging an activity.
	 * This event does not necessarily mean that the activity was dropped onto
	 * another row. The drop might have ended outside the Gantt chart control
	 * and maybe even outside of JavaFX / the JVM.
	 *
	 * @since 1.2
	 */
	public static final EventType<ActivityEvent> DRAG_DONE = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_FINISHED, "DRAG_DONE");

	// --------- PERCENTAGE COMPLETE CHANGE --------- //

	/**
	 * An event type being used when the user has started changing the
	 * percentage complete value of a {@link MutableCompletableActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> PERCENTAGE_CHANGE_STARTED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_STARTED, "PERCENTAGE_CHANGE_STARTED");

	/**
	 * An event type being used when the user has finished changing the
	 * percentage complete value of a {@link MutableCompletableActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> PERCENTAGE_CHANGE_FINISHED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_FINISHED, "PERCENTAGE_CHANGE_FINISHED");

	/**
	 * An event type being used when the user is in the process of changing the
	 * percentage complete value of a {@link MutableCompletableActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> PERCENTAGE_CHANGE_ONGOING = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_ONGOING, "PERCENTAGE_CHANGE_ONGOING");

	// --------- CHART VALUE CHANGES --------- //

	/**
	 * An event type being used when the user has started changing the chart
	 * value of a {@link MutableChartActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> CHART_VALUE_CHANGE_STARTED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_STARTED, "CHART_VALUE_CHANGE_STARTED");

	/**
	 * An event type being used when the user has finished changing the chart
	 * value of a {@link MutableChartActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> CHART_VALUE_CHANGE_FINISHED = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_FINISHED, "CHART_VALUE_CHANGE_FINISHED");

	/**
	 * An event type being used when the user is in the process of changing the
	 * chart value of a {@link MutableChartActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> CHART_VALUE_CHANGE_ONGOING = new EventType<>(ActivityEvent.ACTIVITY_CHANGE_ONGOING, "CHART_VALUE_CHANGE_ONGOING");

	// --------- CHART HIGH VALUE CHANGES --------- //

	/**
	 * An event type being used when the user has started changing the chart
	 * high value of a {@link MutableHighLowChartActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> CHART_HIGH_VALUE_CHANGE_STARTED = new EventType<>(ActivityEvent.CHART_VALUE_CHANGE_STARTED, "CHART_HIGH_VALUE_CHANGE_STARTED");

	/**
	 * An event type being used when the user has finished changing the chart
	 * high value of a {@link MutableHighLowChartActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> CHART_HIGH_VALUE_CHANGE_FINISHED = new EventType<>(ActivityEvent.CHART_VALUE_CHANGE_FINISHED, "CHART_HIGH_VALUE_CHANGE_FINISHED");

	/**
	 * An event type being used when the user is in the process of changing the
	 * chart high value of a {@link MutableHighLowChartActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> CHART_HIGH_VALUE_CHANGE_ONGOING = new EventType<>(ActivityEvent.CHART_VALUE_CHANGE_ONGOING, "CHART_HIGH_VALUE_CHANGE_ONGOING");

	// --------- CHART LOW VALUE CHANGES --------- //

	/**
	 * An event type being used when the user has started changing the chart low
	 * value of a {@link MutableHighLowChartActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> CHART_LOW_VALUE_CHANGE_STARTED = new EventType<>(ActivityEvent.CHART_VALUE_CHANGE_STARTED, "CHART_LOW_VALUE_CHANGE_STARTED");

	/**
	 * An event type being used when the user has finished changing the chart
	 * low value of a {@link MutableHighLowChartActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> CHART_LOW_VALUE_CHANGE_FINISHED = new EventType<>(ActivityEvent.CHART_VALUE_CHANGE_FINISHED, "CHART_LOW_VALUE_CHANGE_FINISHED");

	/**
	 * An event type being used when the user is in the process of changing the
	 * chart low value of a {@link MutableHighLowChartActivity}.
	 *
	 * @since 1.0
	 */
	public static final EventType<ActivityEvent> CHART_LOW_VALUE_CHANGE_ONGOING = new EventType<>(ActivityEvent.CHART_VALUE_CHANGE_ONGOING, "CHART_LOW_VALUE_CHANGE_ONGOING");

	private Instant oldTime;

	private TimeInterval oldTimeInterval;

	private Row<?, ?, ?> oldRow;

	private Row<?, ?, ?> newRow;

	private double oldValue;

	private final ActivityRef<?> activityRef;

	public ActivityEvent(ActivityRef<?> activity, EventTarget target,
			EventType<? extends ActivityEvent> eventType) {
		super(activity, target, eventType);

		requireNonNull(activity);
		this.activityRef = activity;
	}

	public ActivityEvent(ActivityRef<?> activity, EventTarget target,
			EventType<? extends ActivityEvent> eventType, Instant oldTime) {
		super(activity, target, eventType);

		requireNonNull(activity);
		requireNonNull(oldTime);

		this.activityRef = activity;
		this.oldTime = oldTime;
	}

	public ActivityEvent(ActivityRef<?> activity, EventTarget target,
			EventType<? extends ActivityEvent> eventType,
			TimeInterval oldInterval) {
		super(activity, target, eventType);

		requireNonNull(activity);
		requireNonNull(oldInterval);

		this.activityRef = activity;
		this.oldTimeInterval = oldInterval;
	}

	public ActivityEvent(ActivityRef<?> activity, EventTarget target,
			EventType<? extends ActivityEvent> eventType, Row<?, ?, ?> oldRow,
			Row<?, ?, ?> newRow, TimeInterval oldTimeInterval) {
		super(activity, target, eventType);

		requireNonNull(activity);
		requireNonNull(oldRow);
		requireNonNull(newRow);
		requireNonNull(oldTimeInterval);

		this.activityRef = activity;
		this.oldRow = oldRow;
		this.newRow = newRow;
		this.oldTimeInterval = oldTimeInterval;
	}

	public ActivityEvent(ActivityRef<?> activity, EventTarget target,
			EventType<? extends ActivityEvent> eventType, double oldValue) {
		super(activity, target, eventType);

		requireNonNull(activity);
		requireNonNull(oldValue);

		this.activityRef = activity;
		this.oldValue = oldValue;
	}

	public final ActivityRef<?> getActivityRef() {
		return activityRef;
	}

	/**
	 * Returns the end / start time of an activity before the user performed a
	 * change on it.
	 *
	 * @return the original time (e.g. start or end time)
	 *
	 * @since 1.0
	 */
	public final Instant getOldTime() {
		return oldTime;
	}

	/**
	 * Returns the time interval of an activity before the user performed a
	 * change on it.
	 *
	 * @return the original time interval
	 *
	 * @since 1.0
	 */
	public final TimeInterval getOldTimeInterval() {
		return oldTimeInterval;
	}

	/**
	 * Returns the parent row of an activity after the user performed a change
	 * on it (e. g. a vertical drag).
	 *
	 * @return the new parent row
	 *
	 * @since 1.3
	 */
	public final Row<?, ?, ?> getNewRow() {
		return newRow;
	}

	/**
	 * Returns the parent row of an activity before the user performed a change
	 * on it (e. g. a vertical drag).
	 *
	 * @return the original parent row
	 *
	 * @since 1.0
	 */
	public final Row<?, ?, ?> getOldRow() {
		return oldRow;
	}

	/**
	 * Returns the value of an activity before the user performed a change on
	 * it.
	 *
	 * @return the original value (e.g. a chart value or percentage complete)
	 *
	 * @since 1.0
	 */
	public final double getOldValue() {
		return oldValue;
	}

	/**
	 * Returns the entire affected time interval of the change. The method
	 * calculates the time interval of the old location of the activity and the
	 * time interval of the new location of the activity. The result is the
	 * union of these two intervals. This method is useful if the application
	 * needs to recompute data for the "affected" region.
	 *
	 * @return the time interval affected by the event
	 *
	 * @since 1.3
	 */
	public final TimeInterval getAffectedTimeInterval() {
		Activity activity = getActivityRef().getActivity();

		if (isPotentialEndTimeChange()) {
			Instant et1 = activity.getEndTime();
			Instant et2 = getOldTime();
			if (et1.isBefore(et2)) {
				return new TimeInterval(activity.getStartTime(), et2);
			} else {
				return new TimeInterval(activity.getStartTime(), et1);
			}
		} else if (isPotentialStartTimeChange()) {
			Instant st1 = activity.getStartTime();
			Instant st2 = getOldTime();
			if (st1.isBefore(st2)) {
				return new TimeInterval(st1, activity.getEndTime());
			} else {
				return new TimeInterval(st2, activity.getEndTime());
			}

		} else if (isPotentialTimeIntervalChange()) {
			Instant st1 = activity.getStartTime();
			Instant st2 = getOldTimeInterval().getStartTime();

			Instant et1 = activity.getEndTime();
			Instant et2 = getOldTimeInterval().getEndTime();

			Instant st;
			if (st1.isBefore(st2)) {
				st = st1;
			} else {
				st = st2;
			}

			Instant et;
			if (et1.isBefore(et2)) {
				et = et2;
			} else {
				et = et1;
			}

			return new TimeInterval(st, et);
		}

		return new TimeInterval(activity.getStartTime(), activity.getEndTime());
	}

	/**
	 * Determines if the event represents a change of the activity's end time.
	 * This is the case for the event types {@link #END_TIME_CHANGE_STARTED},
	 * {@link #END_TIME_CHANGE_ONGOING}, and {@link #END_TIME_CHANGE_FINISHED}.
	 *
	 * @return true if the activity's end time might be affected by the event
	 *
	 * @since 1.3
	 */
	public final boolean isPotentialEndTimeChange() {
		EventType<? extends InputEvent> type = getEventType();
		return type.equals(END_TIME_CHANGE_STARTED)
				|| type.equals(END_TIME_CHANGE_ONGOING)
				|| type.equals(END_TIME_CHANGE_FINISHED);
	}

	/**
	 * Determines if the event represents a change of the activity's start time.
	 * This is the case for the event types {@link #START_TIME_CHANGE_STARTED},
	 * {@link #START_TIME_CHANGE_ONGOING}, and
	 * {@link #START_TIME_CHANGE_FINISHED}.
	 *
	 * @return true if the activity's start time might be affected by the event
	 *
	 * @since 1.3
	 */
	public final boolean isPotentialStartTimeChange() {
		EventType<? extends InputEvent> type = getEventType();
		return type.equals(START_TIME_CHANGE_STARTED)
				|| type.equals(START_TIME_CHANGE_ONGOING)
				|| type.equals(START_TIME_CHANGE_FINISHED);
	}

	/**
	 * Determines if the event represents a change of the activity's time
	 * interval (start and / or end time). This is the case for the event types
	 * {@link #HORIZONTAL_DRAG_STARTED}, {@link #HORIZONTAL_DRAG_ONGOING},
	 * {@link #HORIZONTAL_DRAG_FINISHED}, {@link #DRAG_STARTED},
	 * {@link #DRAG_ONGOING}, {@link #DRAG_FINISHED}.
	 *
	 * @return true if the activity's time interval might be affected by the
	 *         event
	 *
	 * @since 1.3
	 */
	public final boolean isPotentialTimeIntervalChange() {
		EventType<? extends InputEvent> type = getEventType();
		return type.equals(HORIZONTAL_DRAG_STARTED)
				|| type.equals(HORIZONTAL_DRAG_ONGOING)
				|| type.equals(HORIZONTAL_DRAG_FINISHED)
				|| type.equals(DRAG_STARTED) || type.equals(DRAG_ONGOING)
				|| type.equals(DRAG_FINISHED);
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("event type: ");
		sb.append(getEventType());
		sb.append(", time interval: ");
		if (getOldTimeInterval() != null) {
			sb.append(getOldTimeInterval());
		} else {
			sb.append("[]");
		}
		sb.append(", value (chart value / percentage complete): ");
		sb.append(getOldValue());
		sb.append(", ");
		sb.append(getActivityRef());
		return sb.toString();
	}
}
