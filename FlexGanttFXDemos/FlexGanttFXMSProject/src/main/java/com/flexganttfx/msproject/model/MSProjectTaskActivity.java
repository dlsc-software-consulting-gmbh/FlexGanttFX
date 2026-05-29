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
