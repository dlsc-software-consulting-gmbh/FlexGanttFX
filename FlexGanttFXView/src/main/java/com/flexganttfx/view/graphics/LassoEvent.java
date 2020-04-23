/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.AgendaLayout;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Lasso events are being fired whenever the user uses the lasso tool to select
 * a time interval on one or more rows.
 *
 * <h2>Code Example 1</h2>
 *
 * <pre>
 * GanttChart gantt = new GanttChart();
 * GraphicsView graphics = gantt.getGraphics();
 * graphics.setOnLassoSelectionStarted(evt -&gt; handleChange(evt));
 * </pre>
 *
 * <h2>Code Example 2</h2>
 *
 * <pre>
 * GanttChart gantt = new GanttChart();
 * GraphicsView graphics = gantt.getGraphics();
 * graphics.addEventHandler(LassoEvent.SELECTION_STARTED,
 * 		evt -&gt; handleChange(evt));
 * </pre>
 *
 * @see GraphicsBase#setOnLassoSelectionStarted(javafx.event.EventHandler)
 *
 * @since 1.0
 */
public class LassoEvent extends InputEvent {

	private static final long serialVersionUID = -79848169297372705L;

	/**
	 * The parent event type of all other lasso event types. Gets fired whenever
	 * anything changes.
	 *
	 * @since 1.0
	 */
	public static final EventType<LassoEvent> ALL = new EventType<>(
			InputEvent.ANY, "ALL");

	/**
	 * An event type used when the user starts a selection with the lasso.
	 *
	 * @since 1.0
	 */
	public static final EventType<LassoEvent> SELECTION_STARTED = new EventType<>(
			LassoEvent.ALL, "SELECTION_STARTED");

	/**
	 * An event type used when the user finishes a selection with the lasso.
	 *
	 * @since 1.0
	 */
	public static final EventType<LassoEvent> SELECTION_FINISHED = new EventType<>(
			LassoEvent.ALL, "SELECTION_FINISHED");

	/**
	 * An event type used while the user is using the lasso to perform a
	 * selection.
	 *
	 * @since 1.0
	 */
	public static final EventType<LassoEvent> SELECTION_ONGOING = new EventType<>(
			LassoEvent.ALL, "SELECTION_ONGOING");

	private final LassoInfo info;

	/**
	 * Constructs a new event object.
	 *
	 * @param target
	 *            the graphics control where the event originated
	 * @param eventType
	 *            the type of the event
	 * @param info
	 *            detailed information about the lasso operation (selected rows,
	 *            selected time interval, etc...)
	 */
	public LassoEvent(GraphicsBase<?> target,
			EventType<? extends LassoEvent> eventType, LassoInfo info) {
		super(target, target, eventType);

		this.info = info;
	}

	/**
	 * Returns the detailed information about the lasso operation (selected
	 * rows, selected time interval, etc...).
	 *
	 * @return the lasso event information
	 */
	public final LassoInfo getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return ("type = " + getEventType()) + ", source = " + getSource() + ", target = " + getTarget() + ", info = " + getInfo();
	}

	/**
	 * Stores information about the last lasso operation performed by the user.
	 * This object stores the selected rows, the selected time interval, the
	 * activities found inside the lasso.
	 */
	public final static class LassoInfo {

		private final Instant startTime;
		private final Instant endTime;
		private final LocalTime localStartTime;
		private final LocalTime localEndTime;
		private final List<Row<?, ?, ?>> rows;
		private final List<ActivityRef<?>> activities;
		private final MouseEvent mouseEvent;

		/**
		 * Constructs a new info object.
		 *
		 * @param mouseEvent
		 *            the mouse event that triggered the lasso event
		 * @param startTime
		 *            the beginning of the lasso
		 * @param endTime
		 *            the end of the lasso
		 * @param localStartTime
		 *            the agenda start time (if used in combination with
		 *            {@link AgendaLayout})
		 * @param localEndTime
		 *            the agenda end time (if used in combination with
		 *            {@link AgendaLayout})
		 * @param rows
		 *            the rows that were selected
		 * @param activities
		 *            the activities inside the lasso
		 */
		public LassoInfo(MouseEvent mouseEvent, Instant startTime, Instant endTime,
				LocalTime localStartTime, LocalTime localEndTime,
				List<Row<?, ?, ?>> rows, List<ActivityRef<?>> activities) {

			this.mouseEvent = Objects.requireNonNull(mouseEvent);
			this.startTime = Objects.requireNonNull(startTime);
			this.endTime = Objects.requireNonNull(endTime);
			this.localStartTime = localStartTime;
			this.localEndTime = localEndTime;
			this.rows = rows;
			this.activities = activities;

		}

		/**
		 * Returns the mouse event that triggered the lasso event.
		 *
		 * @return the source (mouse) event
		 */
		public MouseEvent getMouseEvent() {
			return mouseEvent;
		}

		/**
		 * Returns the start time of the lasso / the beginning.
		 *
		 * @return the lasso start time
		 */
		public final Instant getStartTime() {
			return startTime;
		}

		/**
		 * Returns the end time of the lasso / the end.
		 *
		 * @return the lasso end time
		 */
		public final Instant getEndTime() {
			return endTime;
		}

		/**
		 * Returns the agenda start time of the lasso when the lasso is used in
		 * combination with the {@link AgendaLayout}.
		 *
		 * @return the agenda start time
		 */
		public final LocalTime getLocalStartTime() {
			return localStartTime;
		}

		/**
		 * Returns the agenda end time of the lasso when the lasso is used in
		 * combination with the {@link AgendaLayout}.
		 *
		 * @return the agenda end time
		 */
		public final LocalTime getLocalEndTime() {
			return localEndTime;
		}

		/**
		 * Returns the rows with which the lasso intersects.
		 *
		 * @return the affected rows.
		 */
		public final List<Row<?, ?, ?>> getRows() {
			return rows;
		}

		/**
		 * Returns the activities found inside the lasso.
		 *
		 * @return the selected activities.
		 */
		public final List<ActivityRef<?>> getActivities() {
			return activities;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("start time = ").append(startTime);
			sb.append(", end time = ").append(endTime);
			sb.append(", local start time = ").append(localStartTime);
			sb.append(", local end time = ").append(localEndTime);
			if (rows != null) {
				sb.append(", rows = ").append(Arrays.toString(rows.toArray()));
			}
			if (activities != null) {
				sb.append(", activities = ").append(Arrays.toString(activities.toArray()));
			}

			return sb.toString();
		}
	}
}
