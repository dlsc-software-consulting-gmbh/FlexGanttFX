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
import com.flexganttfx.view.GanttChartBase;
import org.controlsfx.control.MasterDetailPane;

/**
 * A specialization of {@link QuadGanttChartContainerBase} capable of displaying
 * exactly four instances of {@link GanttChart} and keeping their layouts (same
 * table width, same timeline) and their scrolling and zooming behavior in
 * sync. The container distinguishes between four Gantt chart locations, where the
 * right and the lower Gantt charts are located in the detail node sections
 * of several {@link MasterDetailPane} instances. They can be hidden or shown on
 * demand. Each one of the four Gantt charts can have its own header and footer.
 * <p>
 * The screenshot below shows the initial appearance of an empty Quad Gantt
 * chart container control.
 * <p>
 *     <img src="doc-files/quad-gantt-chart.png" alt="Quad Gantt Chart Container" width="100%">
 *
 *
 * @since 1.6
 */
public class QuadGanttChartContainer extends QuadGanttChartContainerBase<GanttChart<?>> {

    /**
     * Constructs a new container with the given Gantt chart controls.
     *
     * @param upperLeft
     *            the Gantt chart shown in the upper left position
     * @param upperRight
     *            the Gantt chart shown in the upper right position
     * @param lowerLeft
     *            the Gantt chart shown in the lower left position
     * @param lowerRight
     *            the Gantt chart shown in the lower right position
     *
     * @since 1.6
     */
    public QuadGanttChartContainer(GanttChart<?> upperLeft, GanttChart<?> upperRight, GanttChart<?> lowerLeft, GanttChart<?> lowerRight) {
        super(upperLeft, upperRight, lowerLeft, lowerRight);
    }

    /**
     * Constructs a new container. Gantt charts must be added by calling
     * {@link QuadGanttChartContainerBase#setUpperLeftGanttChart(GanttChartBase)} and
     * {@link QuadGanttChartContainerBase#setUpperRightGanttChart(GanttChartBase)} and
     * {@link QuadGanttChartContainerBase#setLowerLeftGanttChart(GanttChartBase)} and
     * {@link QuadGanttChartContainerBase#setLowerRightGanttChart(GanttChartBase)}.
     *
     * @since 1.6
     */
    public QuadGanttChartContainer() {
        this(new GanttChart<>(),
                new GanttChart<>(),
                new GanttChart<>(),
                new GanttChart<>());
    }
}
