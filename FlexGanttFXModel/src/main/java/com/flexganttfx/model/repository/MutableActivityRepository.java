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
