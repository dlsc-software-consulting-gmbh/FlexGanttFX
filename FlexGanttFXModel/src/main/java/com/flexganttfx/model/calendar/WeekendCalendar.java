/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.calendar;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.repository.RepositoryEvent;
import javafx.event.Event;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.requireNonNull;

/**
 * A calendar specialized on returning activities that represent weekend days
 * (default: saturday, sunday). The days that are considered weekend days can be
 * configured by calling {@link #setWeekendDays(DayOfWeek...)}.
 *
 * @since 1.0
 */
public class WeekendCalendar extends CalendarBase<WeekendCalendarActivity> {

	private Instant lastStartTime = Instant.MIN;
	private Instant lastEndTime = Instant.MAX;
	private ZoneId lastZoneId;

	private List<WeekendCalendarActivity> entries;

	private EnumSet<DayOfWeek> weekendDays = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

	/**
	 * Constructs a new weekend calendar.
	 *
	 * @since 1.0
	 */
	public WeekendCalendar() {
		super("Weekends");
	}

	/**
	 * Sets the days of the week that are considered to be a weekend day. By
	 * default {@link DayOfWeek#SATURDAY} and {@link DayOfWeek#SUNDAY} are
	 * considered weekend days.
	 *
	 * @param days
	 *            the days of the week that are to be considered weekend days
	 * @since 1.0
	 */
	public void setWeekendDays(DayOfWeek... days) {
		requireNonNull(days);

		weekendDays.clear();
		weekendDays.addAll(Arrays.asList(days));

		Event.fireEvent(this, new RepositoryEvent(this));
	}

	/**
	 * Returns the days of the week that are to be considered weekend days. By
	 * default {@link DayOfWeek#SATURDAY} and {@link DayOfWeek#SUNDAY} are
	 * considered weekend days.
	 *
	 * @return the days of the week used as weekend days
	 * @since 1.0
	 */
	public DayOfWeek[] getWeekendDays() {
		return weekendDays.toArray(new DayOfWeek[weekendDays.size()]);
	}

	@Override
	public Iterator<WeekendCalendarActivity> getActivities(Layer layer,
			Instant startTime, Instant endTime, TemporalUnit temporalUnit,
			ZoneId zoneId) {

		if (!(temporalUnit instanceof ChronoUnit)) {
			return Collections.emptyListIterator();
		}

		if (startTime.equals(lastStartTime) && endTime.equals(lastEndTime)
				&& zoneId.equals(lastZoneId)) {
			/*
			 * We already answered this query for the given time interval. Let's
			 * return the result from last time.
			 */
			if (entries != null) {
				return entries.iterator();
			}
		} else {
			ChronoUnit unit = (ChronoUnit) temporalUnit;

			/*
			 * The time interval has changed. Find the weekends within the new
			 * interval, but only if the user is currently looking at days or
			 * weeks.
			 */

			if (isSupportedUnit(unit)) {

				/*
				 * Lazily create list structure.
				 */
				if (entries == null) {
					entries = new ArrayList<>();
				} else {
					entries.clear();
				}

				ZonedDateTime st = ZonedDateTime.ofInstant(startTime, zoneId);
				ZonedDateTime et = ZonedDateTime.ofInstant(endTime, zoneId);

				findWeekends(st, et);

				lastStartTime = startTime;
				lastEndTime = endTime;
				lastZoneId = zoneId;

				return entries.iterator();
			}
		}

		return Collections.emptyListIterator();
	}

	/**
	 * Determines if weekends will be shown for the given temporal unit. By
	 * default we only show weekends for {@link ChronoUnit#DAYS} and
	 * {@link ChronoUnit#WEEKS}. To support more units simply override this
	 * method in a subclass.
	 *
	 * @param unit
	 *            the unit to check
	 * @return true if weekend information will be shown in the Gantt chart
	 * @since 1.0
	 */
	protected boolean isSupportedUnit(TemporalUnit unit) {
		if (unit instanceof ChronoUnit) {
			ChronoUnit chronoUnit = (ChronoUnit) unit;
			switch (chronoUnit) {
			case DAYS:
			case WEEKS:
				return true;
			default:
				return false;
			}
		}

		return false;
	}

	private void findWeekends(ZonedDateTime st, ZonedDateTime et) {
		while (st.isBefore(et) || st.equals(et)) {

			if (weekendDays.contains(st.getDayOfWeek())) {

				st = st.truncatedTo(DAYS);

				entries.add(new WeekendCalendarActivity(st.getDayOfWeek()
						.toString(), Instant.from(st), Instant.from(st
						.plusDays(1)), st.getDayOfWeek()));
			}

			st = st.plusDays(1);
		}
	}
}
