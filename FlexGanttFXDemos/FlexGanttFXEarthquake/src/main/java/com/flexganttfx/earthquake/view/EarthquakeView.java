/**
 * License Notice for FlexGanttFX
 * <p>
 * The FlexGanttFX software library is distributed under a dual licensing model.
 * <p>
 * 1. Commercial Use
 * Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 * The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 * <p>
 * 2. Open Source Use
 * For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 * The full text of the license is available at:
 * <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 * <p>
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.earthquake.view;

import com.flexganttfx.earthquake.model.DataModel;
import com.flexganttfx.earthquake.model.EarthquakeActivity;
import com.flexganttfx.earthquake.model.MagnitudeBandRow;
import com.flexganttfx.earthquake.renderer.EarthquakeRenderer;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class EarthquakeView extends VBox {

    private final DatePicker fromDatePicker = new DatePicker(LocalDate.now().minusYears(1));
    private final DatePicker toDatePicker = new DatePicker(LocalDate.now());
    private final Slider minMagnitudeSlider = new Slider(5.0, 8.0, 5.5);
    private final Label magnitudeLabel = new Label();
    private final Button fetchButton = new Button("Fetch");
    private final BooleanProperty loading = new SimpleBooleanProperty();
    private final MagnitudeBandRow phantomRoot = new MagnitudeBandRow("Earthquakes", 0);
    private final GanttChart<MagnitudeBandRow> gantt;

    public EarthquakeView() {
        Layer layer = new Layer("Earthquakes");
        gantt = new GanttChart<>(phantomRoot);
        gantt.getLayers().add(layer);
        gantt.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 14);
        gantt.getGraphics().setActivityRenderer(EarthquakeActivity.class, GanttLayout.class, new EarthquakeRenderer(gantt.getGraphics()));
        gantt.getTreeTable().setShowRoot(false);

        minMagnitudeSlider.setBlockIncrement(0.5);
        minMagnitudeSlider.setMajorTickUnit(0.5);
        minMagnitudeSlider.setMinorTickCount(0);
        minMagnitudeSlider.setShowTickLabels(true);
        minMagnitudeSlider.setShowTickMarks(true);
        minMagnitudeSlider.setSnapToTicks(true);
        magnitudeLabel.textProperty().bind(Bindings.createStringBinding(
                () -> String.format(Locale.US, "%.1f", minMagnitudeSlider.getValue()),
                minMagnitudeSlider.valueProperty()));

        HBox toolControls = new HBox(10,
                new Label("From:"), fromDatePicker,
                new Label("To:"), toDatePicker,
                new Label("Min M:"), minMagnitudeSlider, magnitudeLabel,
                fetchButton);
        toolControls.setAlignment(Pos.CENTER_LEFT);
        toolControls.getStyleClass().add("showcase-gantt-toolbar");
        HBox.setHgrow(minMagnitudeSlider, Priority.ALWAYS);
        toolControls.setPadding(new Insets(10));

        GanttChartToolBar<MagnitudeBandRow> chartToolBar = new GanttChartToolBar<>(gantt);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(90, 90);

        StackPane progressOverlay = new StackPane(progressIndicator);
        progressOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");
        progressOverlay.visibleProperty().bind(loading);
        progressOverlay.managedProperty().bind(loading);

        fromDatePicker.disableProperty().bind(loading);
        toDatePicker.disableProperty().bind(loading);
        minMagnitudeSlider.disableProperty().bind(loading);
        fetchButton.disableProperty().bind(loading);

        StackPane chartPane = new StackPane(gantt, progressOverlay);
        VBox.setVgrow(gantt, Priority.ALWAYS);
        VBox.setVgrow(chartPane, Priority.ALWAYS);

        getChildren().addAll(chartToolBar, toolControls, chartPane);

        fetchButton.setOnAction(evt -> fetchData());
        Platform.runLater(this::fetchData);
    }

    private void fetchData() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        if (fromDate == null || toDate == null) {
            showErrorAlert(new IllegalArgumentException("Please select both dates."));
            return;
        }
        if (fromDate.isAfter(toDate)) {
            showErrorAlert(new IllegalArgumentException("The start date must not be after the end date."));
            return;
        }

        loading.set(true);

        Task<DataModel> task = new Task<>() {
            @Override
            protected DataModel call() throws Exception {
                return new DataModel(fromDate, toDate, minMagnitudeSlider.getValue());
            }
        };

        task.setOnSucceeded(evt -> Platform.runLater(() -> {
            loading.set(false);
            DataModel model = task.getValue();
            phantomRoot.getChildren().setAll(model.getRoot().getChildren());
            gantt.getLayers().setAll(model.getLayer());
            gantt.getTimeline().showTime(model.getEarliestTime(), false);
            gantt.getGraphics().showEarliestActivities();
        }));

        task.setOnFailed(evt -> Platform.runLater(() -> {
            loading.set(false);
            showErrorAlert(task.getException());
        }));

        Thread thread = new Thread(task, "earthquake-loader");
        thread.setDaemon(true);
        thread.start();
    }

    private void showErrorAlert(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Unable to load earthquake data");
        alert.setContentText(throwable == null || throwable.getMessage() == null || throwable.getMessage().isBlank()
                ? "An unexpected error occurred."
                : throwable.getMessage());
        alert.show();
    }
}
