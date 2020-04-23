/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.msproject.model;

import net.sf.mpxj.Task;

import com.flexganttfx.model.activity.MutableCompletableActivityBase;

public class MSProjectTaskActivity extends
		MutableCompletableActivityBase<MSProjectTaskRow> {

	public MSProjectTaskActivity(MSProjectTaskRow row) {
		super(row.getName(), row.getStartTime(), row.getFinishTime());

		setUserObject(row);
		setPercentageComplete(row.getPercentageComplete());
	}

	@Override
	public void setPercentageComplete(double complete) {
		super.setPercentageComplete(complete);
		getUserObject().setPercentageComplete(complete);
	}

	public Task getTask() {
		return getUserObject().getTask();
	}
}
