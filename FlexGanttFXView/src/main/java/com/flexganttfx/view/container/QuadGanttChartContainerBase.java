/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.container;

import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.util.Position;
import impl.com.flexganttfx.skin.container.QuadGanttChartContainerSkin;
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
 * exactly four instances of {@link GanttChartBase} and keeping their layouts and
 * their scrolling and zooming behavior in sync.
 *
 * @param <T> the type of the Gantt chart
 * @since 1.6
 */
public abstract class QuadGanttChartContainerBase<T extends GanttChartBase<?>> extends ContainerBase<T> {

    /**
     * Constructs a new container with the given Gantt chart controls.
     *
     * @param upperLeftGanttChart  the upper left Gantt chart shown in the top position
     * @param upperRightGanttChart the upper right Gantt chart shown in the bottom position
     * @param lowerLeftGanttChart  the lower left Gantt chart shown in the top position
     * @param lowerRightGanttChart the lower right Gantt chart shown in the bottom position
     * @since 1.6
     */
    protected QuadGanttChartContainerBase(T upperLeftGanttChart, T upperRightGanttChart, T lowerLeftGanttChart, T lowerRightGanttChart) {
        super(true);

        upperLowerMasterDetailPane = new MasterDetailPane(Side.BOTTOM);
        upperLowerMasterDetailPane.setId("upper-lower-master-detail-pane");
        upperLowerMasterDetailPane.setDividerPosition(.5);

        upperMasterDetailPane = new MasterDetailPane(Side.RIGHT);
        upperMasterDetailPane.setId("upper-master-detail-pane");
        upperMasterDetailPane.setDividerPosition(.5);

        lowerMasterDetailPane = new MasterDetailPane(Side.RIGHT);
        lowerMasterDetailPane.setId("lower-master-detail-pane");
        lowerMasterDetailPane.setDividerPosition(.5);

        upperLowerMasterDetailPane.animatedProperty().bind(animatedProperty());
        upperMasterDetailPane.animatedProperty().bind(animatedProperty());
        lowerMasterDetailPane.animatedProperty().bind(animatedProperty());

        upperLeftGanttChartProperty().addListener((observable, oldGantt, newGantt) -> {
            if (oldGantt != null) {
                getGanttCharts().remove(oldGantt);
            }

            newGantt.getTimeline().setId("upper-left-timeline");

            // always add at the beginning of the list
            getGanttCharts().add(0, newGantt);
            newGantt.setPosition(Position.FIRST);
        });

        upperRightGanttChartProperty().addListener((observable, oldGantt, newGantt) -> {
            if (oldGantt != null) {
                getGanttCharts().remove(oldGantt);
            }

            newGantt.getTimeline().setId("upper-right-timeline");

            getGanttCharts().add(newGantt);
            newGantt.setPosition(Position.FIRST);
        });

        lowerLeftGanttChartProperty().addListener((observable, oldGantt, newGantt) -> {
            if (oldGantt != null) {
                getGanttCharts().remove(oldGantt);
            }

            newGantt.getTimeline().setId("lower-left-timeline");

            getGanttCharts().add(newGantt);
            newGantt.setPosition(Position.LAST);
        });

        lowerRightGanttChartProperty().addListener((observable, oldGantt, newGantt) -> {
            if (oldGantt != null) {
                getGanttCharts().remove(oldGantt);
            }

            newGantt.getTimeline().setId("lower-right-timeline");

            getGanttCharts().add(newGantt);
            newGantt.setPosition(Position.LAST);
        });

        setUpperLeftGanttChart(upperLeftGanttChart);
        setUpperRightGanttChart(upperRightGanttChart);
        setLowerLeftGanttChart(lowerLeftGanttChart);
        setLowerRightGanttChart(lowerRightGanttChart);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new QuadGanttChartContainerSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return GanttChart.class.getResource("gantt.css").toExternalForm();
    }

    private final MasterDetailPane upperLowerMasterDetailPane;

    /**
     * Returns the {@link MasterDetailPane} instance used by the skin of this
     * control to arrange the upper and lower Gantt charts. The upper Gantt
     * charts will be the master and the lower Gantt charts will be the detail node.
     *
     * @return the master detail pane
     * @see MasterDetailPane#setMasterNode(Node)
     * @see MasterDetailPane#setDetailNode(Node)
     * @since 1.6
     */
    public final MasterDetailPane getUpperLowerMasterDetailPane() {
        return upperLowerMasterDetailPane;
    }

