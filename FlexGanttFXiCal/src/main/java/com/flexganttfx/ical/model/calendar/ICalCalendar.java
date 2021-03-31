/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.ical.model.calendar;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.calendar.CalendarBase;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.filter.Rule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class ICalCalendar extends CalendarBase<ICalCalendarActivity> {

	private Instant lastStartTime = Instant.MIN;
	private Instant lastEndTime = Instant.MAX;

	private List<ICalCalendarActivity> entries;

	private Calendar calendar;

	public ICalCalendar(String name, Calendar calendar) {
		super(name);

		Objects.requireNonNull(calendar);

		this.calendar = calendar;
	}

	@Override
	public Iterator<ICalCalendarActivity> getActivities(Layer layer,
			Instant startTime, Instant endTime, TemporalUnit temporalUnit,
			ZoneId zoneId) {

		if (!(temporalUnit instanceof ChronoUnit)) {
			return Collections.emptyIterator();
		}

		if (startTime.equals(lastStartTime) && endTime.equals(lastEndTime)) {
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
			switch (unit) {
			case DAYS:
			case WEEKS:
			case MONTHS:
			case YEARS:
				/*
				 * Lazily create list structure.
				 */
				if (entries == null) {
					entries = new ArrayList<ICalCalendarActivity>();
				} else {
					entries.clear();
				}

				findEvents(startTime, endTime);

				lastStartTime = startTime;
				lastEndTime = endTime;

				return entries.iterator();
			default:
			}

		}

		return Collections.emptyListIterator();
	}

	private void findEvents(Instant startTime, Instant endTime) {

		java.util.Calendar st = java.util.Calendar.getInstance();
		st.setTimeInMillis(startTime.toEpochMilli());

		java.util.Calendar et = java.util.Calendar.getInstance();
		et.setTimeInMillis(endTime.toEpochMilli());

		Period period = new Period(new DateTime(st.getTime()), new DateTime(
				et.getTime()));

		Rule[] rule = new Rule[] { new PeriodRule(period) };
		Filter filter = new Filter(rule, Filter.MATCH_ANY);

		@SuppressWarnings("unchecked")
		Collection<VEvent> events = filter.filter(calendar
				.getComponents(Component.VEVENT));

		for (VEvent evt : events) {
			entries.add(new ICalCalendarActivity(evt));
		}
	}
}
