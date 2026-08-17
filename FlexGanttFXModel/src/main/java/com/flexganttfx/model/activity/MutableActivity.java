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
package com.flexganttfx.model.activity;

import com.flexganttfx.model.Activity;

import java.time.Instant;

/**
 * An add-on interface that needs to be implemented by any activity that can be
 * edited interactively by the user.
 * 
 * @since 1.0
 */
public interface MutableActivity extends Activity {

	/**
	 * Sets the name of the activity.
	 * 
	 * @param name
	 *            the new name of the activity
	 * @since 1.0
	 */
	void setName(String name);

	/**
	 * Sets a new start time on the activity.
	 * 
	 * @param time
	 *            the new start time
	 * @since 1.0
	 */
	void setStartTime(Instant time);

	/**
	 * Sets a new end time on the activity.
	 * 
	 * @param time
	 *            the new end time
	 * @since 1.0
	 */
	void setEndTime(Instant time);
}