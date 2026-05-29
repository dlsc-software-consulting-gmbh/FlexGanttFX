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
        ganttPane.setTop(toolbar);
        ganttPane.setCenter(ganttChart);
        ganttPane.setBottom(statusbar);

        BorderPane.setMargin(ganttChart, new Insets(0));

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
