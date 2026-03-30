/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.util;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.timeline.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.controlsfx.control.PlusMinusSlider;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

/**
 * A specialized {@link PlusMinusSlider} for controlling the {@link Timeline} inside the
 * {@link GanttChart} view. Updates the start time property of the underlying
 * {@link TimelineModel}.
 *
 * @see TimelineModel#setStartTime(Instant)
 */
public class TimelineScrollBar extends PlusMinusSlider {

    private final ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>(this, "timeline");

    /**
     * Constructs a new scrollbar.
     */
    public TimelineScrollBar() {
        getStyleClass().add("time-slider");

        addEventHandler(PlusMinusEvent.VALUE_CHANGED,
                evt -> {
                    long st = getTimeline().getVisibleStartTime().toEpochMilli();
                    long et = getTimeline().getVisibleEndTime().toEpochMilli();

                    long delta = et - st;

                    double value = evt.getValue();

                    long millis = (long) (value * delta) / 10;

                    TimelineModel<?> model = getTimeline().getModel();
                    Instant time = model.getStartTime().plus(Duration.ofMillis(millis));

                    if (LoggingDomain.NAVIGATION.isLoggable(Level.FINER)) {
                        LoggingDomain.NAVIGATION.finer("visible start time: " + st);
                        LoggingDomain.NAVIGATION.finer("visible end time: " + et);
                        LoggingDomain.NAVIGATION.finer("plus minus slider value: " + value);
                        LoggingDomain.NAVIGATION.finer("setting new time on timeline model to " + time);
                    }

                    double moveX = model.calculateLocationForTime(model.getStartTime()) - model.calculateLocationForTime(time);
                    if (Math.abs(moveX) >= 1) {
                        // performance tuning: no need to redraw subpixel changes
                        model.setStartTime(time);
                    }
                });
    }

    /**
     * Returns {@code true} when the application is currently using an AtlantaFX
     * theme. Detection is done by checking whether the user-agent stylesheet URL
     * contains the string {@code "atlantafx"}. No compile-time dependency on the
     * AtlantaFX library is required.
     */
    private boolean isAtlantaFXActive() {
        String uas = Application.getUserAgentStylesheet();
        if (uas == null) {
            return false;
        }
        return uas.toLowerCase(Locale.ROOT).contains("atlantafx");
    }

    @Override
    public String getUserAgentStylesheet() {
        if (isAtlantaFXActive()) {
            return Objects.requireNonNull(GanttChartBase.class.getResource("gantt-atlantafx.css")).toExternalForm();
        }
        return Objects.requireNonNull(GanttChartBase.class.getResource("gantt.css")).toExternalForm();
    }

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
