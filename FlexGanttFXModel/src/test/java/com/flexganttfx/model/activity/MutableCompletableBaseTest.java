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

import com.flexganttfx.model.util.ActivityHelper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class MutableCompletableBaseTest {

	@Test
	public void shouldCreateFullyInitializedNewActivity() {

		// when
		CompletableActivity completableActivity = new CompletableActivityBase<>();

		// then
		assertThat(completableActivity.getStartTime(), is(notNullValue()));
		assertThat(completableActivity.getEndTime(), is(notNullValue()));
	}

	@Test
	public void shouldCreateFullyInitializedNewActivityWithProperties() {

		// when
		CompletableActivity completableActivity = new CompletableActivityBase<>();

		// then
		assertThat(completableActivity.getStartTime(), is(notNullValue()));
		assertThat(completableActivity.getEndTime(), is(notNullValue()));
	}

	@Test
	public void shouldCreateFullyInitializedNewActivityWithParameters() {

		// when
		String name = "My CompletableActivity";
		Instant startTime = Instant.now().plusMillis(100000);
		Instant endTime = Instant.now().plusMillis(200000);

		CompletableActivity completableActivity = new CompletableActivityBase<>(
				name, startTime, endTime);

		// then
		assertThat(completableActivity.getName(), is(equalTo(name)));
		assertThat(completableActivity.getStartTime(), is(equalTo(startTime)));
		assertThat(completableActivity.getEndTime(), is(equalTo(endTime)));
	}

	@Test
	public void shouldSetAndGetName() {
		// given
		MutableCompletableActivityBase<String> completable = new MutableCompletableActivityBase<>(
				"My CompletableActivity");

		// then
		assertThat(completable.getName(), is(equalTo("My CompletableActivity")));

		// when / then
		completable.setName("xxx");
		assertThat(completable.getName(), is(equalTo("xxx")));

		// when / then
		completable.setName("yyy");
		assertThat(completable.getName(), is(equalTo("yyy")));

		// when / then
		completable.setName(null);
		assertThat(completable.getName(), is(nullValue()));
	}

	@Test
	public void shouldSetAndGetNewStartTime() {

		// given
		MutableCompletableActivityBase<String> completable = new MutableCompletableActivityBase<>(
				"My CompletableActivity");
		Instant time = Instant.now().plus(Duration.ofDays(1));

		// when
		completable.setStartTime(time);

		// then
		assertThat(completable.getStartTime(), is(equalTo(time)));
	}

	@Test
	public void shouldSetAndGetNewEndTime() {

		// given
		MutableCompletableActivityBase<String> completable = new MutableCompletableActivityBase<>(
				"My CompletableActivity");
		Instant time = Instant.now().plus(Duration.ofDays(1));

		// when
		completable.setEndTime(time);

		// then
		assertThat(completable.getEndTime(), is(equalTo(time)));
	}

	@Test
	public void shouldSetAndGetUserObject() {
		// given
		MutableCompletableActivityBase<String> completable = new MutableCompletableActivityBase<>(
				"My CompletableActivity");

		// when
		completable.setUserObject("ttt");

		// then
		assertThat(completable.getUserObject(), is(equalTo("ttt")));
	}

	@Test
	public void shouldIntersectWhenIdentical() {

		// given
		MutableCompletableActivityBase<String> activity1 = new MutableCompletableActivityBase<>(
				"My CompletableActivity");
		MutableCompletableActivityBase<String> activity2 = new MutableCompletableActivityBase<>();

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
		MutableCompletableActivityBase<String> activity1 = new MutableCompletableActivityBase<>();
		MutableCompletableActivityBase<String> activity2 = new MutableCompletableActivityBase<>();

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
		MutableCompletableActivityBase<String> activity1 = new MutableCompletableActivityBase<>();
		MutableCompletableActivityBase<String> activity2 = new MutableCompletableActivityBase<>();

		Instant time = Instant.now();
		activity1.setStartTime(time.plus(Duration.ofDays(4)));
		activity1.setDuration(Duration.ofDays(10));
		activity2.setDuration(Duration.ofDays(10));

		// when
		boolean intersection = ActivityHelper.intersect(activity1, activity2);

		// then
		assertThat(intersection, is(true));
	}

	@Test
	public void shouldHaveCorrectInitialValues() {
		// given
		CompletableActivity completableActivity = new CompletableActivityBase<String>();

		// when
		assertThat(completableActivity.getPercentageComplete() == 0, is(true));
		assertThat(completableActivity.getName(), is(nullValue()));
	}
}
