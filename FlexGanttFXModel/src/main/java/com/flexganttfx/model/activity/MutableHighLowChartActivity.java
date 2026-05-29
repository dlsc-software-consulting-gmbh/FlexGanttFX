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

/**
 * An add-on interface for high-low activities which allows the user to
 * interactively edit the high and low value of the activity.
 * 
 * @since 1.0
 */
public interface MutableHighLowChartActivity extends MutableActivity, HighLowChartActivity {

	/**
	 * Sets the low value of the activity.
	 * 
	 * @param low
	 *            the new low value
	 * @since 1.0
	 */
	void setLow(double low);

	/**
	 * Sets the high value of the activity.
	 * 
	 * @param high
	 *            the new high value
	 * @since 1.0
	 */
	void setHigh(double high);
}