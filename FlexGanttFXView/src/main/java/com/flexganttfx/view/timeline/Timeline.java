/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.timeline;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.timeline.ChronoUnitTimelineModel;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.util.FlexGanttFXControl;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.timeline.TimelineSkin;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Skin;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.util.Duration;
import org.controlsfx.control.PropertySheet.Item;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * The timeline control is a container for the {@link Dateline} and the
 * {@link Eventline}. It is displayed above the {@link GraphicsBase} and
 * provides several methods for scrolling and zooming, both of which can be done
 * in an animated way (default) or not.
 * <p>&nbsp;</p>
 *
 * <img src="doc-files/timeline.png" alt="Timeline">
 *
 *
 * @see GanttChart#getTimeline()
 * @see #setMoveAnimated(boolean)
 * @see #setZoomAnimated(boolean)
 * @since 1.0
 */
public class Timeline extends FlexGanttFXControl {

    /**
     * An enum used to control the way that zooming operations will happen.
     *
     * @since 1.0
     */
    public enum ZoomMode {

        /**
         * The time shown in the middle of the timeline will also be in the
         * middle after the zoom has finished.
         *
         * @since 1.0
         */
        CENTER,

        /**
         * The time shown at the beginning of the timeline will also be at the
         * beginning after the zoom has finished.
         *
         * @since 1.0
         */
        KEEP_START_TIME,

        /**
         * The time shown at the end of the timeline will also be at the end
         * after the zoom has finished.
         *
         * @since 1.0
         */
        KEEP_END_TIME
    }

    private static final String DEFAULT_STYLE_CLASS = "timeline";

    private final Dateline dateline;

    private final Eventline eventline;

    private Instant zoomTime;

    /**
     * Constructs a new timeline control that is using the
     * {@link ChronoUnitTimelineModel}.
     *
     * @since 1.0
     */
    public Timeline() {

        setPrefWidth(0);
        setMinWidth(0);

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        setFocusTraversable(true);

        // register listeners first before setting the model
        registerListeners();

        setModel(new ChronoUnitTimelineModel());

        addEventHandler(ScrollEvent.SCROLL, evt -> {

            getDateline().getProperties().put("com.flexganttfx.dateline.hover.interval", null);

            double deltaX = evt.getDeltaX();
            if (deltaX != 0) {
                Instant time = getModel().calculateTimeForLocation(-deltaX);
                getModel().setStartTime(time);
            } else if (evt.isShiftDown()) {
                double deltaY = evt.getDeltaY();
                if (deltaY > 0) {
                    zoomIn();
                } else if (deltaY < 0) {
                    zoomOut();
                }

                evt.consume();
            }
        });

        addEventHandler(ZoomEvent.ZOOM_STARTED, event -> {
            TimelineModel<?> model = getModel();
            zoomTime = model.calculateTimeForLocation(event.getX());
        });

        addEventHandler(ZoomEvent.ZOOM, event -> zoom(Math.abs(event.getZoomFactor() - 1), event.getZoomFactor() > 1, zoomTime));

        widthProperty().addListener(observable -> {
            if (requestedInterval != null) {
                boolean wasAnimated = isZoomAnimated();
                setZoomAnimated(false);
                showRange(requestedInterval);
                setZoomAnimated(wasAnimated);
            }

            if (requestedTime != null) {
                boolean wasAnimated = isZoomAnimated();
                setZoomAnimated(false);
                showTime(requestedTime, requestedTimeCenter);
                setZoomAnimated(wasAnimated);
            }

        });

        dateline = new Dateline(this);
        eventline = new Eventline(this);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TimelineSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return super.getUserAgentStylesheet(Timeline.class, "timeline.css");
    }

    /**
     * Returns the dateline contained within the timeline. The timeline is a container that
     * consists of a dateline and an eventline.
     *
     * @return the dateline
     * @since 1.0
     */
    public final Dateline getDateline() {
        return dateline;
    }

