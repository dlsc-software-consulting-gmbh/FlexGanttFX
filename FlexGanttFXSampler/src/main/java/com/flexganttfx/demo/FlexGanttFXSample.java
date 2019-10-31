/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.timeline.DatelineScrollingEvent;
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
	private DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
	private boolean visible = true;
	private BorderPane ganttPane;

	protected FlexGanttFXSample() {
	}

	public final void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public final boolean isVisible() {
		return visible;
	}

	@Override
	public final Node getPanel(Stage stage) {
		if (ganttPane != null) {
			return ganttPane;
		}

		try {
			ganttChart = createGanttChart();

			ganttChart.getTimeline().getDateline().addEventHandler(DatelineScrollingEvent.ANY_SCROLLING, evt -> {
				ZonedDateTime st = ZonedDateTime.ofInstant(evt.getStartTime(), ZoneId.systemDefault());
				ZonedDateTime et = ZonedDateTime.ofInstant(evt.getEndTime(), ZoneId.systemDefault());
				getStatusbar().setText(formatter.format(st) + " - " + formatter.format(et));
			});
		} catch (Exception e) {
			e.printStackTrace();
			visible = false;
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

	@Override
	public final String getProjectName() {
		return "FlexGanttFX";
	}

	@Override
	public final String getProjectVersion() {
		return FlexGanttFX.getVersion();
	}
}
