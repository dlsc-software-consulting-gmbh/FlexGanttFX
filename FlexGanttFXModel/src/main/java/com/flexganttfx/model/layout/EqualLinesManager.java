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
package com.flexganttfx.model.layout;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.exception.IllegalLineIndexException;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;

import static java.util.Objects.requireNonNull;

/**
 * A lines manager that equally distributes the available row height to all
 * lines.
 *
 * @see Row#getLineCount()
 * @param <R>
 *            the type of the row
 * @param <A>
 *            the type of the activities
 * @since 1.0
 */
public class EqualLinesManager<R extends Row<?, ?, A>, A extends Activity> implements LinesManager<A> {

	private final R row;
	private final InvalidationListener clearCacheListener = observable -> clearCache();
	private final WeakInvalidationListener weakClearCacheListener = new WeakInvalidationListener(clearCacheListener);

	/**
	 * Constructs a new lines manager for the given row. The manager attaches
	 * listeners to {@link Row#lineCountProperty()} and
	 * {@link Row#heightProperty()} in order to recalculate the line locations
	 * and heights.
	 *
	 * @param row
	 *            the row for which to use the manager
	 */
	public EqualLinesManager(R row) {
		requireNonNull(row);

		this.row = row;
		this.row.lineCountProperty().addListener(weakClearCacheListener);
		this.row.heightProperty().addListener(weakClearCacheListener);
	}

	/**
	 * Returns the row for which the manager is used.
	 *
	 * @return the row
	 */
	public final R getRow() {
		return row;
	}

	private void clearCache() {
		lineLocations = null;
		lineHeights = null;
	}

	private void assertLineIndex(int lineIndex)
			throws IllegalLineIndexException {
		if (lineIndex < 0 || lineIndex >= row.getLineCount()) {
			throw new IllegalLineIndexException(row, lineIndex,
					row.getLineCount());
		}
	}

	private double[] lineLocations;

	@Override
	public final double getLineLocation(int lineIndex, double rowHeight)
			throws IllegalLineIndexException {

		assertLineIndex(lineIndex);

		if (lineLocations == null) {
			int s = row.getLineCount();
			lineLocations = new double[s];
			double h = row.getHeight() / s;
			for (int i = 0; i < s; i++) {
				lineLocations[i] = i * h;
			}
		}

		return lineLocations[lineIndex];
	}

	private Layout lineLayout;

	@Override
	public Layout getLineLayout(int lineIndex)
			throws IllegalLineIndexException {
		assertLineIndex(lineIndex);

		if (lineLayout == null) {
			lineLayout = new GanttLayout();
		}

		return lineLayout;
	}

	private double[] lineHeights;

	@Override
	public final double getLineHeight(int lineIndex, double rowHeight)
			throws IllegalLineIndexException {

		assertLineIndex(lineIndex);

		if (lineHeights == null) {
			int s = row.getLineCount();
			lineHeights = new double[s];
			double h = row.getHeight() / s;
			for (int i = 0; i < s; i++) {
				lineHeights[i] = h;
			}
		}

		return lineHeights[lineIndex];
	}

	@Override
	public int getLineIndex(A activity) {
		return -1;
	}
}
