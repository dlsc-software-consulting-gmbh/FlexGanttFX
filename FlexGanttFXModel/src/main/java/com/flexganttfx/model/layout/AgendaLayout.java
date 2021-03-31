/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.layout;

import com.flexganttfx.model.Layout;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.Duration;
import java.time.LocalTime;

import static java.util.Objects.requireNonNull;

/**
 * The agenda layout class is used to lay out activities in a style similar to a
 * regular calendar where a vertical scale will display hours. Activities are
 * used to represent appointments for a given day.
 * <p>
 * Note: Activities shown in agenda layout might be rendered several times. This
 * is, for example, the case when an activity spans several days. <img
 * src="doc-files/layout-agenda.png" alt="Agenda Layout">
 *
 * @see Row#setLayout(Layout)
 * @see Row#getLineLayout(int)
 * @see LinesManager#getLineLayout(int)
 *
 * @since 1.0
 */
public class AgendaLayout extends Layout {

	// TODO: javadoc: add image to show the difference between overlapping and
	// parallel

	/**
	 * An enumerator for the different ways overlapping agenda entries can be
	 * laid out.
	 *
	 * @since 1.0
	 */
	public enum LayoutStrategy {

		/**
		 * Intersecting agenda entries will be drawn on top of each other but
		 * with one of them being indented by a couple of pixels (see
		 * {@link AgendaLayout#setOverlapOffset(double)}).
		 *
		 * @since 1.0
		 */
		OVERLAPPING,

		/**
		 * Intersecting agenda entries will be displayed in different columns
		 * within the same day.
		 *
		 * @since 1.0
		 */
		PARALLEL,

		/**
		 * Intersecting agenda entries will be displayed in different columns
		 * within the same day but overlapping each other.
		 *
		 * @since 1.0
		 */
		PARALLEL_OVERLAPPING
	}

	/**
	 * Constructs a new agenda layout instance.
	 *
	 * @since 1.0
	 */
	public AgendaLayout() {
		setPadding(10);
	}

	// Line spacing support.

	private final DoubleProperty minLineSpacing = new SimpleDoubleProperty(this, "minLineSpace", 20);

	/**
	 * Stores the minimum space between two hour lines.
	 *
	 * @return the minimum space between hour lines
	 */
	public final DoubleProperty minLineSpacingProperty() {
		return minLineSpacing;
	}

	/**
	 * Sets the value of {@link #minLineSpacingProperty()}.
	 *
	 * @param min the minimum space between hour lines
	 */
	public final void setMinLineSpacing(double min) {
		minLineSpacing.set(min);
	}

	/**
	 * Returns the value of {@link #minLineSpacingProperty()}.
	 *
	 * @return the minimum space between hour lines
	 */
	public final double getMinLineSpacing() {
		return minLineSpacing.get();
	}

	// Agenda support.

	private ObjectProperty<LocalTime> startTime = new SimpleObjectProperty<>(this, "startTime", LocalTime.MIN);

	/**
	 * Returns the property used for storing the start time of the agenda. The
	 * start time is used for the vertical time scale. The default value of this
	 * property is {@link LocalTime#MIN}, which is equivalent to midnight.
	 * <img src="doc-files/scale-agenda.png" alt="Agenda Scale">
	 *
	 * @return the start time used for the agenda layout
	 * @since 1.0
	 */
	public final ObjectProperty<LocalTime> startTimeProperty() {
		return startTime;
	}

	/**
	 * Returns the value of the {@link #startTimeProperty()}.
	 *
	 * @return the agenda start time
	 * @since 1.0
	 */
	public final LocalTime getStartTime() {
		return startTimeProperty().get();
	}

	/**
	 * Sets the value of the {@link #startTimeProperty()}.
	 *
	 * @param time
	 *            the new agenda start time
	 * @since 1.0
	 */
	public final void setStartTime(LocalTime time) {
		// TODO: add checks to verify that end time is AFTER start time
		startTimeProperty().set(time);
	}

	private ObjectProperty<LocalTime> endTime = new SimpleObjectProperty<>(this, "endTime", LocalTime.MAX);

	/**
	 * Returns the property used for storing the end time of the agenda. The end
	 * time is used for the vertical time scale. The default value of this
	 * property is {@link LocalTime#MAX}, which is equivalent to the time just
	 * before midnight. <img src="doc-files/scale-agenda.png" alt="Agenda Scale">
	 *
	 * @return the start time used for the agenda layout
	 * @since 1.0
	 */
	public final ObjectProperty<LocalTime> endTimeProperty() {
		return endTime;
	}

	/**
	 * Returns the value of the {@link #endTimeProperty()}.
	 *
	 * @return the agenda end time
	 * @since 1.0
	 */
	public final LocalTime getEndTime() {
		return endTimeProperty().get();
	}

