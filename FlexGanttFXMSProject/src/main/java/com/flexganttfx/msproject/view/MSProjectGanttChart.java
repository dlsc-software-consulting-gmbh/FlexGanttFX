/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.msproject.view;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.msproject.model.MSProjectGanttChartModel;
import com.flexganttfx.msproject.model.MSProjectTaskActivity;
import com.flexganttfx.msproject.model.MSProjectTaskRow;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.timeline.Timeline;
import com.jpro.webapi.WebAPI;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;

public class MSProjectGanttChart extends GanttChart<MSProjectTaskRow> {

    public MSProjectGanttChart() {
        super();

        setFixedCellSize(Row.DEFAULT_ROW_HEIGHT);
        setScrollBarType(ScrollBarType.FIXED_HORIZON);

        getStylesheets().add(MSProjectGanttChart.class.getResource("msproject.css").toExternalForm());

        getGraphics().setActivityRenderer(MSProjectTaskActivity.class, GanttLayout.class, new MSProjectTaskActivityRenderer(getGraphics()));

        getTreeTable().setShowRoot(false);

        List<TreeTableColumn<MSProjectTaskRow, ?>> columns = new ArrayList<>();

        TreeTableColumn<MSProjectTaskRow, String> nameColumn = new TreeTableColumn<>("Name");
        nameColumn.setPrefWidth(250);
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));

        Callback<TreeTableColumn<MSProjectTaskRow, String>, TreeTableCell<MSProjectTaskRow, String>> nameCellFactory = param -> new TreeTableCell<>() {

            private FontIcon parentImage = new FontIcon(MaterialDesign.MDI_CHECKBOX_MULTIPLE_MARKED);
            private FontIcon childImage = new FontIcon(MaterialDesign.MDI_CHECKBOX_MARKED);

            {
                getStyleClass().add("task-name-cell");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(item);

                getStyleClass().removeAll("parent", "child");

                MSProjectTaskRow row = getTreeTableRow().getItem();
                if (row != null && row.getChildren().size() > 0) {
                    getStyleClass().add("parent");
                    setGraphic(parentImage);
                } else {
                    getStyleClass().add("child");
                    setGraphic(childImage);
                }
            }
        };

        nameColumn.setCellFactory(nameCellFactory);

        Callback<TreeTableColumn<MSProjectTaskRow, Instant>, TreeTableCell<MSProjectTaskRow, Instant>> dateTimeCellFactory = new Callback<TreeTableColumn<MSProjectTaskRow, Instant>, TreeTableCell<MSProjectTaskRow, Instant>>() {

            private DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

            @Override
            public TreeTableCell<MSProjectTaskRow, Instant> call(TreeTableColumn<MSProjectTaskRow, Instant> param) {
                return new TreeTableCell<>() {
                    @Override
                    protected void updateItem(Instant item, boolean empty) {
                        if (item != null) {
                            setText(formatter.format(ZonedDateTime.ofInstant(item, ZoneId.systemDefault())));
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        };

        columns.add(nameColumn);

        if (!WebAPI.isBrowser()) {

            TreeTableColumn<MSProjectTaskRow, Instant> startColumn = new TreeTableColumn<>("Start");
            TreeTableColumn<MSProjectTaskRow, Instant> finishColumn = new TreeTableColumn<>("Finish");
            TreeTableColumn<MSProjectTaskRow, Double> percentageCompleteColumn = new TreeTableColumn<>("%");
            TreeTableColumn<MSProjectTaskRow, Double> percentageCompleteVisualColumn = new TreeTableColumn<>("Complete");

            startColumn.setPrefWidth(100);
            finishColumn.setPrefWidth(100);
            percentageCompleteColumn.setPrefWidth(40);
            percentageCompleteVisualColumn.setPrefWidth(100);

            startColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("startTime"));
            finishColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("finishTime"));
            percentageCompleteColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("percentageComplete"));

            percentageCompleteVisualColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("percentageComplete"));

            startColumn.setCellFactory(dateTimeCellFactory);
            finishColumn.setCellFactory(dateTimeCellFactory);

            Callback<TreeTableColumn<MSProjectTaskRow, Double>, TreeTableCell<MSProjectTaskRow, Double>> percentageCellFactory = new Callback<TreeTableColumn<MSProjectTaskRow, Double>, TreeTableCell<MSProjectTaskRow, Double>>() {
                private NumberFormat formatter = DecimalFormat.getPercentInstance();

                @Override
                public TreeTableCell<MSProjectTaskRow, Double> call(TreeTableColumn<MSProjectTaskRow, Double> param) {
                    return new TreeTableCell<>() {
                        @Override
                        protected void updateItem(Double item, boolean empty) {
                            if (item != null) {
                                setText(formatter.format(item.doubleValue() / 100));
                            } else {
                                setText(null);
                            }
                        }
                    };
                }
            };

            percentageCompleteColumn.setCellFactory(percentageCellFactory);

            Callback<TreeTableColumn<MSProjectTaskRow, Double>, TreeTableCell<MSProjectTaskRow, Double>> percentageVisualCellFactory = param -> new TreeTableCell<MSProjectTaskRow, Double>() {
                private ProgressBar progressBar;

                @Override
                protected void updateItem(Double item, boolean empty) {

                    if (progressBar == null) {
                        progressBar = new ProgressBar();
                        setContentDisplay(GRAPHIC_ONLY);
                    }

                    if (item != null) {
                        progressBar.setProgress(item / 100);
                        setGraphic(progressBar);
                    } else {
                        setGraphic(null);
                    }
                }
            };

            percentageCompleteVisualColumn.setCellFactory(percentageVisualCellFactory);

            columns.add(percentageCompleteColumn);
            columns.add(percentageCompleteVisualColumn);
            columns.add(startColumn);
            columns.add(finishColumn);
        }

        getTreeTable().getColumns().setAll(columns);
        getTreeTable().setTreeColumn(nameColumn);

        getGraphics().setRowEditorFactory(param -> new MSProjectTaskDetails(param.getGraphics(), param.getRow()));
    }

    public final void load(String fileName, InputStream stream) {
        try {
            ProjectReader reader = ProjectReaderUtility.getProjectReader(fileName);
            ProjectFile projectFile = reader.read(stream);

            MSProjectGanttChartModel model = new MSProjectGanttChartModel(projectFile);

            setRoot(model.getRoot());
            getLayers().setAll(model.getLayers());
            model.getLinks().forEach(link -> getLinks().add(link));

            model.getRoot().setExpanded(true);

            Timeline timeline = getTimeline();
            TimelineModel<?> timelineModel = timeline.getModel();
            timelineModel.setHorizonStartTime(ZonedDateTime.ofInstant(projectFile.getStartDate().toInstant(), ZoneId.systemDefault()).minusDays(2).toInstant());
            timelineModel.setHorizonEndTime(ZonedDateTime.ofInstant(projectFile.getFinishDate().toInstant(), ZoneId.systemDefault()).plusDays(2).toInstant());
            timelineModel.setStartTime(timelineModel.getHorizonStartTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public final void load(File file) throws FileNotFoundException {
        load(file.getName(), new FileInputStream(file));
    }
}
