/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.util;

import com.flexganttfx.model.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A utility class to fix the problem of overlapping activities. The resolver
 * will calculate the positions for each activity.
 */
public final class Resolver {

    /**
     * Constructs a new resolver.
     */
    private Resolver() {
    }

    /**
     * Resolves overlapping conflicts for the given activities.
     *
     * @param <A> the activity type
     * @param activities
     *            the activities
     * @return the resolving result
     */
    public static <A extends Activity> ResolverResult<A> resolve(
            List<A> activities) {
        return resolve(activities, activity -> true);
    }

    /**
     * Resolves overlapping conflicts for the given activities.
     *
     * @param <A> the activity type
     * @param activities
     *            the activities
     * @param filter
     *            a predicate to determine which activities will be considered
     *            at all for the resolution strategy
     * @return the resolving result
     * @since 1.4
     */
    public static <A extends Activity> ResolverResult<A> resolve(
            List<A> activities, Predicate<A> filter) {
        int maxColumns = 0;

        final Map<A, Placement<A>> placements = new HashMap<>();

        if (activities.size() == 1) {
            /*
			 * Special case when there is only a single activity in the list.
			 */
            A activity = activities.get(0);
            placements.put(activity, new Placement<>(activity, 0, 1));
            maxColumns = 1;
        } else {
            List<Cluster<A>> clusters = new ArrayList<>();

            Cluster<A> cluster = null;

            for (A activity : activities) {
                if (cluster == null || !cluster.intersects(activity)) {
                    cluster = new Cluster<>();
                    clusters.add(cluster);
                }

                cluster.add(activity);
            }

            for (Cluster<A> c : new ArrayList<>(clusters)) {

                if (c.getActivities().size() == 1) {
					/*
					 * Specical case when there is only a single activity in the
					 * cluster.
					 */
                    A activity = c.getActivities().get(0);
                    placements.put(activity, new Placement<>(activity, 0, 1));
                    maxColumns = Math.max(maxColumns, 1);
                } else {
                    placements.putAll(c.resolve(filter));
                    maxColumns = Math.max(maxColumns, c.getColumnCount());
                }
            }
        }

        return new ResolverResult<>(placements, maxColumns);
    }
}
