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
/**
 * Classes related to activity repositories. These repositories
 * are used to store the activities for a row and to return an iterator for
 * iterating over a list of activities returned for a given time interval.
 * <p>
 * Every {@link com.flexganttfx.model.Row} owns exactly one repository. The
 * repository is queried during each rendering pass, which is why the lookup of
 * activities within a time interval has to be fast.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.model.repository.IntervalTreeActivityRepository} - the
 * default repository. Stores the activities inside an interval tree, which makes time
 * interval queries very fast even for large numbers of activities.</li>
 * <li>{@link com.flexganttfx.model.repository.ListActivityRepository} - stores the
 * activities inside a simple list. Supports different iterator strategies (linear,
 * binary, simple).</li>
 * <li>{@link com.flexganttfx.model.repository.ActivityRepositoryBase} - the base class
 * for custom read-only repositories. Adds event handler support.</li>
 * <li>{@link com.flexganttfx.model.repository.MutableActivityRepositoryBase} - the base
 * class for custom repositories that support adding and removing activities.</li>
 * <li>{@link com.flexganttfx.model.repository.RepositoryEvent} - fired whenever
 * activities get added, removed, or cleared.</li>
 * </ul>
 * Custom repositories are, for example, used to implement lazy loading strategies where
 * activities get loaded from a backend only when they are actually needed for rendering.
 *
 * @see com.flexganttfx.model.ActivityRepository
 * @see com.flexganttfx.model.Row#setRepository(com.flexganttfx.model.ActivityRepository)
 *
 * @since 1.0
 */
package com.flexganttfx.model.repository;

