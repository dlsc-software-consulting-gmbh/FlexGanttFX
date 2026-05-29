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

/**
 * An add-on interface for any {@link Activity} that wants to visualize a
 * percentage complete value. This is often done by filling a section of the
 * activity bar depending on the value represented.
 *
 * @since 1.0
 */
public interface CompletableActivity extends Activity {

	/**
	 * Returns the "percentage complete" value of the activity. Must be a value
	 * between 0 and 100%.
	 *
	 * @return the percentage complete value [0, 100];
	 */
	double getPercentageComplete();
}
