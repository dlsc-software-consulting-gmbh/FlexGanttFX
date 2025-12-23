/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.container;

import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import org.controlsfx.control.MasterDetailPane;

/**
 * A specialization of {@link DualGanttChartContainerBase} capable of displaying
 * exactly two instances of {@link GanttChart} and keeping their layouts (same
 * table width, same timeline) and their scrolling and zooming behavior in
 * sync. The container distinguishes between a primary and a secondary Gantt
 * chart, where the secondary Gantt chart is located in the detail node section
 * of a {@link MasterDetailPane}. It can be hidden or shown on demand. Each one
 * of the two Gantt charts can have its own header and footer.
 * <p/>
 * The screenshot below shows the initial appearance of an empty Dual Gantt
 * chart control.<br>
 *  <img src="doc-files/dual-gantt-chart.png"
 * alt="Dual Gantt Chart Container" width="100%">
 *
 *
 * @since 1.0
 */
public class DualGanttChartContainer extends DualGanttChartContainerBase<GanttChart<?>> {

	/**
	 * Constructs a new container with the given Gantt chart controls.
	 *
	 * @param autoBinding
	 *            if true, many properties of the secondary control will be bound
	 *            to their equivalent of the primary Gantt chart
	 * @param primaryGanttChart
	 *            the primary Gantt chart shown in the top position
	 * @param secondaryGanttChart
	 *            the secondary Gantt chart shown in the bottom position
	 * @since 1.0
	 */
	public DualGanttChartContainer(boolean autoBinding, GanttChart<?> primaryGanttChart, GanttChart<?> secondaryGanttChart) {
		super(autoBinding, primaryGanttChart, secondaryGanttChart);
	}

	/**
	 * Constructs a new container with the given Gantt chart controls. The
	 * properties of the secondary Gantt chart will be bound to their equivalent
	 * of the primary Gantt chart.
	 *
	 * @param primaryGanttChart
	 *            the primary Gantt chart shown in the top position
	 * @param secondaryGanttChart
	 *            the secondary Gantt chart shown in the bottom position
	 * @since 1.0
	 */
	public DualGanttChartContainer(GanttChart<?> primaryGanttChart, GanttChart<?> secondaryGanttChart) {
		this(true, primaryGanttChart, secondaryGanttChart);
	}

	/**
	 * Constructs a new container. Gantt charts must be added by calling
	 * {@link DualGanttChartContainerBase#setPrimaryGanttChart(GanttChartBase)} and
	 * {@link DualGanttChartContainerBase#setSecondaryGanttChart(GanttChartBase)}.
	 *
	 * @param autoBinding
	 *            if true, many properties of the secondary Gantt chart will be
	 *            bound to their equivalent of the primary Gantt chart
	 * @since 1.0
	 */
	public DualGanttChartContainer(boolean autoBinding) {
		this(autoBinding, new GanttChart<>(),
                new GanttChart<>());
	}

	/**
	 * Constructs a new container. Gantt charts must be added by calling
	 * {@link DualGanttChartContainerBase#setPrimaryGanttChart(GanttChartBase)} and
	 * {@link DualGanttChartContainerBase#setSecondaryGanttChart(GanttChartBase)}. The properties of the
	 * secondary Gantt chart will be bound to their equivalent of the primary
	 * Gantt chart.
	 *
	 * @since 1.0
	 */
	public DualGanttChartContainer() {
		this(true);
	}
}
