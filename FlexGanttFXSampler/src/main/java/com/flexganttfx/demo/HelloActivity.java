/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javafx.scene.paint.Color;

import com.flexganttfx.model.activity.MutableCompletableActivityBase;

public class HelloActivity extends MutableCompletableActivityBase<String> {

	private int lineIndex;
	private Color color;

	public HelloActivity(String name) {
		super(name);
		
		Instant start = Instant.now().plus((int) (Math.random() * 365), ChronoUnit.DAYS);
		Instant end = start.plus((int) (Math.random() * 3), ChronoUnit.DAYS);

		setStartTime(start);
		setEndTime(end);
	}

	public HelloActivity() {
		this(null);
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	public int getLineIndex() {
		return lineIndex;
	}
}