    /**
     * Returns the eventline contained within the timeline. The timeline is a container that
     * consists of a dateline and an eventline.
     *
     * @return the dateline
     * @since 1.0
     */
    public final Eventline getEventline() {
        return eventline;
    }

    private void registerListeners() {
        widthProperty().addListener((value, oldWidth, newWidth) -> updateVisibleStartAndEndTime());

        final ChangeListener<Instant> startTimeListener = (value, oldInstant, newInstant) -> updateVisibleStartAndEndTime();
        final ChangeListener<Number> mppListener = (value, oldNumber, newNumber) -> updateVisibleStartAndEndTime();

        modelProperty().addListener((value, oldModel, newModel) -> {

            if (oldModel != null) {
                oldModel.startTimeProperty().removeListener(startTimeListener);
                oldModel.millisPerPixelProperty().removeListener(mppListener);
            }

            if (newModel != null) {
                updateVisibleStartAndEndTime();

                newModel.startTimeProperty().addListener(startTimeListener);
                newModel.millisPerPixelProperty().addListener(mppListener);
            }
        });
    }

    // Scroll drag

    private final BooleanProperty scrollDragEnabled = new SimpleBooleanProperty(this, "scrollDragEnabled", false);

    /**
     * A property used to control whether the user is allowed to perform a horizontal scroll
     * by dragging the timeline. Normally a drag gesture triggers the selection of a time interval used
     * for zooming into time.
     *
     * @return true if the user can perform a horizontal scrolling by dragging the timeline
     * @since 1.0
     */
    public final BooleanProperty scrollDragEnabledProperty() {
        return scrollDragEnabled;
    }

    /**
     * Returns the value of {@link #scrollDragEnabledProperty}.
     *
     * @return true if the user can trigger horizontal scrolling by dragging the timeline
     * @since 1.0
     */
    public final boolean isScrollDragEnabled() {
        return scrollDragEnabled.get();
    }

    /**
     * Sets the value of {@link #scrollDragEnabledProperty}.
     *
     * @param enabled if true the user can trigger horizontal scrolling by dragging the timeline
     * @since 1.0
     */
    public final void setScrollDragEnabled(boolean enabled) {
        scrollDragEnabled.set(enabled);
    }

    // Visible time range support.

    private final ReadOnlyObjectWrapper<Instant> visibleStartTime = new ReadOnlyObjectWrapper<>(this, "visibleStartTime", Instant.now());

