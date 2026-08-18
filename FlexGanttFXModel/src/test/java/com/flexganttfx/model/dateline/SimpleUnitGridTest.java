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
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SimpleUnitGridTest {

	/*
	 * Granularity: TEN
	 */

	@Test
	public void shouldRoundDownZeroToZeroWhenUsingTen() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.TEN, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(0),
				ZoneId.systemDefault(), false, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(0L)));
	}

	@Test
	public void shouldRoundDownNineToZeroWhenUsingTen() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.TEN, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(9),
				ZoneId.systemDefault(), false, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(0L)));
	}

	@Test
	public void shouldRoundDownTwelveToTenWhenUsingTen() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.TEN, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(12),
				ZoneId.systemDefault(), false, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(10L)));
	}

	@Test
	public void shouldRoundUpNineToTenWhenUsingTen() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.TEN, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(9),
				ZoneId.systemDefault(), true, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(10L)));
	}

	@Test
	public void shouldRoundUpTwelveToTwentyWhenUsingTen() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.TEN, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(12),
				ZoneId.systemDefault(), true, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(20L)));
	}

	/*
	 * Granularity: HUNDRED
	 */

	@Test
	public void shouldRoundDownZeroToZeroWhenUsingHundred() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.HUNDRED, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(0),
				ZoneId.systemDefault(), false, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(0L)));
	}

	@Test
	public void shouldRoundDownNineToZeroWhenUsingHundred() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.HUNDRED, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(9),
				ZoneId.systemDefault(), false, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(0L)));
	}

	@Test
	public void shouldRoundDownHundredTwelveToHundredWhenUsingHundred() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.HUNDRED, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(112),
				ZoneId.systemDefault(), false, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(100L)));
	}

	@Test
	public void shouldRoundUpNinetyToHundredWhenUsingHundred() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.HUNDRED, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(90),
				ZoneId.systemDefault(), true, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(100L)));
	}

	@Test
	public void shouldRoundUpHundredTwelveToTwoHundredWhenUsingHundred() {

		// given
		SimpleUnitGrid grid = new SimpleUnitGrid("test", SimpleUnit.HUNDRED, 1);

		// when
		Instant time = grid.adjustTime(Instant.ofEpochMilli(112),
				ZoneId.systemDefault(), true, DayOfWeek.MONDAY);

		// then
		assertThat(time.toEpochMilli(), is(equalTo(200L)));
	}
}
