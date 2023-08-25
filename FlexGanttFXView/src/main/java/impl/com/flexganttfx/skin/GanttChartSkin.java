/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.util.RowHeaderColumn;
import com.flexganttfx.view.util.VirtualFlowUtil;
import impl.com.flexganttfx.skin.treetable.GanttChartTreeItem;
import impl.com.flexganttfx.skin.treetable.GanttChartTreeTableRow;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.MasterDetailPane;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class GanttChartSkin<R extends Row<?, ?, ?>> extends GanttChartBaseSkin<R, GanttChart<R>>
{

    private final TreeTableView<R> treeTable;
    private final ListView<R> listView;
    private final ScrollBar treeTableScrollBar;
    private final MasterDetailPane treeTableMasterDetailPane;
    private final MasterDetailPane graphicsMasterDetailPane;
    private final Node detailNode;
    private final RowHeaderColumn<R> rowHeader;
    private final HiddenSidesPane leftHandSideHiddenSidesPane;
    private final VBox leftHandSideBox;

    // flow position locking
    AtomicReference<Instant> lastExpandEventTime = new AtomicReference<>(Instant.now());
    VirtualFlow<?> flowLeft = null;
    Duration sameExpandCollapseSkipRetentionTime = Duration.ofMillis(300);

    public GanttChartSkin(GanttChart<R> ganttChart)
    {
        super(ganttChart);

        listView = ganttChart.getGraphics().getListView();
        treeTable = ganttChart.getTreeTable();
        treeTable.getStylesheets().add(GanttChart.class.getResource("gantt.css").toExternalForm());
        treeTable.fixedCellSizeProperty().bind(ganttChart.fixedCellSizeProperty());
        treeTable.setTableMenuButtonVisible(false);
        treeTable.setRowFactory(view -> new GanttChartTreeTableRow<>());
        treeTable.showRootProperty().addListener(it -> updateListRows());

        ganttChart.rootProperty().addListener(observable -> updateRoot());

        rowHeader = ganttChart.getRowHeaderColumn();

        ListViewGraphics<R> graphics = ganttChart.getGraphics();

        treeTableMasterDetailPane = ganttChart.getTreeTableMasterDetailPane();
        graphicsMasterDetailPane = ganttChart.getGraphicsMasterDetailPane();
        treeTableScrollBar = ganttChart.getTreeTableScrollBar();
        detailNode = ganttChart.getDetail();

        treeTable.setMinWidth(0);
        graphics.setMinSize(0, 0);
        treeTableScrollBar.setMinWidth(0);

        treeTable.skinProperty().addListener(
                (oldSkin, skin, newSkin) -> {
                    bindHorizontalReplacementWithBuiltInHorizontalTreeTableScrollBar();
                    getSkinnable().requestLayout();
                });

        leftHandSideHiddenSidesPane = new HiddenSidesPane();
        leftHandSideBox = new VBox();

        SplitPane.setResizableWithParent(leftHandSideHiddenSidesPane, false);
        SplitPane.setResizableWithParent(detailNode, false);

        applyLayout();

        InvalidationListener layoutListener = evt -> applyLayout();
        ganttChart.displayModeProperty().addListener(layoutListener);
        ganttChart.scrollBarTypeProperty().addListener(layoutListener);
        ganttChart.autoHideScrollBarProperty().addListener(layoutListener);

        treeTable.getColumns().addListener((Observable e) -> updateColumns());

        updateRoot();
        updateColumns();

        VirtualFlowUtil.bindVirtualFlows(treeTable, listView);
    }

    public HiddenSidesPane getLeftHandSideHiddenSidesPane()
    {
        return leftHandSideHiddenSidesPane;
    }

    private void applyLayout()
    {
        getChildren().clear();

        double dividerPosition = treeTableMasterDetailPane.getDividerPosition();

        applyLeftHandSideLayout();

        switch (getSkinnable().getDisplayMode())
        {
            case STANDARD:
                applyLayoutStandard();
                break;
            case GRAPHICS_ONLY:
                applyLayoutGraphicsOnly();
                break;
            case TABLE_ONLY:
                applyLayoutTableOnly();
                break;
        }

        treeTableMasterDetailPane.setDividerPosition(dividerPosition);
    }

    private void applyLeftHandSideLayout()
    {
        /*
         * The extra setting of labels is needed so that the hidden sides pane will react properly.
         */
        leftHandSideHiddenSidesPane.setContent(new Label());
        leftHandSideHiddenSidesPane.setBottom(new Label());

        if (getSkinnable().isAutoHideScrollBar())
        {
            leftHandSideHiddenSidesPane.setContent(treeTable);
            leftHandSideHiddenSidesPane.setBottom(treeTableScrollBar);
        }
        else
        {
            treeTable.setManaged(true);
            treeTableScrollBar.setManaged(true);
            treeTableScrollBar.setVisible(true);
            VBox.setVgrow(treeTable, Priority.ALWAYS);
            leftHandSideBox.getChildren().setAll(treeTable, treeTableScrollBar);
        }
    }

    private void applyLayoutTableOnly()
    {
        graphicsMasterDetailPane.setDetailNode(new Label("Placeholder"));
        graphicsMasterDetailPane.setMasterNode(new Label("Placeholder"));

        if (getSkinnable().isAutoHideScrollBar())
        {
            graphicsMasterDetailPane.setMasterNode(leftHandSideHiddenSidesPane);
        }
        else
        {
            graphicsMasterDetailPane.setMasterNode(leftHandSideBox);
        }

        graphicsMasterDetailPane.setDetailNode(detailNode);

        getChildren().add(graphicsMasterDetailPane);
    }

    private void applyLayoutGraphicsOnly()
    {
        graphicsMasterDetailPane.setDetailNode(new Label("Placeholder"));
        graphicsMasterDetailPane.setMasterNode(new Label("Placeholder"));

        if (getSkinnable().isAutoHideScrollBar())
        {
            graphicsMasterDetailPane.setMasterNode(getRightHandSideHiddenSidesPane());
        }
        else
        { // NONE or HORIZON
            graphicsMasterDetailPane.setMasterNode(getRightHandSideBox());
        }
        graphicsMasterDetailPane.setDetailNode(detailNode);

        getChildren().add(graphicsMasterDetailPane);
    }

    private void applyLayoutStandard()
    {
        /*
         * The extra setting of placeholder labels is needed so that the master
         * detail pane will react properly.
         */
        treeTableMasterDetailPane.setDetailNode(new Label("Placeholder"));
        treeTableMasterDetailPane.setMasterNode(new Label("Placeholder"));

        if (getSkinnable().isAutoHideScrollBar())
        {
            treeTableMasterDetailPane.setDetailNode(leftHandSideHiddenSidesPane);
            treeTableMasterDetailPane.setMasterNode(getRightHandSideHiddenSidesPane());
        }
        else
        { // NONE or HORIZON
            treeTableMasterDetailPane.setDetailNode(leftHandSideBox);
            treeTableMasterDetailPane.setMasterNode(getRightHandSideBox());
        }

        graphicsMasterDetailPane.setDetailNode(new Label("Placeholder"));
        graphicsMasterDetailPane.setMasterNode(new Label("Placeholder"));

        graphicsMasterDetailPane.setMasterNode(treeTableMasterDetailPane);
        graphicsMasterDetailPane.setDetailNode(detailNode);

        getChildren().add(graphicsMasterDetailPane);
    }

    private void updateRoot()
    {
        R root = getSkinnable().getRoot();
        if (root != null)
        {
            GanttChartTreeItem<R> treeItem = new GanttChartTreeItem<>(root);
            treeItem.filterProperty().bind(getSkinnable().rowFilterProperty());

            treeTable.setRoot(treeItem);
            updateListRows();

            treeItem.addEventHandler(TreeItem.treeNotificationEvent(), this::updateListRows);
        }
    }

    private void updateListRows(TreeModificationEvent<Object> evt)
    {
        LoggingDomain.EDITING.fine("updating list rows after tree modification event: " + evt);
        preUpdateListRows(evt);
        updateListRows();
    }

    private void preUpdateListRows(TreeModificationEvent<Object> evt)
    {
        if (VirtualFlowUtil.isMode(VirtualFlowUtil.MODE.POS_LOCKING) && TreeItem.branchExpandedEvent().equals(evt.getEventType()) || TreeItem.branchCollapsedEvent().equals(evt.getEventType()))
        {
            LoggingDomain.NAVIGATION.fine("MODE.POS_LOCKING, tree modification event: " + evt.getEventType());
            Instant now = Instant.now();
            Duration duratSinceLastEvent = Duration.between(lastExpandEventTime.get(), now).abs();
            lastExpandEventTime.set(now);

            if (duratSinceLastEvent.compareTo(sameExpandCollapseSkipRetentionTime) <= 0)
            {
                // skip events because of expand all
                LoggingDomain.NAVIGATION.fine("MODE.POS_LOCKING, skipping VirtualFlowUtil.storeCurrentPosition. Duration since last event: " + duratSinceLastEvent);
                return;
            }
            flowLeft = flowLeft != null ? flowLeft : (VirtualFlow) treeTable.lookup("VirtualFlow");
            VirtualFlowUtil.storeCurrentPosition(flowLeft);
        }
    }

    private void updateListRows()
    {
        doUpdateListRows();
    }

    private void doUpdateListRows()
    {
        List<R> list = new ArrayList<>();

        if (treeTable.getRoot() != null)
        {
            if (treeTable.isShowRoot())
            {
                doUpdateListRows(treeTable.getRoot(), list);
            }
            else
            {
                for (TreeItem<R> child : treeTable.getRoot().getChildren())
                {
                    doUpdateListRows(child, list);
                }
            }
        }

        getSkinnable().getGraphics().getRows().setAll(list);
    }

    private void doUpdateListRows(TreeItem<R> item, List<R> list)
    {
        list.add(item.getValue());

        if (!item.isLeaf() && item.isExpanded())
        {
            for (TreeItem<R> child : item.getChildren())
            {
                doUpdateListRows(child, list);
            }
        }
    }

    private void updateColumns()
    {
        List<TreeTableColumn<R, ?>> columns = treeTable.getColumns();
        if (!columns.contains(rowHeader))
        {
            if (columns.size() == 0)
            {
                treeTable.getColumns().add(rowHeader);
            }
            else
            {
                treeTable.getColumns().add(0, rowHeader);
            }
        }
    }

    private void bindHorizontalReplacementWithBuiltInHorizontalTreeTableScrollBar()
    {
        ScrollBar builtInScrollBar = findScrollBar(treeTable, Orientation.HORIZONTAL);

        if (builtInScrollBar != null && treeTableScrollBar != null)
        {

            Bindings.bindBidirectional(builtInScrollBar.valueProperty(), treeTableScrollBar.valueProperty());
            Bindings.bindBidirectional(builtInScrollBar.visibleAmountProperty(), treeTableScrollBar.visibleAmountProperty());
            Bindings.bindBidirectional(builtInScrollBar.blockIncrementProperty(), treeTableScrollBar.blockIncrementProperty());
            Bindings.bindBidirectional(builtInScrollBar.unitIncrementProperty(), treeTableScrollBar.unitIncrementProperty());
            Bindings.bindBidirectional(builtInScrollBar.minProperty(), treeTableScrollBar.minProperty());
            Bindings.bindBidirectional(builtInScrollBar.maxProperty(), treeTableScrollBar.maxProperty());

            if (!builtInScrollBar.isVisible())
            {
                disableTreeTableScrollBar();
            }

            builtInScrollBar.visibleProperty().addListener((value, oldVisible, newVisible) -> {
                if (newVisible.equals(Boolean.FALSE))
                {
                    disableTreeTableScrollBar();
                }
            });
        }
    }

    private void disableTreeTableScrollBar()
    {
        treeTableScrollBar.setMin(0);
        treeTableScrollBar.setMax(0);
        treeTableScrollBar.setValue(0);
    }

    private ScrollBar findScrollBar(Parent parent, Orientation orientation)
    {
        for (Node node : parent.getChildrenUnmodifiable())
        {
            if (node instanceof ScrollBar)
            {
                ScrollBar b = (ScrollBar) node;
                if (b.getOrientation().equals(orientation))
                {
                    return b;
                }
            }

            /*
             * Make sure that we do not find scrollbars added to header
             * controls. It is really hard to exactly determine where we should
             * look for the scrollbar. Other controls might as well use a
             * virtual flow with their own scrollbars. But the most likely place
             * for them would be in the table header. See issue FLEXFX-67 for
             * more information.
             */
            if (node instanceof Parent && !(node instanceof TableHeaderRow))
            {
                ScrollBar b = findScrollBar((Parent) node, orientation);
                if (b != null)
                {
                    return b;
                }
            }
        }

        return null;
    }
}
