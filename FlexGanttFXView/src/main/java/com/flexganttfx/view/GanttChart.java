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
package com.flexganttfx.view;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.container.DualGanttChartContainer;
import com.flexganttfx.view.container.MultiGanttChartContainer;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.Messages;
import com.flexganttfx.view.util.Position;
import com.flexganttfx.view.util.RowHeaderColumn;
import com.flexganttfx.view.util.ThemingUtil;
import impl.com.flexganttfx.skin.GanttChartSkin;
import impl.com.flexganttfx.skin.treetable.GanttChartTreeItem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.MasterDetailPane;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static javafx.geometry.Orientation.HORIZONTAL;
import static javafx.geometry.Side.LEFT;

/**
 * A control used to visualize any kind of scheduling data along a
 * timeline. The model data needed by the control consists of rows with
 * activities, links between activities, and layers to group activities
 * together.
 * <ul>
 * <li>{@link #setRoot(Row)} - sets the root row.</li>
 * <li>{@link #getLayers()} - returns the list of layers</li>
 * <li>{@link #getLinks()} - returns the list of links between activities</li>
 * </ul>
 * <p>
 * <p>
 * The control consists of several children controls:
 * <ul>
 * <li>{@link TreeTableView}: shown on the left-hand side to display a
 * hierarchical structure of rows</li>
 * <li>{@link GraphicsBase}: shown on the right-hand side to display a graphical
 * representation of the model data</li>
 * <li>{@link Timeline}: shown above the graphics view. The timeline itself
 * consists of two child controls.</li>
 * <li>{@link Dateline}: displays days, weeks, months, years, etc...</li>
 * <li>{@link Eventline}: displays various time markers</li>
 * </ul>
 * The screenshot belows shows the initial appearance of an empty Gantt chart
 * control.<br>
 *  <img src="doc-files/gantt-chart.png" width="100%" alt=
 * "Gantt Chart Control" >
 * <h2>Master / Detail Panes</h2> The Gantt chart uses two MasterDetailPane
 * instances from ControlsFX for the high-level layout. The <u>tree table</u>
 * master detail pane displays the tree table as its detail node. The
 * <u>graphics</u> master detail pane displays a property sheet as
 * its detail node. The property sheet is used at development time and can be
 * replaced with any node by calling {@link #setDetail(Node)}. The property
 * sheet displays a lot of properties that are used by the controls, the
 * renderers, the system layers to fine-tune the appearance of the control. Many
 * of them can be changed at runtime.
 * <h2>Standalone vs. Multi- / Dual Gantt Chart</h2> A Gantt chart can be used
 * standalone or inside a {@link MultiGanttChartContainer} or
 * {@link DualGanttChartContainer}. When used in one of these containers the
 * {@link Position} of the Gantt chart becomes important. The control can be the
 * first chart, the last chart, the only chart, or a chart somewhere in the
 * middle. A "first" or "only" chart always displays a timeline. A "middle" or
 * "last" displays a special header (see {@link #setGraphicsHeader(Node)}). The
 * containers are also the reason why the control distinguishes between a
 * timeline ({@link #getTimeline()}) and a master timeline (
 * {@link #getMasterTimeline()}). The master timeline is the one shown by the
 * "first" chart, while the regular timeline is the one that belongs directly to
 * an individual Gantt chart instance.
 * <p>
 * <h2>Code Example</h2>
 * <p>
 * <pre>
 * import java.time.Duration;
 * import java.time.Instant;
 * import java.time.temporal.ChronoUnit;
 *
 * import javafx.application.Application;
 * import javafx.scene.Scene;
 * import javafx.stage.Stage;
 *
 * import com.flexganttfx.model.GanttChartModel;
 * import com.flexganttfx.model.Layer;
 * import com.flexganttfx.model.Row;
 * import com.flexganttfx.model.activity.MutableActivityBase;
 * import com.flexganttfx.model.layout.GanttLayout;
 * import com.flexganttfx.view.GanttChart;
 * import com.flexganttfx.view.graphics.GraphicsView;
 * import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
 * import com.flexganttfx.view.timeline.Timeline;
 *
 * public class TutorialAircraftFlight extends Application {
 *
 *
 *  class FlightData {
 *  	String flightNo;
 *  	Instant departureTime = Instant.now();
 *  	Instant arrivalTime = Instant.now().plus(Duration.ofHours(6));
 *
 *  	public FlightData(String flightNo, int day) {
 *  		this.flightNo = flightNo;
 *  		departureTime = departureTime.plus(Duration.ofDays(day));
 *  		arrivalTime = arrivalTime.plus(Duration.ofDays(day));
 *    }
 *  }
 *
 *  class Flight extends MutableActivityBase&lt;FlightData&gt; {
 *  	public Flight(FlightData data) {
 *  		setUserObject(data);
 *  		setName(data.flightNo);
 *  		setStartTime(data.departureTime);
 *  		setEndTime(data.arrivalTime);
 *    }
 *  }
 *
 *  class Aircraft extends Row&lt;Aircraft, Aircraft, Flight&gt; {
 *  	public Aircraft(String name) {
 *  		super(name);
 *    }
 *  }
 *
 *  public void start(Stage stage) {
 *  	// Create the root row
 *  	Aircraft root = new Aircraft("Root");
 *  	root.setExpanded(true);
 *
 *  	// Create the Gantt chart
 *  	GanttChart&lt;Aircraft&gt; gantt = new GanttChart&lt;&gt;(new FlightSchedule(new Aircraft(&quot;ROOT&quot;)));
 *
 *  	Layer flightsLayer = new Layer(&quot;Flights&quot;);
 *  	gantt.getLayers().add(flightsLayer);
 *
 *  	Aircraft b747 = new Aircraft(&quot;B747&quot;);
 *  	b747.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight1&quot;, 1)));
 *  	b747.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight2&quot;, 2)));
 *  	b747.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight3&quot;, 3)));
 *
 *  	Aircraft a380 = new Aircraft(&quot;A380&quot;);
 *  	a380.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight1&quot;, 1)));
 *  	a380.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight2&quot;, 2)));
 *  	a380.addActivity(flightsLayer, new Flight(new FlightData(&quot;flight3&quot;, 3)));
 *
 *  	root.getChildren().setAll(b747, a380);
 *
 *  	Timeline timeline = gantt.getTimeline();
 *  	timeline.showTemporalUnit(ChronoUnit.HOURS, 10);
 *
 *  	GraphicsView&lt;Aircraft&gt; graphics = gantt.getGraphics();
 *  	graphics.setActivityRenderer(Flight.class, GanttLayout.class,
 *  			new ActivityBarRenderer&lt;&gt;(graphics, &quot;Flight Renderer&quot;));
 *  	graphics.showEarliestActivities();
 *
 *  	Scene scene = new Scene(gantt);
 *  	stage.setScene(scene);
 *  	stage.sizeToScene();
 *  	stage.centerOnScreen();
 *  	stage.show();
 *  }
 *
 *  public static void main(String[] args) {
 *  	launch(args);
 *  }
 * </pre>
 *
 * @param <R> the type of the rows shown by the Gantt chart (e.g. "Aircraft")
 * @since 1.0
 */
