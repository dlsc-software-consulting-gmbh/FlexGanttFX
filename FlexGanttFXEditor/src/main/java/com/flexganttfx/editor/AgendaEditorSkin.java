/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import com.flexganttfx.extras.VirtualGridControl;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.dateline.ChronoUnitGrid;
import com.flexganttfx.model.dateline.ChronoUnitResolution;
import com.flexganttfx.model.dateline.DatelineModel;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.layout.AgendaLayout.LayoutStrategy;
import com.flexganttfx.model.repository.MutableActivityRepository;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.LassoSelectionBehaviour;
import com.flexganttfx.view.graphics.layer.LayoutLayer;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.StatusBar;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.controlsfx.control.PopOver.ArrowLocation.BOTTOM_CENTER;

public class AgendaEditorSkin extends SkinBase<AgendaEditor> {

	private static final double INITIAL_DAY_WIDTH = 100;

	private Layer layer = new Layer("Default");
	private Label gridLabel;
	private GraphicsBase<AgendaRow> graphics;
	private AgendaRow agendaRow;
	private Label lassoLabel;

	private AgendaConflictResolver<AgendaRow> conflictResolver;

	/**
	 * Constructs a new editor skin.
	 *
	 * @param editor
	 *            the editor for which to create a skin
	 */
	@SuppressWarnings("unchecked")
	public AgendaEditorSkin(AgendaEditor editor) {
		super(editor);

		this.conflictResolver = new AgendaConflictResolver<>(editor);

		Timeline timeline = new Timeline();
		timeline.getStylesheets().add(
				AgendaEditorApp.class.getResource("editor.css")
						.toExternalForm());

		timeline.getEventline().setVisible(false);
		timeline.showTemporalUnit(DAYS, INITIAL_DAY_WIDTH);
		Instant startTime = getHorizonStartTime();
		timeline.getModel().setHorizonStartTime(startTime);

		Dateline dateline = timeline.getDateline();
		timeline.showTime(startTime);

		DatelineModel<ChronoUnit> datelineModel = (DatelineModel<ChronoUnit>) dateline
				.getModel();
		datelineModel.clearResolutions(DAYS);
		ChronoUnitResolution res = new ChronoUnitResolution(DAYS, "D", 1);
		datelineModel.addResolution(res);
		datelineModel.setScaleCount(1);

		graphics = editor.getGraphics();
		graphics.setTimeline(timeline);
		graphics.getLayers().add(layer);
		graphics.setShowMarkedTimeInterval(false);
		graphics.setContextMenuCallback(new AgendaEditorContextMenu(graphics));
		graphics.setVirtualGrid(new ChronoUnitGrid("15 Minutes", MINUTES, 15));
		graphics.setAutoGridEnabled(true);
		graphics.setLassoSnapsToGrid(false);
		graphics.setLassoSelectionBehaviour(LassoSelectionBehaviour.INTERSECTION);

		AgendaEntryRenderer renderer = new AgendaEntryRenderer(graphics);
		graphics.setActivityRenderer(AgendaEntryBase.class, AgendaLayout.class,
				renderer);
		graphics.getSystemLayer(LayoutLayer.class).setPaddingFill(
				Color.ALICEBLUE);

		List<AgendaRow> rows = new ArrayList<>();
		agendaRow = getSkinnable().getAgendaRow();
		rows.add(agendaRow);

		graphics.getRows().setAll(rows);

		/*
		 * The controller is used for all interaction aspects. Mouse events,
		 * dragging, key pressed, etc...
		 */
		AgendaController<AgendaRow> controller = new AgendaController<>(editor,
				layer);

		/*
		 * A specialized background layer for drawing a cursor and the paste
		 * locations for copy / paste commands.
		 */
		AgendaEditorBackgroundLayer<AgendaRow> backgroundLayer = new AgendaEditorBackgroundLayer<>(
				getSkinnable(), controller);
		backgroundLayer.showPasteLocationsProperty().bind(
				getSkinnable().showPasteLocationsProperty());

		graphics.getBackgroundSystemLayers().add(backgroundLayer);

		Region corner = new Region();
		corner.getStyleClass().addAll("corner-stone");

		StackPane scaleWrapper = new StackPane();
		scaleWrapper.setMinSize(0, 0);
		scaleWrapper.setPrefHeight(0);
		scaleWrapper.getStyleClass().addAll("corner-stone");

		AgendaLayout agendaLayout = getSkinnable().getAgendaLayout();
		ScaleView scale = new ScaleView(agendaLayout);
		scale.heightProperty().bind(scaleWrapper.heightProperty());
		scaleWrapper.getChildren().add(scale);

		StatusBar statusBar = createStatusBar();

		ToolBar toolBar = new ToolBar();

		Button fixIt = new Button("Fix It");
		fixIt.setOnAction(evt -> conflictResolver
				.fixScheduleAfterEndTimeChange(true));
		toolBar.getItems().add(fixIt);

		Label startTimeLabel = new Label("Start Time:");

		ComboBox<LocalTime> startTimeBox = new ComboBox<>();
		for (int hour = 0; hour < 12; hour++) {
			startTimeBox.getItems().add(LocalTime.of(hour, 0));
		}

		Bindings.bindBidirectional(startTimeBox.valueProperty(),
				agendaLayout.startTimeProperty());

		Label endTimeLabel = new Label("End Time:");

		ComboBox<LocalTime> endTimeBox = new ComboBox<>();
		for (int hour = 12; hour < 24; hour++) {
			endTimeBox.getItems().add(LocalTime.of(hour, 59));
		}

		Bindings.bindBidirectional(endTimeBox.valueProperty(),
				agendaLayout.endTimeProperty());

		toolBar.getItems().addAll(startTimeLabel, startTimeBox, endTimeLabel,
				endTimeBox);

		ToggleButton overlapping = new ToggleButton("Overlapping");
		Bindings.bindBidirectional(overlapping.selectedProperty(),
				getSkinnable().allowOverlappingProperty());
		toolBar.getItems().add(overlapping);

		ToggleButton pasteLocations = new ToggleButton("Paste Locations");
		Bindings.bindBidirectional(pasteLocations.selectedProperty(),
				getSkinnable().showPasteLocationsProperty());
		toolBar.getItems().add(pasteLocations);

		toolBar.getItems().add(new Separator());

		ToggleButton reflection = new ToggleButton("Reflections");
		Bindings.bindBidirectional(reflection.selectedProperty(),
				renderer.showReflectionsProperty());
		toolBar.getItems().add(reflection);

		ToggleButton debugInfo = new ToggleButton("Info");
		Bindings.bindBidirectional(debugInfo.selectedProperty(),
				renderer.showDebugInfoProperty());
		toolBar.getItems().add(debugInfo);

		ToggleButton restore = new ToggleButton("Restore");
		Bindings.bindBidirectional(restore.selectedProperty(), getSkinnable()
				.restoreProperty());
		toolBar.getItems().add(restore);

		ToggleButton icons = new ToggleButton("Icons");
		Bindings.bindBidirectional(icons.selectedProperty(),
				renderer.showIconsProperty());
		toolBar.getItems().add(icons);

		toolBar.getItems().add(new Separator());

		// Button debug = new Button("Debug");
		// debug.setOnAction(evt -> ScenicView.show(getSkinnable().getScene()));
		// toolBar.getItems().add(debug);

		Button fixHorizon = new Button("Pack");
		fixHorizon.setOnAction(evt -> fixHorizon(true));
		toolBar.getItems().add(fixHorizon);

		toolBar.getItems().add(new Separator());

		ComboBox<LayoutStrategy> strategyBox = new ComboBox<>();
		strategyBox.getItems().addAll(LayoutStrategy.values());
		strategyBox.setValue(agendaLayout.getLayoutStrategy());
		strategyBox.valueProperty().addListener(evt -> graphics.redraw());
		agendaLayout.layoutStrategyProperty().bind(
				strategyBox.valueProperty());
		toolBar.getItems().add(strategyBox);

		toolBar.getItems().add(new Separator());

		toolBar.getItems().add(new Label("Zoom:"));

		Slider slider = new Slider(50, 400, INITIAL_DAY_WIDTH);
		slider.valueProperty().addListener(
				evt -> timeline.showTemporalUnit(ChronoUnit.DAYS,
						slider.getValue()));
		toolBar.getItems().add(slider);

		toolBar.getItems().add(new Label("Delay:"));

		Slider delaySlider = new Slider(0, 1000, getSkinnable()
				.getChangeDelay());
		delaySlider.valueProperty().addListener(
				evt -> getSkinnable().setChangeDelay(
						(long) delaySlider.getValue()));
		toolBar.getItems().add(delaySlider);

		Label mintLogo = new Label();
		mintLogo.getStyleClass().add("mint-logo");
		BorderPane.setAlignment(mintLogo, Pos.CENTER_LEFT);

		Label fedexLogo = new Label();
		fedexLogo.setAlignment(Pos.CENTER_RIGHT);
		fedexLogo.getStyleClass().add("fedex-logo");
		BorderPane.setAlignment(fedexLogo, Pos.CENTER_RIGHT);

		BorderPane header = new BorderPane();
		header.getStyleClass().add("header");
		header.setLeft(mintLogo);
		header.setCenter(fedexLogo);

		GridPane gridPane = new GridPane();
		gridPane.setGridLinesVisible(true);
		gridPane.setAlignment(Pos.CENTER);
		gridPane.add(header, 0, 0);
		gridPane.add(toolBar, 0, 1);
		gridPane.add(corner, 0, 2);
		gridPane.add(scaleWrapper, 0, 3);
		gridPane.add(timeline, 1, 2);
		gridPane.add(graphics, 1, 3);
		gridPane.add(statusBar, 0, 4);

		GridPane.setColumnSpan(header, 2);
		GridPane.setColumnSpan(toolBar, 2);
		GridPane.setColumnSpan(statusBar, 2);

		GridPane.setFillHeight(corner, true);
		GridPane.setFillWidth(corner, true);

		GridPane.setFillHeight(scaleWrapper, true);
		GridPane.setFillWidth(scaleWrapper, true);

		GridPane.setFillWidth(timeline, true);
		GridPane.setFillHeight(graphics, true);
		GridPane.setFillWidth(graphics, true);

		GridPane.setHgrow(timeline, Priority.ALWAYS);
		GridPane.setVgrow(timeline, Priority.NEVER);
		GridPane.setHgrow(graphics, Priority.ALWAYS);
		GridPane.setVgrow(graphics, Priority.ALWAYS);

		graphics.getCalendars().clear();

		getChildren().add(gridPane);

		graphics.addEventHandler(ActivityEvent.START_TIME_CHANGE_FINISHED,
				evt -> fixHorizon(false));
		graphics.addEventHandler(ActivityEvent.END_TIME_CHANGE_FINISHED,
				evt -> fixHorizon(false));
		graphics.addEventHandler(ActivityEvent.HORIZONTAL_DRAG_FINISHED,
				evt -> fixHorizon(false));
	}

