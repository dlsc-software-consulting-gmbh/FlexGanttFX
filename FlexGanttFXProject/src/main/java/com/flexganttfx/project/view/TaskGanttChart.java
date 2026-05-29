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
package com.flexganttfx.project.view;

import java.time.LocalDate;

import javafx.application.Platform;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import com.flexganttfx.project.model.business.Task;
import com.flexganttfx.project.model.view.TaskRow;
import com.flexganttfx.view.GanttChart;

public class TaskGanttChart extends GanttChart<TaskRow> {

	public static final double ROW_HEIGHT = 30;

	/*
	 * This is the row that we will use for creating new rows. It does not have
	 * a task associated with it. It is the only row using the empty
	 * constructor.
	 */
	private final TaskRow createRow = new TaskRow();

	/*
	 * This is the column where editing will happen.
	 */
	private final TreeTableColumn<TaskRow, String> titleColumn;

	public TaskGanttChart() {
		super(new TaskRow());

		getLayers().add(Layers.taskLayer);

		setFixedCellSize(ROW_HEIGHT);

		TreeTableView<TaskRow> treeTable = getTreeTable();
		treeTable.setShowRoot(false);

		getRoot().getChildren().add(createRow);

		titleColumn = new TreeTableColumn<>("Title");
		titleColumn.setEditable(true);
		titleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>(
				"title"));
		titleColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		titleColumn.setPrefWidth(100);

		/*
		 * React when the user commits the edit and create a new row.
		 */
		titleColumn.setOnEditCommit(evt -> updateTitle(evt));

		TreeTableColumn<TaskRow, LocalDate> startDateColumn = new TreeTableColumn<>(
				"Start");
		startDateColumn.setEditable(true);
		startDateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>(
				"startDate"));
		startDateColumn
				.setCellFactory((TreeTableColumn<TaskRow, LocalDate> param) -> new DateTreeTableCell());
		startDateColumn.setPrefWidth(110);
		startDateColumn.setOnEditCommit(evt -> updateStartDate(evt));

		TreeTableColumn<TaskRow, LocalDate> endDateColumn = new TreeTableColumn<>(
				"End");
		endDateColumn.setEditable(true);
		endDateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>(
				"endDate"));
		endDateColumn
				.setCellFactory((TreeTableColumn<TaskRow, LocalDate> param) -> new DateTreeTableCell());
		endDateColumn.setPrefWidth(110);
		endDateColumn.setOnEditCommit(evt -> updateEndDate(evt));

		treeTable.getColumns().clear();
		treeTable.getColumns().add(titleColumn);
		treeTable.getColumns().add(startDateColumn);
		treeTable.getColumns().add(endDateColumn);
		treeTable.setEditable(true);
		treeTable.setTreeColumn(titleColumn);

		treeTable.edit(0, titleColumn);
	}

	class DateTreeTableCell extends TreeTableCell<TaskRow, LocalDate> {

		private final DatePicker datePicker;

		public DateTreeTableCell() {
			datePicker = new DatePicker();
			datePicker.setOnAction(evt -> commitEdit(datePicker.getValue()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javafx.scene.control.TreeTableCell#startEdit()
		 */
		@Override
		public void startEdit() {
			setGraphic(datePicker);
			LocalDate item = getItem();
			if (item != null) {
				datePicker.setValue(item);
			}
			super.startEdit();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javafx.scene.control.TreeTableCell#commitEdit(java.lang.Object)
		 */
		@Override
		public void commitEdit(LocalDate newValue) {
			super.commitEdit(newValue);

			setGraphic(null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
		 */
		@Override
		protected void updateItem(LocalDate item, boolean empty) {
			super.updateItem(item, empty);
		}
	}

	/*
	 * This gets called when the user commits the edit in the "create row". We
	 * are then creating a new row and inserting it just in front of the
	 * "create row".
	 */
	private void updateTitle(CellEditEvent<TaskRow, String> evt) {
		TaskRow row = evt.getRowValue().getValue();
		if (row == createRow) {
			Task newTask = new Task(evt.getNewValue());
			TaskRow newRow = new TaskRow(newTask);
			int createRowIndex = getRoot().getChildren().indexOf(createRow);
			getRoot().getChildren().add(createRowIndex, newRow);
			Platform.runLater(() -> getTreeTable().edit(createRowIndex + 1,
					titleColumn));
		} else {
			row.setTitle(evt.getNewValue());
		}
	}

	private void updateStartDate(CellEditEvent<TaskRow, LocalDate> evt) {
		TaskRow row = evt.getRowValue().getValue();
		row.setStartDate(evt.getNewValue());
	}

	private void updateEndDate(CellEditEvent<TaskRow, LocalDate> evt) {
		TaskRow row = evt.getRowValue().getValue();
		row.setEndDate(evt.getNewValue());
	}
}
