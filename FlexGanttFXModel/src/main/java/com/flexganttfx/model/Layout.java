/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model;

import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.layout.ChartLayout;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Each row and each inner line of a row are associated with a layout. The
 * layout influences several aspects during rendering and editing of activities.
 * Additionally several of the system layers used to draw the row background
 * also utilize the layout information.
 * <p>
 * The following layout types are supported:
 *
 * <ul>
 * <li>GanttLayout: activities are laid out horizontally below the timeline.<br>
 * <img src="doc-files/layout-gantt.png" alt="Gantt Layout"></li>
 * <li>AgendaLayout: activities are laid out vertically next to a time scale
 * displaying the time of day. Hour lines are drawn in the background.<br>
 * <img src="doc-files/layout-agenda.png" alt="Agenda Layout"></li>
 * <li>ChartLayout: activities are laid out as bars below the timeline. Chart
 * lines are drawn in the background.<br>
 * <img src="doc-files/layout-capacity.png" alt="Capacity Layout"></li>
 * </ul>
 *
 * @see Row#setLayout(Layout)
 * @see Row#getLineLayout(int)
 * @see LinesManager#getLineLayout(int)
 *
 * @since 1.0
 */
public abstract class Layout {

	// Padding support.

	private DoubleProperty padding = new SimpleDoubleProperty(this, "padding",
			0);

	/**
	 * Returns the property used to specify a padding that will be added to the
	 * top and the bottom of a row or an inner line.
	 *
	 * @return the padding property
	 * @since 1.0
	 */
	public final DoubleProperty paddingProperty() {
		return padding;
	}

	/**
	 * Returns the value of {@link #paddingProperty()}.
	 *
	 * @return the padding value
	 * @since 1.0
	 */
	public final double getPadding() {
		return paddingProperty().get();
	}

	/**
	 * Sets the value of the {@link #paddingProperty()}.
	 *
	 * @param padding
	 *            the new padding value
	 * @since 1.0
	 */
	public final void setPadding(double padding) {
		paddingProperty().set(padding);
	}

	/**
	 * Determines if the UI should be able to show a horizontal cursor line. Currently only the
	 * {@link ChartLayout} and the {@link AgendaLayout} support this.
	 *
	 * @return true if a horizontal cursor line makes sense
	 * @since 1.4
	 */
    public abstract boolean isSupportingHorizontalCursorLine();
}
