/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.Calendar;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.util.IntervalTree;
import com.flexganttfx.view.container.DualGanttChartContainer;
import com.flexganttfx.view.container.MultiGanttChartContainerBase;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.graphics.SingleRowGraphics;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.FlexGanttFXControl;
import com.flexganttfx.view.util.Position;
import com.flexganttfx.view.util.TimelineScrollBar;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.MasterDetailPane;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;
import static javafx.geometry.Side.RIGHT;

/**
 * Abstract base class for all Gantt chart controls. For detailed information
 * please refer to the documentation on those classes.
 *
 * @see GanttChart
 * @see GanttChartLite
 * @param <R> the type of the rows shown by the Gantt chart (e.g. "Aircraft")
 * @since 1.6
 */
public abstract class GanttChartBase<R extends Row<?, ?, ?>> extends FlexGanttFXControl {

    private static final String DEFAULT_STYLE_CLASS = "gantt-chart";

    private ListViewGraphics<R> graphics;

    private final Timeline timeline;
    private final TimelineScrollBar timelineScrollBar;
    private final MasterDetailPane graphicsMasterDetailPane;

    /**
     * Constructs a new Gantt Chart control.
     *
     * @since 1.6
     */
    protected GanttChartBase() {

        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        /**
         * I do not know why but for some reason some of the styles inside gantt.css for
         * controls like MasterDetailPane will only be applied if we add the stylesheet
         * also like this.
         */
        getStylesheets().add(GanttChartBase.class.getResource("gantt.css").toExternalForm());

        // children controls
        timeline = createTimeline();
        timeline.extraWidthProperty().bind(rowHeaderWidthProperty());
        setMasterTimeline(timeline);

        graphics = createGraphics();
        graphics.timelineProperty().bind(masterTimelineProperty());
        graphics.fixedCellSizeProperty().bind(fixedCellSizeProperty());
        graphics.rowHeaderWidthProperty().bind(rowHeaderWidthProperty());

        timelineScrollBar = new TimelineScrollBar();
        timelineScrollBar.timelineProperty().bind(masterTimelineProperty());

        final Label noDetailsLabel = new Label("No Details");
        noDetailsLabel.setAlignment(Pos.CENTER);
        setDetail(noDetailsLabel);

        graphicsMasterDetailPane = new MasterDetailPane(RIGHT);
        graphicsMasterDetailPane.setDividerPosition(.8);
        graphicsMasterDetailPane.setId("graphics-master-detail-pane");
        Bindings.bindBidirectional(graphicsMasterDetailPane.showDetailNodeProperty(), showDetailProperty());

        redrawObservable(masterTimeline);

        final SingleRowGraphics<Row<?, ?, ?>> eventlineGraphics = timeline.getEventline().getGraphics();
        eventlineGraphics.setOnActivityChange(evt -> getGraphics().redraw());
    }

    @Override
    public String getUserAgentStylesheet() {
        return super.getUserAgentStylesheet(GanttChartBase.class, "gantt.css");
    }

    private final DoubleProperty rowHeaderWidth = new SimpleDoubleProperty(this, "rowHeaderWidth", 100);

    /**
     * Specifies the width of the so-called "row headers". inside the graphics area. These are custom
     * nodes that can be placed in front of every row inside the graphics area. For proper layout the
     * width of all row headers has to be the same.
     *
     * @see GraphicsBase#setRowHeaderFactory(Callback)
     * @return the width in pixels used for all row headers
     * @since 11.11.0
     */
    public final DoubleProperty rowHeaderWidthProperty() {
        return rowHeaderWidth;
    }

    public final double getRowHeaderWidth() {
        return rowHeaderWidth.get();
    }

    public final void setRowHeaderWidth(double rowHeaderWidth) {
        this.rowHeaderWidth.set(rowHeaderWidth);
    }

    private final ObjectProperty<Predicate<R>> rowFilter = new SimpleObjectProperty<>(this, "rowFilter", row -> true);

    /**
     * A predicate used to filter the rows.
     *
     * @return the filter predicate
     */
    public final ObjectProperty<Predicate<R>> rowFilterProperty() {
        return rowFilter;
    }

    /**
     * Sets the value of {@link #rowFilterProperty}.
     *
     * @param predicate the filter predicate
     */
    public final void setRowFilter(Predicate<R> predicate) {
        rowFilter.set(predicate);
    }

    /**
     * Returns the value of {@link #rowFilterProperty}.
     *
     * @return the filter predicate
     */
    public final Predicate getRowFilter() {
        return rowFilter.get();
    }

    /**
     * Creates the graphics view used by the Gantt chart. Applications can
     * override this method to return a customized graphics view.
     *
     * @return a graphics view instance
     * @since 1.6
     */
    protected ListViewGraphics<R> createGraphics() {
        return new ListViewGraphics<>();
    }