    private final MasterDetailPane upperMasterDetailPane;

    /**
     * Returns the {@link MasterDetailPane} instance used by the skin of this
     * control to arrange the upper Gantt charts. The upper left Gantt
     * chart will be the master and the upper right Gantt chart will be the detail node.
     *
     * @return the master detail pane
     * @see MasterDetailPane#setMasterNode(Node)
     * @see MasterDetailPane#setDetailNode(Node)
     * @since 1.6
     */
    public final MasterDetailPane getUpperMasterDetailPane() {
        return upperMasterDetailPane;
    }

    private final MasterDetailPane lowerMasterDetailPane;

    /**
     * Returns the {@link MasterDetailPane} instance used by the skin of this
     * control to arrange the lower Gantt charts. The lower left Gantt
     * chart will be the master and the lower right Gantt chart will be the detail node.
     *
     * @return the master detail pane
     * @see MasterDetailPane#setMasterNode(Node)
     * @see MasterDetailPane#setDetailNode(Node)
     * @since 1.6
     */
    public final MasterDetailPane getLowerMasterDetailPane() {
        return lowerMasterDetailPane;
    }

    // Upper left Gantt chart support.

    private final ObjectProperty<T> upperLeftGanttChart = new SimpleObjectProperty<>(this, "upperLeftGanttChart");

    /**
     * A property used to store the reference to the Gantt chart control that is
     * serving as the upper left Gantt chart.
     *
     * @return the upper left Gantt chart property
     * @since 1.6
     */
    public final ObjectProperty<T> upperLeftGanttChartProperty() {
        return upperLeftGanttChart;
    }

    /**
     * Sets the value of {@link #upperLeftGanttChartProperty()}.
     *
     * @param ganttChart the gantt chart control
     * @since 1.6
     */
    public final void setUpperLeftGanttChart(T ganttChart) {
        upperLeftGanttChart.set(ganttChart);
    }

    /**
     * Returns the value of {@link #upperLeftGanttChartProperty()}.
     *
     * @return the upper left Gantt chart control
     * @since 1.6
     */
    public final T getUpperLeftGanttChart() {
        return upperLeftGanttChart.get();
    }

    // Upper left header support.

    private final ObjectProperty<Node> upperLeftHeader = new SimpleObjectProperty<>(this, "upperLeftHeader");

    /**
     * A property used to store the reference to a control that will be serving
     * as a header for the upper left Gantt chart.
     *
     * @return the upper left header control property
     * @since 1.6
     */
    public final ObjectProperty<Node> upperLeftHeaderProperty() {
        return upperLeftHeader;
    }

    /**
     * Sets the value of the {@link #upperLeftHeaderProperty()}.
     *
     * @param header the node that will be used as a header
     * @since 1.6
     */
    public final void setUpperLeftHeader(Node header) {
        upperLeftHeaderProperty().set(header);
    }

    /**
     * Returns the value of {@link #upperLeftHeaderProperty()}.
     *
     * @return the upper left header node
     * @since 1.6
     */
    public final Node getUpperLeftHeader() {
        return upperLeftHeaderProperty().get();
    }

    // Upper left footer support.

    private final ObjectProperty<Node> upperLeftFooter = new SimpleObjectProperty<>(this, "upperLeftFooter");

    /**
     * A property used to store the reference to a node that will be displayed
     * in the footer position of the upper left Gantt chart.
     *
     * @return the upper left footer property
     * @since 1.6
     */
    public final ObjectProperty<Node> upperLeftFooterProperty() {
        return upperLeftFooter;
    }

    /**
     * Sets the value of {@link #upperLeftFooterProperty()}.
     *
     * @param footer the node that will be displayed in the footer position of the
     *               upper left Gantt chart
     * @since 1.6
     */
    public final void setUpperLeftFooter(Node footer) {
        upperLeftFooterProperty().set(footer);
    }

    /**
     * Returns the value of {@link #upperLeftFooterProperty()}.
     *
     * @return the node displayed in the footer position of the upper left Gantt
     * chart
     * @since 1.6
     */
    public final Node getUpperLeftFooter() {
        return upperLeftFooterProperty().get();
    }

    // Upper right header support.

