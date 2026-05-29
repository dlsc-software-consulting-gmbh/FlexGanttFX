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

import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.util.Position;
import impl.com.flexganttfx.skin.container.DualGanttChartContainerSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import org.controlsfx.control.MasterDetailPane;

/**
 * A specialization of {@link ContainerBase} capable of displaying
 * exactly two instances of
 * their scrolling and zooming behavior in sync. The container distinguishes between
 * a primary and a secondary Gantt chart, where the secondary Gantt chart is located
 * in the detail node section of a {@link MasterDetailPane}. It can be hidden or shown
 * on demand. Each one of the two Gantt charts can have its own header and footer.
 *
 * @param <T> the type of the Gantt chart
 * @since 1.6
 */
public abstract class DualGanttChartContainerBase<T extends GanttChartBase<?>> extends ContainerBase<T> {

    /**
     * Constructs a new container with the given Gantt chart controls.
     *
     * @param autoBinding         if true, many properties of the secondary control will be bound
     *                            to their equivalent of the primary Gantt chart
     * @param primaryGanttChart   the primary Gantt chart shown in the top position
     * @param secondaryGanttChart the secondary Gantt chart shown in the bottom position
     * @since 1.6
     */
    protected DualGanttChartContainerBase(boolean autoBinding, T primaryGanttChart, T secondaryGanttChart) {
        super(autoBinding);

        masterDetailPane = new MasterDetailPane(Side.BOTTOM);
        masterDetailPane.setId("dual-gantt-chart-master-detail-pane");

        primaryGanttChartProperty().addListener((observable, oldPrimaryGanttChart, newPrimaryGanttChart) -> {

            if (oldPrimaryGanttChart != null) {
                getGanttCharts().remove(oldPrimaryGanttChart);
            }

            newPrimaryGanttChart.getTimeline().setId("primary timeline");

            // always add at the beginning of the list
            getGanttCharts().add(0, newPrimaryGanttChart);
            newPrimaryGanttChart.setPosition(Position.FIRST);
        });

        secondaryGanttChartProperty().addListener(
                (observable, oldSecondaryGanttChart,
                 newSecondaryGanttChart) -> {

                    if (oldSecondaryGanttChart != null) {
                        getGanttCharts().remove(oldSecondaryGanttChart);
                    }

                    newSecondaryGanttChart.getTimeline().setId("secondary timeline");

                    getGanttCharts().add(newSecondaryGanttChart);
                    newSecondaryGanttChart.setPosition(Position.LAST);
                });

        setPrimaryGanttChart(primaryGanttChart);
        setSecondaryGanttChart(secondaryGanttChart);
    }

    /**
     * Constructs a new container with the given Gantt chart controls. The
     * properties of the secondary Gantt chart will be bound to their equivalent
     * of the primary Gantt chart.
     *
     * @param primaryGanttChart   the primary Gantt chart shown in the top position
     * @param secondaryGanttChart the secondary Gantt chart shown in the bottom position
     * @since 1.6
     */
    protected DualGanttChartContainerBase(T primaryGanttChart, T secondaryGanttChart) {
        this(true, primaryGanttChart, secondaryGanttChart);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DualGanttChartContainerSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return super.getUserAgentStylesheet(GanttChart.class, "gantt.css");
    }    private final MasterDetailPane masterDetailPane;

    /**
     * Returns the {@link MasterDetailPane} instance used by the skin of this
     * control to arrange the primary and secondary Gantt charts. The primary Gantt
     * chart will be the master and the secondary Gantt chart will be the detail node.
     *
     * @return the master detail pane
     * @see MasterDetailPane#setMasterNode(Node)
     * @see MasterDetailPane#setDetailNode(Node)
     * @since 1.3
     */
    public final MasterDetailPane getMasterDetailPane() {
        return masterDetailPane;
    }

    // Primary Gantt chart support.

    private final ObjectProperty<T> primaryGanttChart = new SimpleObjectProperty<>(this, "primaryGanttChart");

    /**
     * A property used to store the reference to the Gantt chart control that is
     * serving as the primary Gantt chart.
     *
     * @return the primary Gantt chart property
     * @since 1.6
     */
    public final ObjectProperty<T> primaryGanttChartProperty() {
        return primaryGanttChart;
    }

    /**
     * Sets the value of {@link #primaryGanttChartProperty()}.
     *
     * @param ganttChart the gantt chart control
     * @since 1.6
     */
    public final void setPrimaryGanttChart(T ganttChart) {
        primaryGanttChart.set(ganttChart);
    }

    /**
     * Returns the value of {@link #primaryGanttChartProperty()}.
     *
     * @return the primary Gantt chart control
     * @since 1.6
     */
    public final T getPrimaryGanttChart() {
        return primaryGanttChart.get();
    }

    // Primary header support.

    private final ObjectProperty<Node> primaryHeader = new SimpleObjectProperty<>(this, "primaryHeader");

    /**
     * A property used to store the reference to a control that will be serving
     * as a header for the primary Gantt chart. In most cases the primary header
     * will be a toolbar.
     *
     * @return the primary header control property
     * @since 1.6
     */
    public final ObjectProperty<Node> primaryHeaderProperty() {
        return primaryHeader;
    }

