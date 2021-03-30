/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.GanttChartBase;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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
        BorderPane.setMargin(ganttChart, new Insets(10));
        ganttPane.setTop(toolbar);
        ganttPane.setCenter(ganttChart);
        ganttPane.setBottom(statusbar);

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