    /**
     * A property used to store the reference to the Gantt chart control that is
     * serving as the upper right Gantt chart.
     *
     * @since 1.6
     */
    private final ObjectProperty<Node> upperRightHeader = new SimpleObjectProperty<>(this, "upperRightHeader");

    /**
     * A property used to store the reference to a control that will be serving
     * as a header for the upper right Gantt chart.
     *
     * @return the upper right header control property
     * @since 1.6
     */
    public final ObjectProperty<Node> upperRightHeaderProperty() {
        return upperRightHeader;
    }

    /**
     * Sets the value of {@link #upperRightHeaderProperty()}.
     *
     * @param header the control used as a header for the upper right Gantt chart
     * @since 1.6
     */
    public final void setUpperRightHeader(Node header) {
        upperRightHeaderProperty().set(header);
    }

    /**
     * Returns the value of {@link #upperRightHeaderProperty()}.
     *
     * @return the upper right header node
     * @since 1.6
     */
    public final Node getUpperRightHeader() {
        return upperRightHeaderProperty().get();
    }

    // Upper right footer support.

    private final ObjectProperty<Node> upperRightFooter = new SimpleObjectProperty<>(this, "upperRightFooter");

    /**
     * A property used to store the reference to a node that will be used in the
     * footer position of the upper right Gantt chart.
     *
     * @return the upper right footer property
     * @since 1.6
     */
    public final ObjectProperty<Node> upperRightFooterProperty() {
        return upperRightFooter;
    }

    /**
     * Sets the value of {@link #upperRightFooterProperty()}.
     *
     * @param footer the node that will be shown in the footer position of the
     *               upper right Gantt chart
     * @since 1.6
     */
    public final void setUpperRightFooter(Node footer) {
        upperRightFooter.set(footer);
    }

    /**
     * Returns the value of {@link #upperRightFooterProperty()}.
     *
     * @return the upper right footer node
     * @since 1.6
     */
    public final Node getUpperRightFooter() {
        return upperRightFooter.get();
    }

    // Secondary Gantt chart support.

    private final ObjectProperty<T> upperRightGanttChart = new SimpleObjectProperty<>(this, "upperRightGanttChart");

    /**
     * A property used to store the reference to the upper right Gantt chart.
     *
     * @return the upper right Gantt chart property
     */
    public final ObjectProperty<T> upperRightGanttChartProperty() {
        return upperRightGanttChart;
    }

    /**
     * Sets the value of {@link #upperRightGanttChartProperty()}.
     *
     * @param ganttChart the gantt chart control
     * @since 1.6
     */
    public final void setUpperRightGanttChart(T ganttChart) {
        upperRightGanttChart.set(ganttChart);
    }

    /**
     * Returns the value of {@link #upperRightGanttChartProperty()}.
     *
     * @return the upper right Gantt chart control
     * @since 1.6
     */
    public final T getUpperRightGanttChart() {
        return upperRightGanttChart.get();
    }

    // Lower right header support.

    /**
     * A property used to store the reference to the control that is
     * serving as the upper right header.
     *
     * @since 1.6
     */
    private final ObjectProperty<Node> lowerRightHeader = new SimpleObjectProperty<>(this, "lowerRightHeader");

    /**
     * A property used to store the reference to a control that will be serving
     * as a header for the lower right Gantt chart.
     *
     * @return the lower right header control property
     * @since 1.6
     */
    public final ObjectProperty<Node> lowerRightHeaderProperty() {
        return lowerRightHeader;
    }

    /**
     * Sets the value of {@link #lowerRightHeaderProperty()}.
     *
     * @param header the control used as a header for the lower right Gantt chart
     * @since 1.6
     */
    public final void setLowerRightHeader(Node header) {
        lowerRightHeaderProperty().set(header);
    }

    /**
     * Returns the value of {@link #lowerRightHeaderProperty()}.
     *
     * @return the lower right header node
     * @since 1.6
     */
    public final Node getLowerRightHeader() {
        return lowerRightHeaderProperty().get();
    }

    // Lower right footer support.

    private final ObjectProperty<Node> lowerRightFooter = new SimpleObjectProperty<>(this, "lowerRightFooter");

    /**
     * A property used to store the reference to a node that will be used in the
     * footer position of the lower right Gantt chart.
     *
     * @return the lower right footer property
     * @since 1.6
     */
    public final ObjectProperty<Node> lowerRightFooterProperty() {
        return lowerRightFooter;
    }

