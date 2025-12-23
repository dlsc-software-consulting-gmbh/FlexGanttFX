/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.timeline;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;

import static java.util.Objects.requireNonNull;

/**
 * The timeline model stores the most important properties related to navigating
 * within time (move forward and backward in time, zoom in and out).
 * <ul>
 * <li>Now - the "current" time (e.g. system time).</li>
 * <li>Start time - the first time point that will be visible to the user.</li>
 * <li>Millis per pixel - how much time is represented by a single pixel
 * (important for zooming).</li>
 * <li>Horizon - the earliest and latest point in time to which the user can
 * scroll.</li>
 * <li>Lowest temporal unit - the lowest unit that the user will be able to see
 * (e.g. MINUTES).</li>
 * <li>Highest temporal unit - the highest unit that the user will be able to
 * see (e.g. MONTHS).</li>
 * </ul>
 * This class is also responsible for calculating the location for a given time
 * and vice versa.
 * <p>
 *
 * @param <T>
 *            the temporal unit supported by the model (e.g. ChronoUnit).
 */
public abstract class TimelineModel<T extends TemporalUnit> {

    /**
     * Constructs a new timeline model.
     */
    protected TimelineModel() {
        final InvalidationListener updateNowListener = it -> updateNowLocation();

        nowProperty().addListener(updateNowListener);
        startTimeProperty().addListener(updateNowListener);
        millisPerPixelProperty().addListener(updateNowListener);
    }

    private void updateNowLocation() {
        nowLocation.set(calculateLocationForTime(getNow()));
    }

    // Offset support

    private final DoubleProperty offset = new SimpleDoubleProperty(this, "offset");

    public final double getOffset() {
        return offset.get();
    }

    public final DoubleProperty offsetProperty() {
        return offset;
    }

    public final void setOffset(double offset) {
        this.offset.set(offset);
    }

    // Horizon support

    private Instant _horizonStartTime;

    private ObjectProperty<Instant> horizonStartTime;

    /**
     * Returns the object property used for storing the start time of the
     * horizon.
     *
     * @return the horizon start time
     */
    public final ObjectProperty<Instant> horizonStartTimeProperty() {
    	if (horizonStartTime == null) {
    		horizonStartTime = new SimpleObjectProperty<>(this, "horizonStart", _horizonStartTime);
    	}
        return horizonStartTime;
    }

    /**
     * Returns the value of {@link #horizonStartTimeProperty()}.
     *
     * @return the horizon start time
     */
    public final Instant getHorizonStartTime() {
        return horizonStartTime == null ? _horizonStartTime : horizonStartTime.get();
    }

    /**
     * Sets the value of {@link #horizonStartTimeProperty()}.
     *
     * @param time
     *            the horizon start time
     */
    public final void setHorizonStartTime(Instant time) {
    	if (horizonStartTime == null) {
    		_horizonStartTime = time;
    	} else {
            horizonStartTime.set(time);
    	}
    }

    private Instant _horizonEndTime;

    private ObjectProperty<Instant> horizonEndTime;

    /**
     * Returns the object property used for storing the end time of the
     * horizon.
     *
     * @return the horizon end time
     */
    public final ObjectProperty<Instant> horizonEndTimeProperty() {
    	if (horizonEndTime == null) {
    		horizonEndTime = new SimpleObjectProperty<>(this, "horizonEndTime", _horizonEndTime);
    	}
        return horizonEndTime;
    }

    /**
     * Returns the value of {@link #horizonEndTimeProperty()}.
     *
     * @return the horizon end time
     */
    public final Instant getHorizonEndTime() {
        return horizonEndTime == null ? _horizonEndTime : horizonEndTime.get();
    }

    /**
     * Sets the value of {@link #horizonEndTimeProperty()}.
     *
     * @param time
     *            the horizon end time
     */
    public final void setHorizonEndTime(Instant time) {
    	if (horizonEndTime == null) {
    		_horizonEndTime = time;
    	} else {
    		horizonEndTime.set(time);
    	}
    }

    // MPP support.

    private final DoubleProperty millisPerPixel = new SimpleDoubleProperty(this, "millisPerPixel", 24.0 * 60.0 * 60.0 * 1000.0 / 30.0) {

        @Override
        public void set(double newValue) {
            double min = getMinimumMillisPerPixel();
            double max = getMaximumMillisPerPixel();
            super.set(Math.max(min, Math.min(max, newValue)));
        }
    };

    /**
     * Returns the property used to store the millis per pixel value. This value
     * determines how much time is represented by a single pixel in the user
     * interface. Changing the value of this property will cause the control to
     * show more or less time within the visible timeline area, meaning zooming
     * can be controlled by this property.
     *
     * @return the millis per pixel property
     */
    public final DoubleProperty millisPerPixelProperty() {
        return millisPerPixel;
    }

    /**
     * Sets the value of the {@link #millisPerPixelProperty()}.
     *
     * @param millis
     *            the millis represented by a pixel
     */
    public final void setMillisPerPixel(double millis) {
        millisPerPixelProperty().set(millis);
    }

