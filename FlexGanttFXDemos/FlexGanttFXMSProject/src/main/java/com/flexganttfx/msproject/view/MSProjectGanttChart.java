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
import javafx.application.Platform;
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
import java.util.Objects;

import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;

public class MSProjectGanttChart extends GanttChart<MSProjectTaskRow> {

    public MSProjectGanttChart() {
        super();

        setFixedCellSize(Row.DEFAULT_ROW_HEIGHT);
        setScrollBarType(ScrollBarType.FIXED_HORIZON);
        setAutoHideScrollBar(false);

        getTreeTableMasterDetailPane().setDividerPosition(.15);

        getStylesheets().add(Objects.requireNonNull(MSProjectGanttChart.class.getResource("msproject.css")).toExternalForm());

        getGraphics().setActivityRenderer(MSProjectTaskActivity.class, GanttLayout.class, new MSProjectTaskActivityRenderer(getGraphics()));

        getTreeTable().setShowRoot(false);

        List<TreeTableColumn<MSProjectTaskRow, ?>> columns = new ArrayList<>();

        TreeTableColumn<MSProjectTaskRow, String> nameColumn = new TreeTableColumn<>("Name");
        nameColumn.setPrefWidth(250);
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));

        Callback<TreeTableColumn<MSProjectTaskRow, String>, TreeTableCell<MSProjectTaskRow, String>> nameCellFactory = param -> new TreeTableCell<>() {

            private final FontIcon parentImage = new FontIcon(MaterialDesign.MDI_CHECKBOX_MULTIPLE_MARKED);
            private final FontIcon childImage = new FontIcon(MaterialDesign.MDI_CHECKBOX_MARKED);

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
                if (row != null && !row.getChildren().isEmpty()) {
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

            private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

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

        TreeTableColumn<MSProjectTaskRow, Instant> startColumn = new TreeTableColumn<>("Start");
        TreeTableColumn<MSProjectTaskRow, Instant> finishColumn = new TreeTableColumn<>("Finish");
        TreeTableColumn<MSProjectTaskRow, Double> percentageCompleteColumn = new TreeTableColumn<>("%");
        TreeTableColumn<MSProjectTaskRow, Double> percentageCompleteVisualColumn = new TreeTableColumn<>("Complete");

        startColumn.setPrefWidth(120);
        finishColumn.setPrefWidth(120);
        percentageCompleteColumn.setPrefWidth(50);
        percentageCompleteVisualColumn.setPrefWidth(120);

        startColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("startTime"));
        finishColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("finishTime"));
        percentageCompleteColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("percentageComplete"));

        percentageCompleteVisualColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("percentageComplete"));

        startColumn.setCellFactory(dateTimeCellFactory);
        finishColumn.setCellFactory(dateTimeCellFactory);

        Callback<TreeTableColumn<MSProjectTaskRow, Double>, TreeTableCell<MSProjectTaskRow, Double>> percentageCellFactory = new Callback<TreeTableColumn<MSProjectTaskRow, Double>, TreeTableCell<MSProjectTaskRow, Double>>() {
            private final NumberFormat formatter = DecimalFormat.getPercentInstance();

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

        getTreeTable().getColumns().setAll(columns);
        getTreeTable().setTreeColumn(nameColumn);
    }

    public final void load(ProjectFile projectFile) {
        try {
            MSProjectGanttChartModel model = new MSProjectGanttChartModel(projectFile);

            setRoot(model.getRoot());
            getLayers().setAll(model.getLayers());
            getLinks().clear();
            model.getLinks().forEach(link -> getLinks().add(link));

            model.getRoot().setExpanded(true);

            Timeline timeline = getTimeline();
            TimelineModel<?> timelineModel = timeline.getModel();

            // Null-safe: fall back to a sensible window when dates are not set
            java.time.Instant horizonStart = projectFile.getStartDate() != null
                    ? ZonedDateTime.ofInstant(projectFile.getStartDate().toInstant(), ZoneId.systemDefault()).minusDays(2).toInstant()
                    : java.time.Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS);
            java.time.Instant horizonEnd = projectFile.getFinishDate() != null
                    ? ZonedDateTime.ofInstant(projectFile.getFinishDate().toInstant(), ZoneId.systemDefault()).plusDays(2).toInstant()
                    : java.time.Instant.now().plus(90, java.time.temporal.ChronoUnit.DAYS);

            timelineModel.setHorizonStartTime(horizonStart);
            timelineModel.setHorizonEndTime(horizonEnd);
            timelineModel.setStartTime(horizonStart);

            Platform.runLater(() -> getGraphics().showAllActivities());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
