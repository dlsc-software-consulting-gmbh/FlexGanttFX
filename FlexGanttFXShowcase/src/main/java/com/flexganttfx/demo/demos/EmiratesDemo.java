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
package com.flexganttfx.demo.demos;

import com.flexganttfx.demo.DemoBase;
import com.flexganttfx.emirates.model.DataModel;
import com.flexganttfx.emirates.model.DataModel.DataSet;
import com.flexganttfx.emirates.view.EmiratesAircraftGanttChart;
import com.flexganttfx.emirates.view.GlassPane;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.view.GanttChartBase;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

/**
 * Showcase wrapper for the standalone Emirates aircraft scheduling demo.
 */
public class EmiratesDemo extends DemoBase {

    private EmiratesAircraftGanttChart gantt;
    private GlassPane glassPane;

    @Override
    public String getName() {
        return "Emirates Aircraft Scheduling";
    }

    @Override
    public String getDescription() {
        return "A real-world aircraft scheduling demo using Emirates flight data. " +
               "Shows aircraft assignments across several years with grouping by fleet. " +
               "Demonstrates GanttChartLite with a custom flight renderer, background data loading, " +
               "and multiple data sets.";
    }

    @Override
    public Node getPanel(Stage stage) {
        gantt = new EmiratesAircraftGanttChart();
        gantt.setScrollBarType(GanttChartBase.ScrollBarType.FIXED_HORIZON);
        VBox.setVgrow(gantt, Priority.ALWAYS);

        glassPane = new GlassPane();

        GanttChartToolBar<?> toolBar = new GanttChartToolBar<>(gantt);

        HBox selectorBar = buildSelectorBar();

        VBox content = new VBox(0, toolBar, selectorBar, gantt);
        VBox.setVgrow(content, Priority.ALWAYS);

        StackPane stack = new StackPane(content, glassPane);
        stack.getStylesheets().add(
            EmiratesAircraftGanttChart.class.getResource("/com/flexganttfx/emirates/emirates.css").toExternalForm()
        );

        // Load the first dataset automatically
        load(DataSet.values()[0]);

        return stack;
    }

    @Override
    public void dispose() {
        super.dispose();
        gantt = null;
        glassPane = null;
    }

    private HBox buildSelectorBar() {
        Label label = new Label("Data Set:");
        label.setStyle("-fx-font-weight: bold;");

        ComboBox<DataSet> dataSetBox = new ComboBox<>();
        dataSetBox.getItems().setAll(DataSet.values());
        dataSetBox.getSelectionModel().selectFirst();
        dataSetBox.setPrefWidth(200);
        dataSetBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                load(newVal);
            }
        });

        HBox bar = new HBox(10, label, dataSetBox);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(6, 12, 6, 12));
        bar.getStyleClass().add("showcase-gantt-toolbar");
        return bar;
    }

    private void load(DataSet dataSet) {
        if (gantt == null) {
            return;
        }

        gantt.getRows().clear();
        gantt.getLayers().clear();
        gantt.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 25);

        glassPane.setProgress(0.01);

        Task<DataModel> task = new Task<>() {
            @Override
            protected DataModel call() throws Exception {
                return new DataModel(dataSet, glassPane.progressProperty());
            }
        };

        task.setOnSucceeded(evt -> Platform.runLater(() -> {
            try {
                DataModel model = task.get();
                gantt.getRows().setAll(model.getRows());
                gantt.getLayers().setAll(model.getLayers());

                gantt.getTimeline().getModel().setHorizonStartTime(
                    ZonedDateTime.of(LocalDate.of(2010, 12, 27), LocalTime.MIN, ZoneId.systemDefault()).toInstant());
                gantt.getTimeline().getModel().setHorizonEndTime(
                    ZonedDateTime.of(LocalDate.of(2013, 1, 31), LocalTime.MIN, ZoneId.systemDefault()).toInstant());
                gantt.getTimeline().getModel().setStartTime(
                    gantt.getTimeline().getModel().getHorizonStartTime());

                Platform.runLater(() -> {
                    gantt.getGraphics().showEarliestActivities();
                    glassPane.setProgress(1);
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
