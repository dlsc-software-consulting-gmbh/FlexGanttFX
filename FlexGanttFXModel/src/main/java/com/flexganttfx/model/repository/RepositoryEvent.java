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

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.ActivityRepository;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

import static java.util.Objects.requireNonNull;

/**
 * An event class used by activity repositories to inform event handlers about
 * changes.
 *
 * @see ActivityRepository#addEventHandler(EventHandler)
 * @see ActivityRepository#removeEventHandler(EventHandler)
 * @since 1.0
 */
public final class RepositoryEvent extends Event {

    private static final long serialVersionUID = -635203617936620841L;

    /**
     * An event type that indicates that "something" inside the repository has changed.
     *
     * @since 1.0
     */
    public static final EventType<RepositoryEvent> REPOSITORY_CHANGED = new EventType<>(Event.ANY, "REPOSITORY_CHANGED");

    /**
     * An event type that indicates that an activity has been added to the repository.
     *
     * @since 1.0
     */
    public static final EventType<RepositoryEvent> ACTIVITY_ADDED = new EventType<>(RepositoryEvent.REPOSITORY_CHANGED, "ACTIVITY_ADDED");

    /**
     * An event type that indicates that an activity has been removed from the repository.
     *
     * @since 1.0
     */
    public static final EventType<RepositoryEvent> ACTIVITY_REMOVED = new EventType<>(RepositoryEvent.REPOSITORY_CHANGED, "ACTIVITY_REMOVED");

    private ActivityRef<?> activityRef;

    private final ActivityRepository<?> repository;

    /**
     * Constructs a new repository event.
     *
     * @param eventType   the type of the event, e.g. #ACTIVITY_ADDED
     * @param repository  the repository where the event occurred
     * @param activityRef the affected activity (ref)
     * @throws NullPointerException if the event type, the repository, or the activity reference is {@code null}
     * @since 1.0
     */
    public RepositoryEvent(EventType<RepositoryEvent> eventType, ActivityRepository<?> repository, ActivityRef<?> activityRef) {
        super(repository, repository, eventType);

        requireNonNull(repository);
        requireNonNull(activityRef);

        this.activityRef = activityRef;
        this.repository = repository;
    }

    /**
     * Constructs a new repository event, the event type will be set to #REPOSITORY_CHANGED.
     *
     * @param repository the repository where the event occurred
     * @since 1.0
     */
    public RepositoryEvent(ActivityRepository<?> repository) {
        super(repository, repository, REPOSITORY_CHANGED);

        this.repository = repository;
    }

    /**
     * Returns the affected activity (ref).
     *
     * @return the activity
     * @since 1.0
     */
    public final ActivityRef<?> getActivityRef() {
        return activityRef;
    }

    /**
     * Returns the affected repository.
     *
     * @return the repository
     * @since 1.0
     */
    public final ActivityRepository<?> getRepository() {
        return repository;
    }
}