	private StatusBar createStatusBar() {
		StatusBar statusBar = new StatusBar();

		// autogrid button
		ToggleButton autogrid = new ToggleButton("Autogrid");
		Bindings.bindBidirectional(autogrid.selectedProperty(), getSkinnable()
				.getGraphics().autoGridEnabledProperty());
		statusBar.getRightItems().add(autogrid);

		// grid label
		gridLabel = new Label();
		gridLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		gridLabel.getStyleClass().add("statusbar-grid-label");
		statusBar.getRightItems().add(gridLabel);
		graphics.virtualGridProperty().addListener(evt -> updateGridLabel());
		updateGridLabel();

		gridLabel.setOnMouseClicked(evt -> showGridPopOver(gridLabel));

		// separator
		statusBar.getRightItems().add(new Separator(Orientation.VERTICAL));

		// lasso label
		lassoLabel = new Label();
		lassoLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		lassoLabel.getStyleClass().add("statusbar-lasso-label");
		graphics.lassoActiveProperty().addListener(evt -> {
			updateLassoLabel();
		});
		updateLassoLabel();

		statusBar.getRightItems().add(lassoLabel);
		return statusBar;
	}

	private void updateLassoLabel() {
		if (graphics.isLassoActive()) {
			lassoLabel.setText("Lasso: ON");
		} else {
			lassoLabel.setText("Lasso: OFF");
		}
	}