public class GanttChart<R extends Row<?, ?, ?>> extends GanttChartBase<R> {

    private static final String STANDARD_DISPLAY_MODE_TREE_TABLE_VIEW = "tree-table-view-standard-display-mode";
    private static final String TABLE_ONLY_DISPLAY_MODE_TREE_TABLE_VIEW = "tree-table-view-table-only-display-mode";
    private static final String GRAPHICS_ONLY_DISPLAY_MODE_TREE_TABLE_VIEW = "tree-table-view-graphics-only-display-mode";

    private final TreeTableView<R> treeTableView;
    private final RowHeaderColumn<R> rowHeader;
    private final ScrollBar treeTableScrollBar;
    private final MasterDetailPane treeTableMasterDetailPane;

    /**
     * Constructs a new Gantt chart control.
     *
     * @since 1.0
     */
    public GanttChart() {
        this(null);
    }

    /**
     * Constructs a new Gantt Chart control.
     *
     * @param root the root row of the Gantt chart
     * @since 1.0
     */
    public GanttChart(R root) {
        setRoot(root);

        // children controls

        treeTableView = createTreeTable();
        treeTableView.getStyleClass().addAll("gantt-tree-table-view");

        redrawObservable(treeTableView.sortModeProperty());

        displayModeProperty().addListener(evt -> updateTreeTableStyles());
        updateTreeTableStyles();

        treeTableScrollBar = new ScrollBar();
        treeTableScrollBar.setOrientation(HORIZONTAL);

        scrollBarTypeProperty().addListener(it -> {
            if (!(treeTableScrollBar.getParent() instanceof HiddenSidesPane)) {
                treeTableScrollBar.setVisible(getScrollBarType().equals(ScrollBarType.FIXED_HORIZON));
                treeTableScrollBar.setManaged(getScrollBarType().equals(ScrollBarType.FIXED_HORIZON));
            }
        });

        treeTableMasterDetailPane = new MasterDetailPane(LEFT) {
            /**
             * Returns no user agent stylesheet for the tree table master detail pane.
             *
             * @return {@code null}
             */
            @Override
            public String getUserAgentStylesheet() {
                return null;
            }
        };

        treeTableMasterDetailPane.setId("treetable-master-detail-pane");
        Bindings.bindBidirectional(treeTableMasterDetailPane.showDetailNodeProperty(), showTreeTableProperty());

        redrawObservable(rootProperty());

        rowHeader = createRowHeaderColumn();

        TreeTableColumn<R, String> nameColumn = new TreeTableColumn<>(Messages.getString("GanttChart.NAME_COLUMN"));
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        nameColumn.setEditable(true);

        getTreeTable().getColumns().add(nameColumn);
        getTreeTable().setTreeColumn(nameColumn);

        Region graphicViewHeader = new Region();
        graphicViewHeader.getStyleClass().add("graphic-view-header");
        setGraphicsHeader(graphicViewHeader);

        GraphicsBase<R> graphics = getGraphics();
        graphics.rowFilterProperty().bindBidirectional(rowFilterProperty());
    }