    /**
     * Sets the value of {@link #lowerRightFooterProperty()}.
     *
     * @param footer the node that will be shown in the footer position of the
     *               lower right Gantt chart
     * @since 1.6
     */
    public final void setLowerRightFooter(Node footer) {
        lowerRightFooter.set(footer);
    }

    /**
     * Returns the value of {@link #lowerRightFooterProperty()}.
     *
     * @return the lower right footer node
     * @since 1.6
     */
    public final Node getLowerRightFooter() {
        return lowerRightFooter.get();
    }

    // Lower right Gantt chart support.

    private final ObjectProperty<T> lowerRightGanttChart = new SimpleObjectProperty<>(this, "lowerRightGanttChart");

    /**
     * A property used to store the reference to the lower right Gantt chart.
     *
     * @return the lower right Gantt chart property
     */
    public final ObjectProperty<T> lowerRightGanttChartProperty() {
        return lowerRightGanttChart;
    }

    /**
     * Sets the value of {@link #lowerRightGanttChartProperty()}.
     *
     * @param ganttChart the gantt chart control
     * @since 1.6
     */
    public final void setLowerRightGanttChart(T ganttChart) {
        lowerRightGanttChart.set(ganttChart);
    }

    /**
     * Returns the value of {@link #lowerRightGanttChartProperty()}.
     *
     * @return the lower right Gantt chart control
     * @since 1.6
     */
    public final T getLowerRightGanttChart() {
        return lowerRightGanttChart.get();
    }

    // Lower left Gantt chart support.

    private final ObjectProperty<T> lowerLeftGanttChart = new SimpleObjectProperty<>(this, "lowerLeftGanttChart");

    /**
     * A property used to store the reference to the Gantt chart control that is
     * serving as the lower left Gantt chart.
     *
     * @return the lower left Gantt chart property
     * @since 1.6
     */
    public final ObjectProperty<T> lowerLeftGanttChartProperty() {
        return lowerLeftGanttChart;
    }

    /**
     * Sets the value of {@link #lowerLeftGanttChartProperty()}.
     *
     * @param ganttChart the gantt chart control
     * @since 1.6
     */
    public final void setLowerLeftGanttChart(T ganttChart) {
        lowerLeftGanttChart.set(ganttChart);
    }

    /**
     * Returns the value of {@link #lowerLeftGanttChartProperty()}.
     *
     * @return the lower left Gantt chart control
     * @since 1.6
     */
    public final T getLowerLeftGanttChart() {
        return lowerLeftGanttChart.get();
    }

    // Lower left header support.

    private final ObjectProperty<Node> lowerLeftHeader = new SimpleObjectProperty<>(this, "lowerLeftHeader");

    /**
     * A property used to store the reference to a control that will be serving
     * as a header for the lower left Gantt chart.
     *
     * @return the lower left header control property
     * @since 1.6
     */
    public final ObjectProperty<Node> lowerLeftHeaderProperty() {
        return lowerLeftHeader;
    }

    /**
     * Sets the value of the {@link #lowerLeftHeaderProperty()}.
     *
     * @param header the node that will be used as a header for the lower left Gantt chart
     * @since 1.6
     */
    public final void setLowerLeftHeader(Node header) {
        lowerLeftHeaderProperty().set(header);
    }

    /**
     * Returns the value of {@link #lowerLeftHeaderProperty()}.
     *
     * @return the lower left header node
     * @since 1.6
     */
    public final Node getLowerLeftHeader() {
        return lowerLeftHeaderProperty().get();
    }

    // Lower left footer support.

    private final ObjectProperty<Node> lowerLeftFooter = new SimpleObjectProperty<>(this, "lowerLeftFooter");

    /**
     * A property used to store the reference to a node that will be displayed
     * in the footer position of the lower left Gantt chart.
     *
     * @return the lower left footer property
     * @since 1.6
     */
    public final ObjectProperty<Node> lowerLeftFooterProperty() {
        return lowerLeftFooter;
    }

    /**
     * Sets the value of {@link #lowerLeftFooterProperty()}.
     *
     * @param footer the node that will be displayed in the footer position of the
     *               lower left Gantt chart
     * @since 1.6
     */
    public final void setLowerLeftFooter(Node footer) {
        lowerLeftFooterProperty().set(footer);
    }

