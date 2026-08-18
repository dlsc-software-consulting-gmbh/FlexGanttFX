/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.model.repository;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;

import java.time.Instant;
import java.util.logging.Level;

import static com.flexganttfx.core.LoggingDomain.DND;

/**
 * An abstract base implementation of {@link ActivityRepository} that provides support
 * for event handlers (adding, removing, firing events).
 *
 * @param <A> the type of the activities stored in this repository
 * @since 1.0
 */
public abstract class ActivityRepositoryBase<A extends Activity> implements ActivityRepository<A> {

    /**
     * Constructs a new repository.
     *
     * @since 1.0
     */
    protected ActivityRepositoryBase() {
    }

    private final ObservableList<EventHandler<RepositoryEvent>> repositoryListeners = FXCollections.observableArrayList();

    @Override
    public void addEventHandler(EventHandler<RepositoryEvent> l) {
        repositoryListeners.add(l);
    }

    @Override
    public void removeEventHandler(EventHandler<RepositoryEvent> l) {
        repositoryListeners.remove(l);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This default implementation always returns {@link Instant#MIN}, as the base class
     * does not know anything about the way the activities are stored. Subclasses should
     * override this method whenever they are able to determine the earliest time used.
     */
    @Override
    public Instant getEarliestTimeUsed() {
        return Instant.MIN;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This default implementation always returns {@link Instant#MAX}, as the base class
     * does not know anything about the way the activities are stored. Subclasses should
     * override this method whenever they are able to determine the latest time used.
     */
    @Override
    public Instant getLatestTimeUsed() {
        return Instant.MAX;
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return tail.append((event, tail1) -> {
            if (event instanceof RepositoryEvent) {
                for (EventHandler<RepositoryEvent> handler : repositoryListeners) {
                    handler.handle((RepositoryEvent) event);
                }
            }

            return event;
        });
    }

    /**
     * Fires the given repository event so that all registered event handlers get
     * notified about a change of the repository content.
     *
     * @param evt the event that will be delivered to the registered handlers
     * @throws NullPointerException if the given event is {@code null}
     * @see #addEventHandler(EventHandler)
     */
    protected void fireEvent(RepositoryEvent evt) {
        if (DND.isLoggable(Level.FINER)) {
            DND.finer("firing event: " + evt);
        }

        Event.fireEvent(this, evt);
    }
}
