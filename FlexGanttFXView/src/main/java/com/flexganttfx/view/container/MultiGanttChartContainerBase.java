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

import com.flexganttfx.view.GanttChartBase;
import impl.com.flexganttfx.skin.container.MultiGanttChartContainerSkin;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;

import static javafx.geometry.Orientation.VERTICAL;

/**
 * A container capable of displaying multiple instances of {@link GanttChartBase}
 * and keeping their layouts and their scrolling and zooming behavior in sync. The
 * control uses a standard {@link SplitPane} for laying out the charts vertically.
 *
 * @see DualGanttChartContainerBase
 *
 * @since 1.0
 * @param <T> the type of the Gantt chart
 */
public class MultiGanttChartContainerBase<T extends GanttChartBase<?>> extends ContainerBase<T> {

    private final SplitPane splitPane;

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
    @SafeVarargs
    protected MultiGanttChartContainerBase(boolean autoBinding, T... ganttCharts) {

        super(autoBinding, ganttCharts);

        this.splitPane = new SplitPane();
        this.splitPane.setOrientation(VERTICAL);

        resetDividerPositions();
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
    @SafeVarargs
    public MultiGanttChartContainerBase(T... ganttCharts) {
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
     * @since 1.0
     */
    public MultiGanttChartContainerBase(boolean autoBinding) {
        this(autoBinding, (T) null);
    }

    /**
     * Constructs a new container. Gantt charts must be added to the list
     * returned by {@link #getGanttCharts()}.The properties of the charts will
     * be bound to the same properties of the Gantt chart on the first position.
     *
     * @since 1.0
     */
    public MultiGanttChartContainerBase() {
        this(true);
    }

    /**
     * Equally distributes the available height of the container to all charts.
     *
     * @since 1.0
     */
    public final void resetDividerPositions() {
        int count = getGanttCharts().size();
        if (count > 0) {
            double[] positions = new double[count - 1];
            // equally distribute the entire width
            for (int i = 0; i < count - 1; i++) {
                positions[i] = (i + 1) * (1 / (double) getGanttCharts().size());

            }
            splitPane.setDividerPositions(positions);
        }
    }

    /**
     * Returns the split pane used by the container to lay out the Gantt
     * charts.
     *
     * @return the split pane used by the container
     *
     * @since 1.0
     */
    public final SplitPane getSplitPane() {
        return splitPane;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MultiGanttChartContainerSkin(this);
    }
}