    /**
     * Returns the value of {@link #lowerLeftFooterProperty()}.
     *
     * @return the node displayed in the footer position of the lower left Gantt
     * chart
     * @since 1.6
     */
    public final Node getLowerLeftFooter() {
        return lowerLeftFooterProperty().get();
    }

    // Expanded state support support.

    private final BooleanProperty showLower = new SimpleBooleanProperty(this, "showLower", true);

    /**
     * A property used to toggle the visibility of the upper right Gantt chart.
     *
     * @return a property storing the visibility flag of the lower Gantt
     * charts
     * @since 1.6
     */
    public final BooleanProperty showLowerProperty() {
        return showLower;
    }

    /**
     * Returns the value of {@link #showLowerProperty()}.
     *
     * @return true if the lower Gantt charts shall be visible
     * @since 1.6
     */
    public final boolean isShowLower() {
        return showLowerProperty().get();
    }

    /**
     * Sets the value of {@link #showLowerProperty()}.
     *
     * @param show if true the lower Gantt charts will be visible
     * @since 1.6
     */
    public final void setShowLower(boolean show) {
        showLowerProperty().set(show);
    }

    private final BooleanProperty animated = new SimpleBooleanProperty(this, "animated", true);

    /**
     * A property used to control whether the opening / closing of the three instances of
     * MasterDetailPane in this container will be animated or not.
     *
     * @return true if animation is desired
     * @since 1.6
     */
    public final BooleanProperty animatedProperty() {
        return animated;
    }

    /**
     * Sets the value of {@link #animatedProperty()}.
     *
     * @param animated if true the opening and closing operations will be animated
     * @since 1.6
     */
    public final void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    /**
     * Returns the value of {@link #animatedProperty()}.
     *
     * @return true if the opening and closing operations will be animated
     * @since 1.6
     */
    public final boolean isAnimated() {
        return animated.get();
    }

    /**
     * Convenience method to change the container settings in such a way that the
     * Gantt chart in the upper left corner will take over the entire width and height
     * of the container.
     *
     * @see #setShowLower(boolean)
     * @see MasterDetailPane#showDetailNode
     * @since 1.6
     */
    public final void showSingleChart() {
        setShowLower(false);
        getUpperMasterDetailPane().setShowDetailNode(false);
    }

    /**
     * Convenience method to change the container settings in such a way that the
     * Gantt chart in the upper left corner and the Gantt chart in the lower left
     * corner will take over the entire width and height of the container.
     *
     * @param equalHeight if true then both Gantt charts will receive the same height,
     *                    otherwise the current value of the divider position will be kept
     * @see #setShowLower(boolean)
     * @see MasterDetailPane#showDetailNode
     * @since 1.6
     */
    public final void showHorizontalSplitScreen(boolean equalHeight) {
        if (equalHeight) {
            getUpperLowerMasterDetailPane().setDividerPosition(.5);
        }
        setShowLower(true);
        getUpperMasterDetailPane().setShowDetailNode(false);
    }

    /**
     * Convenience method to change the container settings in such a way that the
     * Gantt chart in the upper left corner and the Gantt chart in the upper right
     * corner will take over the entire width and height of the container.
     *
     * @param equalWidth if true then both Gantt charts will receive the same width,
     *                   otherwise the current value of the divider position will be kept
     * @see #setShowLower(boolean)
     * @see MasterDetailPane#showDetailNode
     * @since 1.6
     */
    public final void showVerticalSplitScreen(boolean equalWidth) {
        if (equalWidth) {
            getUpperMasterDetailPane().setDividerPosition(.5);
        }
        setShowLower(false);
        getUpperMasterDetailPane().setShowDetailNode(true);
    }

    /**
     * Convenience method to change the container settings in such a way that all
     * four Gantt charts in all four corners of the container will take over the
     * entire width and height of the container.
     *
     * @param equalSize if true then all Gantt charts will receive the same width and height,
     *                  otherwise the current value of the divider position will be kept
     * @see #setShowLower(boolean)
     * @see MasterDetailPane#showDetailNode
     * @since 1.6
     */
    public final void showAllFour(boolean equalSize) {
        if (equalSize) {
            getUpperMasterDetailPane().setDividerPosition(.5);
            getUpperLowerMasterDetailPane().setDividerPosition(.5);
        }
        setShowLower(true);
        getUpperMasterDetailPane().setShowDetailNode(true);
    }
}
