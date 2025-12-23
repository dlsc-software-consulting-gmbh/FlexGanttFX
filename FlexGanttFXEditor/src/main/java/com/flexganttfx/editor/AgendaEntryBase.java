/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

import static com.flexganttfx.editor.AgendaEntryBase.Type.GERMAN;

/**
 * A base implementation of the {@link AgendaEntry} interface. This class is
 * only used for the showcase application.
 */
public class AgendaEntryBase extends MutableActivityBase<String> implements
		AgendaEntry {

	public enum Type {
		ENGLISH("English"), GERMAN("German"), MATH("Math"), SPORT("Sport"), CHEMISTRY(
				"Chemistry"), PHYSICS("Physics"), BIOLOGY("Biology"), RELIGION(
				"Religion");

		private final String displayName;

		Type(String name) {
			this.displayName = name;
		}

		public String getDisplayName() {
			return displayName;
		}
	}

	private Instant originalStartTime;
	private Instant originalEndTime;

	private Type type = GERMAN;
	private Object groupId;

	public AgendaEntryBase(Type type) {
		this.type = type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public void setGroupId(Object id) {
		this.groupId = id;
	}

	@Override
	public Object getGroupId() {
		return groupId;
	}

	@Override
	public void setOriginalStartTime(Instant originalStartTime) {
		this.originalStartTime = originalStartTime;
	}

	@Override
	public Instant getOriginalStartTime() {
		return originalStartTime;
	}

	@Override
	public void setOriginalEndTime(Instant originalEndTime) {
		this.originalEndTime = originalEndTime;
	}

	@Override
	public Instant getOriginalEndTime() {
		return originalEndTime;
	}

	private AgendaEntry pusher;

	@Override
	public void setPusher(AgendaEntry pusher) {
		this.pusher = pusher;
	}

	@Override
	public AgendaEntry getPusher() {
		return pusher;
	}

	private PushDirection pushDirection = PushDirection.NONE;

	@Override
	public void setPushDirection(PushDirection pushDirection) {
		this.pushDirection = pushDirection;
	}

	@Override
	public PushDirection getPushDirection() {
		return pushDirection;
	}
}