/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
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

    /**
     * The time property. Exposes the current tracked time.
     *
     * @return the time property
     */
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

    /**
     * Updates the tracked time with the current instant.
     */
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
