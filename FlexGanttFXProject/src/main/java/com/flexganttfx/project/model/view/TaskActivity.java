/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
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
		return subtasks != null && !subtasks.isEmpty();
	}

	public List<TaskActivity> getSubtasks() {
		if (subtasks == null) {
			subtasks = new ArrayList<TaskActivity>();
		}

		return subtasks;
	}
}
