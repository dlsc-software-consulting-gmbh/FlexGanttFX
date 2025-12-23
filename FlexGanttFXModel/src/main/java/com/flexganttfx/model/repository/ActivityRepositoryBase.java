/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
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
