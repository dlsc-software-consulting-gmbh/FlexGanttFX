/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.msproject.view;

import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.msproject.model.MSProjectGanttChartModel;
import com.flexganttfx.msproject.model.MSProjectTaskActivity;
import com.flexganttfx.msproject.model.MSProjectTaskRow;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.timeline.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.ProjectReaderUtility;

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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;

public class MSProjectGraphicsChartLite extends GanttChartLite<MSProjectTaskRow> {

	public MSProjectGraphicsChartLite() {
		super();

		// setFixedCellSize(Row.DEFAULT_ROW_HEIGHT);

		getStylesheets().add(
				MSProjectGraphicsChartLite.class.getResource("msproject.css")
						.toExternalForm());

		getGraphics().setActivityRenderer(MSProjectTaskActivity.class,
				GanttLayout.class,
				new MSProjectTaskActivityRenderer(getGraphics()));

		List<TreeTableColumn<MSProjectTaskRow, ?>> columns = new ArrayList<TreeTableColumn<MSProjectTaskRow, ?>>();

		TreeTableColumn<MSProjectTaskRow, String> nameColumn = new TreeTableColumn<MSProjectTaskRow, String>(
				"Name");
		TreeTableColumn<MSProjectTaskRow, Instant> startColumn = new TreeTableColumn<MSProjectTaskRow, Instant>(
				"Start");
		TreeTableColumn<MSProjectTaskRow, Instant> finishColumn = new TreeTableColumn<MSProjectTaskRow, Instant>(
				"Finish");
		TreeTableColumn<MSProjectTaskRow, Double> percentageCompleteColumn = new TreeTableColumn<MSProjectTaskRow, Double>(
				"%");
		TreeTableColumn<MSProjectTaskRow, Double> percentageCompleteVisualColumn = new TreeTableColumn<MSProjectTaskRow, Double>(
				"Complete");

		nameColumn.setPrefWidth(250);
		startColumn.setPrefWidth(100);
		finishColumn.setPrefWidth(100);
		percentageCompleteColumn.setPrefWidth(40);
		percentageCompleteVisualColumn.setPrefWidth(100);

		nameColumn
				.setCellValueFactory(new TreeItemPropertyValueFactory<MSProjectTaskRow, String>(
						"name"));
		startColumn
				.setCellValueFactory(new TreeItemPropertyValueFactory<MSProjectTaskRow, Instant>(
						"startTime"));
		finishColumn
				.setCellValueFactory(new TreeItemPropertyValueFactory<MSProjectTaskRow, Instant>(
						"finishTime"));
		percentageCompleteColumn
				.setCellValueFactory(new TreeItemPropertyValueFactory<MSProjectTaskRow, Double>(
						"percentageComplete"));

		percentageCompleteVisualColumn
				.setCellValueFactory(new TreeItemPropertyValueFactory<MSProjectTaskRow, Double>(
						"percentageComplete"));

		Callback<TreeTableColumn<MSProjectTaskRow, String>, TreeTableCell<MSProjectTaskRow, String>> nameCellFactory = param -> new TreeTableCell<MSProjectTaskRow, String>() {
			private Label parentImage;
			private Label childImage;

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);

				if (parentImage == null) {
					parentImage = new Label();
					parentImage.setContentDisplay(GRAPHIC_ONLY);
					parentImage.getStyleClass().add("parent-task-image");

					childImage = new Label();
					childImage.setContentDisplay(GRAPHIC_ONLY);
					childImage.getStyleClass().add("child-task-image");
				}

				if (item == null) {
					setText(null);
					setGraphic(null);
					return;
				}

				setText(item);

				MSProjectTaskRow row = getTreeTableRow().getItem();
				if (row != null && row.getChildren().size() > 0) {
					setGraphic(parentImage);
				} else {
					setGraphic(childImage);
				}
			}
		};

		nameColumn.setCellFactory(nameCellFactory);

		Callback<TreeTableColumn<MSProjectTaskRow, Instant>, TreeTableCell<MSProjectTaskRow, Instant>> dateTimeCellFactory = new Callback<TreeTableColumn<MSProjectTaskRow, Instant>, TreeTableCell<MSProjectTaskRow, Instant>>() {
			private DateTimeFormatter formatter = DateTimeFormatter
					.ofLocalizedDateTime(FormatStyle.SHORT);

			@Override
			public TreeTableCell<MSProjectTaskRow, Instant> call(
					TreeTableColumn<MSProjectTaskRow, Instant> param) {
				return new TreeTableCell<MSProjectTaskRow, Instant>() {
					@Override
					protected void updateItem(Instant item, boolean empty) {
						if (item != null) {
							setText(formatter.format(ZonedDateTime.ofInstant(
									item, ZoneId.systemDefault())));
						} else {
							setText(null);
						}
					}
				};
			}
		};

		startColumn.setCellFactory(dateTimeCellFactory);
		finishColumn.setCellFactory(dateTimeCellFactory);

		Callback<TreeTableColumn<MSProjectTaskRow, Double>, TreeTableCell<MSProjectTaskRow, Double>> percentageCellFactory = new Callback<TreeTableColumn<MSProjectTaskRow, Double>, TreeTableCell<MSProjectTaskRow, Double>>() {
			private NumberFormat formatter = DecimalFormat.getPercentInstance();

			@Override
			public TreeTableCell<MSProjectTaskRow, Double> call(
					TreeTableColumn<MSProjectTaskRow, Double> param) {
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

		percentageCompleteVisualColumn
				.setCellFactory(percentageVisualCellFactory);

		columns.add(nameColumn);
		columns.add(percentageCompleteColumn);
		columns.add(percentageCompleteVisualColumn);
		columns.add(startColumn);
		columns.add(finishColumn);

		getGraphics().setRowEditorFactory(
				param -> new MSProjectTaskDetails(param.getGraphics(),
						param.getRow()));
	}

	public final void load(String fileName, InputStream stream) {
		try {
			ProjectReader reader = ProjectReaderUtility
					.getProjectReader(fileName);
			ProjectFile projectFile = reader.read(stream);

			MSProjectGanttChartModel model = new MSProjectGanttChartModel(
					projectFile);

			getLayers().setAll(model.getLayers());
			model.getLinks().forEach(link -> getLinks().add(link));

			Timeline timeline = getTimeline();
			timeline.showTime(projectFile.getStartDate().toInstant().minus(3, ChronoUnit.DAYS));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public final void load(File file) throws FileNotFoundException {
		load(file.getName(), new FileInputStream(file));
	}
}
