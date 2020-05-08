/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.util;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import impl.com.flexganttfx.skin.treetable.GanttChartTreeItem;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.geometry.Pos.CENTER_RIGHT;
import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;
import static javafx.scene.control.ContentDisplay.TEXT_ONLY;

/**
 * The row header is a specialized column which is used for controlling the
 * height of each row and for adding row numbers / row outline levels in front
 * of each row. Additionally it is possible to place an arbitrary node into each
 * row header cell. For this to work a row header node factory has to be
 * registered with the Gantt chart control.<br>
 * <br>
 * <p>
 *  <img src="doc-files/row-header.png" alt=
 * "Row Header Control" >
 *
 * @param <R> the type of the table rows
 * @see GanttChart#setRowHeaderNodeFactory(Callback)
 * @since 1.0
 */
public class RowHeaderColumn<R extends Row<?, ?, ?>> extends TreeTableColumn<R, R> {

    private TreeTableView<R> treeTable;
    private GanttChart<R> ganttChart;
    private Menu columns;
    private Pane cornerRegion;

    private final InvalidationListener columnListener = it -> updateColumnsMenu();

    private final InvalidationListener tableMenuButtonListener = it -> cornerRegion.setVisible(ganttChart.tableMenuButtonVisibleProperty().get());

    /**
     * Constructs a new row header.
     *
     * @param ganttChart the chart for which the header will be used
     */
    @SuppressWarnings("deprecation")
    public RowHeaderColumn(GanttChart<R> ganttChart) {
        super();

        requireNonNull(ganttChart);

        this.ganttChart = ganttChart;
        this.treeTable = ganttChart.getTreeTable();

        setCellFactory(column -> new RowHeaderCell());

        setPrefWidth(30);
        setResizable(true);
        setSortable(false);
        setEditable(false);

        setReorderable(false);

        // build context menu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem resizeColumns = new MenuItem(Messages.getString("RowHeader.MENU_ITEM_FIT_COLUMNS"));
        resizeColumns.setOnAction(evt -> ganttChart.resizeColumns());
        contextMenu.getItems().add(resizeColumns);

        columns = new Menu(Messages.getString("RowHeader.MENU_COLUMNS"));
        contextMenu.getItems().add(columns);

        contextMenu.getItems().add(new SeparatorMenuItem());

        MenuItem expandAllItem = new MenuItem(Messages.getString("RowHeader.MENU_ITEM_EXPAND_ALL"));
        expandAllItem.setOnAction(evt -> ganttChart.expandRows());
        contextMenu.getItems().add(expandAllItem);

        MenuItem expandOnce = new MenuItem(Messages.getString("RowHeader.MENU_ITEM_EXPAND_ONCE"));
        expandOnce.setOnAction(evt -> ganttChart.expandRowsByOneLevel());
        contextMenu.getItems().add(expandOnce);

        MenuItem collapseAllItem = new MenuItem(Messages.getString("RowHeader.MENU_ITEM_COLLAPSE_ALL"));
        collapseAllItem.setOnAction(evt -> ganttChart.collapseRows());
        contextMenu.getItems().add(collapseAllItem);

        MenuItem collapseOnceItem = new MenuItem(Messages.getString("RowHeader.MENU_ITEM_COLLAPSE_ONCE"));
        collapseOnceItem.setOnAction(evt -> ganttChart.collapseRowsByOneLevel());
        contextMenu.getItems().add(collapseOnceItem);

        ganttChart.getTreeTable().getColumns().addListener(new WeakInvalidationListener(columnListener));

        updateColumnsMenu();

        // build the corner region button for showing the popup menu
        final StackPane image = new StackPane();
        image.setSnapToPixel(false);
        image.getStyleClass().setAll("show-hide-column-image");
        cornerRegion = new StackPane() {
            @Override
            protected void layoutChildren() {
                double imageWidth = image.snappedLeftInset() + image.snappedRightInset();
                double imageHeight = image.snappedTopInset() + image.snappedBottomInset();

                image.resize(imageWidth, imageHeight);
                positionInArea(image, 0, 0, getWidth(), getHeight() - 3, 0, HPos.CENTER, VPos.CENTER);
            }
        };

        /*
         * We are using the tableMenuButtonVisible property of the gantt chart
         * control and not the property with the same name from the tree table,
         * because we only want OUR control to be visible, not the one that
         * ships by default with JavaFX. That one always needs to be hidden as
         * we have styled away the vertical scrollbars.
         */
        cornerRegion.getStyleClass().setAll("show-hide-columns-button");
        cornerRegion.getChildren().addAll(image);
        cornerRegion.setVisible(ganttChart.tableMenuButtonVisibleProperty().get());
        ganttChart.tableMenuButtonVisibleProperty().addListener(new WeakInvalidationListener(tableMenuButtonListener));

        setContextMenu(contextMenu);

        cornerRegion.setPrefSize(20, 20);

        setGraphic(cornerRegion);
    }

