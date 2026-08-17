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
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.repository.MutableActivityRepositoryBase;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChart.RowHeaderType;
import com.flexganttfx.view.timeline.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LazyLoadingDemo extends GanttChartDemoBase {

    private final Layer layer = new Layer("Default Layer");

    private GanttChart<LazyRow> gantt;

    @Override
    public void dispose() {
        super.dispose();
        gantt = null;
    }

    @Override
    protected GanttChart<?> createGanttChart() throws FileNotFoundException {
        gantt = new GanttChart<>();
        gantt.setRowHeaderType(RowHeaderType.LEVEL_NUMBER);
        gantt.getRowHeaderColumn().setPrefWidth(60);

        gantt.getLayers().add(layer);

        LazyRow root = new LazyRow("Root");

        gantt.setRoot(root);
        gantt.getTreeTable().setShowRoot(false);

        final Timeline timeline = gantt.getTimeline();

        LazyLoadingManager lazyloadingManager = new LazyLoadingManager();

        timeline.visibleTimeIntervalProperty().addListener(it -> {
            final TimeInterval visibleTimeInterval = timeline.getVisibleTimeInterval();
            lazyloadingManager.ensureData(visibleTimeInterval, false);
        });

        gantt.getTreeTable().rootProperty().addListener(it -> gantt.getTreeTable().getRoot().addEventHandler(TreeItem.branchExpandedEvent(), evt -> lazyloadingManager.ensureData(timeline.getVisibleTimeInterval(), true)));

        Platform.runLater(() -> timeline.getModel().setStartTime(Instant.now().plusSeconds(1)));

        return gantt;
    }

    /**
     * A singleton used for querying the data from the server. Please note that
     * this class currently does not manage already loaded time intervals /
     * slices but always performs a server-side query.
     */
    class LazyLoadingManager {

        private final LazyLoadingService service;

        private TimeInterval lastTimeInterval;

        private LazyLoadingManager() {
            service = new LazyLoadingService();
            service.setOnSucceeded(evt -> afterSuccess(evt));
        }

        public void ensureData(TimeInterval timeInterval, boolean force) {
            if (!force && timeInterval.equals(lastTimeInterval)) {
                return;
            }

            this.lastTimeInterval = timeInterval;

            service.setTimeInterval(timeInterval);
            service.restart();
        }

        /**
         * This method updates the various interval trees that are used for
         * storing activities. It only gets called after the service has
         * successfully executed the background task of loading the data.
         */
        private void afterSuccess(WorkerStateEvent evt) {
            List<LazyLoadingActivity> activities = service.getValue();
            for (LazyLoadingActivity activity : activities) {
                LazyRow row = activity.getRow();
                MutableActivityRepositoryBase<LazyLoadingActivity> repository = (MutableActivityRepositoryBase<LazyLoadingActivity>) row.getRepository();
                repository.clearActivities();
            }
            for (LazyLoadingActivity activity : activities) {
                LazyRow row = activity.getRow();
                row.addActivity(layer, activity);
            }
        }
    }

    /**
     * A service is used for concurrent operations in JavaFX. A service can be
     * restarted many times. The service creates a task to do its work in a
     * background thread. If a service gets restarted before the task has
     * finished then the task will be cancelled.
     */
    class LazyLoadingService extends Service<List<LazyLoadingActivity>> {

        private TimeInterval timeInterval;

        @Override
        protected Task<List<LazyLoadingActivity>> createTask() {
            return new LazyLoadingTask(timeInterval);
        }

        public void setTimeInterval(TimeInterval timeInterval) {
            this.timeInterval = Objects.requireNonNull(timeInterval);
        }
    }

    /**
     * This task is doing the actual work of looking up activities. Normally it
     * would call some kind of web service in its call() method.
     */
    class LazyLoadingTask extends Task<List<LazyLoadingActivity>> {

        private final TimeInterval timeInterval;

        public LazyLoadingTask(TimeInterval timeInterval) {
            this.timeInterval = Objects.requireNonNull(timeInterval);
        }

        @Override
        protected List<LazyLoadingActivity> call() throws Exception {

            /*
             * Always sleep a little bit to give the application the chance to
             * cancel this task when the user keeps on scrolling.
             */
            Thread.sleep(100);

            /*
             * Has the user moved on to another time interval? Then the task is
             * cancelled.
             */
            if (!isCancelled()) {

                List<LazyLoadingActivity> activities = new ArrayList<LazyLoadingActivity>();

                for (LazyRow row : gantt.getGraphics().getRows()) {

                    Instant time = timeInterval.getStartTime().truncatedTo(ChronoUnit.DAYS);

                    /*
                     * Only continue if the task hasn't been cancelled. Has the
                     * user moved on to another time interval? Then the task is
                     * cancelled.
                     */
                    if (!isCancelled()) {

                        while (time.isBefore(timeInterval.getEndTime())) {
                            LazyLoadingActivity activity = new LazyLoadingActivity(row);
                            activity.setStartTime(time);
                            activity.setEndTime(time.plus(1, ChronoUnit.DAYS));
                            activities.add(activity);
                            time = time.plus(2, ChronoUnit.DAYS);
                        }

                    } else {
                        break;
                    }
                }

                return activities;
            }

            return null;
        }
    }

    static class LazyLoadingActivity extends DemoActivity {

        private final LazyRow row;

        public LazyLoadingActivity(LazyRow row) {
            this.row = Objects.requireNonNull(row);
        }

        public LazyRow getRow() {
            return row;
        }
    }

    static class LazyRow extends Row<LazyRow, LazyRow, LazyLoadingActivity> {

        public LazyRow(String name) {
            super(name);

            expandedProperty().addListener(it -> loadChildrenLazily());
        }

        /*
         * We keep on generating children on the fly 3 levels down. Then the
         * rows will be leafs.
         */
        @Override
        public boolean isLeaf() {
            return getPath().length > 3;
        }

        private void loadChildrenLazily() {
            for (int i = 0; i < 3 + (int) (Math.random() * 10); i++) {
                getChildren().add(new LazyRow("Child " + (i + 1)));
            }
        }
    }

    @Override
    public Node getControlPanel() {

        HBox box = new HBox();
        box.setSpacing(10);

        Button showRoot = new Button("Show Root");
        showRoot.setMaxWidth(Double.MAX_VALUE);
        showRoot.setOnAction(event -> gantt.getTreeTable().setShowRoot(!gantt.getTreeTable().isShowRoot()));
        showRoot.setMinWidth(Region.USE_COMPUTED_SIZE);

        Button addSingleRow = new Button("Add Single Row");
        addSingleRow.setMaxWidth(Double.MAX_VALUE);
        addSingleRow.setOnAction(event -> addSingleRow());
        addSingleRow.setMinWidth(Region.USE_COMPUTED_SIZE);
        addSingleRow.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        Button addRows = new Button("Add Rows");
        addRows.setMaxWidth(Double.MAX_VALUE);
        addRows.setOnAction(event -> addRows());
        addRows.setMinWidth(Region.USE_COMPUTED_SIZE);
        addRows.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        Button setRows = new Button("Set Rows");
        setRows.setMaxWidth(Double.MAX_VALUE);
        setRows.setOnAction(event -> setRows());
        setRows.setMinWidth(Region.USE_COMPUTED_SIZE);
        setRows.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        Button clearRows = new Button("Clear Rows");
        clearRows.setMaxWidth(Double.MAX_VALUE);
        clearRows.setOnAction(event -> clearRows());
        clearRows.setMinWidth(Region.USE_COMPUTED_SIZE);
        clearRows.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        Button removeRows = new Button("Remove Selected Rows");
        removeRows.setMaxWidth(Double.MAX_VALUE);
        removeRows.setOnAction(event -> removeRows());
        removeRows.setMinWidth(Region.USE_COMPUTED_SIZE);
        removeRows.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        box.getChildren().addAll(showRoot, addSingleRow, addRows, setRows,
                removeRows, clearRows);

        return box;
    }

    private LazyRow getSelectedRow() {
        TreeItem<LazyRow> selectedItem = gantt.getTreeTable().getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            return selectedItem.getValue();
        }

        return gantt.getRoot();
    }

    private int rowCounter = 0;

    private void clearRows() {
        gantt.getRoot().getChildren().clear();
    }

    private void removeRows() {
        ObservableList<TreeItem<LazyRow>> selectedItems = gantt.getTreeTable().getSelectionModel()
                .getSelectedItems();
        // working with copy to avoid side effects when working on same list
        ArrayList<TreeItem<LazyRow>> arrayList = new ArrayList<>(selectedItems);
        gantt.getTreeTable().getSelectionModel().clearSelection();
        for (TreeItem<LazyRow> item : arrayList) {
            LazyRow value = item.getValue();
            value.getParent().getChildren().remove(value);
        }
    }

    private void addRows() {
        List<LazyRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new LazyRow("Added Row " + rowCounter++));
        }
        getSelectedRow().getChildren().addAll(rows);
    }

    private void setRows() {
        List<LazyRow> rows = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rows.add(new LazyRow("Set Row " + rowCounter++));
        }
        getSelectedRow().getChildren().setAll(rows);
    }

    private void addSingleRow() {
        LazyRow row = new LazyRow("Added Row " + rowCounter++);
        getSelectedRow().getChildren().add(row);
    }

    @Override
    public String getName() {
        return "Lazy Loading";
    }

    @Override
    public String getDescription() {
        return "This demo shows how a lazy loading strategy can be implemented. The children items in the "
                + "tree view on the left-hand side are added when the user expands a tree node (vertical lazy loading)."
                + "The activities on the right-hand side are generated when the user scrolls horizontally (horizontal lazy loading).";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
