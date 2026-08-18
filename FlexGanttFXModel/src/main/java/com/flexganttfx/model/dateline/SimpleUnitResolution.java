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
package com.flexganttfx.model.dateline;

import com.flexganttfx.model.util.SimpleUnit;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;

/**
 * A resolution type for {@link SimpleUnit} that can be used in combination with
 * the {@link SimpleUnitDatelineModel}.
 *
 * @see SimpleUnitDatelineModel#addResolution(Resolution)
 * @since 1.0
 */
public final class SimpleUnitResolution extends Resolution<SimpleUnit> {

	private final NumberFormat formatter = DecimalFormat.getInstance();

	/**
	 * Constructs a new resolution for the given unit, format, step rate, and positions.
	 *
	 * @param unit               the simple unit supported by this resolution (e.g. TEN)
	 * @param format             the format how the unit will be shown on the screen
	 * @param stepRate           the step rate (e.g. "1", "5", "15")
	 * @param supportedPositions the position inside the dateline where this resolution can be displayed (top, middle, bottom)
	 * @since 1.0
	 */
	public SimpleUnitResolution(SimpleUnit unit, String format, int stepRate, Position... supportedPositions) {
		super(unit, format, stepRate, supportedPositions);
	}

	/**
	 * Constructs a new resolution for the given unit, format, step rate, and positions.
	 *
	 * @param unit               the simple unit supported by this resolution (e.g. TEN)
	 * @param format             the format how the unit will be shown on the screen
	 * @param stepRate           the step rate (e.g. "1", "5", "15")
	 * @since 1.0
	 */
	public SimpleUnitResolution(SimpleUnit unit, String format, int stepRate) {
		super(unit, format, stepRate);
	}

	@Override
	public String format(Instant instant, ZoneId zoneId) {
		SimpleUnit unit = getTemporalUnit();
		if (unit.ordinal() < SimpleUnit.values().length - 1) {
			return formatter.format(instant.toEpochMilli()
					% SimpleUnit.values()[unit.ordinal() + 1].getMillis());			
		}
		
		return formatter.format(instant.toEpochMilli());			
	}

	@Override
	public Instant truncate(Instant temporal, ZoneId zoneId,
			DayOfWeek firstDayOfWeek) {
		long millis = temporal.toEpochMilli();
		millis -= millis % getTemporalUnit().getMillis();
		millis -= millis % getStepRate();
		return Instant.ofEpochMilli(millis);
	}

	@Override
	public Instant increment(Instant instant, ZoneId zoneId) {
		return instant.plus(getTemporalUnit().getDuration().multipliedBy(getStepRate()));
	}

	@Override
	public Instant decrement(Instant instant, ZoneId zoneId) {
		return instant.minus(getTemporalUnit().getDuration().multipliedBy(getStepRate()));
	}

	@Override
	public VirtualGrid<SimpleUnit> createGrid() {
		return new SimpleUnitGrid("Auto", getTemporalUnit(), getStepRate());
	}
}
