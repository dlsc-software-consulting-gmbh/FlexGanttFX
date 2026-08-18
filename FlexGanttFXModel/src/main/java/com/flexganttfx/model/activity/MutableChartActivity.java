/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.model.activity;

/**
 * An add-on interface for chart activities where it should be possible to
 * change the chart value at a later time. This turns a chart activity into a
 * mutable activity and allows for the user to edit the value interactively.
 *
 * @since 1.0
 */
public interface MutableChartActivity extends MutableActivity, ChartActivity {

	/**
	 * Sets a new chart value.
	 *
	 * @param value
	 *            the new value
	 * @since 1.0
	 */
	void setChartValue(double value);
}