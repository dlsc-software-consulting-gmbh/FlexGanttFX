/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.repository;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.Layer;

/**
 * The required interface for a repository that is also mutable (activities
 * can be added and removed).
 *
 * @param <A> the activity type
 * @since 1.0
 */
public interface MutableActivityRepository<A extends Activity> extends ActivityRepository<A> {

    // add

    /**
     * Adds the given activity to the repository.
     *
     * @param activity the activity
     * @since 1.0
     */
    void addActivity(ActivityRef<A> activity);

    /**
     * Removes the given activity from the repository.
     *
     * @param activity the activity
     * @since 1.0
     */
    void removeActivity(ActivityRef<A> activity);

    // clear

    /**
     * Removes all activities from all layers from the repository.
     *
     * @since 1.0
     */
    void clearActivities();

    /**
     * Removes the activities on the given layer from the repository.
     *
     * @param layer the layer to clear
     * @since 1.0
     */
    void clearActivities(Layer layer);
}
