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
import com.flexganttfx.model.layout.ChartLayout;

/**
 * An add-on interface for {@link Activity} used for activities that want to be
 * shown as "High Low Sticks" (e.g. Stock Charts) in a {@link ChartLayout}.
 *
 * @since 1.0
 */
public interface HighLowChartActivity extends Activity {

	/**
	 * Returns the low value of the activity.
	 *
	 * @return the low value
	 * @since 1.0
	 */
	double getLow();

	/**
	 * Returns the high value of the activity.
	 *
	 * @return the high value
	 * @since 1.0
	 */
	double getHigh();
}