    /**
     * Sets the value of the {@link #primaryHeaderProperty()}.
     *
     * @param header the node that will be used as a header
     * @since 1.6
     */
    public final void setPrimaryHeader(Node header) {
        primaryHeaderProperty().set(header);
    }

    /**
     * Returns the value of {@link #primaryHeaderProperty()}.
     *
     * @return the primary header node
     * @since 1.6
     */
    public final Node getPrimaryHeader() {
        return primaryHeaderProperty().get();
    }

    // Primary footer support.

    private final ObjectProperty<Node> primaryFooter = new SimpleObjectProperty<>(this, "primaryFooter");

    /**
     * A property used to store the reference to a node that will be displayed
     * in the footer position of the primary Gantt chart.
     *
     * @return the primary footer property
     * @since 1.6
     */
    public final ObjectProperty<Node> primaryFooterProperty() {
        return primaryFooter;
    }

    /**
     * Sets the value of {@link #primaryFooterProperty()}.
     *
     * @param footer the node that will be displayed in the footer position of the
     *               primary Gantt chart
     * @since 1.6
     */
    public final void setPrimaryFooter(Node footer) {
        primaryFooterProperty().set(footer);
    }

    /**
     * Returns the value of {@link #primaryFooterProperty()}.
     *
     * @return the node displayed in the footer position of the primary Gantt
     * chart
     * @since 1.6
     */
    public final Node getPrimaryFooter() {
        return primaryFooterProperty().get();
    }

    // Secondary header support.

    /**
     * A property used to store the reference to the Gantt chart control that is
     * serving as the secondary Gantt chart.
     *
     * @since 1.6
     */
    private final ObjectProperty<Node> secondaryHeader = new SimpleObjectProperty<>(this, "secondaryHeader");

    /**
     * A property used to store the reference to a control that will be serving
     * as a header for the secondary Gantt chart. In most cases the secondary
     * header will not be used.
     *
     * @return the secondary header control property
     * @since 1.6
     */
    public final ObjectProperty<Node> secondaryHeaderProperty() {
        return secondaryHeader;
    }

    /**
     * Sets the value of {@link #secondaryHeaderProperty()}.
     *
     * @param header the control used as a header for the secondary Gantt chart
     * @since 1.6
     */
    public final void setSecondaryHeader(Node header) {
        secondaryHeaderProperty().set(header);
    }

    /**
     * Returns the value of {@link #secondaryHeaderProperty()}.
     *
     * @return the secondary header node
     * @since 1.6
     */
    public final Node getSecondaryHeader() {
        return secondaryHeaderProperty().get();
    }

    // Primary header support.

    private final ObjectProperty<Node> secondaryFooter = new SimpleObjectProperty<>(this, "secondaryFooter");

    /**
     * A property used to store the reference to a node that will be used in the
     * footer position of the secondary Gantt chart.
     *
     * @return the secondary footer property
     * @since 1.6
     */
    public final ObjectProperty<Node> secondaryFooterProperty() {
        return secondaryFooter;
    }

    /**
     * Sets the value of {@link #secondaryFooterProperty()}.
     *
     * @param footer the node that will be shown in the footer position of the
     *               secondary Gantt chart
     * @since 1.6
     */
    public final void setSecondaryFooter(Node footer) {
        secondaryFooter.set(footer);
    }

    /**
     * Returns the value of {@link #secondaryFooterProperty()}.
     *
     * @return the secondary footer node
     * @since 1.6
     */
    public final Node getSecondaryFooter() {
        return secondaryFooter.get();
    }

    // Secondary Gantt chart support.

    private final ObjectProperty<T> secondaryGanttChart = new SimpleObjectProperty<>(this, "secondaryGanttChart");

    /**
     * A property used to store the reference to the secondary Gantt chart.
     *
     * @return the secondary Gantt chart property
     */
    public final ObjectProperty<T> secondaryGanttChartProperty() {
        return secondaryGanttChart;
    }

    /**
     * Sets the value of {@link #secondaryGanttChartProperty()}.
     *
     * @param ganttChart the gantt chart control
     * @since 1.6
     */
    public final void setSecondaryGanttChart(T ganttChart) {
        secondaryGanttChart.set(ganttChart);
    }

    /**
     * Returns the value of {@link #secondaryGanttChartProperty()}.
     *
     * @return the secondary Gantt chart control
     * @since 1.6
     */
    public final T getSecondaryGanttChart() {
        return secondaryGanttChart.get();
    }

    // Expanded state support support.

    private final BooleanProperty showSecondary = new SimpleBooleanProperty(this, "showSecondary", true);

    /**
     * A property used to toggle the visibility of the secondary Gantt chart.
     *
     * @return a property storing the visibility flag of the secondary Gantt
     * chart
     * @since 1.6
     */
    public final BooleanProperty showSecondaryProperty() {
        return showSecondary;
    }

    /**
     * Returns the value of {@link #showSecondaryProperty()}.
     *
     * @return true if the secondary Gantt chart shall be visible
     * @since 1.6
     */
    public final boolean isShowSecondary() {
        return showSecondaryProperty().get();
    }

    /**
     * Sets the value of {@link #showSecondaryProperty()}.
     *
     * @param show if true, the secondary Gantt chart will be visible
     * @since 1.6
     */
    public final void setShowSecondary(boolean show) {
        showSecondaryProperty().set(show);
    }
}
