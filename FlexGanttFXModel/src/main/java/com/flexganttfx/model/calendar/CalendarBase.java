/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.calendar;

import com.flexganttfx.model.Calendar;
import com.flexganttfx.model.repository.ActivityRepositoryBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static java.util.Objects.requireNonNull;

/**
 * An abstract base implementation of an activity repository that implements the
 * {@link Calendar} interface.
 * 
 * @param <A>
 *            the type of the activities returned by the calendar
 * @since 1.0
 */
public abstract class CalendarBase<A extends CalendarActivity> extends ActivityRepositoryBase<A> implements Calendar<A> {

	/**
	 * Constructs a new calendar.
	 * 
	 * @param name
	 *            the name of the calendar
	 * 
	 * @since 1.0
	 */
	protected CalendarBase(String name) {
		requireNonNull(name);
		setName(name);
	}

	// Name support

	private final StringProperty name = new SimpleStringProperty(this, "name");

	@Override
	public final StringProperty nameProperty() {
		return name;
	}

	/**
	 * Returns the value of the {@link #nameProperty()}.
	 * 
	 * @return the calendar name
	 * @since 1.0
	 */
	public final String getName() {
		return nameProperty().get();
	}

	/**
	 * Sets the value of the {@link #nameProperty()}.
	 * 
	 * @param name
	 *            the calendar name
	 * @since 1.0
	 */
	public final void setName(String name) {
		requireNonNull(name);
		nameProperty().set(name);
	}

	// Visibility support

	private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible", true);

	@Override
	public final BooleanProperty visibleProperty() {
		return visible;
	}

	/**
	 * Returns the value of the {@link #visibleProperty()}.
	 * 
	 * @return the calendar visibility
	 * @since 1.0
	 */
	public final boolean isVisible() {
		return visibleProperty().get();
	}

	/**
	 * Sets the value of the {@link #visibleProperty()}.
	 * 
	 * @param visible
	 *            the calendar visibility
	 * @since 1.0
	 */
	public final void setVisible(boolean visible) {
		visibleProperty().set(visible);
	}
}
