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
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.util.ActivityComparator;
import com.flexganttfx.model.util.ActivityHelper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.flexganttfx.core.LoggingDomain.REPOSITORY;
import static com.flexganttfx.model.repository.ListActivityRepository.IteratorType.BINARY_ITERATOR;
import static com.flexganttfx.model.repository.RepositoryEvent.ACTIVITY_ADDED;
import static com.flexganttfx.model.repository.RepositoryEvent.ACTIVITY_REMOVED;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.*;

/**
 * A repository implementation that utilizes several lists for storing activities.
 * The repository can return three different iterator types when the application is
 * asking for the activities within a given time interval.
 *
 * @param <A> the type of the activities
 * @since 1.0
 */
public class ListActivityRepository<A extends Activity> extends MutableActivityRepositoryBase<A> {

    /**
     * A list of possible iterators that the repository can return when the
     * application queries the repository for the activities within a given
     * time interval.
     *
     * @since 1.0
     */
    public enum IteratorType {

        /**
         * Causes the repository to return the normal list iterator (
         * {@link List#iterator()}), which means that the repository will always
         * return all activities independent of whether they are located within
         * the given time interval.
         *
         * @since 1.0
         */
        SIMPLE_ITERATOR,

        /**
         * Causes the repository to return an instance of type
         * {@link BinarySearchActivityIterator}. This type of iterator performs
         * a log-n binary search to find only those activities that are located
         * within the given time interval.
         *
         * @since 1.0
         */
        BINARY_ITERATOR,

        /**
         * Causes the repository to return an instance of type
         * {@link LinearSearchActivityIterator}. This type of iterator performs
         * a left to right (index = 0 to n) within the underlying list to find
         * the first visible activity inside the given time interval. The
         * iterator stops once it reaches an activity with a start time after
         * the given time interval.
         *
         * @since 1.0
         */
        LINEAR_ITERATOR
    }

    private IteratorType iteratorType = BINARY_ITERATOR;

    /*
     * Package private for testing purposes only.
     */
    private final Map<Layer, List<A>> activitiesMap = new HashMap<>();

    /**
     * Constructs a new repository that returns a binary iterator when the
     * application queries the repository for the activities within a specific
     * time interval.
     *
     * @see com.flexganttfx.model.repository.ListActivityRepository.IteratorType#BINARY_ITERATOR
     * @since 1.0
     */
    public ListActivityRepository() {
        this(IteratorType.BINARY_ITERATOR);
    }

    /**
     * Constructs a new repository that returns an iterator of the specified type
     * when the application queries the repository for the activities within a specific
     * time interval.
     *
     * @param iteratorType the type of iterator used for time interval queries
     * @throws NullPointerException if the given iterator type is {@code null}
     * @since 1.0
     */
    public ListActivityRepository(IteratorType iteratorType) {
        setIteratorType(iteratorType);
    }

    /**
     * Returns the iterator type that is being returned by the repository.
     *
     * @return the iterator type
     * @since 1.0
     */
    public final IteratorType getIteratorType() {
        return iteratorType;
    }

