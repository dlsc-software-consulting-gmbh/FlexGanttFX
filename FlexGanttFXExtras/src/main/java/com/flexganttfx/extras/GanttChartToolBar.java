/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras;

import com.flexganttfx.extras.util.Messages;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Timeline.ZoomMode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.text.MessageFormat;

import static java.util.Objects.requireNonNull;
import static org.controlsfx.control.PopOver.ArrowLocation.TOP_CENTER;

/**
 * A toolbar implementation that can be used in combination with the Gantt chart
 * control. Please note that this toolbar is used for rapid prototyping and does
 * not present a feature-complete implementation that could be used for any kind
 * of application. An entire framework could be written just for that purpose.
 *
 * @param <R>
 *            the type of the rows in the Gantt chart
 *
 * @since 1.0
 */
public class GanttChartToolBar<R extends Row<?, ?, ?>> extends ToolBar {

	/**
	 * Constructs a new toolbar control. The Gantt chart has to be set later by
	 * calling {@link #setGanttChart(GanttChartBase)}.
	 *
	 * @since 1.0
	 */
	public GanttChartToolBar() {
		setOrientation(Orientation.HORIZONTAL);
		getStylesheets().add(GanttChartToolBar.class.getResource("toolbar.css").toExternalForm());
		ganttChartProperty().addListener(observable -> buildToolBar());
	}

	/**
	 * Constructs a new toolbar control.
	 *
	 * @param ganttChart the Gantt chart for which the toolbar will be used
	 * @since 1.0
	 */
	public GanttChartToolBar(GanttChartBase<R> ganttChart) {
		this();
		setGanttChart(ganttChart);
	}

	private final ObjectProperty<GanttChartBase<R>> ganttChart = new SimpleObjectProperty<>(this, "ganttChart");

	/**
	 * A property used to store the reference to the Gantt chart that will be
	 * watched by this status bar.
	 *
	 * @return the Gantt chart property
	 * @since 1.0
	 */
	public final ObjectProperty<GanttChartBase<R>> ganttChartProperty() {
		return ganttChart;
	}

	/**
	 * Returns the value of {@link #ganttChartProperty()}.
	 *
	 * @return the property used for the Gantt chart reference
	 * @since 1.0
	 */
	public final GanttChartBase<R> getGanttChart() {
		return ganttChartProperty().get();
	}

	/**
	 * Sets the value of {@link #ganttChartProperty()}.
	 *
	 * @param ganttChart
	 *            the Gantt chart
	 * @since 1.0
	 */
	public final void setGanttChart(GanttChartBase<R> ganttChart) {
		requireNonNull(ganttChart);
		ganttChartProperty().set(ganttChart);
	}

