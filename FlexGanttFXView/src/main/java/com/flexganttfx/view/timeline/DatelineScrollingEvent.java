/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.timeline;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.time.Instant;
import java.time.ZoneId;

import static java.util.Objects.requireNonNull;

/**
 * A special event type used for informing the application about horizontal
 * scrolling within the dateline. Scrolling = moving forward or back in time.
 * Listening for this event is extremely useful for implementing a lazy loading
 * strategy. Applications can listen for this event and then dynamically load the
 * data that belongs to the new time interval.
 *
 * @see Dateline#setOnVisibleRangeChanged(EventHandler)
 * @since 1.0
 */
public final class DatelineScrollingEvent extends Event {

    private static final long serialVersionUID = -1780548387954076371L;

    public static final EventType<DatelineScrollingEvent> ANY_SCROLLING = new EventType<>(Event.ANY, "ANY_SCROLLING");

    public static final EventType<DatelineScrollingEvent> VISIBLE_RANGE_CHANGED = new EventType<>(DatelineScrollingEvent.ANY_SCROLLING, "VISIBLE_RANGE_CHANGED");

    private final Instant startTime;

    private final Instant endTime;

    private final ZoneId zoneId;

    /**
     * Constructs a new event.
     *
     * @param source    the event source (the dateline)
     * @param target    the event target (the dateline)
     * @param eventType the type of event
     * @param startTime the new start time of the visible range
     * @param endTime   the new end time of the visible range
     * @param zoneId    the time zone currently shown by the dateline
     * @since 1.0
     */
    public DatelineScrollingEvent(Object source, EventTarget target,
                                  EventType<DatelineScrollingEvent> eventType, Instant startTime,
                                  Instant endTime, ZoneId zoneId) {

        super(source, target, eventType);

        requireNonNull(startTime);
        requireNonNull(endTime);
        requireNonNull(zoneId);

        this.startTime = startTime;
        this.endTime = endTime;
        this.zoneId = zoneId;
    }

    /**
     * Returns the new start time of the visible time range shown by the dateline / the gantt chart.
     *
     * @return the start time of the visible range
     * @since 1.0
     */
    public final Instant getStartTime() {
        return startTime;
    }

    /**
     * Returns the new end time of the visible time range shown by the dateline / the gantt chart.
     *
     * @return the end time of the visible range
     * @since 1.0
     */
    public final Instant getEndTime() {
        return endTime;
    }

    /**
     * Returns the ID of the time zone currently displayed by the dateline.
     *
     * @return the time zone
     * @since 1.0
     */
    public final ZoneId getZoneId() {
        return zoneId;
    }
}