    /**
     * Sets a different iterator type on this repository.
     *
     * @param iteratorType the new iterator type
     * @throws NullPointerException if the given iterator type is {@code null}
     * @see IteratorType
     * @since 1.0
     */
    public final void setIteratorType(IteratorType iteratorType) {
        requireNonNull(iteratorType);

        if (REPOSITORY.isLoggable(FINE)) {
            REPOSITORY.fine("setting iterator type to " + iteratorType);
        }

        this.iteratorType = iteratorType;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The type of the returned iterator depends on the value of
     * {@link #getIteratorType()}. Note that {@link IteratorType#SIMPLE_ITERATOR} returns
     * <b>all</b> activities of the layer, no matter whether they intersect with the
     * given time interval or not.
     *
     * @see #setIteratorType(IteratorType)
     */
    @Override
    public final Iterator<A> getActivities(Layer layer, Instant startTime,
                                           Instant endTime, TemporalUnit temporalUnit, ZoneId zoneId) {

        if (REPOSITORY.isLoggable(FINEST)) {
            REPOSITORY.fine("layer = " + layer + ", temporal unit = "
                    + temporalUnit + ", zone = " + zoneId + ", startTime = "
                    + startTime + ", endTime = " + endTime);
        }

        switch (iteratorType) {
            case BINARY_ITERATOR:
                if (REPOSITORY.isLoggable(FINEST)) {
                    REPOSITORY.fine("using iterator of type "
                            + BinarySearchActivityIterator.class.getName());
                }

                return new BinarySearchActivityIterator<>(getActivities(layer),
                        startTime, endTime);
            case LINEAR_ITERATOR:
                if (REPOSITORY.isLoggable(FINEST)) {
                    REPOSITORY.fine("using iterator of type "
                            + LinearSearchActivityIterator.class.getName());
                }

                return new LinearSearchActivityIterator<>(getActivities(layer),
                        startTime, endTime);
            case SIMPLE_ITERATOR:
                if (REPOSITORY.isLoggable(FINEST)) {
                    REPOSITORY
                            .fine("using the iterator provided by the list (which returns ALL activities)");
                }

                return getActivities(layer).iterator();
            default:
                throw new IllegalArgumentException("unknown iterator type: "
                        + iteratorType);
        }
    }

    @Override
    public final void addActivity(ActivityRef<A> activityRef) {
        requireNonNull(activityRef);

        A activity = activityRef.getActivity();
        Layer layer = activityRef.getLayer();
        List<A> activities = getActivities(layer);

        int index = Collections.binarySearch(activities, activity,
                ActivityComparator.getInstance());

        if (index < 0) {
            activities.add(-index - 1, activity);
        }

        if (REPOSITORY.isLoggable(FINER)) {
            REPOSITORY.finer("added activity " + activity.getName()
                    + ", layer = " + layer.getName() + ", row = "
                    + activityRef.getRow().getName()
                    + ", new activities count = " + activities.size());
        }

        fireEvent(new RepositoryEvent(ACTIVITY_ADDED, this, activityRef));
    }

    @Override
    public final void removeActivity(ActivityRef<A> activityRef) {
        requireNonNull(activityRef);

        A activity = activityRef.getActivity();
        Layer layer = activityRef.getLayer();
        List<A> activities = getActivities(layer);
        activities.remove(activity);

        if (REPOSITORY.isLoggable(FINER)) {
            REPOSITORY.finer("removed activity " + activity.getName()
                    + " from layer " + layer.getName() + ", row = "
                    + activityRef.getRow().getName() + ", member = "
                    + getActivities(layer).contains(activity)
                    + ", new activities count = " + activities.size());
        }

        fireEvent(new RepositoryEvent(ACTIVITY_REMOVED, this, activityRef));
    }

    @Override
    public final void clearActivities() {
        for (Layer layer : activitiesMap.keySet()) {
            List<A> activities = getActivities(layer);
            activities.clear();
        }

        fireEvent(new RepositoryEvent(this));
    }

    @Override
    public final void clearActivities(Layer layer) {
        List<A> activities = getActivities(layer);
        activities.clear();

        fireEvent(new RepositoryEvent(this));
    }

    @Override
    public final Instant getEarliestTimeUsed() {
        if (!activitiesMap.isEmpty()) {

            Instant time = null;

            for (List<A> list : activitiesMap.values()) {
                if (!list.isEmpty()) {
                    A activity = list.get(0);
                    if (time == null || activity.getStartTime().isBefore(time)) {
                        time = activity.getStartTime();
                    }
                }
            }

            if (REPOSITORY.isLoggable(FINE)) {
                if (time != null) {
                    REPOSITORY.fine("returning "
                            + DateTimeFormatter.ofLocalizedDateTime(
                            FormatStyle.SHORT).format(time)
                            + " for earliest time used");
                } else {
                    REPOSITORY.fine("no earliest time found");
                }
            }

            return time;
        }

        return null;
    }

    @Override
    public final Instant getLatestTimeUsed() {
        if (!activitiesMap.isEmpty()) {

            Instant time = null;

            for (List<A> list : activitiesMap.values()) {
                if (!list.isEmpty()) {
                    A activity = list.get(list.size() - 1);
                    if (time == null || activity.getEndTime().isAfter(time)) {
                        time = activity.getEndTime();
                    }
                }
            }

            if (REPOSITORY.isLoggable(FINE)) {
                if (time != null) {
                    REPOSITORY.fine("returning "
                            + DateTimeFormatter.ofLocalizedDateTime(
                            FormatStyle.SHORT).format(time)
                            + " for latest time used");
                } else {
                    REPOSITORY.fine("no latest time found");
                }
            }

            return time;
        }

        return null;
    }

    /*
     * Package private for testing purposes only.
     */
    List<A> getActivities(Layer layer) {
        List<A> activities = activitiesMap.get(layer);

        if (activities == null) {
            activities = new ArrayList<>();
            activitiesMap.put(layer, activities);
        }

        return activities;
    }

    /**
     * Returns all activities on all layers.
     *
     * @return all activities
     * @since 1.0
     */
    public final List<A> getAllActivities() {
        List<A> result = new ArrayList<>();
        activitiesMap.values().forEach(result::addAll);
        return result;
    }

    static class LinearSearchActivityIterator<T extends Activity> implements
            Iterator<T> {

        private static final Logger LOGGER = Logger
                .getLogger(LinearSearchActivityIterator.class.getName());

        private final List<T> activities;

        private int index = -1;

        private final Instant startTime;

        private final Instant endTime;

        private final boolean reverse;

        public LinearSearchActivityIterator(List<T> activities,
                                            Instant startTime, Instant endTime, boolean reverse) {

            requireNonNull(activities);
            requireNonNull(startTime);
            requireNonNull(endTime);

            this.activities = activities;
            this.startTime = startTime;
            this.endTime = endTime;
            this.reverse = reverse;
            this.index = findFirstObject();

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("object list size = " + activities.size());
                LOGGER.fine("start time = " + startTime);
                LOGGER.fine("end time = " + startTime);
                LOGGER.fine("reverse = " + reverse);
                LOGGER.fine("index of first activity = " + index);
            }
        }

        public LinearSearchActivityIterator(List<T> objectList,
                                            Instant startTime, Instant endTime) {
            this(objectList, startTime, endTime, false);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("index = " + index);
            }
            if (index >= 0 && index < activities.size()) {
                T activity = activities.get(index);
                return ActivityHelper.intersect(activity.getStartTime(),
                        activity.getEndTime(), startTime, endTime);
            }
            return false;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#next()
         */
        @Override
        public T next() {
            if (hasNext()) {
                if (reverse) {
                    return activities.get(index--);
                }
                return activities.get(index++);
            }
            throw new NoSuchElementException(
                    "iterator has no more elements to return");
        }

        /*
         * Returns the index of the first entry that is visible within the given
         * time span. The algorithm uses a linear search.
         */
        private int findFirstObject() {
            if (activities != null && !activities.isEmpty()) {
                if (reverse) {
                    for (int i = activities.size() - 1; i >= 0; i--) {
                        T activity = activities.get(i);
                        if (ActivityHelper.intersect(startTime, endTime,
                                activity.getStartTime(), activity.getEndTime())) {
                            return i;
                        }
                    }
                } else {
                    for (int i = 0; i < activities.size(); i++) {
                        T activity = activities.get(i);
                        if (ActivityHelper.intersect(startTime, endTime,
                                activity.getStartTime(), activity.getEndTime())) {
                            return i;
                        }
                    }
                }
            }
            return -1;
        }
    }

    static class BinarySearchActivityIterator<T extends Activity> implements
            Iterator<T> {

        private final List<T> objectList;

        private final Instant startTime;

        private final Instant endTime;

        private int index = -1;

        public BinarySearchActivityIterator(List<T> activities,
                                            Instant startTime, Instant endTime) {

            requireNonNull(activities);
            requireNonNull(startTime);
            requireNonNull(endTime);

            this.objectList = activities;
            this.startTime = startTime;
            this.endTime = endTime;
            this.index = findFirstObject();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            if (objectList.size() > index) {
                T obj = objectList.get(index);
                return !obj.getStartTime().isAfter(endTime);
            }

            return false;
        }

        @Override
        public T next() {
            if (hasNext()) {
                return objectList.get(index++);
            }
            throw new NoSuchElementException(
                    "iterator has no more elements to return");
        }

        /*
         * Returns the valueIndex of the first entry that is visible within the
         * given time span. The algorithm uses a binary search.
         */
        private int findFirstObject() {
            int m;
            int result = 0;
            if (objectList != null && !objectList.isEmpty()) {
                Instant startTime = this.startTime;
                Instant endTime = this.endTime;
                int low = 0;
                int high = objectList.size() - 1;
                do {
                    m = low + (high - low) / 2;
                    T obj = objectList.get(m);
                    Instant su = obj.getStartTime();
                    Instant eu = obj.getEndTime();
                    if (!(eu.isBefore(startTime) || su.isAfter(endTime))) { // it's
                        // visible
                        high = m - 1;
                    } else { // it's not visible
                        if (eu.isBefore(startTime)) {
                            low = m + 1;
                        } else {
                            if (su.isAfter(endTime)) {
                                high = m - 1;
                            }
                        }
                    }
                } while (high >= low);
                result = low;
                if (low > 0) {
                    low = Math.min(low, objectList.size() - 1);
                    // let's scan to the left to see if there are any
                    // other objectList with the SAME start time
                    for (int i = low; low >= 0; low--) {
                        T obj = objectList.get(i);
                        if (ActivityHelper.intersect(obj.getStartTime(),
                                obj.getEndTime(), startTime, endTime)) {
                            result = i; // because it is also visible
                        } else {
                            break;
                        }
                    }
                }
            }
            return result;
        }
    }
}