	/**
	 * Fixes the horizon. This can either be necessary after the user has
	 * dragged entries to the left of the first day or when the user has
	 * explicitly requested that all entries are moved to the left so that the
	 * agenda starts on day one.
	 *
	 * @param pack
	 *            a flag used to make the agenda start on day one
	 */
	private void fixHorizon(boolean pack) {
		LocalDate horizonStartDate = LocalDate.from(ZonedDateTime.ofInstant(
				getHorizonStartTime(), agendaRow.getZoneId()));

		LocalDate date = null;

		MutableActivityRepository<AgendaEntry> repository = (MutableActivityRepository<AgendaEntry>) agendaRow
				.getRepository();
		Instant st = repository.getEarliestTimeUsed();
		Instant et = repository.getLatestTimeUsed();

		if (st == null || et == null) {
			return;
		}

		// Calculate the earliest date used.
		Iterator<AgendaEntry> activities = repository.getActivities(layer, st,
				et, ChronoUnit.DAYS, agendaRow.getZoneId());
		while (activities.hasNext()) {
			AgendaEntry entry = activities.next();
			LocalDate startDate = LocalDate.from(ZonedDateTime.ofInstant(
					entry.getStartTime(), agendaRow.getZoneId()));
			if (date == null || startDate.isBefore(date)) {
				date = startDate;
			}
		}

		if (date == null) {
		    return;
		}

		long deltaDays = date.until(horizonStartDate, ChronoUnit.DAYS);
		if (deltaDays > 0) {
			activities = repository.getActivities(layer,
					st.minus(Duration.ofDays(deltaDays)), et, ChronoUnit.DAYS,
					agendaRow.getZoneId());
			while (activities.hasNext()) {
				AgendaEntry entry = activities.next();
				ActivityRef<AgendaEntry> activityRef = new ActivityRef<>(
						agendaRow, layer, entry);
				repository.removeActivity(activityRef);
				entry.setStartTime(entry.getStartTime().plus(
						Duration.ofDays(deltaDays)));
				entry.setEndTime(entry.getEndTime().plus(
						Duration.ofDays(deltaDays)));
				repository.addActivity(activityRef);
			}
		} else if (pack) {
			activities = repository.getActivities(layer, st, et,
					ChronoUnit.DAYS, agendaRow.getZoneId());
			while (activities.hasNext()) {
				AgendaEntry entry = activities.next();
				ActivityRef<AgendaEntry> activityRef = new ActivityRef<>(
						agendaRow, layer, entry);
				repository.removeActivity(activityRef);
				entry.setStartTime(entry.getStartTime().minus(
						Duration.ofDays(-deltaDays)));
				entry.setEndTime(entry.getEndTime().minus(
						Duration.ofDays(-deltaDays)));
				repository.addActivity(activityRef);
			}
		}
	}

