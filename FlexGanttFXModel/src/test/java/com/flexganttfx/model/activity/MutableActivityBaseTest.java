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

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.util.ActivityHelper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class MutableActivityBaseTest {

	@Test
	public void shouldCreateFullyInitializedNewActivity() {

		// when
		Activity activity = new MutableActivityBase<>();

		// then
		assertThat(activity.getStartTime(), is(notNullValue()));
		assertThat(activity.getEndTime(), is(notNullValue()));
	}

	@Test
	public void shouldCreateFullyInitializedNewActivityWithProperties() {

		// when
		Activity activity = new MutableActivityBase<>();

		// then
		assertThat(activity.getStartTime(), is(notNullValue()));
		assertThat(activity.getEndTime(), is(notNullValue()));
	}

	@Test
	public void shouldCreateFullyInitializedNewActivityWithParameters() {

		// when
		String name = "My Activity";
		Instant startTime = Instant.now().plusMillis(100000);
		Instant endTime = Instant.now().plusMillis(200000);

		Activity activity = new MutableActivityBase<>(name, startTime, endTime);

		// then
		assertThat(activity.getName(), is(equalTo(name)));
		assertThat(activity.getStartTime(), is(equalTo(startTime)));
		assertThat(activity.getEndTime(), is(equalTo(endTime)));
	}

	@Test
	public void shouldSetAndGetName() {
		// given
		MutableActivityBase<String> activity = new MutableActivityBase<>(
				"My Activity");

		// then
		assertThat(activity.getName(), is(equalTo("My Activity")));

		// when / then
		activity.setName("xxx");
		assertThat(activity.getName(), is(equalTo("xxx")));

		// when / then
		activity.setName("yyy");
		assertThat(activity.getName(), is(equalTo("yyy")));

		// when / then
		activity.setName(null);
		assertThat(activity.getName(), is(nullValue()));
	}

	@Test
	public void shouldSetAndGetNewTime() {

		// given
		MutableActivityBase<String> activity = new MutableActivityBase<>();
		Instant time = Instant.now().plus(Duration.ofDays(1));

		// when
		activity.setStartTime(time);

		// then
		assertThat(activity.getStartTime(), is(equalTo(time)));
	}

	@Test
	public void shouldSetAndGetUserObject() {
		// given
		MutableActivityBase<String> activity = new MutableActivityBase<>();

		// when
		activity.setUserObject("ttt");

		// then
		assertThat(activity.getUserObject(), is(equalTo("ttt")));
	}

	@Test
	public void shouldIntersectWhenIdentical() {

		// given
		MutableActivityBase<String> activity1 = new MutableActivityBase<>();
		MutableActivityBase<String> activity2 = new MutableActivityBase<>();

		Instant time = Instant.now();
		activity1.setStartTime(time);
		activity1.setDuration(Duration.ofDays(10));
		activity2.setStartTime(time);
		activity2.setDuration(Duration.ofDays(10));

		// when
		boolean intersection = ActivityHelper.intersect(activity1, activity2);

		// then
		assertThat(intersection, is(true));
	}

	@Test
	public void shouldIntersectWhenSecondActivityStartsInBetween() {

		// given
		MutableActivityBase<String> activity1 = new MutableActivityBase<>();
		MutableActivityBase<String> activity2 = new MutableActivityBase<>();

		Instant time = Instant.now();
		activity1.setStartTime(time);
		activity1.setDuration(Duration.ofDays(10));
		activity2.setStartTime(time.plus(Duration.ofDays(2)));
		activity2.setDuration(Duration.ofDays(10));

		// when
		boolean intersection = ActivityHelper.intersect(activity1, activity2);

		// then
		assertThat(intersection, is(true));
	}

	@Test
	public void shouldIntersectWhenFirstActivityStartsInBetween() {

		// given
		MutableActivityBase<String> activity1 = new MutableActivityBase<>();
		MutableActivityBase<String> activity2 = new MutableActivityBase<>();

		Instant time = Instant.now();
		activity1.setStartTime(time.plus(Duration.ofDays(4)));
		activity1.setDuration(Duration.ofDays(10));
		activity2.setDuration(Duration.ofDays(10));

		// when
		boolean intersection = ActivityHelper.intersect(activity1, activity2);

		// then
		assertThat(intersection, is(true));
	}
}
