/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.treetable;

import java.io.Serializable;

import com.flexganttfx.model.Row;

import javafx.scene.control.TreeTableRow;

public class GanttChartTreeTableRow<R extends Row<?, ?, ?>> extends
		TreeTableRow<R> implements Serializable {

	private static final long serialVersionUID = -2245080748276282382L;

	public GanttChartTreeTableRow() {
	}

	@Override
	protected double computeMinHeight(double width) {
		if (getItem() != null) {
			return getItem().getMinHeight();
		}

		return Row.DEFAULT_ROW_HEIGHT;
	}

	@Override
	protected double computePrefHeight(double width) {
		if (getItem() != null) {
			return getItem().getHeight();
		}

		return Row.DEFAULT_ROW_HEIGHT;
	}

	@Override
	protected double computeMaxHeight(double width) {
		if (getItem() != null) {
			return getItem().getMaxHeight();
		}

		return Row.DEFAULT_ROW_HEIGHT;
	}
}