    /**
     * Creates the default skin for this Gantt chart.
     *
     * @return the default skin
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new GanttChartSkin<>(this);
    }

    private void updateTreeTableStyles() {
        treeTableView.getStyleClass().remove(STANDARD_DISPLAY_MODE_TREE_TABLE_VIEW);
        treeTableView.getStyleClass().remove(TABLE_ONLY_DISPLAY_MODE_TREE_TABLE_VIEW);
        treeTableView.getStyleClass().remove(GRAPHICS_ONLY_DISPLAY_MODE_TREE_TABLE_VIEW);

        switch (getDisplayMode()) {
            case STANDARD:
                treeTableView.getStyleClass().add(STANDARD_DISPLAY_MODE_TREE_TABLE_VIEW);
                break;
            case TABLE_ONLY:
                treeTableView.getStyleClass().add(TABLE_ONLY_DISPLAY_MODE_TREE_TABLE_VIEW);
                break;
            case GRAPHICS_ONLY:
                treeTableView.getStyleClass().add(GRAPHICS_ONLY_DISPLAY_MODE_TREE_TABLE_VIEW);
                break;
            default:
                break;
        }
    }

    /**
     * An enum used for specifying how to layout the Gantt chart.
     *
     * @see GanttChart#setDisplayMode(DisplayMode)
     * @since 1.0
     */
    public enum DisplayMode {

        /**
         * Show the tree table view and the graphics view.
         *
         * @since 1.0
         */
        STANDARD,

        /**
         * Only display the tree table view.
         *
         * @since 1.0
         */
        TABLE_ONLY,

        /**
         * Only display the graphics view.
         *
         * @since 1.0
         */
        GRAPHICS_ONLY
    }

    private final ObjectProperty<DisplayMode> displayMode = new SimpleObjectProperty<>(this, "displayMode", DisplayMode.STANDARD);

    /**
     * A property used to specify the mode in which the Gantt chart will layout
     * its primary views, the table and the graphics. Using this property the
     * application can quickly switch between a standard, table-only, or
     * graphics-only display.
     *
     * @return the current display mode
     * @since 1.0
     */
    public final ObjectProperty<DisplayMode> displayModeProperty() {
        return displayMode;
    }

    /**
     * Sets the value of the {@link #displayModeProperty()}.
     *
     * @param mode the new display mode
     * @since 1.0
     */
    public final void setDisplayMode(DisplayMode mode) {
        displayModeProperty().set(mode);
    }

