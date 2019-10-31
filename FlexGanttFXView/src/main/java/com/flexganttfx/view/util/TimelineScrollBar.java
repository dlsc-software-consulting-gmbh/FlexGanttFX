/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.util;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.controlsfx.control.PlusMinusSlider;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.timeline.Timeline;

/**
 * A specialized {@link PlusMinusSlider} for controlling the {@link Timeline} inside the
 * {@link GanttChart} view. Updates the start time property of the underlying
 * {@link TimelineModel}.
 *
 * @see TimelineModel#setStartTime(Instant)
 */
public class TimelineScrollBar extends PlusMinusSlider {

	/**
	 * Constructs a new scrollbar.
	 */
	public TimelineScrollBar() {
		getStyleClass().add("time-slider");

		getStylesheets().add(
				GanttChart.class.getResource("gantt.css").toExternalForm());

		addEventHandler(
				PlusMinusEvent.VALUE_CHANGED,
				evt -> {
					long st = getTimeline().getVisibleStartTime().toEpochMilli();
					long et = getTimeline().getVisibleEndTime().toEpochMilli();

					long delta = et - st;

					double value = evt.getValue();

					long millis = (long) (value * delta) / 10;

					TimelineModel<?> model = getTimeline().getModel();
					Instant time = model.getStartTime().plus(
							Duration.ofMillis(millis));

					if (LoggingDomain.NAVIGATION.isLoggable(Level.FINER)) {
						LoggingDomain.NAVIGATION.finer("visible start time: "
								+ st);
						LoggingDomain.NAVIGATION.finer("visible end time: "
								+ et);
						LoggingDomain.NAVIGATION
								.finer("plus minus slider value: " + value);
						LoggingDomain.NAVIGATION
								.finer("setting new time on timeline model to "
										+ time);
					}

					model.setStartTime(time);
				});
	}

	private final ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>(this, "timeline");

	/**
	 * Stores a reference to the timeline that will be controlled by this scrollbar.
	 *
	 * @return the timeline property
	 * @since 1.6.1
	 */
	public final ObjectProperty<Timeline> timelineProperty() {
		return timeline;
	}

	/**
	 * Returns the value of {@link #timelineProperty()}.
	 *
	 * @return the controlled timeline
	 * @since 1.6.1
	 */
	public final Timeline getTimeline() {
		return timeline.get();
	}

	/**
	 * Sets the value of {@link #timelineProperty()}.
	 *
	 * @param timeline the timeline that will be controlled by this scrollbar
	 * @since 1.6.1
	 */
	public final void setTimeline(Timeline timeline) {
		this.timeline.set(timeline);
	}
}
