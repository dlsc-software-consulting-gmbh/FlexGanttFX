/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.calendar.WeekendCalendarActivity;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.scene.paint.Color;

public class WeekendCalendarActivityRenderer<A extends WeekendCalendarActivity>
		extends CalendarActivityRenderer<A> {

	public WeekendCalendarActivityRenderer(GraphicsBase<?> graphics, String name) {
		super(graphics, name);
		fillProperty().bindBidirectional(graphics.weekendColorProperty());
		setStroke(Color.TRANSPARENT);
	}
}
