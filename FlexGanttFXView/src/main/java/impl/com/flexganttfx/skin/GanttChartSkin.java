/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.util.RowHeaderColumn;
import impl.com.flexganttfx.skin.treetable.GanttChartTreeItem;
import impl.com.flexganttfx.skin.treetable.GanttChartTreeTableRow;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.skin.TableHeaderRow;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.MasterDetailPane;

import java.util.ArrayList;
import java.util.List;

public class GanttChartSkin<R extends Row<?, ?, ?>> extends
		GanttChartBaseSkin<R, GanttChart<R>> {

	private final TreeTableView<R> treeTable;
	private final ScrollBar treeTableScrollBar;
	private final MasterDetailPane treeTableMasterDetailPane;
	private final MasterDetailPane graphicsMasterDetailPane;
	private final Node detailNode;
	private final RowHeaderColumn<R> rowHeader;
	private final HiddenSidesPane leftHandSide;
	private final HiddenSidesPane rightHandSide;

	public GanttChartSkin(GanttChart<R> ganttChart) {
		super(ganttChart);

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

		treeTable
				.skinProperty()
				.addListener(
						(oldSkin, skin, newSkin) -> {
							bindHorizontalReplacementWithBuiltInHorizontalTreeTableScrollBar();

							getSkinnable().requestLayout();
						});


		rightHandSide = super.getHiddenSidesPane();

		leftHandSide = new HiddenSidesPane();
		leftHandSide.setContent(treeTable);
		leftHandSide.setBottom(treeTableScrollBar);

		SplitPane.setResizableWithParent(leftHandSide, false);
		SplitPane.setResizableWithParent(detailNode, false);

		applyLayout();

		getSkinnable().displayModeProperty().addListener(evt -> applyLayout());

		treeTable.getColumns().addListener((Observable e) -> updateColumns());

		updateRoot();
		updateColumns();

		bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar();

		graphics.getListView()
				.skinProperty()
				.addListener(
						(observable, oldValue, newValue) -> bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar());
	}

	public HiddenSidesPane getRightHandSide() {
		return rightHandSide;
	}

	public HiddenSidesPane getLeftHandSide() {
		return leftHandSide;
	}

	private void applyLayout() {
		getChildren().clear();

		switch (getSkinnable().getDisplayMode()) {
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
	}

	private void applyLayoutTableOnly() {
		graphicsMasterDetailPane.setDetailNode(new Label("Placeholder"));
		graphicsMasterDetailPane.setMasterNode(new Label("Placeholder"));

		graphicsMasterDetailPane.setMasterNode(leftHandSide);
		graphicsMasterDetailPane.setDetailNode(detailNode);

		getChildren().add(graphicsMasterDetailPane);
	}

	private void applyLayoutGraphicsOnly() {
		graphicsMasterDetailPane.setDetailNode(new Label("Placeholder"));
		graphicsMasterDetailPane.setMasterNode(new Label("Placeholder"));

		graphicsMasterDetailPane.setMasterNode(rightHandSide);
		graphicsMasterDetailPane.setDetailNode(detailNode);

		getChildren().add(graphicsMasterDetailPane);
	}

	private void applyLayoutStandard() {
		/*
		 * The extra setting of placeholder labels is needed so that the master
		 * detail pane will react properly.
		 */
		treeTableMasterDetailPane.setDetailNode(new Label("Placeholder"));
		treeTableMasterDetailPane.setMasterNode(new Label("Placeholder"));

		treeTableMasterDetailPane.setDetailNode(leftHandSide);
		treeTableMasterDetailPane.setMasterNode(rightHandSide);

		graphicsMasterDetailPane.setDetailNode(new Label("Placeholder"));
		graphicsMasterDetailPane.setMasterNode(new Label("Placeholder"));

		graphicsMasterDetailPane.setMasterNode(treeTableMasterDetailPane);
		graphicsMasterDetailPane.setDetailNode(detailNode);

		getChildren().add(graphicsMasterDetailPane);
	}

	private void updateRoot() {
		R root = getSkinnable().getRoot();
		if (root != null) {
			GanttChartTreeItem<R> treeItem = new GanttChartTreeItem<>(root);
			treeItem.filterProperty().bind(getSkinnable().rowFilterProperty());

			treeTable.setRoot(treeItem);
			updateListRows();

			treeItem.addEventHandler(TreeItem.treeNotificationEvent(), this::updateListRows);
		}
	}

	private void updateListRows(TreeModificationEvent<Object> evt) {
		LoggingDomain.EDITING.fine("updating list rows after tree modification event: " + evt);
		updateListRows();
	}

	private void updateListRows() {
		doUpdateListRows();
	}

	private void doUpdateListRows() {
		List<R> list = new ArrayList<>();

		if (treeTable.getRoot() != null) {
			if (treeTable.isShowRoot()) {
				doUpdateListRows(treeTable.getRoot(), list);
			} else {
				for (TreeItem<R> child : treeTable.getRoot().getChildren()) {
					doUpdateListRows(child, list);
				}
			}
		}

		getSkinnable().getGraphics().getRows().setAll(list);
	}

	private void doUpdateListRows(TreeItem<R> item, List<R> list) {
		list.add(item.getValue());

		if (!item.isLeaf() && item.isExpanded()) {
			for (TreeItem<R> child : item.getChildren()) {
				doUpdateListRows(child, list);
			}
		}
	}

	private void updateColumns() {
		List<TreeTableColumn<R, ?>> columns = treeTable.getColumns();
		if (!columns.contains(rowHeader)) {
			if (columns.size() == 0) {
				treeTable.getColumns().add(rowHeader);
			} else {
				treeTable.getColumns().add(0, rowHeader);
			}
		}
	}

	private void bindHorizontalReplacementWithBuiltInHorizontalTreeTableScrollBar() {
		final ScrollBar builtInScrollBar = findScrollBar(treeTable,
				Orientation.HORIZONTAL);

		if (builtInScrollBar != null && treeTableScrollBar != null) {
			Bindings.bindBidirectional(builtInScrollBar.valueProperty(),
					treeTableScrollBar.valueProperty());

			Bindings.bindBidirectional(
					builtInScrollBar.visibleAmountProperty(),
					treeTableScrollBar.visibleAmountProperty());

			Bindings.bindBidirectional(
					builtInScrollBar.blockIncrementProperty(),
					treeTableScrollBar.blockIncrementProperty());

			Bindings.bindBidirectional(
					builtInScrollBar.unitIncrementProperty(),
					treeTableScrollBar.unitIncrementProperty());

			Bindings.bindBidirectional(builtInScrollBar.minProperty(),
					treeTableScrollBar.minProperty());

			Bindings.bindBidirectional(builtInScrollBar.maxProperty(),
					treeTableScrollBar.maxProperty());

			if (!builtInScrollBar.isVisible()) {
				disableTreeTableScrollBar();
			}

			builtInScrollBar.visibleProperty().addListener(
					(value, oldVisible, newVisible) -> {
						if (newVisible.equals(Boolean.FALSE)) {
							disableTreeTableScrollBar();
						}
					});
		}
	}

	private void disableTreeTableScrollBar() {
		treeTableScrollBar.setMin(0);
		treeTableScrollBar.setMax(0);
		treeTableScrollBar.setValue(0);
	}

	private ScrollBar findScrollBar(Parent parent, Orientation orientation) {
		for (Node node : parent.getChildrenUnmodifiable()) {
			if (node instanceof ScrollBar) {
				ScrollBar b = (ScrollBar) node;
				if (b.getOrientation().equals(orientation)) {
					return b;
				}
			}

			/*
			 * Make sure that we do not find scrollbars added to header
			 * controls. It is really hard to exactly determine where we should
			 * look for the scrollbar. Other controls might as well use a
			 * virtual flow with their own scrollbars. But the most likely place
			 * for them would be in the table header. See issue FLEXFX-67 more
			 * more information.
			 */
			if (node instanceof Parent && !(node instanceof TableHeaderRow)) {
				ScrollBar b = findScrollBar((Parent) node, orientation);
				if (b != null) {
					return b;
				}
			}
		}

		return null;
	}

	protected void bindVerticalListViewScrollBarWithVerticalTreeTableScrollBar() {

		ScrollBar treeTableScrollBar = findScrollBar(getSkinnable()
				.getTreeTable(), Orientation.VERTICAL);

		ScrollBar graphicsViewScrollBar = findScrollBar(getSkinnable()
				.getGraphics(), Orientation.VERTICAL);

		if (treeTableScrollBar != null && graphicsViewScrollBar != null) {
			Bindings.bindBidirectional(treeTableScrollBar.valueProperty(),
					graphicsViewScrollBar.valueProperty());

			Bindings.bindBidirectional(
					treeTableScrollBar.visibleAmountProperty(),
					graphicsViewScrollBar.visibleAmountProperty());

			Bindings.bindBidirectional(
					treeTableScrollBar.blockIncrementProperty(),
					graphicsViewScrollBar.blockIncrementProperty());

			Bindings.bindBidirectional(
					treeTableScrollBar.unitIncrementProperty(),
					graphicsViewScrollBar.unitIncrementProperty());

			Bindings.bindBidirectional(treeTableScrollBar.minProperty(),
					graphicsViewScrollBar.minProperty());

			Bindings.bindBidirectional(treeTableScrollBar.maxProperty(),
					graphicsViewScrollBar.maxProperty());
		}
	}
}