    /**
     * Returns the value of {@link #millisPerPixelProperty()}.
     *
     * @return the millis represented by a pixel
     */
    public final double getMillisPerPixel() {
        return millisPerPixelProperty().get();
    }

    // Minimum MPP support.

    private final DoubleProperty minimumMillisPerPixel = new SimpleDoubleProperty(this, "minimumMillisPerPixel", 1) {

        @Override
        public void set(double newValue) {
            if (newValue < 0) {
                throw new IllegalArgumentException(
                        "minimum millis per pixel must be >= 0");
            }
            super.set(newValue);

            if (getMillisPerPixel() < newValue) {
                setMillisPerPixel(newValue);
            }
        }
    };

    /**
     * A property used to store the minimum number of milliseconds that will be
     * represented by a single pixel on the screen. Zoom-In operations will be
     * limited by this value.
     *
     * @see #setZoomRange(TemporalUnit, int, double, TemporalUnit, int, double)
     *
     * @return the minimum MPP value
     * @since 1.4
     */
    public final DoubleProperty minimumMillisPerPixelProperty() {
        return minimumMillisPerPixel;
    }

    /**
     * Sets the value of {@link #minimumMillisPerPixelProperty()}.
     *
     * @param min
     *            the minimum MPP value
     * @see #setZoomRange(TemporalUnit, int, double, TemporalUnit, int, double)
     * @since 1.4
     */
    public final void setMinimumMillisPerPixel(double min) {
        minimumMillisPerPixel.set(min);
    }

    /**
     * Returns the value of {@link #minimumMillisPerPixelProperty()}.
     *
     * @return the minimum MPP value
     * @see #setZoomRange(TemporalUnit, int, double, TemporalUnit, int, double)
     * @since 1.4
     */
    public final double getMinimumMillisPerPixel() {
        return minimumMillisPerPixel.get();
    }

    // Maximum MPP support.

    private final DoubleProperty maximumMillisPerPixel = new SimpleDoubleProperty(
            this, "maximumMillisPerPixel", Double.MAX_VALUE) {

        @Override
        public void set(double newValue) {
            super.set(newValue);
            if (getMillisPerPixel() > newValue) {
                setMillisPerPixel(newValue);
            }
        }
    };

    /**
     * A property used to store the maximum number of milliseconds that will be
     * represented by a single pixel on the screen. Zoom-Out operations will be
     * limited by this value.
     *
     * @see #setZoomRange(TemporalUnit, int, double, TemporalUnit, int, double)
     *
     * @return the maximum MPP value
     * @since 1.4
     */
    public final DoubleProperty maximumMillisPerPixelProperty() {
        return maximumMillisPerPixel;
    }

    /**
     * Sets the value of {@link #maximumMillisPerPixelProperty()}.
     *
     * @param max
     *            the maximum MPP value
     * @see #setZoomRange(TemporalUnit, int, double, TemporalUnit, int, double)
     * @since 1.4
     */
    public final void setMaximumMillisPerPixel(double max) {
        maximumMillisPerPixel.set(max);
    }

    /**
     * Returns the value of {@link #maximumMillisPerPixelProperty()}.
     *
     * @return the maximum MPP value
     * @see #setZoomRange(TemporalUnit, int, double, TemporalUnit, int, double)
     * @since 1.4
     */
    public final double getMaximumMillisPerPixel() {
        return maximumMillisPerPixel.get();
    }

    // Zoom range support.

    /**
     * Sets the range in which the user can zoom in and out of the timeline.
     *
     * @param smallestUnit
     *            the smallest unit to which the user can zoom (e.g. "MINUTES")
     * @param smallestUnitCount
     *            the number of smallest units (e.g. "5" MINUTES)
     * @param smallestUnitWidth
     *            the width of the unit in pixels, must be larger than 10 (e.g.
     *            "30")
     * @param largestUnit
     *            the largest unit to which the user can zoom (e.g. "YEARS")
     * @param largestUnitCount
     *            the number of largest units (e.g. "1" YEAR)
     * @param largestUnitWidth
     *            the width of the unit in pixels, must be larger than 10 (e.g.
     *            "30")
     *
     * @see #setMinimumMillisPerPixel(double)
     * @see #setMaximumMillisPerPixel(double)
     *
     * @since 1.4
     */
    public final void setZoomRange(T smallestUnit, int smallestUnitCount,
            double smallestUnitWidth, T largestUnit, int largestUnitCount,
            double largestUnitWidth) {

        requireNonNull(smallestUnit);
        requireNonNull(largestUnit);

        if (smallestUnit.getDuration().toMillis() > largestUnit.getDuration()
                .toMillis()) {
            throw new IllegalArgumentException("zoom range start unit can not be larger than end unit, start = " + smallestUnit + ", end = " + largestUnit);
        }

        if (smallestUnitCount < 1) {
            throw new IllegalArgumentException("smallest unit count must be >= 1 but was " + smallestUnitCount);
        }

        if (largestUnitCount < 1) {
            throw new IllegalArgumentException("largest unit count must be >= 1 but was " + largestUnitCount);
        }

        if (smallestUnitWidth < 10) {
            throw new IllegalArgumentException("smallest unit width must be >= 10 but was " + smallestUnitWidth);
        }

        if (largestUnitWidth < 10) {
            throw new IllegalArgumentException("largest unit width must be >= 10 but was " + largestUnitWidth);
        }

        long min = (long) (smallestUnit.getDuration().toMillis()
                * smallestUnitCount / smallestUnitWidth);

        long max = (long) (largestUnit.getDuration().toMillis()
                * largestUnitCount / largestUnitWidth);

        if (min > max) {
            throw new IllegalArgumentException("minimum MPP value can not be larger than maximum MPP value, min = " + min + ", max = " + max);
        }

        smallestTemporalUnit.set(smallestUnit);
        setMinimumMillisPerPixel(min);
        setMaximumMillisPerPixel(max);
    }

