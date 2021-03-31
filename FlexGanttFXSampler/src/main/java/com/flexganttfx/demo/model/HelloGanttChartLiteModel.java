/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.model;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.GanttChartLite;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class HelloGanttChartLiteModel extends FlexGanttFXSample {

    private HelloRow root;
    private int layerCounter;
    private GanttChartLite<HelloRow> gantt;
    private Layer layer = new Layer("Default");

    class HelloRow extends Row<HelloRow, HelloRow, Activity> {
        public HelloRow(String name) {
            super(name);
        }
    }

    public HelloGanttChartLiteModel() {
        root = new HelloRow("Initial Root");
        root.setExpanded(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        gantt = null;
    }

    @Override
    protected GanttChartBase<?> createGanttChart() throws Exception {
        gantt = new GanttChartLite<>();
        gantt.getLayers().add(layer);
        return gantt;
    }

    @Override
    public Node getControlPanel() {

        VBox box = new VBox();
        box.setSpacing(10);
        box.setFillWidth(true);

        Button newModel = new Button("Set New List");
        newModel.setOnAction(event -> setNewList());
        newModel.setMaxWidth(Double.MAX_VALUE);

        Button addLayer = new Button("Add New Layer");
        addLayer.setOnAction(event -> addLayer());
        addLayer.setMaxWidth(Double.MAX_VALUE);

        Button addSingleRow = new Button("Add Single Row");
        addSingleRow.setOnAction(event -> addSingleRow());
        addSingleRow.setMaxWidth(Double.MAX_VALUE);

        Button addRows = new Button("Add Rows");
        addRows.setOnAction(event -> addRows());
        addRows.setMaxWidth(Double.MAX_VALUE);

        Button setRows = new Button("Set Rows");
        setRows.setOnAction(event -> setRows());
        setRows.setMaxWidth(Double.MAX_VALUE);

        Button clearRows = new Button("Clear Rows");
        clearRows.setOnAction(event -> clearRows());
        clearRows.setMaxWidth(Double.MAX_VALUE);

        Button removeFirstTen = new Button("Remove First 10 Rows");
        removeFirstTen.setOnAction(event -> removeFirstTen());
        removeFirstTen.setMaxWidth(Double.MAX_VALUE);

        Button loadTest = new Button("Load Test 150K Rows");
        loadTest.setOnAction(event -> loadTest());
        loadTest.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(newModel, addLayer, addSingleRow, addRows, setRows, removeFirstTen, clearRows, loadTest);

        return box;
    }

    @Override
    public String getSampleName() {
        return "Gantt Chart Model Lite";
    }

    private void addLayer() {
        layerCounter++;
        Layer layer = new Layer("Layer " + layerCounter);
        gantt.getLayers().add(layer);
    }

    private int rowCounter = 0;

    private void clearRows() {
        gantt.getRows().clear();
    }

    private void setNewList() {
        gantt.setRows(FXCollections.observableArrayList());
    }

    private void removeFirstTen() {
        int counter = 0;
        List<HelloRow> rows = new ArrayList<>();
        for (HelloRow row : gantt.getRows()) {
            rows.add(row);
            counter++;
            if (counter == 10) {
                break;
            }
        }
        gantt.getRows().removeAll(rows);
    }

    private void addRows() {
        List<HelloRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new HelloRow("Row " + rowCounter++));
        }
        gantt.getRows().addAll(rows);
    }

    private void setRows() {
        List<HelloRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new HelloRow("Row " + rowCounter++));
        }
        gantt.getRows().setAll(rows);
    }

    private void addSingleRow() {
        HelloRow row = new HelloRow("Row " + rowCounter++);
        gantt.getRows().add(row);
    }

    private void loadTest() {
        List<HelloRow> topLevelRows = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            HelloRow topLevelRow = new HelloRow("Top level row " + i);
            topLevelRows.add(topLevelRow);

            HelloActivity activity = new HelloActivity();
            activity.setStartTime(ZonedDateTime.now().plusDays(3).toInstant());
            activity.setEndTime(activity.getStartTime().plus(Duration.ofDays(7)));

            topLevelRow.addActivity(layer, activity);

            for (int j = 0; j < 13; j++) {
                HelloRow child = new HelloRow("child " + i + "/" + j);
                topLevelRow.getChildren().add(child);
            }
        }

        gantt.getRows().setAll(topLevelRows);
    }

    @Override
    public String getSampleDescription() {
        return "A couple of model-related actions to verify the behaviour of the control related to model changes.";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
