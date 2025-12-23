/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.ical.model.repository;

import java.time.Instant;

import net.fortuna.ical4j.model.component.VEvent;

import com.flexganttfx.model.activity.MutableActivityBase;

public class ICalActivity extends MutableActivityBase<VEvent> {

	public ICalActivity(VEvent event) {
		super(event.getSummary().getValue());

		setUserObject(event);

		Instant st = event.getStartDate().getDate().toInstant();
		Instant et = event.getEndDate().getDate().toInstant();

		setStartTime(st);
		setEndTime(et);
	}
}