	private void buildToolBar() {
		getItems().clear();

		if (layerControlsPopOver != null) {
			layerControlsPopOver.hide();
			layerControlsPopOver = null;
		}

		GanttChartBase<R> ganttChart = getGanttChart();

		if (ganttChart != null) {

			Button timeNow = new Button(Messages.getString("GanttChartToolBar.BUTTON_NOW"));
			timeNow.setGraphic(new FontIcon(MaterialDesign.MDI_DEBUG_STEP_INTO));
			timeNow.setOnAction(showTimeNow());
			getItems().add(timeNow);

			Button earliest = new Button(Messages.getString("GanttChartToolBar.BUTTON_EARLIEST"));
			earliest.setGraphic(new FontIcon(MaterialDesign.MDI_PAGE_FIRST));
			earliest.setOnAction(showEarliestActivities());
			getItems().add(earliest);

			Button latest = new Button(Messages.getString("GanttChartToolBar.BUTTON_LATEST"));
			latest.setGraphic(new FontIcon(MaterialDesign.MDI_PAGE_LAST));
			latest.setOnAction(showLatestActivities());
			getItems().add(latest);

			Button showAll = new Button(Messages.getString("GanttChartToolBar.BUTTON_ALL"));
			showAll.setGraphic(new FontIcon(MaterialDesign.MDI_ARROW_COMPRESS_ALL));
			showAll.setOnAction(showAllActivities());
			getItems().add(showAll);

			getItems().add(new Separator());

			Button zoomIn = new Button(Messages.getString("GanttChartToolBar.BUTTON_ZOOM_IN"));
			zoomIn.setGraphic(new FontIcon(MaterialDesign.MDI_MAGNIFY_PLUS));
			zoomIn.setOnAction(zoomIn());
			getItems().add(zoomIn);

			Button zoomOut = new Button(Messages.getString("GanttChartToolBar.BUTTON_ZOOM_OUT"));
			zoomOut.setGraphic(new FontIcon(MaterialDesign.MDI_MAGNIFY_MINUS));
			zoomOut.setOnAction(zoomOut());
			getItems().add(zoomOut);

			ComboBox<ZoomMode> zoomModeBox = new ComboBox<>();
			zoomModeBox.setConverter(new StringConverter<>() {
				@Override
				public String toString(ZoomMode mode) {
					switch (mode) {
						case CENTER:
						default:
							return "Keep Center";
						case KEEP_START_TIME:
							return "Keep Start";
						case KEEP_END_TIME:
							return "Keep End";
					}
				}

				@Override
				public ZoomMode fromString(String string) {
					return null;
				}
			});
			zoomModeBox.getItems().setAll(ZoomMode.values());
			zoomModeBox.valueProperty().bindBidirectional(getGanttChart().getTimeline().zoomModeProperty());
			getItems().add(zoomModeBox);

			getItems().add(new Separator());

			ToggleButton links = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_LINKS"));
			links.setGraphic(new FontIcon(MaterialDesign.MDI_VECTOR_LINE));
			links.selectedProperty().bindBidirectional(ganttChart.getGraphics().showLinksProperty());
			getItems().add(links);

			ToggleButton headers = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_SCALE"));
			headers.setGraphic(new FontIcon(MaterialDesign.MDI_RULER));
			headers.selectedProperty().bindBidirectional(ganttChart.getGraphics().showRowHeadersProperty());
			getItems().add(headers);

			Button layers = new Button(Messages.getString("GanttChartToolBar.BUTTON_LAYERS"));
			layers.setGraphic(new FontIcon(MaterialDesign.MDI_LAYERS));
			layers.setOnAction(showLayerControls(layers));
			getItems().add(layers);

			Button radar = new Button(Messages.getString("GanttChartToolBar.BUTTON_RADAR"));
			radar.setGraphic(new FontIcon(MaterialDesign.MDI_RADAR));
			radar.setOnAction(showRadarPopOver(radar));
			getItems().add(radar);

			if (ganttChart instanceof GanttChart) {
				ToggleButton table = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_TABLE"));
				table.setGraphic(new FontIcon(MaterialDesign.MDI_TABLE));
				table.selectedProperty().bindBidirectional(((GanttChart)ganttChart).showTreeTableProperty());
				getItems().add(table);
			}

			getItems().add(new Separator());

			ListViewGraphics<R> graphics = ganttChart.getGraphics();

			ToggleButton cursor = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_CURSOR"));
			cursor.setGraphic(new FontIcon(MaterialDesign.MDI_CURSOR_TEXT));
			cursor.selectedProperty().bindBidirectional(graphics.showVerticalCursorProperty());
			getItems().add(cursor);

			MenuButton gridLines = new MenuButton(Messages.getString("GanttChartToolBar.BUTTON_GRID"));
			gridLines.setGraphic(new FontIcon(MaterialDesign.MDI_GRID));

			MenuItem gridOff = new MenuItem(Messages.getString("GanttChartToolBar.MENU_ITEM_GRID_OFF"));
			gridOff.setGraphic(new FontIcon(MaterialDesign.MDI_GRID_OFF));
			gridOff.setOnAction(hideGridLines());
			gridLines.getItems().add(gridOff);

			for (int i = 1; i <= 2; i++) {
				MenuItem gridOn = new MenuItem(MessageFormat.format(Messages.getString("GanttChartToolBar.MENU_ITEM_GRID_LEVELS"), i));
				gridLines.getItems().add(gridOn);
				gridOn.setOnAction(showGridLines(i));
			}

			getItems().add(gridLines);

			ToggleButton calendars = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_CALENDARS"));
			calendars.setGraphic(new FontIcon(MaterialDesign.MDI_CALENDAR));
			calendars.selectedProperty().bindBidirectional(graphics.showCalendarLayerProperty());
			getItems().add(calendars);

			ToggleButton nowLine = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_NOW_LINE"));
			nowLine.setGraphic(new FontIcon(MaterialDesign.MDI_CLOCK));
			nowLine.selectedProperty().bindBidirectional(graphics.showNowLineLayerProperty());
			getItems().add(nowLine);

			ToggleButton detail = new ToggleButton(Messages.getString("GanttChartToolBar.BUTTON_DETAIL"));
			detail.setGraphic(new FontIcon(MaterialDesign.MDI_BOOK_OPEN));
			detail.selectedProperty().bindBidirectional(ganttChart.showDetailProperty());
			getItems().add(detail);

			getItems().add(new Separator());

			CustomTextField filterField = new CustomTextField();
			filterField.getStyleClass().add("search-field");
			filterField.setPromptText("Filter");
			filterField.textProperty().addListener(it -> filter(filterField.getText()));
			getItems().add(filterField);
		}
	}

