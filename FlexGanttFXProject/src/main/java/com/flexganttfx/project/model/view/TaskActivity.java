/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.project.model.view;

import com.flexganttfx.model.activity.MutableCompletableActivityBase;
import com.flexganttfx.project.model.business.Task;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends MutableCompletableActivityBase<Task> {

	private List<TaskActivity> subtasks;

	public TaskActivity(Task task) {
		setUserObject(task);
		setStartTime(task.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC));
		setEndTime(task.getEndDate().atStartOfDay().toInstant(ZoneOffset.UTC));
	}

	public boolean isParent() {
		return subtasks != null && subtasks.size() > 0;
	}

	public List<TaskActivity> getSubtasks() {
		if (subtasks == null) {
			subtasks = new ArrayList<TaskActivity>();
		}

		return subtasks;
	}
}
