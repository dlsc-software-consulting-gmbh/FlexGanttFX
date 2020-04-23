/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.Collection;

import org.junit.Test;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.activity.ActivityBase;

public class IntervalTreeTest {

	@Test
	public void should() {
		// given
		IntervalTree<Activity> tree = new IntervalTree<>();
		for (int i = 0; i <= 1000; i += 100) {
			Activity activity = new ActivityBase<>("Activity " + i,
					Instant.ofEpochMilli(i), Instant.ofEpochMilli(i + 99));
			tree.add(activity);
		}

		// when
		Collection<Activity> result = tree
				.getIntersectingObjects(new TimeInterval(Instant
						.ofEpochMilli(0), Instant.ofEpochMilli(1001)));

		// then
		assertThat(result.size(), is(equalTo(11)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant
				.ofEpochMilli(0), Instant.ofEpochMilli(0)));

		// then
		assertThat(result.size(), is(equalTo(1)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant
				.ofEpochMilli(1000), Instant.ofEpochMilli(1000)));

		// then
		assertThat(result.size(), is(equalTo(1)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant
				.ofEpochMilli(0), Instant.ofEpochMilli(200)));

		// then
		assertThat(result.size(), is(equalTo(2)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant
				.ofEpochMilli(500), Instant.ofEpochMilli(500)));

		// then
		assertThat(result.size(), is(equalTo(1)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant
				.ofEpochMilli(500), Instant.ofEpochMilli(1000)));

		// then
		assertThat(result.size(), is(equalTo(5)));

		for (int i = 0; i <= 1000; i += 1) {
			// when
			result = tree.getIntersectingObjects(new TimeInterval(Instant
					.ofEpochMilli(i), Instant.ofEpochMilli(i)));

			// then
			assertThat(result.size(), is(equalTo(1)));
		}

	}
}
