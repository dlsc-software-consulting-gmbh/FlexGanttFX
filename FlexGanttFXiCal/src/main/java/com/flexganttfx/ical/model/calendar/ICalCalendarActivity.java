/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.ical.model.calendar;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import net.fortuna.ical4j.model.component.VEvent;

import com.flexganttfx.model.calendar.MutableCalendarActivityBase;

public class ICalCalendarActivity extends MutableCalendarActivityBase<VEvent> {

	public ICalCalendarActivity(VEvent event) {
		super(event.getSummary().getValue());

		Instant st = Instant.from(ZonedDateTime.ofInstant(
				event.getStartDate().getDate().toInstant(),
				ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS));
		Instant et = st.plus(Duration.ofDays(1));

		setStartTime(st);
		setEndTime(et);
	}
}