    private final InvalidationListener redrawListener = observable -> {
        if (observable instanceof ReadOnlyProperty) {
            if (LoggingDomain.RENDERING.isLoggable(Level.FINE)) {
                LoggingDomain.RENDERING
                        .fine("redraw because of property change, property = "
                                + ((ReadOnlyProperty<?>) observable).getName());
            }
        }

        if (graphics != null) {
            graphics.redraw("redraw listener in GanttChartBase fired");
        }
    };

    // Timeline placeholder / graphics header support.

    private final ObjectProperty<Node> graphicsHeader = new SimpleObjectProperty<>(
            this, "graphicsHeader");

    /**
     * A property used to store a node that will be placed above the graphics
     * area instead of the timeline. This can be very useful when, for example,
     * using a {@link DualGanttChartContainer} where the users do not want to
     * see two timelines at the same time. The graphics header node could simply
     * be a gray empty area or it could be used as a toolbar control for the
     * secondary Gantt chart.
     *
     * @return the graphics header node property
     * @since 1.6
     */
    public final ObjectProperty<Node> graphicsHeaderProperty() {
        return graphicsHeader;
    }

    /**
     * Returns the value of {@link #graphicsHeaderProperty()}.
     *
     * @return the graphics header node
     * @since 1.6
     */
    public final Node getGraphicsHeader() {
        return graphicsHeaderProperty().get();
    }

    /**
     * Sets the value of {@link #graphicsHeaderProperty()}.
     *
     * @param node the node used as a header above the graphics area
     * @since 1.6
     */
    public final void setGraphicsHeader(Node node) {
        requireNonNull(node);
        graphicsHeaderProperty().set(node);
    }

    private final InvalidationListener weakRedrawListener = new WeakInvalidationListener(
            redrawListener);

    protected void redrawObservable(Observable property) {
        property.addListener(weakRedrawListener);
    }

	/*
     * Position support (first, middle, last, only). Needed for multi gantt
	 * chart use.
	 */

    private final ObjectProperty<Position> position = new SimpleObjectProperty<>(
            this, "position", Position.ONLY);

    /**
     * A property used to store the position of the Gantt chart in a multi Gantt
     * chart context, for example, inside a {@link DualGanttChartContainer} or a
     * {@link MultiGanttChartContainerBase}. One of the charts will be the first,
     * some will be in the middle, and one will be the last. The first chart is
     * the owner of the master timeline.
     *
     * @return the Gantt chart position
     * @see #getMasterTimeline()
     * @since 1.6
     */
    public final ObjectProperty<Position> positionProperty() {
        return position;
    }

    /**
     * Returns the value of {@link #positionProperty()}.
     *
     * @return the position of the chart
     * @since 1.6
     */
    public final Position getPosition() {
        return positionProperty().get();
    }

    /**
     * Sets the value of {@link #positionProperty()}.
     *
     * @param position the chart position (first, last, middle, only)
     */
    public final void setPosition(Position position) {
        Objects.requireNonNull(position);
        positionProperty().set(position);
    }

    private final ObjectProperty<Timeline> masterTimeline = new SimpleObjectProperty<>(
            this, "masterTimeline");

    /**
     * A property used to store a reference to the timeline that is being
     * considered the "master" timeline. This property is needed when several
     * charts are used in combination with each other (see
     * {@link DualGanttChartContainer} or {@link MultiGanttChartContainerBase}). The
     * master timeline is the one provided by the Gantt chart located at the top
     * of the container (the "first" chart).
     *
     * @return the master timeline
     * @see Position
     * @see #setPosition(Position)
     * @since 1.6
     */
    public final ObjectProperty<Timeline> masterTimelineProperty() {
        return masterTimeline;
    }

    /**
     * Returns the value of {@link #masterTimelineProperty()}.
     *
     * @return the master timeline
     * @since 1.6
     */
    public final Timeline getMasterTimeline() {
        return masterTimeline.get();
    }

    /**
     * Sets the value of {@link #masterTimelineProperty()}.
     *
     * @param timeline the new master timeline
     * @since 1.6
     */
    public final void setMasterTimeline(Timeline timeline) {
        requireNonNull(timeline);
        masterTimelineProperty().set(timeline);
    }

    /**
     * Returns the {@link Timeline} instance, which consists of the
     * {@link Dateline} and the {@link Eventline}.
     *
     * @return the timeline control
     * @see #createTimeline()
     * @since 1.6
     */
    public final Timeline getTimeline() {
        return timeline;
    }

    /**
     * Creates the timeline component used by the Gantt chart. Applications can
     * override this method to return a customized timeline.
     *
     * @return the timeline instance
     * @since 1.6
     */
    protected Timeline createTimeline() {
        return new Timeline();
    }

    /**
     * Returns the graphics view shown on the right-hand side of the Gantt
     * chart. The view is set as the master node on the primary
     * {@link MasterDetailPane} instance.
     *
     * @return the graphics view
     * @see #createGraphics()
     * @since 1.6
     */
    public final ListViewGraphics<R> getGraphics() {
        return graphics;
    }

