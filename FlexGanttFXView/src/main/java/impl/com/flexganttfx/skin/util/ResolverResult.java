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
package impl.com.flexganttfx.skin.util;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import com.flexganttfx.model.Activity;

/**
 * The result object returned by the {@link Resolver} class after resolving
 * conflicts between overlapping activities.
 *
 * @see Resolver#resolve(java.util.List)
 *
 * @param <A>
 *            the type of the activities
 */
public final class ResolverResult<A extends Activity> {

	private final int maxColumnsCount;
	private final Map<A, Placement<A>> placements;

	/**
	 * Constructs a new resolver result.
	 *
	 * @param placements
	 *            the position map
	 * @param maxColumnsCount
	 *            the maximum number of columns needed for all clusters
	 */
	public ResolverResult(Map<A, Placement<A>> placements, int maxColumnsCount) {
		this.placements = requireNonNull(placements);
		this.maxColumnsCount = maxColumnsCount;
	}

	/**
	 * Returns the placement map.
	 *
	 * @return the placements
	 */
	public Map<A, Placement<A>> getPlacements() {
		return placements;
	}

	/**
	 * The maximum number of columns for all clusters found as part of the
	 * resolution.
	 *
	 * @return the maximum number of columns
	 */
	public int getMaxColumns() {
		return maxColumnsCount;
	}
}