    /**
     * A read-only object property storing an {@link Instant} that represents the first visible time
     * point (on the left edge) inside the timeline.
     *
     * @return the visible start time shown by the timeline on its' left edge
     * @since 1.0
     */
    public final ReadOnlyObjectProperty<Instant> visibleStartTimeProperty() {
        return visibleStartTime.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #visibleStartTimeProperty()}.
     *
     * @return the value of #visibleStartTimeProperty
     * @since 1.0
     */
    public final Instant getVisibleStartTime() {
        return visibleStartTimeProperty().get();
    }

    private final ReadOnlyObjectWrapper<Instant> visibleEndTime = new ReadOnlyObjectWrapper<>(this, "visibleEndTime", Instant.now());

    /**
     * A read-only object property storing an {@link Instant} that represents the last visible time
     * point (on the right edge) inside the timeline.
     *
     * @return the visible end time shown by the timeline on its' right edge
     * @since 1.0
     */
    public final ReadOnlyObjectProperty<Instant> visibleEndTimeProperty() {
        return visibleEndTime.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #visibleEndTimeProperty()}.
     *
     * @return the value of #visibleEndTimeProperty
     * @since 1.0
     */
    public final Instant getVisibleEndTime() {
        return visibleEndTimeProperty().get();
    }

    /**
     * Calculates and returns the duration of the visible time interval inside the
     * timeline. The duration is the time difference between the {@link #visibleStartTimeProperty()} and
     * the {@link #visibleEndTimeProperty()}.
     *
     * @return the visible duration
     * @since 1.0
     */
    public final java.time.Duration getVisibleDuration() {
        return java.time.Duration.ofMillis(getVisibleEndTime().toEpochMilli() - getVisibleStartTime().toEpochMilli());
    }

    private void updateVisibleStartAndEndTime() {
        TimelineModel<?> timelineModel = getModel();
        if (timelineModel != null) {
            visibleStartTime.set(timelineModel.getStartTime());
            visibleEndTime.set(timelineModel.calculateTimeForLocation(getWidth()));
        }
    }

    // Moving support

    private final ObjectProperty<Duration> moveDuration = new SimpleObjectProperty<>(this, "moveDuration", Duration.seconds(.33));

    /**
     * An object property used to store the duration used for the animation of a
     * "move" inside the timeline. Moving means that the timeline moves from one visible
     * start time to another.
     *
     * @return the animation duration used for moving to another visible start time
     * @see #showNow(boolean)
     * @see #showTime(Instant)
     * @see #showTime(Instant, boolean)
     * @since 1.0
     */
    public final ObjectProperty<Duration> moveDurationProperty() {
        return moveDuration;
    }

    /**
     * Sets the value of {@link #moveDurationProperty()}.
     *
     * @param duration the time used for animating a move inside the timeline
     * @since 1.0
     */
    public final void setMoveDuration(Duration duration) {
        moveDurationProperty().set(duration);
    }

    /**
     * Returns the value of {@link #moveDurationProperty()}.
     *
     * @return the time used for animating a move inside the timeline
     * @since 1.0
     */
    public final Duration getMoveDuration() {
        return moveDurationProperty().get();
    }

    // Move "animated" support.

    private final BooleanProperty moveAnimated = new SimpleBooleanProperty(this, "moveAnimated", true);

    /**
     * A boolean property used to control whether moving from one time to another
     * will happen animated or not.
     *
     * @return true if the move will be animated
     * @since 1.0
     */
    public final BooleanProperty moveAnimatedProperty() {
        return moveAnimated;
    }

    /**
     * Returns the value of {@link #moveAnimatedProperty()}.
     *
     * @return true if moving in time will be animated
     */
    public final boolean isMoveAnimated() {
        return moveAnimatedProperty().get();
    }

    /**
     * Sets the value of {@link #moveAnimatedProperty()}.
     *
     * @param animated if true the change from one time to another will be animated
     */
    public final void setMoveAnimated(boolean animated) {
        moveAnimatedProperty().set(animated);
    }

    // "Show" support (show now, show time)

    /**
     * Makes the timeline scroll to the time point that is currently considered "now". This
     * time point will be shown in the center of the timeline.
     *
     * @see #showNow(boolean)
     * @see #showTime(Instant)
     * @see #showTime(Instant, boolean)
     * @since 1.0
     */
    public final void showNow() {
        showNow(true);
    }

    /**
     * Makes the timeline scroll to the time point that is currently considered "now". This
     * time point will be shown in the center of the timeline.
     *
     * @param center determines if the time will be shown in the center or on the left-edge of the timeline
     * @see #showNow()
     * @see #showTime(Instant)
     * @see #showTime(Instant, boolean)
     * @since 1.0
     */
    public final void showNow(boolean center) {
        showTime(getModel().getNow(), center);
    }

    /**
     * Makes the timeline scroll to the time point passed to the method. This
     * time point will be shown on the left edge of the timeline.
     *
     * @param time the time to show
     * @see #showNow()
     * @see #showNow(boolean)
     * @see #showTime(Instant, boolean)
     * @since 1.0
     */
    public final void showTime(Instant time) {
        showTime(time, false);
    }

    private Instant requestedTime;
    private boolean requestedTimeCenter;

    /**
     * Makes the timeline scroll to the time point passed to the method.
     *
     * @param time   the time to show
     * @param center if true the time will be centered within the timeline
     * @see #showNow()
     * @see #showNow(boolean)
     * @see #showTime(Instant)
     * @since 1.0
     */
    public final void showTime(Instant time, boolean center) {

        if (getWidth() == 0) {
            requestedTime = time;
            requestedTimeCenter = center;
            return;
        } else {
            requestedTime = null;
        }

        if (center) {
            time = time.minus(getVisibleDuration().dividedBy(2));
        }

        final TimelineModel<?> timelineModel = getModel();

        if (isMoveAnimated()) {
            LongProperty startTimeProperty = new SimpleLongProperty(timelineModel.getStartTime().toEpochMilli());
            startTimeProperty.addListener((value, oldStartTime, newStartTime) -> {
                Instant instant = Instant.ofEpochMilli(newStartTime.longValue());
                timelineModel.setStartTime(instant);
            });

            KeyValue keyStartTimeValue = new KeyValue(startTimeProperty, time.toEpochMilli());
            javafx.animation.Timeline animationTimeline = new javafx.animation.Timeline(new KeyFrame(getMoveDuration(), keyStartTimeValue));
            animationTimeline.play();
        } else {
            timelineModel.setStartTime(time);
        }
    }

    /**
     * Submits a request to the timeline to show the given temporal unit
     * (e.g. {@link java.time.temporal.ChronoUnit#DAYS} at the given width (e.g. 50px).
     * This is a convenient way to make the Gantt chart show up with a good
     * initial zoom.
     *
     * @param temporalUnit the temporal unit to show
     * @param width        the number of pixels to use for each unit (e.g. for one day)
     * @since 1.0
     */
    public final void showTemporalUnit(TemporalUnit temporalUnit, double width) {
        requireNonNull(temporalUnit);
        if (width < 10) {
            throw new IllegalArgumentException(
                    "requested with / temporal unit must be equal to or larger than 10");
        }

        long requestedMillis = temporalUnit.getDuration().toMillis();
        TimelineModel<?> model = getModel();
        model.setMillisPerPixel(requestedMillis / width);
    }

    // Timeline model support.

    private final ObjectProperty<TimelineModel<?>> model = new SimpleObjectProperty<>(this, "model");

    /**
     * Stores the timeline model to be used by the timeline.
     *
     * @return the timeline model
     * @see ChronoUnitTimelineModel
     * @see com.flexganttfx.model.timeline.SimpleUnitTimelineModel
     * @since 1.0
     */
    public final ObjectProperty<TimelineModel<?>> modelProperty() {
        return model;
    }

    /**
     * Returns the value of {@link #modelProperty()}.
     *
     * @return the timeline model
     * @since 1.0
     */
    public final TimelineModel<?> getModel() {
        return model.get();
    }

    /**
     * Sets the value of {@link #modelProperty()}.
     *
     * @param model the timeline model
     * @since 1.0
     */
    public final void setModel(TimelineModel<?> model) {
        requireNonNull(model);
        this.model.set(model);
    }

    // Zoom Duration

    private final ObjectProperty<Duration> zoomDuration = new SimpleObjectProperty<>(this, "zoomDuration", Duration.seconds(.2));

    /**
     * An object property used to store the duration used for the animation of a
     * "zoom" inside the timeline. Zooming means that the timeline changes the currently
     * visible time window (bigger, smaller).
     *
     * @return the animation duration used for moving to another visible start time
     * @see #zoomIn()
     * @see #zoomOut()
     * @see #showRange(Instant, java.time.Duration)
     */
    public final ObjectProperty<Duration> zoomDurationProperty() {
        return zoomDuration;
    }

    /**
     * Sets the value of {@link #zoomDurationProperty()}.
     *
     * @param duration the duration of the zoom animation
     * @since 1.0
     */
    public final void setZoomDuration(Duration duration) {
        zoomDurationProperty().set(duration);
    }

    /**
     * Returns the value of {@link #zoomDurationProperty()}.
     *
     * @return the duration of the zoom animation
     * @since 1.0
     */
    public final Duration getZoomDuration() {
        return zoomDurationProperty().get();
    }

    private final BooleanProperty zoomAnimated = new SimpleBooleanProperty(this, "zoomAnimated", true);

    /**
     * A property used to determine if any zoom operation should be done in an
     * animated fashion or not. Animation happens by not directly switching to the new
     * time interval but by gradually changing the interval until the new interval
     * has been reached.
     *
     * @return true if the zoom will be animated
     * @since 1.0
     */
    public final BooleanProperty zoomAnimatedProperty() {
        return zoomAnimated;
    }

    /**
     * Returns the value of {@link #zoomAnimatedProperty()}.
     *
     * @return true if the zoom in / out operations will be visualized in an animated way
     * @since 1.0
     */
    public final boolean isZoomAnimated() {
        return zoomAnimatedProperty().get();
    }

    /**
     * Sets the value of {@link #zoomAnimatedProperty}.
     *
     * @param animated if true the zoom in / out operations will be visualized in an animated way
     * @since 1.0
     */
    public final void setZoomAnimated(boolean animated) {
        zoomAnimatedProperty().set(animated);
    }

    // Zoom mode

    private final ObjectProperty<ZoomMode> zoomMode = new SimpleObjectProperty<>(this, "zoomMode", ZoomMode.KEEP_START_TIME);

    /**
     * Stores the way a zoom in or out will be executed. Zooming can keep the current start time,
     * the current end time, or the time shown in the center of the timeline inside the visible area.
     * Based on this setting the currently shown activities on the left or the right might be pushed
     * out of the visible area when zooming in or more of them might show up either on the left,
     * the right, or both sides.
     *
     * @return the currently applied zoom mode (center zoom, keep start time, keep end time)
     * @since 1.0
     */
    public final ObjectProperty<ZoomMode> zoomModeProperty() {
        return zoomMode;
    }

    /**
     * Returns the value of {@link #zoomModeProperty()}.
     *
     * @return the currently used zoom mode (center zoom, keep start time, keep end time)
     * @since 1.0
     */
    public final ZoomMode getZoomMode() {
        return zoomModeProperty().get();
    }

    /**
     * Sets the value of {@link #zoomModeProperty()}.
     *
     * @param mode the new zoom mode (center zoom, keep start time, keep end time)
     * @since 1.0
     */
    public final void setZoomMode(ZoomMode mode) {
        zoomModeProperty().set(mode);
    }

    // Zoom factor

    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this,
            "zoomFactor", .5) {
        @Override
        public void setValue(Number number) {
            if (number.doubleValue() <= 0) {
                throw new IllegalArgumentException("zoom factor must be larger than 0");
            }

            super.setValue(number);
        }
    };

    /**
     * A property used to store the zoom factor that will be applied every time the user
     * performs a zoom in or zoom out. The default value of this property is .5, which means that
     * the user will see 50% more or less time inside the visible area.
     *
     * @return the zoom factor
     * @since 1.0
     */
    public final DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    /**
     * Returns the value of the {@link #zoomFactorProperty()}.
     *
     * @return the zoom factor
     * @since 1.0
     */
    public final double getZoomFactor() {
        return zoomFactorProperty().get();
    }

    /**
     * Sets the value of the {@link #zoomFactorProperty()}.
     *
     * @param zoomFactor the new zoom factor
     * @since 1.0
     */
    public final void setZoomFactor(double zoomFactor) {
        zoomFactorProperty().set(zoomFactor);
    }

    /**
     * Requests that the timeline performs a zoom in operation.
     *
     * @see #zoomOut()
     * @see #zoom(double, boolean, Instant)
     * @see #zoomFactorProperty()
     * @see #zoomModeProperty()
     * @since 1.0
     */
    public final void zoomIn() {
        zoom(getZoomFactor(), true, null);
    }

    /**
     * Requests that the timeline performs a zoom out operation.
     *
     * @see #zoomOut()
     * @see #zoom(double, boolean, Instant)
     * @see #zoomFactorProperty()
     * @see #zoomModeProperty()
     * @since 1.0
     */
    public final void zoomOut() {
        zoom(1.0 / getZoomFactor(), false, null);
    }

    /**
     * Requests that the timeline performs a zoom operation.
     *
     * @param factor     the zoom factor (default is .5)
     * @param zoomIn     if true the zoom will show less time
     * @param frozenTime the point in time that will stay where it was before (use case: pinch zoom)
     * @see #zoomIn()
     * @see #zoomOut()
     * @see #zoomFactorProperty()
     * @see #zoomModeProperty()
     * @since 1.0
     */
    public final void zoom(double factor, boolean zoomIn, Instant frozenTime) {
        TimelineModel<?> model = getModel();

        Instant startTime = model.getStartTime();
        Instant endTime = model.calculateTimeForLocation(getWidth());

        long delta = endTime.toEpochMilli() - startTime.toEpochMilli();

        if (frozenTime != null) {

            double frozenXBefore = model.calculateLocationForTime(frozenTime);

            if (zoomIn) {
                startTime = startTime.plusMillis((long) (factor * delta) / 2);
                endTime = endTime.minusMillis((long) (factor * delta) / 2);
            } else {
                startTime = startTime.minusMillis((long) (factor * delta) / 2);
                endTime = endTime.plusMillis((long) (factor * delta) / 2);
            }

			/*
             * This is also being checked in showRange but we also have to make
			 * sure we are not doing anything of the other things.
			 */
            if (startTime.isBefore(endTime)) {
                boolean zoomAnimated = isZoomAnimated();
                setZoomAnimated(false);

                // "limitReached" -> Fix for FLEXFX-332: "Timeline scrolls to the right when trying to zoom in with "frozen" time over the set zoom limit"
                boolean limitReached = showRange(startTime, endTime);
                if (limitReached) {
                    return;
                }

                setZoomAnimated(zoomAnimated);

                double frozenXAfter = model.calculateLocationForTime(frozenTime);
                double deltaX = frozenXBefore - frozenXAfter;

                Instant deltaTime = model.calculateTimeForLocation(deltaX);
                long deltaMillis = deltaTime.toEpochMilli() - startTime.toEpochMilli();

                Instant adjustedStartTime = startTime.minusMillis(deltaMillis);
                model.setStartTime(adjustedStartTime);
            }
        } else {

            switch (getZoomMode()) {
                case CENTER:
                    Instant centerTime = model.calculateTimeForLocation(getWidth() / 2);
                    startTime = centerTime.minusMillis((long) (factor * delta / 2));
                    endTime = centerTime.plusMillis((long) (factor * delta / 2));
                    break;
                case KEEP_END_TIME:
                    startTime = endTime.minusMillis((long) (factor * delta));
                    break;
                case KEEP_START_TIME:
                    endTime = startTime.plusMillis((long) (factor * delta));
                    break;
            }

            showRange(startTime, endTime);
        }
    }

    /**
     * Requests that the given time interval will be completely visible within the timeline.
     *
     * @param startTime the start time of the requested interval
     * @param duration  the duration of the requested interval
     * @see #showRange(TimeInterval)
     * @see #showRange(Instant, Instant)
     * @since 1.0
     */
    public final void showRange(Instant startTime, java.time.Duration duration) {
        requireNonNull(startTime);
        requireNonNull(duration);
        showRange(startTime, startTime.plus(duration));
    }

    /**
     * Requests that the given time interval will be completely visible within the timeline.
     *
     * @param interval the requested interval
     * @see #showRange(Instant, java.time.Duration)
     * @see #showRange(Instant, Instant)
     * @since 1.0
     */
    public final void showRange(TimeInterval interval) {
        requireNonNull(interval);
        showRange(interval.getStartTime(), interval.getEndTime());
    }

    private TimeInterval requestedInterval;

    /**
     * Requests that the given time interval will be completely visible within the timeline.
     *
     * @param startTime the start time of the requested interval
     * @param endTime   the end time of the requested interval
     * @return true if the operation reached the minimum or maximum millis per second supported by the timeline
     * @see #showRange(Instant, java.time.Duration)
     * @see #showRange(TimeInterval)
     * @since 1.0
     */
    public final boolean showRange(Instant startTime, Instant endTime) {
        requireNonNull(startTime);
        requireNonNull(endTime);

        if (startTime.equals(endTime) || startTime.isAfter(endTime)) {
            LoggingDomain.NAVIGATION.warning("start time is NOT before end time, ignoring showing range");
            return false;
        }

        double width = getWidth();
        if (width == 0) {
            requestedInterval = new TimeInterval(startTime, endTime);
            return true;
        } else {
            requestedInterval = null;
        }

        long st = startTime.toEpochMilli();
        long et = endTime.toEpochMilli();

        final TimelineModel<? extends TemporalUnit> timelineModel = getModel();

        double mppMin = timelineModel.getMinimumMillisPerPixel();
        double mppMax = timelineModel.getMaximumMillisPerPixel();
        double mpp = Math.max(mppMin, Math.min(mppMax, (et - st) / width));

        boolean limitReached = false;

        // Fix for FLEXFX-332: "Timeline scrolls to the right when trying to zoom in with "frozen" time over the set zoom limit"
        if (mpp == mppMin || mpp == mppMax) {
            limitReached = true;
        }

        if (!limitReached && isZoomAnimated()) {
            // animate start time
            LongProperty startTimeProperty = new SimpleLongProperty(timelineModel.getStartTime().toEpochMilli());
            startTimeProperty.addListener((value, oldStartTime, newStartTime) -> {
                Instant instant = Instant.ofEpochMilli(newStartTime.longValue());
                timelineModel.setStartTime(instant);
            });

            KeyValue keyStartTimeValue = new KeyValue(startTimeProperty, st);

            // animate zoom in
            DoubleProperty millisPerPixel = timelineModel.millisPerPixelProperty();
            timelineModel.startTimeProperty();

            KeyValue keyMillisPerPixelValue = new KeyValue(millisPerPixel, mpp);

            javafx.animation.Timeline animationTimeline = new javafx.animation.Timeline(
                    new KeyFrame(getZoomDuration(), keyMillisPerPixelValue,
                            keyStartTimeValue));

            animationTimeline.play();
        } else {
            // Fix for FLEXFX-332: "Timeline scrolls to the right when trying to zoom in with "frozen" time over the set zoom limit"
            if (!limitReached) {
                timelineModel.setStartTime(startTime);
            }
            timelineModel.setMillisPerPixel(mpp);
        }

        return limitReached;
    }

    private final int SLOW_SPEED_PERCENTAGE = 20;

    private final int HIGH_SPEED_PERCENTAGE = 40;

    /**
     * Performs a right scroll inside the timeline, meaning that later times will become visible.
     *
     * @see #scrollRightFast()
     * @see #scrollLeft()
     * @see #scrollLeftFast()
     * @since 1.0
     */
    public final void scrollRight() {
        doScroll(SLOW_SPEED_PERCENTAGE);
    }

    /**
     * Performs a fast right scroll inside the timeline, meaning that later times will become visible.
     *
     * @see #scrollRight()
     * @see #scrollLeft()
     * @see #scrollLeftFast()
     * @since 1.0
     */
    public final void scrollRightFast() {
        doScroll(HIGH_SPEED_PERCENTAGE);
    }

    /**
     * Performs a left scroll inside the timeline, meaning that earlier times will become visible.
     *
     * @see #scrollLeftFast()
     * @see #scrollRightFast()
     * @see #scrollRight()
     * @since 1.0
     */
    public final void scrollLeft() {
        doScroll(-SLOW_SPEED_PERCENTAGE);
    }

    /**
     * Performs a fast left scroll inside the timeline, meaning that earlier times will become visible.
     *
     * @see #scrollLeft()
     * @see #scrollRightFast()
     * @see #scrollRight()
     * @since 1.0
     */
    public final void scrollLeftFast() {
        doScroll(-HIGH_SPEED_PERCENTAGE);
    }

    private synchronized void doScroll(double percentage) {
        TimelineModel<?> model = getModel();
        long startTime = model.getStartTime().toEpochMilli();
        double visible = model.calculateTimeForLocation(getWidth()).toEpochMilli() - startTime;
        long jump = (long) (visible / 100 * percentage);

        Instant targetTime = Instant.ofEpochMilli(startTime + jump);

        if (Boolean.getBoolean("flexganttfx.animation.off")) {

            model.setStartTime(targetTime);

        } else {

            final LongProperty prop = new SimpleLongProperty(model.getStartTime().toEpochMilli());
            prop.addListener(it -> model.setStartTime(Instant.ofEpochMilli(prop.get())));

            KeyValue keyValue = new KeyValue(prop, targetTime.toEpochMilli());
            KeyFrame keyFrame = new KeyFrame(Duration.millis(333), keyValue);
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(keyFrame);
            timeline.play();

        }
    }

    private static final String TIMELINE_PROPERTIES_CATEGORY = "Control: Timeline";

    public final List<Item> getPropertySheetItems() {
        List<Item> items = new ArrayList<>();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(zoomAnimatedProperty());
            }

            @Override
            public void setValue(Object value) {
                setZoomAnimated((Boolean) value);
            }

            @Override
            public Object getValue() {
                return isZoomAnimated();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Animated Zoom";
            }

            @Override
            public String getDescription() {
                return "Zoom operations can be performed with or without animation.";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(zoomFactorProperty());
            }

            @Override
            public void setValue(Object value) {
                setZoomFactor((Double) value);
            }

            @Override
            public Object getValue() {
                return getZoomFactor();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Zoom Factor";
            }

            @Override
            public String getDescription() {
                return "The factor used for zooming in or out, default = .5";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(zoomModeProperty());
            }

            @Override
            public void setValue(Object value) {
                setZoomMode((ZoomMode) value);
            }

            @Override
            public Object getValue() {
                return getZoomMode();
            }

            @Override
            public Class<?> getType() {
                return ZoomMode.class;
            }

            @Override
            public String getName() {
                return "Zoom Mode";
            }

            @Override
            public String getDescription() {
                return "The method of zooming in (keep start, keep end, keep center time).";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(zoomDurationProperty());
            }

            @Override
            public void setValue(Object value) {
                setZoomDuration((Duration) value);
            }

            @Override
            public Object getValue() {
                return getZoomDuration();
            }

            @Override
            public Class<?> getType() {
                return Duration.class;
            }

            @Override
            public String getName() {
                return "Zoom Duration";
            }

            @Override
            public String getDescription() {
                return "The duration of the zoom animation.";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(moveAnimatedProperty());
            }

            @Override
            public void setValue(Object value) {
                setMoveAnimated((Boolean) value);
            }

            @Override
            public Object getValue() {
                return isMoveAnimated();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Animated Move";
            }

            @Override
            public String getDescription() {
                return "Move operations can be performed with or without animation.";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(moveDurationProperty());
            }

            @Override
            public void setValue(Object value) {
                setMoveDuration((Duration) value);
            }

            @Override
            public Object getValue() {
                return getMoveDuration();
            }

            @Override
            public Class<?> getType() {
                return Duration.class;
            }

            @Override
            public String getName() {
                return "Move Duration";
            }

            @Override
            public String getDescription() {
                return "The duration of the move animation.";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        return items;
    }
}
