/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.f1.view;

import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.f1.model.ApiClient;
import com.flexganttfx.f1.model.DataModel;
import com.flexganttfx.f1.model.F1Root;
import com.flexganttfx.f1.model.RaceSession;
import com.flexganttfx.f1.model.TireStint;
import com.flexganttfx.f1.renderer.TireStintRenderer;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import javafx.application.Platform;
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

import java.time.temporal.ChronoUnit;
import java.util.List;

public class F1View extends VBox {

    private final ComboBox<Integer> yearBox = new ComboBox<>();
    private final ComboBox<RaceSession> raceBox = new ComboBox<>();
    private final Button loadButton = new Button("Load");
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    private final F1Root phantomRoot = new F1Root("F1 Root");
    private final GanttChart<F1Root> gantt;
    private Layer layer;
    private boolean suppressYearListener = false;

    public F1View() {
        int latestYear = 2025;
        for (int y = 2022; y <= latestYear; y++) {
            yearBox.getItems().add(y);
        }
        yearBox.setValue(latestYear);

        raceBox.setPromptText("Select race");
        HBox.setHgrow(raceBox, Priority.ALWAYS);
        updateLoadButtonState();

        HBox selectorBar = new HBox(10,
                new Label("Year:"),
                yearBox,
                new Label("Race:"),
                raceBox,
                loadButton);
        selectorBar.setAlignment(Pos.CENTER_LEFT);
        selectorBar.setPadding(new Insets(10, 10, 10, 10));
        selectorBar.getStyleClass().add("showcase-gantt-toolbar");

        layer = new Layer("Tire Stints");
        gantt = new GanttChart<>(phantomRoot);
        gantt.getTreeTable().setShowRoot(false);
        gantt.getLayers().add(layer);
        gantt.getTimeline().showTemporalUnit(ChronoUnit.HOURS, 2);
        gantt.getGraphics().setActivityRenderer(TireStint.class, GanttLayout.class, new TireStintRenderer(gantt.getGraphics()));
        gantt.getGraphics().setShowRowHeaders(true);
        gantt.getGraphics().setRowHeadersWidth(120);
        gantt.getGraphics().setRowHeaderFactory(g -> new F1CarRowHeader(g));

        GanttChartToolBar<F1Root> toolBar = new GanttChartToolBar<>(gantt);

        progressIndicator.setMaxSize(96, 96);
        progressIndicator.setVisible(false);
        progressIndicator.managedProperty().bind(progressIndicator.visibleProperty());

        StackPane chartPane = new StackPane(gantt, progressIndicator);
        VBox.setVgrow(chartPane, Priority.ALWAYS);
        VBox.setVgrow(gantt, Priority.ALWAYS);

        getChildren().addAll(toolBar, selectorBar, chartPane);

        yearBox.valueProperty().addListener((obs, oldYear, newYear) -> {
            if (!suppressYearListener && newYear != null) {
                loadSessions(newYear, false);
            }
        });
        raceBox.valueProperty().addListener((obs, oldSession, newSession) -> updateLoadButtonState());
        loadButton.setOnAction(evt -> {
            RaceSession session = raceBox.getValue();
            if (session != null) {
                loadRace(session);
            }
        });

        loadSessions(yearBox.getValue(), true);
    }

    private void loadSessions(int year, boolean autoLoad) {
        setLoading(true);
        raceBox.getItems().clear();

        Task<List<RaceSession>> task = new Task<>() {
            @Override
            protected List<RaceSession> call() throws Exception {
                return ApiClient.fetchSessions(year);
            }
        };

        task.setOnSucceeded(evt -> Platform.runLater(() -> {
            List<RaceSession> sessions = task.getValue();
            if (sessions.isEmpty() && autoLoad && year > 2022) {
                // No races yet for this year — silently fall back to the previous year
                suppressYearListener = true;
                yearBox.setValue(year - 1);
                suppressYearListener = false;
                loadSessions(year - 1, true);
                return;
            }
            raceBox.getItems().setAll(sessions);
            if (!raceBox.getItems().isEmpty()) {
                raceBox.getSelectionModel().selectLast();
                if (autoLoad) {
                    loadRace(raceBox.getValue());
                    return; // loadRace owns the loading indicator from here
                }
            }
            setLoading(false);
        }));
        task.setOnFailed(evt -> Platform.runLater(() -> {
            setLoading(false);
            showError("Failed to load race sessions", "Too many requests?\nThis is running on a free tier.");
        }));

        start(task, "flexganttfx-f1-sessions");
    }

    private void loadRace(RaceSession session) {
        setLoading(true);

        Task<DataModel> task = new Task<>() {
            @Override
            protected DataModel call() throws Exception {
                return new DataModel(session);
            }
        };

        task.setOnSucceeded(evt -> Platform.runLater(() -> {
            DataModel model = task.getValue();
            phantomRoot.getChildren().setAll(model.getRoot().getChildren());
            layer = model.getLayer();
            gantt.getLayers().setAll(layer);
            gantt.expandRows();
            setLoading(false);

            gantt.getGraphics().showAllActivities();
        }));
        task.setOnFailed(evt -> Platform.runLater(() -> {
            setLoading(false);
            showError("Failed to load F1 strategy data", "Too many requests?\nThis is running on a free tier.");
        }));

        start(task, "flexganttfx-f1-race");
    }

    private void start(Task<?> task, String threadName) {
        Thread thread = new Thread(task, threadName);
        thread.setDaemon(true);
        thread.start();
    }

    private void setLoading(boolean loading) {
        progressIndicator.setVisible(loading);
        yearBox.setDisable(loading);
        raceBox.setDisable(loading);
        updateLoadButtonState();
    }

    private void updateLoadButtonState() {
        loadButton.setDisable(progressIndicator.isVisible() || raceBox.getValue() == null);
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(gantt.getScene().getWindow());
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
