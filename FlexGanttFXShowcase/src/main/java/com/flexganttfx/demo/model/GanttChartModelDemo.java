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
package com.flexganttfx.demo.model;

import com.flexganttfx.demo.GanttChartDemoBase;
import com.flexganttfx.demo.DemoActivity;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import javafx.application.Platform;
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

public class GanttChartModelDemo extends GanttChartDemoBase {

    private DemoRow root;
    private int rootCounter;
    private int layerCounter;
    private GanttChart<DemoRow> gantt;
    private final Layer layer = new Layer("Default");

    class DemoRow extends Row<DemoRow, DemoRow, Activity> {
        public DemoRow(String name) {
            super(name);
        }
    }

    public GanttChartModelDemo() {
        root = new DemoRow("Initial Root");
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
    public String getName() {
        return "Gantt Chart Model";
    }

    private void setNewRoot() {
        rootCounter++;
        root = new DemoRow("Root #" + rootCounter);
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
        List<DemoRow> rows = new ArrayList<>();
        for (TreeItem<DemoRow> item : gantt.getTreeTable().getSelectionModel()
                .getSelectedItems()) {
            rows.add(item.getValue());
        }
        gantt.getRoot().getChildren().removeAll(rows);
    }

    private void removeFirstTen() {
        int counter = 0;
        List<DemoRow> rows = new ArrayList<>();
        for (DemoRow row : gantt.getRoot().getChildren()) {
            rows.add(row);
            counter++;
            if (counter == 10) {
                break;
            }
        }
        gantt.getRoot().getChildren().removeAll(rows);
    }

    private void addRows() {
        List<DemoRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new DemoRow("Row " + rowCounter++));
        }
        gantt.getRoot().getChildren().addAll(rows);
    }

    private void setRows() {
        List<DemoRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new DemoRow("Row " + rowCounter++));
        }
        gantt.getRoot().getChildren().setAll(rows);
    }

    private void addSingleRow() {
        DemoRow row = new DemoRow("Row " + rowCounter++);
        gantt.getRoot().getChildren().add(row);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private void loadTest() {
        Task<Object> task = new Task<>() {
            @Override
            protected Object call() throws Exception {
                List<DemoRow> topLevelRows = new ArrayList<>();
                for (int i = 0; i < 10000; i++) {
                    updateMessage("Creating row " + (i + 1));
                    updateProgress(i, 9999);
                    DemoRow topLevelRow = new DemoRow("Top level row " + i);
                    topLevelRows.add(topLevelRow);

                    DemoActivity activity = new DemoActivity();
                    activity.setStartTime(ZonedDateTime.now().plusDays(3).toInstant());
                    activity.setEndTime(activity.getStartTime().plus(Duration.ofDays(7)));

                    topLevelRow.addActivity(layer, activity);

                    for (int j = 0; j < 13; j++) {
                        DemoRow child = new DemoRow("child " + i + "/" + j);
                        topLevelRow.getChildren().add(child);
                    }
                }

                Platform.runLater(() -> {
                    gantt.getRoot().getChildren().setAll(topLevelRows);
                });

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
    public String getDescription() {
        return "A couple of model-related actions to verify the behaviour of the control related to model changes.";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
