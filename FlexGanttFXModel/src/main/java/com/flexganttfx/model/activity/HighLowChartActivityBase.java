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

import com.flexganttfx.model.layout.ChartLayout;

import java.time.Instant;

/**
 * The base implementation of a high-low activity, which are used in combination
 * with a {@link ChartLayout}. A typical use-case for such activities are
 * candle-stick diagrams used for plotting historical stock prices.
 * 
 * @param <T>
 *            the type of the optional user object
 * @since 1.0
 */
public class HighLowChartActivityBase<T> extends ActivityBase<T> implements
		HighLowChartActivity {

	/**
	 * Constructs a new high-low activity. The initial high and low values will
	 * both be equal to zero. The start time will be equal to
	 * {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus {@link ActivityBase#DEFAULT_DURATION}.
	 * 
	 * @since 1.0
	 */
	public HighLowChartActivityBase() {
		super();
	}

	/**
	 * Constructs a new high-low activity with the given initial high and low
	 * values and the given start and end time.
	 * 
	 * @param low
	 *            the low value of the activity
	 * @param high
	 *            the high value of the activity
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 * 
	 * @throws IllegalArgumentException
	 *             if low is larger than high
	 * 
	 * @since 1.0
	 */
	public HighLowChartActivityBase(double low, double high, Instant startTime,
			Instant endTime) {
		super(startTime, endTime);

		if (low > high) {
			throw new IllegalArgumentException(
					"the low value must be smaller or equal to the high value (low = "
							+ low + ", high = " + high + ")");
		}

		this.high = high;
		this.low = low;
	}

	/**
	 * Constructs a new high-low activity with the given initial high and low
	 * values and the start and end time set to the given time.
	 * 
	 * @param low
	 *            the low value of the activity
	 * @param high
	 *            the high value of the activity
	 * @param time
	 *            the start and end time of the activity
	 * 
	 * @throws IllegalArgumentException
	 *             if low is larger than high
	 * 
	 * @since 1.0
	 */
	public HighLowChartActivityBase(double low, double high, Instant time) {
		super(time, time);

		if (low > high) {
			throw new IllegalArgumentException(
					"the low value must be smaller or equal to the high value (low = "
							+ low + ", high = " + high + ")");
		}

		this.high = high;
		this.low = low;
	}

	/**
	 * The low value used when the activity is rendered as part of a chart.
	 */
	protected double low;

	@Override
	public double getLow() {
		return low;
	}

	/**
	 * The high value used when the activity is rendered as part of a chart.
	 */
	protected double high;

	@Override
	public double getHigh() {
		return high;
	}

	@Override
	public String toString() {
		return super.toString() + ", low = " + getLow() + ", high = " + getHigh();
	}
}
