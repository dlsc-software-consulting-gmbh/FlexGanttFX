/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.container;

import com.flexganttfx.view.util.FlexGanttFXControl;
import com.flexganttfx.view.GanttChartBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A common base class for containers that wrap two or more Gantt charts. Containers
 * are used to keep two the charts synchronized. Synchronized elements are the
 * location of the dividers between the tree table and the graphics area, the timeline
 * state (start time, zoom level, etc...), cursors, etc...
 *
 * @since 1.6
 */
public abstract class ContainerBase<T extends GanttChartBase<?>> extends FlexGanttFXControl {

	private final boolean autoBinding;

	/**
	 * Constructs a new container with the given Gantt chart controls.
	 *
	 * @param autoBinding
	 *            if true many properties of the given controls will be bound to
	 *            their equivalent of the Gantt chart on the first position
	 * @param ganttCharts
	 *            the Gantt charts to add to this container
	 *
	 * @since 1.6
	 */
	@SafeVarargs
    protected ContainerBase(boolean autoBinding, T... ganttCharts) {

		this.autoBinding = autoBinding;

		if (ganttCharts != null) {
			this.ganttCharts.setAll(ganttCharts);
		}
	}

	/**
	 * Constructs a new container with the given Gantt chart controls. The
	 * properties of the charts will be bound to the same properties of the
	 * Gantt chart in the first position.
	 *
	 * @param ganttCharts
	 *            the Gantt charts to add to this container
	 *
	 * @since 1.6
	 */
	@SafeVarargs
    public ContainerBase(T... ganttCharts) {
		this(true, ganttCharts);
	}

	/**
	 * Constructs a new empty container. Gantt charts must be added to the list
	 * returned by {@link #getGanttCharts()}.
	 *
	 * @param autoBinding
	 *            if true, many properties of the given controls will be bound to
	 *            their equivalent of the Gantt chart on the first position
	 *
	 * @since 1.6
	 */
	public ContainerBase(boolean autoBinding) {
		this(autoBinding, (T) null);
	}

	/**
	 * Constructs a new container. Gantt charts must be added to the list
	 * returned by {@link #getGanttCharts()}.The properties of the charts will
	 * be bound to the same properties of the Gantt chart on the first position.
	 *
	 * @since 1.6
	 */
	public ContainerBase() {
		this(true);
	}

	/**
	 * Determines if the container performs autobinding of the Gantt chart
	 * properties. Autobinding means that the properties of all charts will be
	 * kept in sync.
	 *
	 * @return true if the container is performing autobinding
	 * @since 1.6
	 */
	public final boolean isAutoBinding() {
		return autoBinding;
	}

	private final ObservableList<T> ganttCharts = FXCollections.observableArrayList();

	/**
	 * Returns the list of Gantt charts that are being managed by the container.
	 *
	 * @return the list of Gantt chart controls
	 * @since 1.6
	 */
	public final ObservableList<T> getGanttCharts() {
		return ganttCharts;
	}
}
