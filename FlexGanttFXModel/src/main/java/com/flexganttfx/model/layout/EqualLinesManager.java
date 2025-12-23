/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.layout;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.exception.IllegalLineIndexException;

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
		this.row.lineCountProperty().addListener(observable -> clearCache());
		this.row.heightProperty().addListener(observable -> clearCache());
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
