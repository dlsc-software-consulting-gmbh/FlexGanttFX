/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.timeline;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.timeline.TimelineModel;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.time.Instant;
import java.util.logging.Level;

/**
 * A time tracker can be used to update the property
 * {@link TimelineModel#nowProperty()}. In most cases the time "now" will be
 * equivalent to the system time but in simulations this might not be the case.
 * The time tracker can be used in combination with the {@link TimelineModel} by
 * binding the {@link TimelineModel#nowProperty()} to the
 * {@link TimeTracker#timeProperty()}.
 *
 * @since 1.0
 */
public class TimeTracker extends Thread {

    private boolean running = true;

    private long delay = 1000;

    private boolean stopped;

    /**
     * Constructs a new tracker.
     *
     * @since 1.0
     */
    public TimeTracker() {
        setName("Time Tracker");
        setDaemon(true);
    }

    private final ReadOnlyObjectWrapper<Instant> time = new ReadOnlyObjectWrapper<>(this, "time", Instant.now());

    public final ReadOnlyObjectProperty<Instant> timeProperty() {
        return time.getReadOnlyProperty();
    }

    public final Instant getTime() {
        return time.get();
    }

    /**
     * Returns the delay in milliseconds between updates of
     * {@link TimelineModel#nowProperty()}. The default is 1000 millis.
     *
     * @return the default delay between update calls
     * @since 1.0
     */
    public final long getDelay() {
        return delay;
    }

    /**
     * Sets the delay between updates of {@link TimelineModel#nowProperty()}.
     * The default is 1000 millis.
     *
     * @param millis
     *            the new delay
     * @throws IllegalArgumentException
     *             if the delay is zero or smaller
     * @since 1.0
     */
    public final void setDelay(long millis) {
        if (millis <= 0) {
            throw new IllegalArgumentException("delay must be larger than zero but was" + millis);
        }

        this.delay = millis;
    }

    /**
     * Starts the tracking of the time.
     *
     * @since 1.0
     */
    public final void startTracking() {
        if (stopped) {
            throw new IllegalStateException("Time tracker has already been stopped and can not be started again.");
        } else {
            running = true;
            start();
        }
    }

    @Override
    public void run() {
        while (running) {
            Platform.runLater(() -> time.set(getNow()));
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                LoggingDomain.CONFIG.log(Level.WARNING, "problem in update thread", e);
            }
        }
    }

    /**
     * Stops the tracking of the time.
     *
     * @since 1.0
     */
    public final void stopTracking() {
        stopped = true;
        running = false;
    }

    /**
     * Override to return the instant that will be set as "now" on the timeline
     * model. The default implementation uses {@link Instant#now()}.
     *
     * @see TimelineModel#setNow(Instant)
     *
     * @return the "now" instant
     */
    protected Instant getNow() {
        return Instant.now();
    }
}