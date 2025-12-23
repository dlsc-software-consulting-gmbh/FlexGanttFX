/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

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

/**
 * A Gantt chart that implements the {@link AgendaEditorContext} interface. This
 * means that conflict resolution can be used by it.
 */
public class AgendaGanttChart extends GanttChart<AgendaRow> implements AgendaEditorContext<AgendaRow> {

	public AgendaGanttChart() {
		Layer layer = new Layer("Agenda Layer");
		getLayers().add(layer);

		AgendaController<AgendaRow> controller = new AgendaController<>(this, layer);

		List<AgendaRow> rows = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			rows.add(new AgendaRow("Row " + i));
		}

		AgendaRow root = new AgendaRow("Root");
		root.getChildren().setAll(rows);

		setRoot(root);
		root.setExpanded(true);

		getTreeTable().setShowRoot(false);

		getGraphics().setActivityRenderer(AgendaEntryBase.class, AgendaLayout.class, new AgendaEntryRenderer(getGraphics()));
		getGraphics().setContextMenu(new AgendaEditorContextMenu(getGraphics()));
		getGraphics().getBackgroundSystemLayers().add(new AgendaEditorBackgroundLayer<>(this, controller));
	}

	private final LongProperty changeDelay = new SimpleLongProperty(this,
			"changeDelay", 5);

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
			this, "initialEntryDuration", Duration.ofHours(1));

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
	public List<TimeInterval> getPasteLocations(double x, double y,
			List<ActivityRef<?>> copiedActivities) {

		if (copiedActivities == null || copiedActivities.isEmpty()) {
			return null;
		}

		GraphicsBase<?> graphics = getGraphics();

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

			ZoneId zoneId = currentRef.getRow().getZoneId();

			TimeInterval interval = new TimeInterval(grid(
					zonedDateTime.toInstant(), false, zoneId), grid(
					zonedDateTime.toInstant(), false, zoneId).plus(
					currentEntry.getDuration()));

			result.add(interval);
		}

		return result;
	}

	private Instant grid(Instant instant, boolean roundUp, ZoneId zoneId) {
		VirtualGrid<?> grid = getGraphics().getVirtualGrid();
		if (grid != null) {
			Dateline dateline = getGraphics().getTimeline().getDateline();
			DayOfWeek firstDayOfWeek = dateline.getFirstDayOfWeek();
			return grid.adjustTime(instant, zoneId, roundUp, firstDayOfWeek);
		}

		return instant;
	}
}
