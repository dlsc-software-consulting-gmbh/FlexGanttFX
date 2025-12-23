/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import com.flexganttfx.model.activity.MutableActivity;

import java.time.Duration;
import java.time.Instant;

/**
 * The interface required by activities so that they can take part in agenda
 * editing and conflict resolution.
 */
public interface AgendaEntry extends MutableActivity {

	/**
	 * An enum used to indicate into which direction the entry is currently
	 * being pushed.
	 */
    enum PushDirection {
		UP, DOWN, NONE
    }

	/**
	 * Sets a group ID on the entry. All entries with the same ID are members of
	 * the same group.
	 */
    void setGroupId(Object id);

	/**
	 * Returns the group ID of the entry.
	 */
    Object getGroupId();

	/**
	 * Sets the original start time on the entry just before it is being pushed
	 * out of the way.
	 */
    void setOriginalStartTime(Instant time);

	/**
	 * Returns the original start time of the entry.
	 */
    Instant getOriginalStartTime();

	/**
	 * Sets the original end time on the entry just before it is being pushed
	 * out of the way.
	 */
    void setOriginalEndTime(Instant time);

	/**
	 * Returns the original end time of the entry.
	 */
    Instant getOriginalEndTime();

	/**
	 * Sets the pusher of the entry. 
	 */
    void setPusher(AgendaEntry pusher);

	/**
	 * Returns the pusher of the entry.
	 */
    AgendaEntry getPusher();

	/**
	 * Sets the push direction (UP, DOWN, NONE) on the entry. 
	 */
    void setPushDirection(PushDirection direction);

	/**
	 * Returns the push direction of the entry.
	 */
    PushDirection getPushDirection();

	/**
	 * Returns the duration of the entry.
	 */
    Duration getDuration();
}