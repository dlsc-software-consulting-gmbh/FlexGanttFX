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
 * An add-on interface for completable activities where the user should be able
 * to interactively edit the percentage complete value.
 *
 * @since 1.0
 */
public interface MutableCompletableActivity extends MutableActivity, CompletableActivity {

	/**
	 * Sets the percentage complete value of the activity.
	 *
	 * @param complete
	 *            the new percentage complete value (must be between 0 and 100).
	 * @throws IllegalArgumentException if the given value is not within the range 0 to 100
	 * @see CompletableActivity#getPercentageComplete()
	 * @since 1.0
	 */
	void setPercentageComplete(double complete);
}
