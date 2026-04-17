/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.util.ArrayList;
import java.util.List;

public class HelloRowHeaderColumn extends FlexGanttFXSample {

    private GanttChart<MyRow> gantt;

    @Override
    public String getSampleName() {
        return "Row Header Column";
    }

    @Override
    public void dispose() {
        super.dispose();
        gantt = null;
    }

    @Override
    protected GanttChart<?> createGanttChart() {
        gantt = new GanttChart<>();
        gantt.getStylesheets().add(HelloRowHeaderColumn.class.getResource("row-header.css").toExternalForm());
        gantt.setRowHeaderType(RowHeaderType.GRAPHIC_NODE);

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
        HBox controlPane = new HBox();
        controlPane.setSpacing(10);
        controlPane.setAlignment(Pos.CENTER_LEFT);

        gantt.setRowHeaderNodeFactory(new ColorCallback());
        gantt.rowHeaderTypeProperty().addListener(evt -> {
            switch (gantt.getRowHeaderType()) {
                case ROW_NUMBER:
                    gantt.getRowHeaderColumn().setPrefWidth(30);
                    break;
                case LEVEL_NUMBER:
                    gantt.getRowHeaderColumn().setPrefWidth(50);
                    break;
                case GRAPHIC_NODE:
                    break;
            }
        });

        // content type box
        ComboBox<RowHeaderType> contentTypeBox = new ComboBox<>();
        contentTypeBox.getItems().addAll(RowHeaderType.values());
        contentTypeBox.setValue(gantt.getRowHeaderType());
        contentTypeBox.setConverter(new StringConverter<RowHeaderType>() {
            @Override
            public String toString(RowHeaderType object) {
                switch (object) {
                    case ROW_NUMBER:
                        return "Row Number";
                    case LEVEL_NUMBER:
                        return "Level Number";
                    case GRAPHIC_NODE:
                        return "Graphic Node";
                    default:
                        return "";
                }
            }

            @Override
            public RowHeaderType fromString(String string) {
                return null;
            }
        });
        gantt.rowHeaderTypeProperty().bind(contentTypeBox.valueProperty());
        controlPane.getChildren().add(contentTypeBox);

        // radio button group
        ToggleGroup group = new ToggleGroup();

        RadioButton colorCallback = new RadioButton("Color");
        colorCallback.setStyle("-fx-background-color: transparent;");
        colorCallback.setToggleGroup(group);
        colorCallback.setOnAction(evt -> {
            gantt.setRowHeaderNodeFactory(new ColorCallback());
            gantt.getRowHeaderColumn().setPrefWidth(24);
        });
        colorCallback.disableProperty().bind(
                Bindings.notEqual(RowHeaderType.GRAPHIC_NODE,
                        gantt.rowHeaderTypeProperty()));
        controlPane.getChildren().add(colorCallback);

        RadioButton statusCallback = new RadioButton("Status");
        statusCallback.setStyle("-fx-background-color: transparent;");
        statusCallback.setToggleGroup(group);
        statusCallback.setOnAction(evt -> {
            gantt.setRowHeaderNodeFactory(new StatusCallback());
            gantt.getRowHeaderColumn().setPrefWidth(30);
        });

        statusCallback.disableProperty().bind(Bindings.notEqual(RowHeaderType.GRAPHIC_NODE, gantt.rowHeaderTypeProperty()));
        controlPane.getChildren().add(statusCallback);

        RadioButton controlCallback = new RadioButton("Control");
        controlCallback.setStyle("-fx-background-color: transparent;");
        controlCallback.setToggleGroup(group);
        controlCallback.setOnAction(evt -> {
            gantt.setRowHeaderNodeFactory(new ControlCallback());
            gantt.getRowHeaderColumn().setPrefWidth(40);
        });
        controlCallback.disableProperty().bind(Bindings.notEqual(RowHeaderType.GRAPHIC_NODE, gantt.rowHeaderTypeProperty()));
        controlPane.getChildren().add(controlCallback);

        statusCallback.fire();

        return controlPane;
    }

    @Override
    public String getSampleDescription() {
        return "The first column of the tree table is the row header. "
                + "The header can be used to display lines numbers, indentation levels, "
                + "or arbitrary nodes.";
    }

    enum Status {
        OK, WARNING, ERROR, OTHER
    }

    class MyRow extends Row<MyRow, MyRow, Activity> {
        private final Paint paint;
        private Status status = Status.OK;

        public MyRow(String name) {
            super(name);

            paint = Color.color(Math.random(), Math.random(), Math.random());

            double rnd = Math.random();

            if (rnd < .2) {
                status = Status.ERROR;
            } else if (rnd < .3) {
                status = Status.WARNING;
            } else if (rnd < .4) {
                status = Status.OTHER;
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
                    label.setGraphic(new FontIcon(MaterialDesign.MDI_ACCESS_POINT));
                    label.getStyleClass().add("error");
                    break;
                case WARNING:
                    label.setGraphic(new FontIcon(MaterialDesign.MDI_ALERT));
                    label.getStyleClass().add("warning");
                    break;
                case OTHER:
                    label.setGraphic(new FontIcon(MaterialDesign.MDI_PENCIL));
                    label.getStyleClass().add("other");
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

    public static void main(String[] args) {
        launch(args);
    }
}