    /**
     * Returns the specialized timeline scrollbar control used for scrolling
     * forward and back in time. The scrollbar becomes visible when the user
     * moves the mouse cursor close to the bottom edge of the graphics area.
     *
     * @return the timeline scrollbar
     * @see HiddenSidesPane
     * @since 1.6
     */
    public final TimelineScrollBar getTimelineScrollBar() {
        return timelineScrollBar;
    }

    /**
     * Returns the {@link MasterDetailPane} instance that is used to display the
     * {@link ListViewGraphics} in the master section and the detail node in the
     * detail section.
     *
     * @return the master detail pane
     * @since 1.6
     */
    public MasterDetailPane getGraphicsMasterDetailPane() {
        return graphicsMasterDetailPane;
    }

    // Detail node support.

    private final ObjectProperty<Node> detail = new SimpleObjectProperty<>(this, "detail");

    /**
     * A property used to store a node that can be made visible on the
     * right-hand side of the Gantt chart. The node can be used to show any kind
     * of controls that the application might require.
     *
     * @return the detail node
     * @see #showDetailProperty()
     * @see MasterDetailPane#detailNodeProperty()
     * @since 1.6
     */
    public final ObjectProperty<Node> detailProperty() {
        return detail;
    }

    /**
     * Sets the value of {@link #detailProperty()}.
     *
     * @param node the new detail node
     * @since 1.6
     */
    public final void setDetail(Node node) {
        detailProperty().set(node);
    }

    /**
     * Returns the value of {@link #detailProperty()}.
     *
     * @return the detail node
     * @since 1.6
     */
    public final Node getDetail() {
        return detailProperty().get();
    }

    // Fixed cell size support.

    private final DoubleProperty fixedCellSize = new SimpleDoubleProperty(this,
            "fixedCellSize", -1);

    /**
     * A property used to set a fixed cell size for the rows shown in the table
     * on the left-hand side and the graphics area on the right-hand side.
     *
     * @return the fixed cell size
     * @see ListView#setFixedCellSize(double)
     * @see TreeTableView#setFixedCellSize(double)
     * @since 1.6
     */
    public final DoubleProperty fixedCellSizeProperty() {
        return fixedCellSize;
    }

    /**
     * Returns the value of {@link #fixedCellSizeProperty()}.
     *
     * @return the fixed cell size
     * @since 1.6
     */
    public final double getFixedCellSize() {
        return fixedCellSize.get();
    }

    /**
     * Sets the value of {@link #fixedCellSizeProperty()}.
     *
     * @param size the fixed cell size
     * @since 1.6
     */
    public final void setFixedCellSize(double size) {
        fixedCellSizeProperty().set(size);
    }

    // Detail support (by default contains the property sheet).

    private final BooleanProperty showDetail = new SimpleBooleanProperty(this,
            "showDetail", false);

    /**
     * A property used to control whether the "details" node will be shown or
     * not. This node gets shown on the right-hand side of the Gantt chart and
     * can contain arbitrary content. By default it is used to display a
     * property sheet with the current settings / configuration of the Gantt
     * chart. The detail node is shown by the secondary master detail pane (see
     * {@link #getGraphicsMasterDetailPane()}).
     *
     * @return the show details property
     * @see MasterDetailPane#detailNodeProperty()
     * @see MasterDetailPane#setDetailNode(Node)
     * @see #getGraphicsMasterDetailPane()
     * @since 1.6
     */
    public final BooleanProperty showDetailProperty() {
        return showDetail;
    }

    /**
     * Returns the value of {@link #showDetailProperty()}.
     *
     * @return true if the detail node gets shown
     * @since 1.6
     */
    public final boolean isShowDetail() {
        return showDetailProperty().get();
    }

    /**
     * Sets the value of {@link #showDetailProperty()}.
     *
     * @param show if true the detail node will be shown on the right-hand side
     *             of the Gantt chart
     * @since 1.6
     */
    public final void setShowDetail(boolean show) {
        showDetailProperty().set(show);
    }

    // Timeline placeholder / graphics header support.

    /**
     * Convenience method to return the list of layers registered on the
     * graphics view.
     *
     * @return the layers registered on the graphics area
     * @see GraphicsBase#getLayers()
     * @since 1.6
     */
    public final ObservableList<Layer> getLayers() {
        return graphics.getLayers();
    }

    /**
     * Convenience method to return the list of links registered on the graphics
     * view.
     *
     * @return the links registered on the graphics area
     * @see GraphicsBase#getLinks()
     * @since 1.6
     */
    public final IntervalTree<ActivityLink> getLinks() {
        return graphics.getLinks();
    }

    /**
     * Convenience method to return the list of calendars registered on the
     * graphics view.
     *
     * @return the calendars registered on the graphics area
     * @see GraphicsBase#getCalendars()
     * @since 1.6
     */
    public final ObservableList<Calendar<?>> getCalendars() {
        return graphics.getCalendars();
    }
}
