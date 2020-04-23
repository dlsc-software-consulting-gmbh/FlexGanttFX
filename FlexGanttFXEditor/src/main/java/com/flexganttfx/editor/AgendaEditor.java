/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import static com.flexganttfx.model.layout.AgendaLayout.LayoutStrategy.OVERLAPPING;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.SingleRowGraphics;
import com.flexganttfx.view.timeline.Dateline;

/**
 * The agenda editor is a custom control for creating agendas. The editor
 * utilizes the {@link AgendaController} and the {@link AgendaConflictResolver}
 * classes to control the editing behaviour.
 */
public class AgendaEditor extends Control implements
		AgendaEditorContext<AgendaRow> {

	private AgendaRow agendaRow;

	private AgendaLayout agendaLayout;

	/**
	 * Constructs a new editor with a single row on which the user can add
	 * agenda entries.
	 *
	 * @see SingleRowGraphics
	 */
	public AgendaEditor() {

		agendaLayout = new AgendaLayout();
		agendaLayout.setLayoutStrategy(OVERLAPPING);
		agendaLayout.startTimeProperty().addListener(evt -> graphics.redraw());
		agendaLayout.endTimeProperty().addListener(evt -> graphics.redraw());
		agendaLayout.setStartTime(LocalTime.of(7, 0));
		agendaLayout.setEndTime(LocalTime.of(17, 0));
		agendaLayout.setPadding(30);

		agendaRow = new AgendaRow("Curriculum");
		agendaRow.setLayout(agendaLayout);
	}

	@Override
	public String getUserAgentStylesheet() {
		return AgendaEditor.class.getResource("editor.css").toExternalForm();
	}

	public final AgendaRow getAgendaRow() {
		return agendaRow;
	}

	public final AgendaLayout getAgendaLayout() {
		return agendaLayout;
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new AgendaEditorSkin(this);
	}

	private final SingleRowGraphics<AgendaRow> graphics = new SingleRowGraphics<>();

	@Override
	public final GraphicsBase<AgendaRow> getGraphics() {
		return graphics;
	}

	private final LongProperty changeDelay = new SimpleLongProperty(this,
			"changeDelay", 50);

	@Override
	public final LongProperty changeDelayProperty() {
		return changeDelay;
	}

	public final void setChangeDelay(long delay) {
		changeDelayProperty().set(delay);
	}

	public final long getChangeDelay() {
		return changeDelayProperty().get();
	}

	private final BooleanProperty restore = new SimpleBooleanProperty(this,
			"restore", true);

	@Override
	public final BooleanProperty restoreProperty() {
		return restore;
	}

	public final void setRestore(boolean b) {
		restoreProperty().set(b);
	}

	public final boolean isRestore() {
		return restoreProperty().get();
	}

	private final BooleanProperty allowOverlapping = new SimpleBooleanProperty(
			this, "allowOverlapping", false);

	@Override
	public final BooleanProperty allowOverlappingProperty() {
		return allowOverlapping;
	}

	public final void setAllowOverlapping(boolean b) {
		allowOverlappingProperty().set(b);
	}

	public final boolean isAllowOverlapping() {
		return allowOverlappingProperty().get();
	}

	private final BooleanProperty showPasteLocations = new SimpleBooleanProperty(
			this, "showPasteLocations", true);

	@Override
	public final BooleanProperty showPasteLocationsProperty() {
		return showPasteLocations;
	}

	public final void setShowPasteLocations(boolean b) {
		showPasteLocationsProperty().set(b);
	}

	public final boolean isShowPasteLocations() {
		return showPasteLocationsProperty().get();
	}

	private final ObjectProperty<Duration> initialEntryDuration = new SimpleObjectProperty<>(
			this, "initialEntryDuration", Duration.ofMinutes(45));

	@Override
	public final ObjectProperty<Duration> initialEntryDurationProperty() {
		return initialEntryDuration;
	}

	public final Duration getInitialEntryDuration() {
		return initialEntryDurationProperty().get();
	}

	public final void setInitialEntryDuration(Duration duration) {
		Objects.requireNonNull(duration);
		initialEntryDurationProperty().set(duration);
	}

	@Override
	public final List<TimeInterval> getPasteLocations(double x, double y,
			List<ActivityRef<?>> copiedActivities) {

		if (copiedActivities == null || copiedActivities.isEmpty()) {
			return null;
		}

		Instant time = graphics.getTimeAt(x);
		LocalTime localTime = graphics.getLocalTimeAt(y);
		ZonedDateTime zonedDateTime = ZonedDateTime
				.ofInstant(time, ZoneId.systemDefault())
				.truncatedTo(ChronoUnit.DAYS).with(localTime);

		List<TimeInterval> result = new ArrayList<>();
		int count = copiedActivities.size();
		for (int i = 0; i < count; i++) {
			ActivityRef<?> currentRef = copiedActivities.get(i);
			AgendaEntryBase currentEntry = (AgendaEntryBase) currentRef
					.getActivity();

			if (i > 0) {
				ActivityRef<?> prevRef = copiedActivities.get(i - 1);
				AgendaEntryBase prevEntry = (AgendaEntryBase) prevRef
						.getActivity();
				zonedDateTime = zonedDateTime.plus(Duration.between(
						prevEntry.getStartTime(), currentEntry.getStartTime()));
			}

			TimeInterval interval = new TimeInterval(grid(
					zonedDateTime.toInstant(), false), grid(
					zonedDateTime.toInstant(), false).plus(
					currentEntry.getDuration()));

			result.add(interval);
		}

		return result;
	}

	private Instant grid(Instant instant, boolean roundUp) {
		VirtualGrid<?> grid = graphics.getVirtualGrid();
		if (grid != null) {
			Dateline dateline = graphics.getTimeline().getDateline();
			DayOfWeek firstDayOfWeek = dateline.getFirstDayOfWeek();
			return grid.adjustTime(instant, agendaRow.getZoneId(), roundUp,
					firstDayOfWeek);
		}

		return instant;
	}
}
