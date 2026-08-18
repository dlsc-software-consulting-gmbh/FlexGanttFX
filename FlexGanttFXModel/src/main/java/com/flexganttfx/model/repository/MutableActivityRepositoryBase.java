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

/**
 * An abstract base implementation for repositories that not only store activities but
 * also support adding and removing them at runtime. It combines the event handler
 * support inherited from {@link ActivityRepositoryBase} with the mutation operations
 * defined by {@link MutableActivityRepository}.
 * <p>
 * Subclasses only need to implement the actual storage and lookup logic. Whenever the
 * content of the repository changes they should notify listeners by calling
 * {@link #fireEvent(RepositoryEvent)}.
 *
 * @param <A> the type of the activities stored in this repository
 * @see ListActivityRepository
 * @see IntervalTreeActivityRepository
 */
public abstract class MutableActivityRepositoryBase<A extends Activity> extends ActivityRepositoryBase<A> implements MutableActivityRepository<A> {

	/**
	 * Constructs a new mutable repository.
	 */
	public MutableActivityRepositoryBase() {
	}
}
