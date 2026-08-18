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
package com.flexganttfx.view.container;

import com.flexganttfx.view.GanttChart;
import javafx.scene.control.SplitPane;

/**
 * A container capable of displaying multiple instances of {@link GanttChart}
 * and keeping their layouts (same table width, same timeline) and their
 * scrolling and zooming behavior in sync. The control uses a standard
 * {@link SplitPane} for laying out the charts vertically.
 * <p>
 * The screenshot below shows the initial appearance of an empty multi Gantt
 * chart container.<br>
 *  <img src="doc-files/multi-gantt-chart.png" alt=
 * "Multi Gantt Chart Container" width="100%">
 *
 * @see DualGanttChartContainer
 *
 * @since 1.0
 */
public class MultiGanttChartContainer extends MultiGanttChartContainerBase<GanttChart<?>> {

	/**
	 * Constructs a new container with the given Gantt chart controls.
	 *
	 * @param autoBinding
	 *            if true, many properties of the given controls will be bound to
	 *            their equivalent of the Gantt chart on the first position
	 * @param ganttCharts
	 *            the Gantt charts to add to this container
	 *
	 * @since 1.0
	 */
	public MultiGanttChartContainer(boolean autoBinding, GanttChart<?>... ganttCharts) {
		super(autoBinding, ganttCharts);
	}

	/**
	 * Constructs a new container with the given Gantt chart controls. The
	 * properties of the charts will be bound to the same properties of the
	 * Gantt chart in the first position.
	 *
	 * @param ganttCharts
	 *            the Gantt charts to add to this container
	 *
	 * @since 1.0
	 */
	public MultiGanttChartContainer(GanttChart<?>... ganttCharts) {
		this(true, ganttCharts);
	}

	/**
	 * Constructs a new empty container. Gantt charts must be added to the list
	 * returned by {@link #getGanttCharts()}.
	 *
	 * @param autoBinding
	 *            if true, many properties of the given controls are bound to
	 *            their equivalent of the Gantt chart on the first position
	 *
	 * @since 1.0
	 */
	public MultiGanttChartContainer(boolean autoBinding) {
		this(autoBinding, (GanttChart<?>[]) null);
	}

	/**
	 * Constructs a new container. Gantt charts must be added to the list
	 * returned by {@link #getGanttCharts()}.The properties of the charts will
	 * be bound to the same properties of the Gantt chart on the first position.
	 *
	 * @since 1.0
	 */
	public MultiGanttChartContainer() {
		this(true);
	}
}
