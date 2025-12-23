/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.ical.renderer;

import javafx.scene.paint.Color;

import com.flexganttfx.ical.model.calendar.ICalCalendarActivity;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.CalendarActivityRenderer;

public class ICalCalendarActivityRenderer<A extends ICalCalendarActivity>
		extends CalendarActivityRenderer<A> {

	public ICalCalendarActivityRenderer(GraphicsBase<?> graphics) {
		super(graphics, "iCalCalendar");

		setFill(new Color(0, 1, 0, .2));
		setStroke(getFill());
	}
}
