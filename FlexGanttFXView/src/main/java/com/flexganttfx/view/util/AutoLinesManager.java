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
package com.flexganttfx.view.util;

import com.flexganttfx.model.*;
import com.flexganttfx.model.layout.EqualLinesManager;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.util.Placement;
import impl.com.flexganttfx.skin.util.Resolver;
import impl.com.flexganttfx.skin.util.ResolverResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * A specialized {@link LinesManager} used for ensuring that activities will not
 * overlap each other. This manager will create as many inner lines as needed
 * and will calculate the placement of all activities on these lines.
 *
 * @param <R>
 *            the type of the row that will be managed
 * @param <A>
 *            the type of the activities that will be managed
 *
 * @since 1.2
 */
public class AutoLinesManager<R extends Row<?, ?, A>, A extends Activity>
		extends EqualLinesManager<R, A> {

	private final GraphicsBase<R> graphics;

	private ResolverResult<A> resolverResult;

	private Predicate<A> filter;

	/**
	 * Constructs a new automatic lines manager. The constructor requires a
	 * reference to the graphics view to lookup various parameters that are
	 * needed when the manager queries the activity repository of the row (e.g.
	 * the currently displayed temporal unit and the list of layers).
	 *
	 * @param row
	 *            the managed row
	 * @param graphics
	 *            the graphics view where the manager will be used
	 *
	 * @since 1.2
	 */
	public AutoLinesManager(R row, GraphicsBase<R> graphics) {
		super(row);

		this.graphics = requireNonNull(graphics);

		layout();
	}

	/**
	 * Returns the graphics view where the manager will be used.
	 *
	 * @return the graphics view
	 * @since 1.2
	 */
	public final GraphicsBase<R> getGraphics() {
		return graphics;
	}

	/**
	 * Sets a filter that will be used in combination with a {@link Resolver}
	 * instance to figure out which activities are relevant for the calculation
	 * of overlapping activities (clusters). If the predicate returns false then
	 * the activity will not be considered when creating the clusters.
	 *
	 * @param filter
	 *            the filter
	 */
	public final void setFilter(Predicate<A> filter) {
		this.filter = filter;
	}

	/**
	 * Triggers a layout of the activities and calculates the line count for the
	 * row.
	 *
	 * @since 1.2
	 */
	public final void layout() {
		R row = getRow();
		ActivityRepository<A> repository = row.getRepository();

		Instant st = repository.getEarliestTimeUsed();
		Instant et = repository.getLatestTimeUsed();

		if (st == null || et == null) {
			return;
		}

		List<A> allActivities = new ArrayList<>();
		for (Layer layer : graphics.getLayers()) {
			Iterator<A> activities = repository.getActivities(layer, st, et,
					graphics.getTimeline().getDateline()
							.getPrimaryTemporalUnit(),
					row.getZoneId());
			if (activities != null) {
				activities.forEachRemaining(allActivities::add);
			}
		}

		resolverResult = Resolver.resolve(allActivities, filter);
		row.setLineCount(resolverResult.getMaxColumns());
	}

	@Override
	public final int getLineIndex(A activity) {
		if (resolverResult != null) {
			Map<A, Placement<A>> placements = resolverResult.getPlacements();
			if (placements != null) {
				Placement<A> placement = placements.get(activity);
				if (placement != null) {
					return placement.getColumnIndex();
				}
			}
		}

		return -1;
	}
}