    // Start time support.

    private final ObjectProperty<Instant> startTime = new SimpleObjectProperty<>(this, "startTime", Instant.now()) {

        @Override
        public void set(Instant newTime) {
            Instant horizonStart = getHorizonStartTime();
            if (horizonStart != null && horizonStart.isAfter(newTime)) {
                return;
            }
            Instant horizonEnd = getHorizonEndTime();
            if (horizonEnd != null && horizonEnd.isBefore(newTime)) {
                return;
            }
            super.set(newTime);
        }
    };

    /**
     * Returns the property used to store the first visible time point.
     *
     * @return the start time property
     */
    public final ObjectProperty<Instant> startTimeProperty() {
        return startTime;
    }

    /**
     * Sets the value of {@link #startTimeProperty()}.
     *
     * @param time
     *            the start time
     */
    public final void setStartTime(Instant time) {
        startTimeProperty().set(time);
    }

    /**
     * Returns the value of {@link #startTimeProperty()}.
     *
     * @return the start time
     */
    public final Instant getStartTime() {
        return startTimeProperty().get();
    }

    // Now support.

    private final ObjectProperty<Instant> now = new SimpleObjectProperty<>(this, "now", Instant.now());

    /**
     * Returns the property used to store "now", the current time, e.g. the
     * current system time.
     *
     * @return the "now" time
     */
    public final ObjectProperty<Instant> nowProperty() {
        return now;
    }

    /**
     * Sets the value of {@link #nowProperty()}.
     *
     * @param now
     *            the "now" time
     */
    public final void setNow(Instant now) {
        nowProperty().set(now);
    }

    /**
     * Returns the value of {@link #nowProperty()}.
     *
     * @return the "now" time
     */
    public final Instant getNow() {
        return nowProperty().get();
    }

    // "Read Only" access to the location of "now".

    private final ReadOnlyDoubleWrapper nowLocation = new ReadOnlyDoubleWrapper(this, "nowLocation", 0);

    /**
     * Stores the location of the "now" time. The location can be computed based
     * on the millis per pixel and the start time value.
     *
     * @return the pixel location of "now"
     */
    public final ReadOnlyDoubleProperty nowLocationProperty() {
        return nowLocation.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #nowLocationProperty()}.
     *
     * @return the pixel location of "now"
     */
    public final double getNowLocation() {
        return nowLocation.get();
    }

    // Smallest temporal unit support

    private final ReadOnlyObjectWrapper<T> smallestTemporalUnit = new ReadOnlyObjectWrapper<>(this, "smallestTemporalUnit");

    /**
     * Stores the smallest temporal unit supported by the control.
     *
     * @return the smallest temporal unit supported (e.g. "MINUTES").
     */
    public final ReadOnlyObjectProperty<T> smallestTemporalUnitProperty() {
        return smallestTemporalUnit.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #smallestTemporalUnitProperty()}.
     *
     * @return the smallest temporal unit
     */
    public final T getSmallestTemporalUnit() {
        return smallestTemporalUnitProperty().get();
    }

    /**
     * Returns the pixel location of the given time.
     *
     * @param time
     *            the time for which to return the pixel location
     * @return the location of the given time
     */
    public final double calculateLocationForTime(Instant time) {
        if (time == null) {
            return -1;
        } else if (time.equals(Instant.MAX)) {
            return Double.MAX_VALUE;
        } else if (time.equals(Instant.MIN)) {
            return Double.MIN_VALUE;
        }

        long startTimeMillis = getStartTime().toEpochMilli();
        long timeMillis = time.toEpochMilli();
        long millisDifference = timeMillis - startTimeMillis;

        return millisDifference / getMillisPerPixel() + getOffset();
    }

    /**
     * Returns the time for the given location.
     *
     * @param location
     *            the location in pixels
     * @return the location time
     */
    public final Instant calculateTimeForLocation(double location) {

        long millis = (long) ((location - getOffset()) * getMillisPerPixel());

        Instant startTime = getStartTime();

        return startTime.plus(Duration.ofMillis(millis));
    }
}