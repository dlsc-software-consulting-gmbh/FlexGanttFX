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
package com.flexganttfx.spacex.view;

import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.spacex.model.DataModel;
import com.flexganttfx.spacex.model.LaunchActivity;
import com.flexganttfx.spacex.model.SpaceXRoot;
import com.flexganttfx.spacex.renderer.LaunchRenderer;
import com.flexganttfx.view.GanttChart;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

public class SpaceXView extends VBox {

    private static final int FIRST_YEAR = 2006;

    private final ComboBox<Integer> fromYearComboBox = new ComboBox<>();
    private final ComboBox<Integer> toYearComboBox = new ComboBox<>();
    private final ComboBox<String> statusComboBox = new ComboBox<>();
    private final BooleanProperty loading = new SimpleBooleanProperty();
    private final SpaceXRoot phantomRoot = new SpaceXRoot();
    private final GanttChart<SpaceXRoot> chart;

    public SpaceXView() {
        int lastYear = 2023;
        fromYearComboBox.getItems().setAll(IntStream.rangeClosed(FIRST_YEAR, lastYear).boxed().toList());
        toYearComboBox.getItems().setAll(IntStream.rangeClosed(FIRST_YEAR, lastYear + 1).boxed().toList());
        statusComboBox.getItems().setAll(List.of("ALL", "SUCCESS", "FAILURE"));

        fromYearComboBox.setValue(fromYearComboBox.getItems().contains(2015) ? 2015 : fromYearComboBox.getItems().get(0));
        toYearComboBox.setValue(lastYear);
        statusComboBox.setValue("ALL");

        Layer layer = new Layer("Launches");
        chart = new GanttChart<>(phantomRoot);
        chart.getLayers().add(layer);
        chart.getTimeline().showTemporalUnit(ChronoUnit.MONTHS, 6);
        chart.getGraphics().setActivityRenderer(LaunchActivity.class, GanttLayout.class, new LaunchRenderer(chart.getGraphics()));
        chart.getTreeTable().setShowRoot(false);

        Button loadButton = new Button("Load");
        HBox controls = new HBox(10,
                new Label("From year:"), fromYearComboBox,
                new Label("To year:"), toYearComboBox,
                new Label("Status:"), statusComboBox,
                loadButton);
        controls.getStyleClass().add("showcase-gantt-toolbar");
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(10));

        GanttChartToolBar<SpaceXRoot> chartToolBar = new GanttChartToolBar<>(chart);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(90, 90);

        StackPane progressOverlay = new StackPane(progressIndicator);
        progressOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");
        progressOverlay.visibleProperty().bind(loading);
        progressOverlay.managedProperty().bind(loading);

        fromYearComboBox.disableProperty().bind(loading);
        toYearComboBox.disableProperty().bind(loading);
        statusComboBox.disableProperty().bind(loading);
        loadButton.disableProperty().bind(loading);

        StackPane chartPane = new StackPane(chart, progressOverlay);
        VBox.setVgrow(chart, Priority.ALWAYS);
        VBox.setVgrow(chartPane, Priority.ALWAYS);

        getChildren().addAll(chartToolBar, controls, chartPane);

        loadButton.setOnAction(evt -> loadData());
        Platform.runLater(this::loadData);
    }

    private void loadData() {
        Integer fromYear = fromYearComboBox.getValue();
        Integer toYear = toYearComboBox.getValue();
        String statusFilter = statusComboBox.getValue();

        if (fromYear == null || toYear == null) {
            showErrorAlert(new IllegalArgumentException("Please select both years."));
            return;
        }
        if (fromYear > toYear) {
            showErrorAlert(new IllegalArgumentException("The start year must not be after the end year."));
            return;
        }

        loading.set(true);

        Task<DataModel> task = new Task<>() {
            @Override
            protected DataModel call() throws Exception {
                return new DataModel(fromYear, toYear, statusFilter);
            }
        };

        task.setOnSucceeded(evt -> Platform.runLater(() -> {
            loading.set(false);
            DataModel model = task.getValue();
            phantomRoot.getChildren().setAll(model.getRoot().getChildren());
            chart.getLayers().setAll(model.getLayer());
            chart.expandRows();
            chart.getTimeline().showTemporalUnit(ChronoUnit.MONTHS, 6);
            chart.getTimeline().showTime(model.getEarliestTime(), false);
            chart.getGraphics().showEarliestActivities();
        }));

        task.setOnFailed(evt -> Platform.runLater(() -> {
            loading.set(false);
            showErrorAlert(task.getException());
        }));

        Thread thread = new Thread(task, "spacex-loader");
        thread.setDaemon(true);
        thread.start();
    }

    private void showErrorAlert(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Unable to load SpaceX launches");
        alert.setContentText(throwable == null || throwable.getMessage() == null || throwable.getMessage().isBlank()
                ? "An unexpected error occurred."
                : throwable.getMessage());
        alert.show();
    }
}
