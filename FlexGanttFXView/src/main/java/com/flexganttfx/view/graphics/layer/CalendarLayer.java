/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Calendar;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.calendar.CalendarActivity;
import com.flexganttfx.model.calendar.CalendarActivityBase;
import com.flexganttfx.model.calendar.MutableCalendarActivityBase;
import com.flexganttfx.model.calendar.WeekendCalendarActivity;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.CalendarActivityRenderer;
import com.flexganttfx.view.graphics.renderer.WeekendCalendarActivityRenderer;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.util.Position;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.canvas.GraphicsContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Draws the entries returned by the calendars attached to a row or attached to
 * the entire graphics view. The calendar layer uses plugable renderers that
 * are mapped to the entry types. Applications can register their own renderers
 * by calling {@link #setCalendarActivityRenderer(Class, CalendarActivityRenderer)}.
 * 
 * @param <R>
 *            the type of the rows
 * 
 * @see Calendar
 * @see CalendarActivity
 * 
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 * 
 * @since 1.0
 */
public class CalendarLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public CalendarLayer(GraphicsBase<R> graphics) {
		super("Calendar", graphics);

		setCalendarActivityRenderer(CalendarActivityBase.class,
				new CalendarActivityRenderer<CalendarActivityBase<?>>(graphics,
						"Calendars"));

		setCalendarActivityRenderer(MutableCalendarActivityBase.class,
				new CalendarActivityRenderer<CalendarActivityBase<?>>(graphics,
						"Mutable Calendars"));

		setCalendarActivityRenderer(WeekendCalendarActivity.class,
				new WeekendCalendarActivityRenderer<>(graphics,
						"Weekends"));

		redrawObservable(calendarRendererMap);

		fadeInOutObservable(graphics.showCalendarLayerProperty());
	}

	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
		Row<?, ?, ?> row = canvas.getRow();

		double height = canvas.getHeight();

		GraphicsBase<R> graphicsView = canvas.getGraphics();

		ObservableList<Calendar<?>> calendars = graphicsView.getCalendars();

		drawCalendars(row, canvas, calendars, height, startTime, endTime);

		if (row != null) {
			calendars = row.getCalendars();
			drawCalendars(row, canvas, calendars, height, startTime, endTime);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void drawCalendars(Row<?, ?, ?> row, RowCanvas canvas, ObservableList<Calendar<?>> calendars, double height, Instant startTime, Instant endTime) {

		GraphicsContext gc = canvas.getGraphicsContext2D();

		GraphicsBase graphics = canvas.getGraphics();
		Dateline dateline = graphics.getTimeline().getDateline();

		ZoneId zoneId = dateline.getZoneId();

		if (row != null) {
			zoneId = row.getZoneId();
		}

		TemporalUnit temporalUnit = dateline.getPrimaryTemporalUnit();

		for (Calendar calendar : calendars) {
			if (calendar.visibleProperty().get()) {
				Iterator<CalendarActivity> entries = calendar.getActivities(null, startTime, endTime, temporalUnit, zoneId);

				while (entries.hasNext()) {
					CalendarActivity activity = entries.next();

					double x1 = getLocation(activity.getStartTime(), canvas);
					double x2 = getLocation(activity.getEndTime(), canvas);

					final CalendarActivityRenderer renderer = getCalendarActivityRenderer(activity.getClass());

					if (renderer != null && renderer.isEnabled()) {
						double alpha = gc.getGlobalAlpha();

						try {
							if (graphics.isSafeRendering()) {
								gc.save();
							}
							gc.setGlobalAlpha(alpha * renderer.getAlpha());
							renderer.draw(new ActivityRef(row, null, activity), Position.ONLY, gc, x1, 0, x2 - x1, height, false, false, false, false);
						} finally {
							if (graphics.isSafeRendering()) {
								gc.restore();
							} else {
								gc.setGlobalAlpha(alpha);
							}
						}
					}
				}
			}
		}
	}

	// Calendar renderers

	// TODO: add caching for renderer lookup

	private final ObservableMap<Class<?>, CalendarActivityRenderer<?>> calendarRendererMap = FXCollections.observableHashMap();

	public final <A extends Activity> void setCalendarActivityRenderer(
			Class<? extends A> clazz,
			CalendarActivityRenderer<? extends A> renderer) {

		requireNonNull(clazz);

		if (renderer != null) {
			LoggingDomain.CONFIG.fine("class = " + clazz + ", policy = "
					+ renderer.getClass().getName());
		} else {
			LoggingDomain.CONFIG.fine("class = " + clazz + ", policy = null");
		}

		calendarRendererMap.put(clazz, renderer);
	}

	@SuppressWarnings("unchecked")
	public final <A extends CalendarActivity> CalendarActivityRenderer<? extends A> getCalendarActivityRenderer(
			Class<? extends A> clazz) {

		Objects.requireNonNull(clazz);

		return (CalendarActivityRenderer<? extends A>) doGetCalendarActivityRenderer(
				calendarRendererMap, clazz);
	}

	private <A extends CalendarActivity> CalendarActivityRenderer<A> doGetCalendarActivityRenderer(
			Map<Class<?>, ? extends CalendarActivityRenderer<?>> map,
			Class<?> clazz) {
		if (clazz != null) {
			@SuppressWarnings("unchecked")
			CalendarActivityRenderer<A> renderer = (CalendarActivityRenderer<A>) map.get(clazz);
			if (renderer == null) {
				return doGetCalendarActivityRenderer(map, clazz.getSuperclass());
			}
			return renderer;
		}

		return null;
	}
}
