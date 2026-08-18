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

import com.flexganttfx.view.GanttChartLite;
import javafx.scene.control.SplitPane;

/**
 * A container capable of displaying multiple instances of {@link GanttChartLite}
 * and keeping their layouts and their scrolling and zooming behavior in sync.
 * The control uses a standard {@link SplitPane} for laying out the charts vertically.
 * <p>
 *     <img src="doc-files/multi-gantt-chart-lite.png" alt="Multi Gantt Chart Lite Container" width="100%">
 *
 * @see DualGanttChartLiteContainer
 *
 * @since 1.6
 */
public class MultiGanttChartLiteContainer extends MultiGanttChartContainerBase<GanttChartLite<?>> {

    /**
     * Constructs a new container with the given Gantt chart controls.
     *
     * @param autoBinding
     *            if true, many properties of the given controls will be bound to
     *            their equivalent of the Gantt chart on the first position
     * @param ganttCharts
     *            the Gantt charts to add to this container
     *
     * @since 1.6
     */
    public MultiGanttChartLiteContainer(boolean autoBinding, GanttChartLite<?>... ganttCharts) {
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
     * @since 1.6
     */
    public MultiGanttChartLiteContainer(GanttChartLite<?>... ganttCharts) {
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
    public MultiGanttChartLiteContainer(boolean autoBinding) {
        this(autoBinding, (GanttChartLite<?>[]) null);
    }

    /**
     * Constructs a new container. Gantt charts must be added to the list
     * returned by {@link #getGanttCharts()}.The properties of the charts will
     * be bound to the same properties of the Gantt chart on the first position.
     *
     * @since 1.6
     */
    public MultiGanttChartLiteContainer() {
        this(true);
    }
}
