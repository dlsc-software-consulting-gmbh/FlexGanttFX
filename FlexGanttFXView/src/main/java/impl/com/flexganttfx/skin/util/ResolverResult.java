/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
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