    /**
     * Returns the value of {@link #displayModeProperty()}.
     *
     * @return the display mode (standard, table only, graphics only)
     * @since 1.0
     */
    public final DisplayMode getDisplayMode() {
        return displayModeProperty().get();
    }

    /**
     * Creates the tree table view instance. Applications can override this
     * method to return a customized table.
     *
     * @return a tree table view instance
     * @since 1.0
     */
    protected TreeTableView<R> createTreeTable() {
        TreeTableView<R> table = new TreeTableView<>() {
            /**
             * Returns the user agent stylesheet for the tree table view.
             *
             * @return the user agent stylesheet URL
             */
            @Override
            public String getUserAgentStylesheet() {
                if (ThemingUtil.isAtlantaFXActive(getScene())) {
                    return requireNonNull(GanttChartBase.class.getResource("gantt-atlantafx.css")).toExternalForm();
                }
                return requireNonNull(GanttChartBase.class.getResource("gantt.css")).toExternalForm();
            }
        };
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setPlaceholder(new Label(Messages.getString("GanttChart.PLACEHOLDER_NO_DATA")));
        table.getStyleClass().add("striped"); // atlantafx
        return table;
    }

    // --- Table menu button visible
    private BooleanProperty tableMenuButtonVisible;

    /**
     * This controls whether a menu button is available when the user clicks in
     * a designated space within the TreeTableView, within which is a check menu
     * item for each column in this table. This menu allows for the user to show
     * and hide all TreeTableColumns easily.
     *
     * @return the property used to store the button visibility
     * @since 1.0
     */
    public final BooleanProperty tableMenuButtonVisibleProperty() {
        if (tableMenuButtonVisible == null) {
            tableMenuButtonVisible = new SimpleBooleanProperty(this, "tableMenuButtonVisible", true);
        }
        return tableMenuButtonVisible;
    }

    /**
     * Sets the value of {@link #tableMenuButtonVisibleProperty()}.
     *
     * @param value if true, the menu button will be shown to the user
     * @since 1.0
     */
    public final void setTableMenuButtonVisible(boolean value) {
        tableMenuButtonVisibleProperty().set(value);
    }

    /**
     * Returns the value of {@link #tableMenuButtonVisibleProperty()}.
     *
     * @return true if the table menu button is visible
     */
    public final boolean isTableMenuButtonVisible() {
        return tableMenuButtonVisible != null && tableMenuButtonVisible.get();
    }

    private final ObjectProperty<R> root = new SimpleObjectProperty<>(this, "root");

    /**
     * Returns the root row property. The root row will become the root node of
     * the Gantt chart's tree table control on the left-hand side (wrapped
     * inside an instance of type {@link GanttChartTreeItem}). Other rows can be
     * added by adding them to the root row or one of its children.
     *
     * @return the object property used for storing the root row
     * @see Row#getChildren()
     * @since 1.0
     */
    public final ObjectProperty<R> rootProperty() {
        return root;
    }

    /**
     * Sets a new root on the Gantt chart, which will cause the framework to set
     * a new root of type {@link GanttChartTreeItem} on the underlying
     * {@link TreeTableView}.
     *
     * @param root the new root of the model
     * @see #rootProperty()
     */
    public final void setRoot(R root) {
        rootProperty().set(root);
    }

    /**
     * Returns the root row of the Gantt chart.
     *
     * @return the root row
     * @since 1.0
     */
    public final R getRoot() {
        return root.get();
    }

    /**
     * Returns the row header control used as the first column of the tree table
     * view. The row header displays line numbers, or level numbers, or any
     * arbitrary graphics node.
     *
     * @return the row header (column)
     */
    public final RowHeaderColumn<R> getRowHeaderColumn() {
        return rowHeader;
    }

    /**
     * Creates the row header column used by the Gantt chart. Applications can
     * override this method to return a customized row header.
     *
     * @return the row header column
     * @since 1.0
     */
    protected RowHeaderColumn<R> createRowHeaderColumn() {
        return new RowHeaderColumn<>(this);
    }

    /**
     * Returns the scrollbar that is being used for horizontal scrolling
     * operations of the tree table view.
     *
     * @return the horizontal tree table view scrollbar
     * @since 1.0
     */
    public final ScrollBar getTreeTableScrollBar() {
        return treeTableScrollBar;
    }

