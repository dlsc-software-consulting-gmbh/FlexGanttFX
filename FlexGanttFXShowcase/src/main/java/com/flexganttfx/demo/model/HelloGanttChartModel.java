/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.model;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.ProgressDialog;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javafx.scene.control.SelectionMode.MULTIPLE;

public class HelloGanttChartModel extends FlexGanttFXSample {

    private HelloRow root;
    private int rootCounter;
    private int layerCounter;
    private GanttChart<HelloRow> gantt;
    private final Layer layer = new Layer("Default");

    class HelloRow extends Row<HelloRow, HelloRow, Activity> {
        public HelloRow(String name) {
            super(name);
        }
    }

    public HelloGanttChartModel() {
        root = new HelloRow("Initial Root");
        root.setExpanded(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        gantt = null;
    }

    @Override
    protected GanttChart<?> createGanttChart() {
        gantt = new GanttChart<>(root);
        gantt.getLayers().add(layer);
        gantt.getTreeTable().getSelectionModel().setSelectionMode(MULTIPLE);
        return gantt;
    }

    @Override
    public Node getControlPanel() {

        HBox box = new HBox();
        box.setSpacing(10);

        Button newModel = new Button("Set New Root");
        newModel.setOnAction(event -> setNewRoot());
        newModel.setMaxWidth(Double.MAX_VALUE);
        newModel.setMinWidth(Region.USE_PREF_SIZE);

        Button addLayer = new Button("Add New Layer");
        addLayer.setOnAction(event -> addLayer());
        addLayer.setMaxWidth(Double.MAX_VALUE);
        addLayer.setMinWidth(Region.USE_PREF_SIZE);

        ToggleButton showRoot = new ToggleButton("Show Root");
        showRoot.setMaxWidth(Double.MAX_VALUE);
        showRoot.setMinWidth(Region.USE_PREF_SIZE);
        Bindings.bindBidirectional(showRoot.selectedProperty(), gantt.getTreeTable().showRootProperty());

        Button addSingleRow = new Button("Add Single Row");
        addSingleRow.setMinWidth(Region.USE_PREF_SIZE);
        addSingleRow.setOnAction(event -> addSingleRow());
        addSingleRow.setMaxWidth(Double.MAX_VALUE);

        Button addRows = new Button("Add Rows");
        addRows.setMinWidth(Region.USE_PREF_SIZE);
        addRows.setOnAction(event -> addRows());
        addRows.setMaxWidth(Double.MAX_VALUE);

        Button setRows = new Button("Set Rows");
        setRows.setMinWidth(Region.USE_PREF_SIZE);
        setRows.setOnAction(event -> setRows());
        setRows.setMaxWidth(Double.MAX_VALUE);

        Button clearRows = new Button("Clear Rows");
        clearRows.setMinWidth(Region.USE_PREF_SIZE);
        clearRows.setOnAction(event -> clearRows());
        clearRows.setMaxWidth(Double.MAX_VALUE);

        Button removeRows = new Button("Remove Selected Rows");
        removeRows.setMinWidth(Region.USE_PREF_SIZE);
        removeRows.setOnAction(event -> removeRows());
        removeRows.setMaxWidth(Double.MAX_VALUE);

        Button removeFirstTen = new Button("Remove First 10 Rows");
        removeFirstTen.setMinWidth(Region.USE_PREF_SIZE);
        removeFirstTen.setOnAction(event -> removeFirstTen());
        removeFirstTen.setMaxWidth(Double.MAX_VALUE);

        Button loadTest = new Button("Load Test 150K Rows");
        loadTest.setMinWidth(Region.USE_PREF_SIZE);
        loadTest.setOnAction(event -> loadTest());
        loadTest.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(newModel, addLayer, showRoot, addSingleRow,
                addRows, setRows, removeRows, removeFirstTen, clearRows, loadTest);

        return box;
    }

    @Override
    public String getSampleName() {
        return "Gantt Chart Model";
    }

    private void setNewRoot() {
        rootCounter++;
        root = new HelloRow("Root #" + rootCounter);
        root.setExpanded(true);
        gantt.setRoot(root);
    }

    private void addLayer() {
        layerCounter++;
        Layer layer = new Layer("Layer " + layerCounter);
        gantt.getLayers().add(layer);
    }

    private int rowCounter = 0;

    private void clearRows() {
        gantt.getRoot().getChildren().clear();
    }

    private void removeRows() {
        List<HelloRow> rows = new ArrayList<>();
        for (TreeItem<HelloRow> item : gantt.getTreeTable().getSelectionModel()
                .getSelectedItems()) {
            rows.add(item.getValue());
        }
        gantt.getRoot().getChildren().removeAll(rows);
    }

    private void removeFirstTen() {
        int counter = 0;
        List<HelloRow> rows = new ArrayList<>();
        for (HelloRow row : gantt.getRoot().getChildren()) {
            rows.add(row);
            counter++;
            if (counter == 10) {
                break;
            }
        }
        gantt.getRoot().getChildren().removeAll(rows);
    }

    private void addRows() {
        List<HelloRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new HelloRow("Row " + rowCounter++));
        }
        gantt.getRoot().getChildren().addAll(rows);
    }

    private void setRows() {
        List<HelloRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new HelloRow("Row " + rowCounter++));
        }
        gantt.getRoot().getChildren().setAll(rows);
    }

    private void addSingleRow() {
        HelloRow row = new HelloRow("Row " + rowCounter++);
        gantt.getRoot().getChildren().add(row);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private void loadTest() {
        Task<Object> task = new Task<>() {
            @Override
            protected Object call() throws Exception {
                List<HelloRow> topLevelRows = new ArrayList<>();
                for (int i = 0; i < 10000; i++) {
                    updateMessage("Creating row " + (i + 1));
                    updateProgress(i, 9999);
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

                gantt.getRoot().getChildren().setAll(topLevelRows);

                return null;
            }
        };

        ProgressDialog progressDialog = new ProgressDialog(task);
        progressDialog.initOwner(gantt.getScene().getWindow());
        progressDialog.setTitle("Loading Test");
        progressDialog.setHeaderText("Loading 150K rows.");
        progressDialog.show();

        executor.execute(task);
    }

    @Override
    public String getSampleDescription() {
        return "A couple of model-related actions to verify the behaviour of the control related to model changes.";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
