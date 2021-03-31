/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.repository;

import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.repository.ListActivityRepository.LinearSearchActivityIterator;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class LinearSearchActivityIteratorTest {

	@Test
	public void shouldCreateEmptyIterator() {

		// given
		List<MutableActivityBase<String>> activities = new ArrayList<>();
		Instant time = Instant.now();

		// when
		LinearSearchActivityIterator<MutableActivityBase<String>> iterator = new LinearSearchActivityIterator<>(activities, time, time.plus(Duration.ofDays(10)));

		// then
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldCreateIteratorThatReturnsAllActivities() {

		// given
		List<MutableActivityBase<String>> activities = new ArrayList<>();

		MutableActivityBase<String> activity1 = new MutableActivityBase<>("Activity1");
		MutableActivityBase<String> activity2 = new MutableActivityBase<>("Activity2");
		MutableActivityBase<String> activity3 = new MutableActivityBase<>("Activity3");
		MutableActivityBase<String> activity4 = new MutableActivityBase<>("Activity4");
		MutableActivityBase<String> activity5 = new MutableActivityBase<>("Activity5");

		Instant time = Instant.now();
		activity1.setStartTime(time);
		activity2.setStartTime(time.plus(Duration.ofDays(1)));
		activity3.setStartTime(time.plus(Duration.ofDays(2)));
		activity4.setStartTime(time.plus(Duration.ofDays(3)));
		activity5.setStartTime(time.plus(Duration.ofDays(4)));

		activities.add(activity1);
		activities.add(activity2);
		activities.add(activity3);
		activities.add(activity4);
		activities.add(activity5);

		// when
		LinearSearchActivityIterator<MutableActivityBase<String>> iterator = new LinearSearchActivityIterator<>(
				activities, time, time.plus(Duration.ofDays(10)));

		// then
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity1)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity2)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity3)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity4)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity5)));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldCreateIteratorThatReturnsFirstThreeActivities() {

		// given
		List<MutableActivityBase<String>> activities = new ArrayList<>();

		MutableActivityBase<String> activity1 = new MutableActivityBase<>("Activity1");
		MutableActivityBase<String> activity2 = new MutableActivityBase<>("Activity2");
		MutableActivityBase<String> activity3 = new MutableActivityBase<>("Activity3");
		MutableActivityBase<String> activity4 = new MutableActivityBase<>("Activity4");
		MutableActivityBase<String> activity5 = new MutableActivityBase<>("Activity5");

		Instant time = Instant.now().truncatedTo(DAYS);

		activity1.setStartTime(time);
		activity2.setStartTime(time.plus(Duration.ofDays(1)));
		activity3.setStartTime(time.plus(Duration.ofDays(2)));
		activity4.setStartTime(time.plus(Duration.ofDays(3)));
		activity5.setStartTime(time.plus(Duration.ofDays(4)));

		activities.add(activity1);
		activities.add(activity2);
		activities.add(activity3);
		activities.add(activity4);
		activities.add(activity5);

		activity1.setDuration(Duration.ofHours(1));
		activity2.setDuration(Duration.ofHours(1));
		activity3.setDuration(Duration.ofHours(1));
		activity4.setDuration(Duration.ofHours(1));
		activity5.setDuration(Duration.ofHours(1));

		// when
		LinearSearchActivityIterator<MutableActivityBase<String>> iterator = new LinearSearchActivityIterator<>(activities, time, time.plus(Duration.ofDays(3)));

		// then
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity1)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity2)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity3)));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldCreateIteratorThatReturnsLastThreeActivities() {

		// given
		List<MutableActivityBase<String>> activities = new ArrayList<>();

		MutableActivityBase<String> activity1 = new MutableActivityBase<>("Activity1");
		MutableActivityBase<String> activity2 = new MutableActivityBase<>("Activity2");
		MutableActivityBase<String> activity3 = new MutableActivityBase<>("Activity3");
		MutableActivityBase<String> activity4 = new MutableActivityBase<>("Activity4");
		MutableActivityBase<String> activity5 = new MutableActivityBase<>("Activity5");

		Instant time = Instant.now().truncatedTo(DAYS);

		activity1.setStartTime(time);
		activity2.setStartTime(time.plus(Duration.ofDays(1)));
		activity3.setStartTime(time.plus(Duration.ofDays(2)));
		activity4.setStartTime(time.plus(Duration.ofDays(3)));
		activity5.setStartTime(time.plus(Duration.ofDays(4)));

		activity1.setDuration(Duration.ofHours(1));
		activity2.setDuration(Duration.ofHours(1));
		activity3.setDuration(Duration.ofHours(1));
		activity4.setDuration(Duration.ofHours(1));
		activity5.setDuration(Duration.ofHours(1));

		activities.add(activity1);
		activities.add(activity2);
		activities.add(activity3);
		activities.add(activity4);
		activities.add(activity5);

		// when
		LinearSearchActivityIterator<MutableActivityBase<String>> iterator = new LinearSearchActivityIterator<>(
				activities, time.plus(Duration.ofDays(2)).truncatedTo(DAYS),
				time.plus(Duration.ofDays(5)).truncatedTo(DAYS));

		// then
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity3)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity4)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity5)));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldCreateIteratorThatReturnsMiddleThreeActivities() {

		// given
		List<MutableActivityBase<String>> activities = new ArrayList<>();

		MutableActivityBase<String> activity1 = new MutableActivityBase<>("Activity1");
		MutableActivityBase<String> activity2 = new MutableActivityBase<>("Activity2");
		MutableActivityBase<String> activity3 = new MutableActivityBase<>("Activity3");
		MutableActivityBase<String> activity4 = new MutableActivityBase<>("Activity4");
		MutableActivityBase<String> activity5 = new MutableActivityBase<>("Activity5");

		Instant time = Instant.now().truncatedTo(DAYS);

		activity1.setStartTime(time);
		activity2.setStartTime(time.plus(Duration.ofDays(1)));
		activity3.setStartTime(time.plus(Duration.ofDays(2)));
		activity4.setStartTime(time.plus(Duration.ofDays(3)));
		activity5.setStartTime(time.plus(Duration.ofDays(4)));

		activity1.setDuration(Duration.ofHours(1));
		activity2.setDuration(Duration.ofHours(1));
		activity3.setDuration(Duration.ofHours(1));
		activity4.setDuration(Duration.ofHours(1));
		activity5.setDuration(Duration.ofHours(1));

		activities.add(activity1);
		activities.add(activity2);
		activities.add(activity3);
		activities.add(activity4);
		activities.add(activity5);

		// when
		LinearSearchActivityIterator<MutableActivityBase<String>> iterator = new LinearSearchActivityIterator<>(
				activities, time.plus(Duration.ofDays(1)).truncatedTo(DAYS),
				time.plus(Duration.ofDays(4)).truncatedTo(DAYS));

		// then
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity2)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity3)));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(equalTo(activity4)));
		assertThat(iterator.hasNext(), is(false));
	}
}
