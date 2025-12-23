/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.calendar;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Calendar;
import com.flexganttfx.model.Row;

/**
 * An extension of the {@link Activity} interface which marks an activity as
 * something that is being returned / managed by a {@link Calendar}. The user
 * can never directly interact with a calendar activity (they are always
 * read-only). They are used to represent calendar entries and are rendered in
 * the background of all or individual rows.
 * 
 * @see Row#getCalendars()
 * 
 * @since 1.0
 */
public interface CalendarActivity extends Activity {
}
