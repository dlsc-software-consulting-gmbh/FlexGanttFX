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

import java.time.Instant;

/**
 * The base implementation of a completable activity, which stores a
 * "percentage complete" value between 0 and 100%. Renderers often fill the
 * background of activity bars depending on this value. The higher the value the
 * more of the background gets filled.
 *
 * @since 1.0
 *
 * @param <T>
 *            the type of the optional user object
 */
public class CompletableActivityBase<T> extends ActivityBase<T> implements
		CompletableActivity {

	/**
	 * Constructs a new activity. The initial start time will be set to
	 * {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 *
	 * @since 1.0
	 */
	public CompletableActivityBase() {
		super();
	}

	/**
	 * Constructs a new activity with the given name. The initial start time
	 * will be set to {@link Instant#now()} and the end time will be equal to
	 * {@link Instant#now()} plus the value of {@link #DEFAULT_DURATION}.
	 *
	 * @param name
	 *            the name of the activity
	 *
	 * @since 1.0
	 */
	public CompletableActivityBase(String name) {
		super(name);
	}

	/**
	 * Constructs a new activity with the start time and end time.
	 *
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 *
	 * @since 1.0
	 */
	public CompletableActivityBase(Instant startTime, Instant endTime) {
		super(startTime, endTime);
	}

	/**
	 * Constructs a new activity with the given name, start time, and end time.
	 *
	 * @param name
	 *            the name of the activity
	 * @param startTime
	 *            the start time of the activity
	 * @param endTime
	 *            the end time of the activity
	 *
	 * @since 1.0
	 */
	public CompletableActivityBase(String name, Instant startTime,
			Instant endTime) {
		super(name, startTime, endTime);
	}

	// Percentage complete support.

	/**
	 * The completion of the activity, a value between 0 and 1.
	 */
	protected double percentageComplete;

	@Override
	public double getPercentageComplete() {
		return percentageComplete;
	}

	@Override
	public String toString() {
		return super.toString() + ", percentage complete = " + getPercentageComplete();
	}
}
