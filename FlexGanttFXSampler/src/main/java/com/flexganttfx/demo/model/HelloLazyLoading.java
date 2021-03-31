/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.model;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
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
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelloLazyLoading extends FlexGanttFXSample {

    private final Layer layer = new Layer("Default Layer");

    private GanttChart<HelloLazyRow> gantt;

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

        HelloLazyRow root = new HelloLazyRow("Root");

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

        private LazyLoadingService service;

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
            List<HelloLazyLoadingActivity> activities = service.getValue();
            for (HelloLazyLoadingActivity activity : activities) {
                HelloLazyRow row = activity.getRow();
                MutableActivityRepositoryBase<HelloLazyLoadingActivity> repository = (MutableActivityRepositoryBase<HelloLazyLoadingActivity>) row.getRepository();
                repository.clearActivities();
            }
            for (HelloLazyLoadingActivity activity : activities) {
                HelloLazyRow row = activity.getRow();
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
    class LazyLoadingService extends Service<List<HelloLazyLoadingActivity>> {

        private TimeInterval timeInterval;

        @Override
        protected Task<List<HelloLazyLoadingActivity>> createTask() {
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
    class LazyLoadingTask extends Task<List<HelloLazyLoadingActivity>> {

        private TimeInterval timeInterval;

        public LazyLoadingTask(TimeInterval timeInterval) {
            this.timeInterval = Objects.requireNonNull(timeInterval);
        }

        @Override
        protected List<HelloLazyLoadingActivity> call() throws Exception {

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

                List<HelloLazyLoadingActivity> activities = new ArrayList<HelloLazyLoadingActivity>();

                for (HelloLazyRow row : gantt.getGraphics().getRows()) {

                    Instant time = timeInterval.getStartTime().truncatedTo(ChronoUnit.DAYS);

                    /*
                     * Only continue if the task hasn't been cancelled. Has the
                     * user moved on to another time interval? Then the task is
                     * cancelled.
                     */
                    if (!isCancelled()) {

                        while (time.isBefore(timeInterval.getEndTime())) {
                            HelloLazyLoadingActivity activity = new HelloLazyLoadingActivity(row);
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

    static class HelloLazyLoadingActivity extends HelloActivity {

        private HelloLazyRow row;

        public HelloLazyLoadingActivity(HelloLazyRow row) {
            this.row = Objects.requireNonNull(row);
        }

        public HelloLazyRow getRow() {
            return row;
        }
    }

    static class HelloLazyRow extends Row<HelloLazyRow, HelloLazyRow, HelloLazyLoadingActivity> {

        public HelloLazyRow(String name) {
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
                getChildren().add(new HelloLazyRow("Child " + (i + 1)));
            }
        }
    }

    @Override
    public Node getControlPanel() {

        VBox box = new VBox();
        box.setSpacing(10);
        box.setFillWidth(true);

        Button showRoot = new Button("Show Root");
        showRoot.setMaxWidth(Double.MAX_VALUE);
        showRoot.setOnAction(event -> gantt.getTreeTable().setShowRoot(!gantt.getTreeTable().isShowRoot()));

        Button addSingleRow = new Button("Add Single Row");
        addSingleRow.setMaxWidth(Double.MAX_VALUE);
        addSingleRow.setOnAction(event -> addSingleRow());
        addSingleRow.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        Button addRows = new Button("Add Rows");
        addRows.setMaxWidth(Double.MAX_VALUE);
        addRows.setOnAction(event -> addRows());
        addRows.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        Button setRows = new Button("Set Rows");
        setRows.setMaxWidth(Double.MAX_VALUE);
        setRows.setOnAction(event -> setRows());
        setRows.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        Button clearRows = new Button("Clear Rows");
        clearRows.setMaxWidth(Double.MAX_VALUE);
        clearRows.setOnAction(event -> clearRows());
        clearRows.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        Button removeRows = new Button("Remove Selected Rows");
        removeRows.setMaxWidth(Double.MAX_VALUE);
        removeRows.setOnAction(event -> removeRows());
        removeRows.disableProperty().bind(Bindings.isNull(gantt.getTreeTable().getSelectionModel().selectedItemProperty()));

        box.getChildren().addAll(showRoot, addSingleRow, addRows, setRows,
                removeRows, clearRows);

        return box;
    }

    private HelloLazyRow getSelectedRow() {
        TreeItem<HelloLazyRow> selectedItem = gantt.getTreeTable().getSelectionModel().getSelectedItem();
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
        ObservableList<TreeItem<HelloLazyRow>> selectedItems = gantt.getTreeTable().getSelectionModel()
                .getSelectedItems();
        // working with copy to avoid side effects when working on same list
        ArrayList<TreeItem<HelloLazyRow>> arrayList = new ArrayList<>(selectedItems);
        gantt.getTreeTable().getSelectionModel().clearSelection();
        for (TreeItem<HelloLazyRow> item : arrayList) {
            HelloLazyRow value = item.getValue();
            value.getParent().getChildren().remove(value);
        }
    }

    private void addRows() {
        List<HelloLazyRow> rows = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            rows.add(new HelloLazyRow("Added Row " + rowCounter++));
        }
        getSelectedRow().getChildren().addAll(rows);
    }

    private void setRows() {
        List<HelloLazyRow> rows = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rows.add(new HelloLazyRow("Set Row " + rowCounter++));
        }
        getSelectedRow().getChildren().setAll(rows);
    }

    private void addSingleRow() {
        HelloLazyRow row = new HelloLazyRow("Added Row " + rowCounter++);
        getSelectedRow().getChildren().add(row);
    }

    @Override
    public String getSampleName() {
        return "Lazy Loading";
    }

    @Override
    public String getSampleDescription() {
        return "This sample shows how a lazy loading strategy can be implemented. The children items in the "
                + "tree view on the left-hand side are added when the user expands a tree node (vertical lazy loading)."
                + "The activities on the right-hand side are generated when the user scrolls horizontally (horizontal lazy loading).";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