    /**
     * Returns the {@link TreeTableView} instance that is shown on the left-hand
     * side of the Gantt chart.
     *
     * @return the tree table view
     * @see #createTreeTable()
     * @since 1.0
     */
    public final TreeTableView<R> getTreeTable() {
        return treeTableView;
    }

    /**
     * Returns the primary {@link MasterDetailPane} instance that is being used
     * to display the {@link TreeTableView} and the {@link ListViewGraphics}.
     *
     * @return the primary master detail pane
     * @since 1.6
     */
    public MasterDetailPane getTreeTableMasterDetailPane() {
        return treeTableMasterDetailPane;
    }

    // Support for showing / hiding the left-hand side / the tree table view

    private final BooleanProperty showTreeTable = new SimpleBooleanProperty(this, "showTreeTable", true);

    /**
     * A property used to control whether the tree table view will be shown or
     * not. This node gets shown on the left-hand side of the Gantt chart and
     * dispays a hierarchy (e.g. a resource hierarchy). The tree table view is
     * the detail node of the primary master detail pane (see
     * {@link #getTreeTableMasterDetailPane()}).
     *
     * @return the show tree table property
     * @see MasterDetailPane#detailNodeProperty()
     * @see MasterDetailPane#setDetailNode(Node)
     * @see #getTreeTableMasterDetailPane()
     * @since 1.0
     */
    public final BooleanProperty showTreeTableProperty() {
        return showTreeTable;
    }

    /**
     * Returns the value of {@link #showTreeTableProperty()}.
     *
     * @return true if the tree table should be shown
     * @since 1.0
     */
    public final boolean isShowTreeTable() {
        return showTreeTableProperty().get();
    }

    /**
     * Sets the value of {@link #showTreeTableProperty()}.
     *
     * @param show if true, the tree table becomes visible
     * @since 1.0
     */
    public final void setShowTreeTable(boolean show) {
        showTreeTableProperty().set(show);
    }

    /**
     * An enum used to control the visuals of the cells in the row header
     * column.
     *
     * @see RowHeaderColumn
     * @see GanttChart#setRowHeaderType(RowHeaderType)
     * @see GanttChart#setRowHeaderNodeFactory(Callback)
     * @since 1.0
     */
    public enum RowHeaderType {

        /**
         * Makes the row header cells display the number of the current row (1,
         * 2, 3, ....).
         *
         * @since 1.0
         */
        ROW_NUMBER,

        /**
         * Makes the row header cells display the level number of the current
         * row (1, 1.1, 1.2, 2, 2.1, 2.2, 2.3, ...).
         *
         * @since 1.0
         */
        LEVEL_NUMBER,

        /**
         * Makes the row header cells display a custom node for each row.
         *
         * @see GanttChart#setRowHeaderNodeFactory(Callback)
         * @since 1.0
         */
        GRAPHIC_NODE
    }

    private final ObjectProperty<RowHeaderType> rowHeaderType = new SimpleObjectProperty<>(this, "rowHeaderType", RowHeaderType.ROW_NUMBER);

    /**
     * A property used to store the currently used type of row headers (row
     * number, level number, custom graphics).
     *
     * @return the row header type
     * @see RowHeaderType
     * @since 1.0
     */
    public final ObjectProperty<RowHeaderType> rowHeaderTypeProperty() {
        return rowHeaderType;
    }

    /**
     * Sets the value of {@link #rowHeaderTypeProperty()}.
     *
     * @param type the row header type (row number, level number, graphics)
     * @since 1.0
     */
    public final void setRowHeaderType(RowHeaderType type) {
        requireNonNull(type);
        rowHeaderTypeProperty().set(type);
    }

    /**
     * Returns the value of {@link #rowHeaderTypeProperty()}.
     *
     * @return the currently used row header type (line number, level number,
     * custom graphics)
     * @since 1.0
     */
    public final RowHeaderType getRowHeaderType() {
        return rowHeaderTypeProperty().get();
    }

    private final ObjectProperty<Callback<R, Node>> rowHeaderNodeFactory = new SimpleObjectProperty<>(this, "rowHeaderNodeFactory", row -> new Label());

