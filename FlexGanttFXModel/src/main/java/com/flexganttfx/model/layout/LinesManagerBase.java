/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.layout;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;

import java.util.Objects;

/**
 * A base class for new {@link LinesManager} types.
 *
 * @param <A>
 *            the type of the activities
 * @since 1.0
 */
public abstract class LinesManagerBase<A extends Activity> implements LinesManager<A> {

	private final Row<?, ?, ?> row;

	/**
	 * Constructs a new lines manager.
	 *
	 * @param row
	 *            the row that will be managed by this manager class
	 * @since 1.0
	 */
	public LinesManagerBase(Row<?, ?, ?> row) {
		Objects.requireNonNull(row);

		this.row = row;
	}

	/**
	 * Returns the row managed by this manager.
	 *
	 * @return the managed row
	 * @since 1.0
	 */
	public Row<?, ?, ?> getRow() {
		return row;
	}
}
