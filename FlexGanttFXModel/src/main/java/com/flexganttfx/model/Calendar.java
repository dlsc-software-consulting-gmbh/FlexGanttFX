/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import com.flexganttfx.model.calendar.CalendarActivity;

/**
 * A calendar is an extension of an activity repository with the additions of a
 * name and a visibility property. Calendars can be added to the whole Gantt
 * chart or to individual rows within the Gantt chart.
 *
 * @see Row#getCalendars()
 *
 * @param <A>
 *            the type of the calendar activities shown by the calendar
 * @since 1.0
 */
public interface Calendar<A extends CalendarActivity> extends
		ActivityRepository<A> {

	/**
	 * Returns the property used to store the name of the calendar. The name
	 * might be displayed by the UI (e.g. in a context menu).
	 *
	 * @return the name of the calendar, for example ("Weekends")
	 * @since 1.0
	 */
	StringProperty nameProperty();

	/**
	 * Returns the property used to store the visibility flag of the calendar.
	 * Calendars can be shown / hidden by the user.
	 *
	 * @return the calendar's visibility
	 * @since 1.0
	 */
	BooleanProperty visibleProperty();
}