    /**
     * A property used to store a callback for creating a node that will be
     * placed to the left of each row in the tree table view. This factory will
     * only be invoked if the row header type is set to
     * {@link RowHeaderType#GRAPHIC_NODE}.
     *
     * @return the row header node callback property
     * @see #setRowHeaderType(RowHeaderType)
     * @since 1.0
     */
    public final ObjectProperty<Callback<R, Node>> rowHeaderNodeFactoryProperty() {
        return rowHeaderNodeFactory;
    }

    /**
     * Sets the value of {@link #rowHeaderNodeFactoryProperty()}.
     *
     * @param factory the factory used for creating the row header nodes
     * @since 1.0
     */
    public final void setRowHeaderNodeFactory(Callback<R, Node> factory) {
        requireNonNull(factory);
        rowHeaderNodeFactory.set(factory);
    }

    /**
     * Returns the value of {@link #rowHeaderNodeFactoryProperty()}.
     *
     * @return the row header nodes factory
     */
    public final Callback<R, Node> getRowHeaderNodeFactory() {
        return rowHeaderNodeFactory.get();
    }

    /**
     * Expands all rows inside the Gantt chart. This method will use recursion
     * to find all rows that are parents and to expand them.
     *
     * @see Row#setExpanded(boolean)
     * @see #expandRowsByOneLevel()
     * @see #collapseRows()
     * @see #collapseRowsByOneLevel()
     * @since 1.3
     */
    public final void expandRows() {
        expandOrCollapseRows(true, true);
    }

    /**
     * Expands the next level of rows inside the Gantt chart. This method will
     * use recursion to find the first rows that are parents and currently not
     * expanded. It will then expand it to make its children visible. Successive
     * calls to this method will eventually show all rows.
     *
     * @see Row#setExpanded(boolean)
     * @see #expandRows()
     * @see #collapseRows()
     * @see #collapseRowsByOneLevel()
     * @since 1.3
     */
    public final void expandRowsByOneLevel() {
        expandOrCollapseRows(true, false);
    }

    /**
     * Collapses all rows inside the Gantt chart. This method will use recursion
     * to find all rows that are parents and to collapse them.
     *
     * @see Row#setExpanded(boolean)
     * @see #collapseRowsByOneLevel()
     * @see #expandRows()
     * @see #expandRowsByOneLevel()
     * @since 1.3
     */
    public final void collapseRows() {
        expandOrCollapseRows(false, true);
    }

    /**
     * Collapses the hightest level of rows inside the Gantt chart that is
     * currently being used. Successive calls to this method will eventually
     * close the entire tree.
     *
     * @see Row#setExpanded(boolean)
     * @see #expandRows()
     * @see #collapseRows()
     * @see #collapseRowsByOneLevel()
     * @since 1.3
     */
    public final void collapseRowsByOneLevel() {
        expandOrCollapseRows(false, false);
    }

    private void expandOrCollapseRows(boolean expand, boolean all) {
        Row<?, ?, ?> root = getRoot();
        if (root == null) {
            return;
        }

        if (expand) {
            openItem(root, all);
        } else {
            int maxDepth = 0;
            if (!all) {
                maxDepth = computeMaxDepth(root, 0);
            }

            closeItem(root, all, 0, maxDepth);

            if (!getTreeTable().isShowRoot()) {
                root.setExpanded(true);
            }
        }
    }

    private int computeMaxDepth(Row<?, ?, ?> row, int maxDepth) {
        int depth = row.getPath().length;
        if (row.isExpanded()) {
            for (Row<?, ?, ?> child : row.getChildren()) {
                depth = Math.max(computeMaxDepth(child, depth), depth);
            }
        }

        return Math.max(depth, maxDepth);
    }

    private void openItem(Row<?, ?, ?> item, boolean all) {
        if (item != null) {
            if (!item.isLeaf()) {
                if (item.isExpanded()) {
                    for (Object child : item.getChildren()) {
                        openItem((Row<?, ?, ?>) child, all);
                    }
                } else {
                    item.setExpanded(true);
                    if (all) {
                        for (Object child : item.getChildren()) {
                            openItem((Row<?, ?, ?>) child, true);
                        }
                    }
                }
            }
        }
    }

