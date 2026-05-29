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

    @Override
    public Instant getEarliestTimeUsed() {
        return Instant.MIN;
    }

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

    protected void fireEvent(RepositoryEvent evt) {
        if (DND.isLoggable(Level.FINER)) {
            DND.finer("firing event: " + evt);
        }

        Event.fireEvent(this, evt);
    }
}
