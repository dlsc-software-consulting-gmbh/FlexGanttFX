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
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.util.IntervalTree;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.flexganttfx.core.LoggingDomain.REPOSITORY;
import static com.flexganttfx.model.repository.RepositoryEvent.ACTIVITY_ADDED;
import static com.flexganttfx.model.repository.RepositoryEvent.ACTIVITY_REMOVED;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;

/**
 * A repository implementation that utilizes binary interval trees to store its
 * activities. A binary tree provides the best performance when searching for
 * activities within a given time interval when the total number of activities is
 * very large. The repository manages one binary tree per layer.
 *
 * @param <A> the activity type
 *           @see com.flexganttfx.model.Row#setRepository(ActivityRepository)
 * @since 1.0
 */
public class IntervalTreeActivityRepository<A extends Activity> extends MutableActivityRepositoryBase<A> {

    private final Map<Layer, IntervalTree<A>> treeMap = new HashMap<>();

    private IntervalTree<A> getTree(Layer layer) {
        IntervalTree<A> tree = treeMap.get(layer);
        if (tree == null) {
            tree = new IntervalTree<>();
            treeMap.put(layer, tree);
        }

        return tree;
    }

    @Override
    public final void addActivity(ActivityRef<A> activityRef) {
        A activity = activityRef.getActivity();
        Layer layer = activityRef.getLayer();
        IntervalTree<A> tree = getTree(layer);
        tree.add(activity);

        if (REPOSITORY.isLoggable(FINER)) {
            REPOSITORY.finer("added activity " + activity.getName()
                    + ", layer = " + layer.getName() + ", row = "
                    + activityRef.getRow().getName() + ", activity count = "
                    + tree.size());
        }

        fireEvent(new RepositoryEvent(ACTIVITY_ADDED, this, activityRef));
    }

    @Override
    public final void removeActivity(ActivityRef<A> activityRef) {
        A activity = activityRef.getActivity();
        Layer layer = activityRef.getLayer();

        IntervalTree<A> tree = getTree(layer);
        boolean member = tree.remove(activity);

        if (REPOSITORY.isLoggable(FINER)) {
            REPOSITORY.finer("removed activity " + activity.getName()
                    + " from layer " + layer.getName() + ", row = "
                    + activityRef.getRow().getName() + ", was member = "
                    + member + ", new activities count = " + tree.size());
        }

        if (!member) {
            throw new IllegalArgumentException(
                    "given activity was not a member of this repository, maybe the start and / or end time were modified before the remove?");

        }

        fireEvent(new RepositoryEvent(ACTIVITY_REMOVED, this, activityRef));
    }

    @Override
    public final void clearActivities() {
        for (Layer layer : treeMap.keySet()) {
            IntervalTree<A> tree = getTree(layer);
            tree.clear();
        }

        fireEvent(new RepositoryEvent(this));
    }

    @Override
    public final void clearActivities(Layer layer) {
        requireNonNull(layer);

        IntervalTree<A> tree = getTree(layer);
        tree.clear();

        fireEvent(new RepositoryEvent(this));
    }

    @Override
    public final Instant getEarliestTimeUsed() {
        if (!treeMap.isEmpty()) {

            Instant time = null;

            for (IntervalTree<A> tree : treeMap.values()) {
                Instant earliest = tree.getEarliestTimeUsed();

                if (earliest != null) {
                    if (time == null || earliest.isBefore(time)) {
                        time = earliest;
                    }
                }
            }

            if (REPOSITORY.isLoggable(FINER)) {
                REPOSITORY.finer("returning " + time
                        + " for earliest time used");
            }

            return time;
        }

        return null;
    }

    @Override
    public final Instant getLatestTimeUsed() {
        if (!treeMap.isEmpty()) {

            Instant time = null;

            for (IntervalTree<A> tree : treeMap.values()) {
                Instant latest = tree.getLatestTimeUsed();
                if (latest != null) {
                    if (time == null || latest.isAfter(time)) {
                        time = latest;
                    }
                }
            }

            if (REPOSITORY.isLoggable(FINER)) {
                REPOSITORY.finer("returning " + time + " for latest time used");
            }

            return time;
        }

        return null;
    }

    @Override
    public final Iterator<A> getActivities(Layer layer, Instant startTime, Instant endTime, TemporalUnit temporalUnit, ZoneId zoneId) {

        if (REPOSITORY.isLoggable(FINEST)) {
            REPOSITORY.finest("layer = " + layer + ", temporal unit = "
                    + temporalUnit + ", zone = " + zoneId + ", startTime = "
                    + startTime + ", endTime = " + endTime);
        }

        IntervalTree<A> tree = getTree(layer);
        Collection<A> activities = tree.getIntersectingObjects(startTime.toEpochMilli(), endTime.toEpochMilli());
        return activities.iterator();
    }

    /**
     * Returns all activities for all layers.
     *
     * @return all activities
     * @since 1.0
     */
    public final List<A> getAllActivities() {
        Instant st = getEarliestTimeUsed();
        Instant et = getLatestTimeUsed();

        List<A> result = new ArrayList<>();

        if (st != null && et != null) {
            for (IntervalTree<A> tree : treeMap.values()) {
                result.addAll(tree.getIntersectingObjects(st.toEpochMilli(), et.toEpochMilli()));
            }
        }

        return result;
    }
}
