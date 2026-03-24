/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Random;

public abstract class FlexGanttFXSample extends FlexGanttFXSampleBase {
    private GanttChartBase<?> ganttChart;
    private GanttChartToolBar<?> toolbar;
    private GanttChartStatusBar<?> statusbar;
    private BorderPane ganttPane;

    protected FlexGanttFXSample() {
    }

    @Override
    public void dispose() {
        super.dispose();

        ganttChart = null;
        toolbar = null;
        statusbar = null;
        ganttPane = null;
    }

    @Override
    public final Node getPanel(Stage stage) {
        try {
            ganttChart = createGanttChart();

            ganttChart.getTimeline().visibleTimeIntervalProperty().addListener(it -> {
                if (ganttChart != null) {
                    TimeInterval interval = ganttChart.getTimeline().getVisibleTimeInterval();
                    ZonedDateTime st = ZonedDateTime.ofInstant(interval.getStartTime(), ZoneId.systemDefault());
                    ZonedDateTime et = ZonedDateTime.ofInstant(interval.getEndTime(), ZoneId.systemDefault());
                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
                    getStatusbar().setText(formatter.format(st) + " - " + formatter.format(et));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar = new GanttChartToolBar<>(ganttChart);

        statusbar = new GanttChartStatusBar<>(ganttChart);

        ganttPane = new BorderPane();
        BorderPane.setMargin(ganttChart, new Insets(0));
        ganttPane.setTop(toolbar);
        ganttPane.setCenter(ganttChart);
        ganttPane.setBottom(statusbar);

        TreeTableView<HelloRow> tableView = new TreeTableView<>();
        tableView.setFixedCellSize(-1);
        tableView.setShowRoot(true);

        TreeTableColumn<HelloRow, String> col1 = new TreeTableColumn<>("Name");
        col1.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        col1.setCellFactory(column -> new TreeTableCell<>() {
            {
                indexProperty().addListener(it -> {
                    if (getIndex() == 0) {
                        setPrefHeight(20);
                    } else {
                        setPrefHeight(50);
                    }
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
            }
        });

        tableView.getColumns().setAll(col1);

        HelloRow rootRow = new HelloRow("Root");
        TreeItem<HelloRow> rootItem = new TreeItem<>(rootRow);
        tableView.setRoot(rootItem);

        for (int i = 0; i < 500; i++) {
            HelloRow row = new HelloRow("Row " + i);
            row.setHeight(50);
            TreeItem<HelloRow> child = new TreeItem<>(row);
            rootItem.getChildren().add(child);
        }

        ListView<String> listView = new ListView<>();
        listView.setFixedCellSize(Region.USE_COMPUTED_SIZE);
        listView.setCellFactory(view -> new ListCell<>() {
            {
                indexProperty().addListener(it -> {
                    if (getIndex() == 0) {
                        setPrefHeight(20);
                    } else {
                        setPrefHeight(50);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                setText(item);
            }
        });

        for (int i = 0; i < 500; i++) {
            listView.getItems().add("Row " + i);
        }

        Button buttonScroll = new Button("scroll to");
        buttonScroll.setOnAction(event -> {
            int index = new Random().nextInt(200);
            //listView.scrollTo(index);
            //listView.getSelectionModel().select(index);
//            tableView.scrollTo(index);
//            tableView.getSelectionModel().selectIndices(index);
            ((GanttChart) ganttChart).getTreeTable().scrollTo(index);
            ((GanttChart) ganttChart).getTreeTable().getSelectionModel().clearAndSelect(index);
            buttonScroll.setText("scrolled to: " + index);
        });

        //ganttPane.setCenter(tableView);
        ganttPane.setBottom(buttonScroll);

        return ganttPane;
    }

    protected final GanttChartBase<?> getGanttChart() {
        return ganttChart;
    }

    protected final GanttChartToolBar<?> getToolbar() {
        return toolbar;
    }

    protected final GanttChartStatusBar<?> getStatusbar() {
        return statusbar;
    }

    protected abstract GanttChartBase<?> createGanttChart() throws Exception;
}