	/**
	 * Sets the value of {@link #endTimeProperty()}.
	 *
	 * @param time
	 *            the new agenda end time
	 * @since 1.0
	 */
	public final void setEndTime(LocalTime time) {
		// TODO: add checks to verify that end time is AFTER start time
		endTimeProperty().set(time);
	}

	private ObjectProperty<Duration> minDuration = new SimpleObjectProperty<Duration>(this, "minDuration", Duration.ofMinutes(15)) {

		@Override
		public void set(Duration duration) {
			if (duration == null) {
				throw new IllegalArgumentException("duration can not be null");
			}
			if (duration.equals(Duration.ZERO)) {
				throw new IllegalArgumentException(
						"the minimum duration must be larger than zero");
			}
			super.set(duration);
		}
	};

	/**
	 * A property used to store the minimum duration of activities in the agenda
	 * layout.
	 *
	 * @return the minimum duration of activities
	 * @since 1.0
	 */
	public final ObjectProperty<Duration> minDurationProperty() {
		return minDuration;
	}

	/**
	 * Returns the value of the {@link #minDurationProperty()}.
	 *
	 * @return the minimum duration of agenda activities
	 * @since 1.0
	 */
	public final Duration getMinDuration() {
		return minDurationProperty().get();
	}

	/**
	 * Sets the value of the {@link #minDurationProperty()}.
	 *
	 * @param duration
	 *            the new minimum duration of agenda activities
	 * @since 1.0
	 */
	public final void setMinDuration(Duration duration) {
		requireNonNull(duration);
		minDurationProperty().set(duration);
	}

	private final ObjectProperty<LayoutStrategy> layoutStrategy = new SimpleObjectProperty<>(
			this, "layoutStrategy", LayoutStrategy.PARALLEL);

	/**
	 * The property used to store the strategy that will be applied when the
	 * time intervals of activities in agenda layout intersect with each other.
	 * The strategy determines if the overlapping activities will be drawn on
	 * top of each other or in parallel (swim lanes).
	 *
	 * @return the layout strategy property
	 * @since 1.0
	 */
	public final ObjectProperty<LayoutStrategy> layoutStrategyProperty() {
		return layoutStrategy;
	}

	/**
	 * Returns the value of the {@link #layoutStrategyProperty()}.
	 *
	 * @return the currently used layout strategy
	 * @since 1.0
	 */
	public final LayoutStrategy getLayoutStrategy() {
		return layoutStrategyProperty().get();
	}

	/**
	 * Sets the value of the {@link #layoutStrategyProperty()}.
	 *
	 * @param strategy
	 *            the layout strategy to use
	 * @since 1.0
	 */
	public final void setLayoutStrategy(LayoutStrategy strategy) {
		requireNonNull(strategy);
		layoutStrategyProperty().set(strategy);
	}

	// Overlap insets

	private final DoubleProperty overlapOffset = new SimpleDoubleProperty(this,
			"overlapOffset", .25);

	/**
	 * A property used to store an offset value between 0 and .5 that is used to
	 * indent activities when they overlap each other. The value must be between
	 * 0 and .5, which will be interpreted as a percentage of the width of the
	 * agenda entries. A value of .5 indicates that half the width of an entry
	 * will be overlapped by another one.
	 *
	 * @return the offset in pixels
	 * @since 1.0
	 */
	public final DoubleProperty overlapOffsetProperty() {
		return overlapOffset;
	}

	/**
	 * Sets the value of {@link #overlapOffsetProperty()}. The value must be
	 * between 0 and .5, which will be interpreted as a percentage of the width
	 * of the agenda entries. A value of .5 indicates that half the width of an
	 * entry will be overlapped by another one.
	 *
	 * @param offset
	 *            the offset in pixels
	 * @since 1.0
	 */
	public final void setOverlapOffset(double offset) {
		if (offset < 0 || offset > .5) {
			throw new IllegalArgumentException(
					"offset must be between 0 and .5");
		}

		overlapOffsetProperty().set(offset);
	}

	/**
	 * Returns the value of {@link #overlapOffsetProperty()}.
	 *
	 * @return the offset in pixels
	 * @since 1.0
	 */
	public final double getOverlapOffset() {
		return overlapOffsetProperty().get();
	}

	@Override
	public boolean isSupportingHorizontalCursorLine() {
	    return true;
	}


	@Override
    public String toString() {
        return "AgendaLayout [minLineSpacing=" + getMinLineSpacing() + ", startTime="
                + getStartTime() + ", endTime=" + getEndTime() + ", minDuration="
                + getMinDuration() + ", layoutStrategy=" + getLayoutStrategy()
                + ", overlapOffset=" + getOverlapOffset() + "]";
    }
}
