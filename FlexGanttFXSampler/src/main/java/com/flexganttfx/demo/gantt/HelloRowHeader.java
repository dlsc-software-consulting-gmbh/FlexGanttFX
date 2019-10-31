/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChart.RowHeaderType;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class HelloRowHeader extends FlexGanttFXSample {
	private GanttChart<MyRow> gantt;

	@Override
	public String getSampleName() {
		return "Row Header";
	}

	@Override
	protected GanttChart<?> createGanttChart() throws Exception {
		gantt = new GanttChart<>();
		gantt.getStylesheets().add(
				HelloRowHeader.class.getResource("row-header.css")
						.toExternalForm());

		List<MyRow> rows = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			MyRow row = new MyRow("Row " + i);
			row.setExpanded(true);
			rows.add(row);
			for (int j = 0; j < 10; j++) {
				MyRow subRow = new MyRow("Child row " + j);
				subRow.setExpanded(true);

				row.getChildren().add(subRow);

				for (int k = 0; k < 10; k++) {
					MyRow subSubRow = new MyRow("Child child row " + k);
					subRow.getChildren().add(subSubRow);
				}
			}
		}

		MyRow root = new MyRow("Root Row");
		root.setExpanded(true);
		root.getChildren().addAll(rows);
		gantt.setRoot(root);

		return gantt;
	}

	@Override
	public Node getControlPanel() {
		VBox controlPane = new VBox();
		controlPane.setSpacing(10);

		gantt.setRowHeaderNodeFactory(new ColorCallback());
		gantt.rowHeaderTypeProperty().addListener(evt -> {
			switch (gantt.getRowHeaderType()) {
			case ROW_NUMBER:
				gantt.getRowHeader().setPrefWidth(30);
				break;
			case LEVEL_NUMBER:
				gantt.getRowHeader().setPrefWidth(50);
				break;
			case GRAPHIC_NODE:
				break;
			}
		});

		// content type box
		ComboBox<RowHeaderType> contentTypeBox = new ComboBox<>();
		contentTypeBox.getItems().addAll(RowHeaderType.values());
		contentTypeBox.setValue(gantt.getRowHeaderType());
		gantt.rowHeaderTypeProperty().bind(
				contentTypeBox.valueProperty());
		controlPane.getChildren().add(contentTypeBox);

		// radio button group
		ToggleGroup group = new ToggleGroup();
		RadioButton colorCallback = new RadioButton("Color");
		colorCallback.setSelected(true);
		colorCallback.setToggleGroup(group);
		colorCallback.setOnAction(evt -> {
			gantt.setRowHeaderNodeFactory(new ColorCallback());
			gantt.getRowHeader().setPrefWidth(24);
		});
		colorCallback.disableProperty().bind(
				Bindings.notEqual(RowHeaderType.GRAPHIC_NODE,
						gantt.rowHeaderTypeProperty()));
		controlPane.getChildren().add(colorCallback);

		RadioButton statusCallback = new RadioButton("Status");
		statusCallback.setToggleGroup(group);
		statusCallback.setOnAction(evt -> {
			gantt.setRowHeaderNodeFactory(new StatusCallback());
			gantt.getRowHeader().setPrefWidth(30);
		});
		statusCallback.disableProperty().bind(
				Bindings.notEqual(RowHeaderType.GRAPHIC_NODE,
						gantt.rowHeaderTypeProperty()));
		controlPane.getChildren().add(statusCallback);

		RadioButton controlCallback = new RadioButton("Control");
		controlCallback.setToggleGroup(group);
		controlCallback.setOnAction(evt -> {
			gantt.setRowHeaderNodeFactory(new ControlCallback());
			gantt.getRowHeader().setPrefWidth(40);
		});
		controlCallback.disableProperty().bind(
				Bindings.notEqual(RowHeaderType.GRAPHIC_NODE,
						gantt.rowHeaderTypeProperty()));
		controlPane.getChildren().add(controlCallback);

		return controlPane;
	}

	@Override
	public String getSampleDescription() {
		return "The first column of the tree table is the row header. "
				+ "The header can be used to display lines numbers, indentation levels, "
				+ "or arbitrary nodes.";
	}

	enum Status {
		OK, WARNING, ERROR
    }

	class MyRow extends Row<MyRow, MyRow, Activity> {
		private Paint paint;
		private Status status = Status.OK;

		public MyRow(String name) {
			super(name);

			paint = Color.color(Math.random(), Math.random(), Math.random());

			double rnd = Math.random();

			if (rnd < .2) {
				status = Status.ERROR;
			} else if (rnd < .3) {
				status = Status.WARNING;
			}
		}

		public Paint getPaint() {
			return paint;
		}

		public Status getStatus() {
			return status;
		}
	}

	class StatusCallback implements Callback<MyRow, Node> {

		@Override
		public Node call(MyRow param) {
			Label label = new Label();
			label.setMaxWidth(Double.MAX_VALUE);
			label.setMaxHeight(Double.MAX_VALUE);
			label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			label.setAlignment(Pos.CENTER);

			switch (param.getStatus()) {
			case ERROR:
				label.getStyleClass().add("error");
				break;
			case WARNING:
				label.getStyleClass().add("warning");
				break;
			case OK:
				label.getStyleClass().clear();
				break;
			}

			return label;
		}
	}

	class ColorCallback implements Callback<MyRow, Node> {

		@Override
		public Node call(MyRow param) {
			Region region = new Region();
			region.setBackground(new Background(new BackgroundFill(param
					.getPaint(), new CornerRadii(4), new Insets(2))));
			return region;
		}

	}

	class ControlCallback implements Callback<MyRow, Node> {

		@Override
		public Node call(MyRow param) {
			CheckBox box = new CheckBox();
			box.setAllowIndeterminate(true);
			box.setMaxWidth(Double.MAX_VALUE);
			box.setAlignment(Pos.CENTER);
			box.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

			switch (param.getStatus()) {
			case ERROR:
				box.setSelected(true);
				break;
			case WARNING:
				box.setIndeterminate(true);
				break;
			case OK:
				break;
			}
			return box;
		}

	}

	@Override
	public String getJavaDocURL() {
		return getJavaDocBase() + "com/flexganttfx/view/util/RowHeader.html";
	}

	public static void main(String[] args) {
		launch(args);
	}
}