	private void filter(String txt) {
		if (txt.trim().equals("")) {
			// intentional null to see if missing filter crashes anything
			getGanttChart().setRowFilter(null);
		} else {
			getGanttChart().setRowFilter(row -> row.getName().toLowerCase().contains(txt.toLowerCase()));
		}
	}

	private PopOver layerControlsPopOver;

	private EventHandler<ActionEvent> showLayerControls(Button button) {
		return evt -> {
			if (layerControlsPopOver == null) {
				LayersView<R> layersView = new LayersView<>();
				layersView.setGraphics(getGanttChart().getGraphics());
				layerControlsPopOver = new PopOver(layersView);
				layerControlsPopOver.setTitle(Messages.getString("GanttChartToolBar.BUTTON_LAYERS"));
				layerControlsPopOver.setArrowLocation(TOP_CENTER);
			}

			Point2D localToScreen = button.localToScreen(0, 0);
			layerControlsPopOver.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
			layerControlsPopOver.show(button, localToScreen.getX() + button.getWidth() / 2, localToScreen.getY() + button.getHeight() - 2);
		};
	}

	private PopOver radarPopOver;

	private EventHandler<ActionEvent> showRadarPopOver(Button button) {
		return evt -> {
			if (radarPopOver == null) {
				RadarView<R> radarView = new RadarView<>();
				radarView.setGraphics(getGanttChart().getGraphics());
				radarPopOver = new PopOver(radarView);
				radarPopOver.setTitle(Messages.getString("GanttChartToolBar.TITLE_RADAR"));
				radarPopOver.setArrowLocation(TOP_CENTER);
			}

			Point2D localToScreen = button.localToScreen(0, 0);
			radarPopOver.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
			radarPopOver.show(button, localToScreen.getX() + button.getWidth() / 2, localToScreen.getY() + button.getHeight() - 2);
		};
	}

	private EventHandler<ActionEvent> showGridLines(final int level) {
		return evt -> {
			getGanttChart().getGraphics().setMaxGridLevel(level);
			getGanttChart().getGraphics().setShowGridLineLayer(true);
		};
	}

	private EventHandler<ActionEvent> hideGridLines() {
		return evt -> getGanttChart().getGraphics().setShowGridLineLayer(false);
	}

	private EventHandler<ActionEvent> zoomOut() {
		return evt -> getGanttChart().getMasterTimeline().zoomOut();
	}

	private EventHandler<ActionEvent> zoomIn() {
		return evt -> getGanttChart().getMasterTimeline().zoomIn();
	}

	private EventHandler<ActionEvent> showAllActivities() {
		return evt -> getGanttChart().getGraphics().showAllActivities();
	}

	private EventHandler<ActionEvent> showLatestActivities() {
		return evt -> getGanttChart().getGraphics().showLatestActivities();
	}

	private EventHandler<ActionEvent> showEarliestActivities() {
		return evt -> getGanttChart().getGraphics().showEarliestActivities();
	}

	private EventHandler<ActionEvent> showTimeNow() {
		return evt -> getGanttChart().getMasterTimeline().showNow();
	}
}
