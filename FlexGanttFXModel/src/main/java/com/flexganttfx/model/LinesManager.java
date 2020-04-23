/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model;

import com.flexganttfx.model.exception.IllegalLineIndexException;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.layout.GanttLayout;

/**
 * A lines manager is used to control the layout of lines inside a row.
 * Activities located on different lines do not overlap each other, except if
 * the lines themselves overlap each other. Each line can have its own height
 * and a location within the row. Each line can also have its own {@link Layout}
 * . By using lines and layouts it is possible to display activities that belong
 * to the same row in different ways (see {@link ChartLayout},
 * {@link AgendaLayout}, {@link GanttLayout}).
 *
 * @param <A>
 *            the type of the activities that will be shown on the lines
 * @since 1.0
 */
public interface LinesManager<A extends Activity> {

	/**
	 * Returns the line index for the given activity. This method is responsible
	 * for placing activities on different lines.
	 * 
	 * @param activity
	 *            the activity for which a line index is requested
	 * @return the line index of the given activity
	 * @since 1.0
	 */
	int getLineIndex(A activity);

	/**
	 * Returns the location of the line with the given index. In most cases the
	 * value of the location is somewhere between 0 and {@link Row#getHeight()}.
	 * Lines are free to overlap each other.
	 * 
	 * @param lineIndex
	 *            the index of the line
	 * @param rowHeight
	 *            the height of the row where the line is located
	 * @return the location of the given line (y-coordinate)
	 * @throws IllegalLineIndexException
	 *             if no line with the given index exists
	 * @since 1.0
	 */
	double getLineLocation(int lineIndex, double rowHeight);

	/**
	 * Returns the height of the line with the given index. In most cases the
	 * height is somewhere between 0 and {@link Row#getHeight()}.
	 * 
	 * @param lineIndex
	 *            the index of the line
	 * @param rowHeight
	 *            the height of the row where the line is located
	 * @return the height of the given line
	 * @throws IllegalLineIndexException
	 *             if no line with the given index exists
	 * @since 1.0
	 */
	double getLineHeight(int lineIndex, double rowHeight);

	/**
	 * Returns the layout for the line with the given line index. A row and each
	 * line within a row can have their own layout.
	 * 
	 * @param lineIndex
	 *            the index of the line
	 * @return the layout of the given line
	 * @throws IllegalLineIndexException
	 *             if no line with the given index exists
	 * @since 1.0
	 */
	Layout getLineLayout(int lineIndex);
}