	/**
	 * A convenience method to calculate the first day of the current year and
	 * to make sure the horizon starts at midnight.
	 */
	private Instant getHorizonStartTime() {
		Instant startTime = Instant.from(ZonedDateTime.now()
				.with(LocalTime.MIN).with(TemporalAdjusters.firstDayOfYear()));
		return startTime;
	}

	private void updateGridLabel() {
		VirtualGrid<?> grid = graphics.getVirtualGrid();
		if (grid == null) {
			gridLabel.setText("No Grid");
		} else {
			gridLabel.setText("Grid: " + grid.getName());
		}
	}

	private PopOver popOver;

	private void showGridPopOver(Node owner) {
		if (popOver == null) {
			popOver = new PopOver();
			popOver.getStyleClass().add("virtual-grid-popover");
			popOver.setDetachable(false);
			popOver.setArrowSize(8);
			VirtualGridControl gridControl = new VirtualGridControl();
			Bindings.bindContent(gridControl.getGrids(),
					graphics.getVirtualGrids());
			gridControl.valueProperty().addListener(evt -> popOver.hide());
			gridControl.getStylesheets().addAll(graphics.getStylesheets());
			Bindings.bindBidirectional(gridControl.valueProperty(),
					graphics.virtualGridProperty());
			popOver.setContentNode(gridControl);
			popOver.setArrowLocation(BOTTOM_CENTER);
		}

		popOver.show(owner);
	}
}
