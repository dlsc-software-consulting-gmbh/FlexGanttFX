/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.view.container;

import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.GanttChartLite;
import org.controlsfx.control.MasterDetailPane;

/**
 * A specialization of {@link DualGanttChartContainerBase} capable of displaying
 * exactly two instances of {@link GanttChartLite} and keeping their layouts (e.g.,
 * same timeline) and their scrolling and zooming behavior in
 * sync. The container distinguishes between a primary and a secondary Gantt
 * chart, where the secondary Gantt chart is located in the detail node section
 * of a {@link MasterDetailPane}. It can be hidden or shown on demand. Each one
 * of the two Gantt charts can have its own header and footer.
 * <p/>
 * <img src="doc-files/dual-gantt-chart-lite.png" alt="Dual Gantt Chart Lite Container" width="100%">
 * <p/>
 *
 * @since 1.6
 */
public class DualGanttChartLiteContainer extends DualGanttChartContainerBase<GanttChartLite<?>> {

    /**
     * Constructs a new container with the given graphics controls.
     *
     * @param autoBinding         if true, many properties of the secondary control will be bound
     *                            to their equivalent of the primary graphics chart
     * @param primaryGanttChart   the primary graphics shown in the top position
     * @param secondaryGanttChart the secondary graphics shown in the bottom position
     * @since 1.6
     */
    public DualGanttChartLiteContainer(boolean autoBinding, GanttChartLite<?> primaryGanttChart, GanttChartLite<?> secondaryGanttChart) {
        super(autoBinding, primaryGanttChart, secondaryGanttChart);
    }

    /**
     * Constructs a new container with the given graphics controls. The
     * properties of the secondary graphics will be bound to their equivalent
     * of the primary graphics.
     *
     * @param primaryGanttChart   the primary graphics shown in the top position
     * @param secondaryGanttChart the secondary graphics shown in the bottom position
     * @since 1.6
     */
    public DualGanttChartLiteContainer(GanttChartLite<?> primaryGanttChart, GanttChartLite<?> secondaryGanttChart) {
        this(true, primaryGanttChart, secondaryGanttChart);
    }

    /**
     * Constructs a new container. GanttChartLite must be added by calling
     * {@link DualGanttChartContainerBase#setPrimaryGanttChart(GanttChartBase)} and
     * {@link DualGanttChartContainerBase#setSecondaryGanttChart(GanttChartBase)}.
     *
     * @param autoBinding if true, many properties of the secondary graphics will be
     *                    bound to their equivalent of the primary graphics
     * @since 1.6
     */
    public DualGanttChartLiteContainer(boolean autoBinding) {
        this(autoBinding, new GanttChartLite<>(), new GanttChartLite<>());
    }

    /**
     * Constructs a new container. GanttChartLite must be added by calling
     * {@link DualGanttChartContainerBase#setPrimaryGanttChart(GanttChartBase)} and
     * {@link DualGanttChartContainerBase#setSecondaryGanttChart(GanttChartBase)}. The properties of the
     * secondary graphics will be bound to their equivalent of the primary
     * graphics.
     *
     * @since 1.6
     */
    public DualGanttChartLiteContainer() {
        this(true);
    }
}