    private void closeItem(Row<?, ?, ?> item, boolean all, int depth, int closeLevel) {
        if (item != null) {
            for (Row<?, ?, ?> child : item.getChildren()) {
                closeItem(child, all, depth + 1, closeLevel);
            }
            if (all) {
                item.setExpanded(false);
            } else {
                if (item.getPath().length == closeLevel) {
                    Row<?, ?, ?> parent = item.getParent();
                    if (parent != null) {
                        parent.setExpanded(false);
                    }
                }
            }
        }
    }

    /**
     * This method will resize all columns in the tree table view to ensure that
     * the content of all cells will be completely visible. Note: this is a very
     * expensive operation and should only be used when the number of rows is
     * small.
     *
     * @see #resizeColumn(TreeTableColumn, int)
     * @since 1.3
     */
    public final void resizeColumns() {
        resizeColumns(-1);
    }

    /**
     * This method will resize all columns in the tree table view to ensure that
     * the content of all cells will be completely visible. Note: this is a very
     * expensive operation and should only be used with a small number of rows.
     *
     * @param maxRows the maximum number of rows that will be considered for the
     *                width calculations
     * @see #resizeColumn(TreeTableColumn, int)
     * @since 1.3
     */
    public final void resizeColumns(int maxRows) {
        for (TreeTableColumn<R, ?> column : getTreeTable().getColumns()) {
            resizeColumn(column, maxRows);
        }
    }

    /**
     * This method will resize the given column in the tree table view to ensure
     * that the content of the column cells will be completely visible. Note:
     * this is a very expensive operation and should only be used when the
     * number of rows is small.
     *
     * @param column the column that will be resized
     * @see #resizeColumn(TreeTableColumn, int)
     * @since 1.3
     */
    public final void resizeColumn(TreeTableColumn<R, ?> column) {
        resizeColumn(column, -1);
    }

    /**
     * This method will resize the given column in the tree table view to ensure
     * that the content of the column cells will be completely visible. Note:
     * this is a very expensive operation and should only be used when the
     * number of rows is small.
     *
     * @param tc      the column that will be resized
     * @param maxRows the maximum number of rows that will be evaluated for the
     *                width calculation
     * @see #resizeColumn(TreeTableColumn, int)
     * @since 1.3
     */
    public final void resizeColumn(TreeTableColumn tc, int maxRows) {
        List<?> items = getGraphics().getRows();
        if (items == null || items.isEmpty()) {
            return;
        }

        Callback cellFactory = tc.getCellFactory();
        if (cellFactory == null) {
            return;
        }

        TreeTableCell<R, ?> cell = (TreeTableCell<R, ?>) cellFactory.call(tc);
        if (cell == null) {
            return;
        }

        // set this property to tell the TableCell we want to know its actual
        // preferred width, not the width of the associated TableColumnBase
        cell.getProperties().put("deferToParentPrefWidth", Boolean.TRUE);

        // determine cell padding
        double padding = 10;
        Node n = cell.getSkin() == null ? null : cell.getSkin().getNode();
        if (n instanceof Region) {
            Region r = (Region) n;
            padding = r.snappedLeftInset() + r.snappedRightInset();
        }

        TreeTableRow<R> treeTableRow = new TreeTableRow<>();
        treeTableRow.updateTreeTableView(treeTableView);

        int rows = maxRows == -1 ? items.size() : Math.min(items.size(), maxRows);
        double maxWidth = 0;
        for (int row = 0; row < rows; row++) {
            treeTableRow.updateIndex(row);
            treeTableRow.updateTreeItem(treeTableView.getTreeItem(row));

            cell.updateTreeTableColumn(tc);
            cell.updateTreeTableView(treeTableView);
            cell.updateTreeTableRow(treeTableRow);
            cell.updateIndex(row);

            if ((cell.getText() != null && !cell.getText().isEmpty()) || cell.getGraphic() != null) {
                getChildren().add(cell);
                cell.applyCss();

                double w = cell.prefWidth(-1);

                maxWidth = Math.max(maxWidth, w);
                getChildren().remove(cell);
            }
        }

        // dispose of the cell to prevent it retaining listeners (see RT-31015)
        cell.updateIndex(-1);

        // RT-23486
        double widthMax = maxWidth + padding;
        if (treeTableView.getColumnResizePolicy() == TreeTableView.CONSTRAINED_RESIZE_POLICY) {
            widthMax = Math.max(widthMax, tc.getWidth());
        }

        tc.setPrefWidth(widthMax);
    }
}
