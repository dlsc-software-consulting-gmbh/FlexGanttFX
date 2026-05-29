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
package com.flexganttfx.model.dateline;

import com.flexganttfx.model.util.SimpleUnit;

/**
 * Represents a dateline model for managing temporal resolutions using the SimpleUnit enumeration.
 * This class extends the generic {@code DatelineModel} class and provides specific behaviors
 * for handling {@code SimpleUnit}-based resolutions.
 *
 * The model initializes by adding resolutions corresponding to all available values of the
 * {@code SimpleUnit} enumeration, each mapped to an increment value of 1.
 *
 * @since 1.0
 */
public final class SimpleUnitDatelineModel extends DatelineModel<SimpleUnit> {

	public SimpleUnitDatelineModel() {
		for (SimpleUnit unit : SimpleUnit.values()) {
			addResolution(new SimpleUnitResolution(unit, "", 1));
		}
	}

	@Override
	public SimpleUnit nextTemporalUnit(SimpleUnit unit) {
		int ordinal = unit.ordinal();
		if (ordinal < SimpleUnit.values().length - 1) {
			return SimpleUnit.values()[ordinal + 1];
		}

		return null;
	}
}
