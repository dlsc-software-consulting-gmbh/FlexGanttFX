/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class HelloRow extends Row<HelloRow, HelloRow, HelloActivity> {
	public static final Layer layer = new Layer("Hello Layer");
	
	public HelloRow(String name, int activityCount) {
		super(name);
		
		Instant time = Instant.now();
		
		for (int i = 0; i < activityCount; i++) {
			long days = (long) (Math.random() * 10);
			
			Instant st = Instant.from(time);
			Instant et = st.plus(days, ChronoUnit.DAYS);

			HelloActivity activity = new HelloActivity();
			activity.setStartTime(st);
			activity.setEndTime(et);
			
			days = Math.min(1, (long) (Math.random() * 5));
			time = et.plus(days, ChronoUnit.DAYS);
			
			addActivity(layer, activity);
		}
	}
	
	public HelloRow(String name) {
		super(name);
	}
}
