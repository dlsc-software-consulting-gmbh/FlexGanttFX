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
package com.flexganttfx.naturalevents.view;

import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.naturalevents.model.DataModel;
import com.flexganttfx.naturalevents.model.EventCategoryRow;
import com.flexganttfx.naturalevents.model.NaturalEventActivity;
import com.flexganttfx.naturalevents.renderer.NaturalEventRenderer;
import com.flexganttfx.view.GanttChart;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class NaturalEventsView extends VBox {

    private final Spinner<Integer> daysSpinner = new Spinner<>(30, 730, 365);
    private final Button loadButton = new Button("Load");
    private final CheckBox wildfiresBox = createFilterCheckBox("Wildfires");
    private final CheckBox stormsBox = createFilterCheckBox("Storms");
    private final CheckBox floodsBox = createFilterCheckBox("Floods");
    private final CheckBox volcanoesBox = createFilterCheckBox("Volcanoes");
    private final CheckBox seaIceBox = createFilterCheckBox("Sea / Ice");
    private final BooleanProperty loading = new SimpleBooleanProperty();
    private final EventCategoryRow phantomRoot = new EventCategoryRow("Natural Events", "root", Color.TRANSPARENT);
    private final GanttChart<EventCategoryRow> chart;
    private List<EventCategoryRow> allRows = List.of();

    public NaturalEventsView() {
        Layer layer = new Layer("Natural Events");
        chart = new GanttChart<>(phantomRoot);
        chart.getLayers().add(layer);
        chart.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 30);
        chart.getGraphics().setActivityRenderer(NaturalEventActivity.class, GanttLayout.class,
                new NaturalEventRenderer(chart.getGraphics()));
        chart.getTreeTable().setShowRoot(false);

        daysSpinner.setEditable(true);

        HBox controls = new HBox(10,
                new Label("Days back:"), daysSpinner,
                loadButton,
                new Label("Categories:"),
                wildfiresBox, stormsBox, floodsBox, volcanoesBox, seaIceBox);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(10));
        controls.getStyleClass().add("showcase-gantt-toolbar");

        GanttChartToolBar<EventCategoryRow> chartToolBar = new GanttChartToolBar<>(chart);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(90, 90);

        StackPane progressOverlay = new StackPane(progressIndicator);
        progressOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");
        progressOverlay.visibleProperty().bind(loading);
        progressOverlay.managedProperty().bind(loading);

        daysSpinner.disableProperty().bind(loading);
        loadButton.disableProperty().bind(loading);
        wildfiresBox.disableProperty().bind(loading);
        stormsBox.disableProperty().bind(loading);
        floodsBox.disableProperty().bind(loading);
        volcanoesBox.disableProperty().bind(loading);
        seaIceBox.disableProperty().bind(loading);

        StackPane chartPane = new StackPane(chart, progressOverlay);
        VBox.setVgrow(chart, Priority.ALWAYS);
        VBox.setVgrow(chartPane, Priority.ALWAYS);

        getChildren().addAll(chartToolBar, controls, chartPane);

        loadButton.setOnAction(evt -> loadData());
        wildfiresBox.selectedProperty().addListener((obs, oldValue, newValue) -> applyCategoryFilter());
        stormsBox.selectedProperty().addListener((obs, oldValue, newValue) -> applyCategoryFilter());
        floodsBox.selectedProperty().addListener((obs, oldValue, newValue) -> applyCategoryFilter());
        volcanoesBox.selectedProperty().addListener((obs, oldValue, newValue) -> applyCategoryFilter());
        seaIceBox.selectedProperty().addListener((obs, oldValue, newValue) -> applyCategoryFilter());

        Platform.runLater(this::loadData);
    }

    private void loadData() {
        loading.set(true);

        Task<DataModel> task = new Task<>() {
            @Override
            protected DataModel call() throws Exception {
                return new DataModel(daysSpinner.getValue());
            }
        };

        task.setOnSucceeded(evt -> Platform.runLater(() -> {
            loading.set(false);
            DataModel model = task.getValue();
            allRows = new ArrayList<>(model.getRoot().getChildren());
            chart.getLayers().setAll(model.getLayer());
            applyCategoryFilter();
            chart.getGraphics().showEarliestActivities();
        }));

        task.setOnFailed(evt -> Platform.runLater(() -> {
            loading.set(false);
            showErrorAlert(task.getException());
        }));

        Thread thread = new Thread(task, "natural-events-loader");
        thread.setDaemon(true);
        thread.start();
    }

    private void applyCategoryFilter() {
        if (allRows.isEmpty()) {
            phantomRoot.getChildren().clear();
            return;
        }

        List<EventCategoryRow> visibleRows = allRows.stream()
                .filter(this::isRowVisible)
                .toList();
        phantomRoot.getChildren().setAll(visibleRows);
    }

    private boolean isRowVisible(EventCategoryRow row) {
        return switch (row.getCategoryId()) {
            case "wildfires" -> wildfiresBox.isSelected();
            case "severeStorms" -> stormsBox.isSelected();
            case "floods" -> floodsBox.isSelected();
            case "volcanoes" -> volcanoesBox.isSelected();
            case "seaLakeIce" -> seaIceBox.isSelected();
            default -> true;
        };
    }

    private void showErrorAlert(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Unable to load natural events");
        alert.setContentText(throwable == null || throwable.getMessage() == null || throwable.getMessage().isBlank()
                ? "An unexpected error occurred."
                : throwable.getMessage());
        alert.show();
    }

    private CheckBox createFilterCheckBox(String text) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(true);
        return checkBox;
    }
}