    private void updateColumnsMenu() {
        List<MenuItem> items = new ArrayList<>();
        ganttChart.getTreeTable().getColumns().stream().filter(column -> !(column instanceof RowHeaderColumn)).forEach(column -> {
            CheckMenuItem item = new CheckMenuItem();
            item.textProperty().bind(column.textProperty());
            Bindings.bindBidirectional(item.selectedProperty(), column.visibleProperty());
            items.add(item);
        });

        columns.getItems().setAll(items);
    }

    /**
     * Returns the tree table view for which the header is used.
     *
     * @return the tree table view
     */
    public final TreeTableView<R> getTreeTable() {
        return treeTable;
    }

    /**
     * Returns the Gantt chart for which the header is used.
     *
     * @return the Gantt chart
     */
    public final GanttChart<R> getGanttChart() {
        return ganttChart;
    }

    class RowHeaderCell extends TreeTableCell<R, R> {
        private static final String DEFAULT_STYLE_CLASS = "row-header-cell";

        private R row;

        private double startY;

        private boolean empty;

        private final InvalidationListener typeListener = observable -> {
            updateIndex(getIndex());
            updateItem(row, empty);
        };

        private final InvalidationListener nodeFactoryListener = observable -> {
            updateIndex(getIndex());
            updateItem(row, empty);
        };

        public RowHeaderCell() {

            ganttChart.rowHeaderTypeProperty().addListener(new WeakInvalidationListener(typeListener));
            ganttChart.rowHeaderNodeFactoryProperty().addListener(new WeakInvalidationListener(nodeFactoryListener));

            getStyleClass().add(DEFAULT_STYLE_CLASS);

            addEventHandler(MouseEvent.MOUSE_MOVED, evt -> {
                if (evt.getY() > getHeight() - 4 && getRow() != null) {
                    if (ganttChart.getFixedCellSize() == -1) {
                        setCursor(Cursor.V_RESIZE);
                    }
                } else {
                    setCursor(Cursor.DEFAULT);
                }
            });

            addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
                if (evt.getY() > getHeight() - 4 && ganttChart.getFixedCellSize() == -1) {
                    startY = evt.getY();
                } else {
                    startY = -1;
                }
            });

            addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
                if (startY != -1) {
                    double delta = evt.getY() - startY;
                    startY = evt.getY();

                    if (row != null) {
                        row.setHeight(Math.min(Math.max(row.getHeight() + delta, row.getMinHeight()), row.getMaxHeight()));
                    }
                }
            });

            addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {
                if (evt.isShiftDown() || evt.isShortcutDown()) {
                    if (row != null) {
                        double rowHeight = row.getHeight();

                        ganttChart.getGraphics().getRows().forEach(r -> {
                            if (rowHeight < r.getMinHeight()) {
                                r.setHeight(r.getMinHeight());
                            } else if (rowHeight > r.getMaxHeight()) {
                                r.setHeight(r.getMaxHeight());
                            } else {
                                r.setHeight(rowHeight);
                            }
                        });
                    }
                }
            });

            setContentDisplay(ContentDisplay.CENTER);
        }

        public Row<?, ?, ?> getRow() {
            return row;
        }

        @Override
        public void updateIndex(int i) {
            super.updateIndex(i);

            switch (ganttChart.getRowHeaderType()) {
                case ROW_NUMBER:
                    if (getRow() != null) {
                        setContentDisplay(TEXT_ONLY);
                        setText(Integer.toString(i + 1));
                        setAlignment(CENTER_RIGHT);
                    } else {
                        setText(null);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void updateItem(R row, boolean empty) {
            super.updateItem(row, empty);

            this.empty = empty;

            if (this.row != null) {
                Bindings.unbindBidirectional(this.row.heightProperty(), prefHeightProperty());
            }

            this.row = getTreeTableRow().getItem();

            if (this.row != null) {

                switch (ganttChart.getRowHeaderType()) {
                    // TODO: we keep creating new nodes here ... better to re-use the same one (use same concept as Graphics?)
                    case GRAPHIC_NODE:
                        Callback<R, Node> rowHeaderNodeFactory = ganttChart.getRowHeaderNodeFactory();
                        Node headerNode = rowHeaderNodeFactory.call(this.row);
                        setGraphic(headerNode);
                        setContentDisplay(GRAPHIC_ONLY);
                        break;
                    case LEVEL_NUMBER:
                        GanttChartTreeItem<R> item = (GanttChartTreeItem<R>) getTreeTableRow().getTreeItem();
                        setText(item.getLevelNumber());
                        setContentDisplay(TEXT_ONLY);
                        setAlignment(CENTER_LEFT);
                        break;
                    default:
                        break;
                }

                setPrefHeight(this.row.getHeight());

                Bindings.bindBidirectional(this.row.heightProperty(), prefHeightProperty());
            } else {
                /*
                 * Empty rows need to be cleaned up.
                 */
                switch (ganttChart.getRowHeaderType()) {
                    case GRAPHIC_NODE:
                        setGraphic(null);
                        setContentDisplay(GRAPHIC_ONLY);
                        break;
                    default:
                        setText(null);
                        setContentDisplay(TEXT_ONLY);
                        break;
                }

                setPrefHeight(Row.DEFAULT_ROW_HEIGHT);
            }
        }
    }
}
