/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.ical.model.repository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.filter.Rule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.repository.ActivityRepositoryBase;

public final class ICalActivityRepository extends
		ActivityRepositoryBase<ICalActivity> {

	private final Calendar calendar;

	public ICalActivityRepository(Calendar calendar) {
		this.calendar = calendar;
	}

	@Override
	public Iterator<ICalActivity> getActivities(Layer layer, Instant startTime,
			Instant endTime, TemporalUnit unit, ZoneId zoneId) {

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

		List<ICalActivity> result = new ArrayList<ICalActivity>();
		for (VEvent evt : events) {
			result.add(new ICalActivity(evt));
		}

		return result.iterator();
	}

	@Override
	public Instant getEarliestTimeUsed() {
		ComponentList list = calendar.getComponents(Component.VEVENT);
		if (!list.isEmpty()) {
			VEvent event = (VEvent) list.get(0);
			return event.getStartDate().getDate().toInstant();
		}

		return null;
	}

	@Override
	public Instant getLatestTimeUsed() {
		ComponentList list = calendar.getComponents(Component.VEVENT);
		if (!list.isEmpty()) {
			VEvent event = (VEvent) list.get(list.size() - 1);
			return event.getEndDate().getDate().toInstant();
		}

		return null;
	}
}
