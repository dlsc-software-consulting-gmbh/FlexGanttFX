/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.activity.ActivityBase;
import de.sandec.jmemorybuddy.JMemoryBuddy;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class IntervalTreeTest {

	@Test
	public void shouldCollectIntervalTree() {
		JMemoryBuddy.memoryTest(checker -> {
			IntervalTree referenced = new IntervalTree() {};
			checker.setAsReferenced(referenced);

			IntervalTree notReferenced = new IntervalTree() {};

			checker.assertCollectable(notReferenced);
			checker.assertNotCollectable(referenced);
		});
	}

	@Test
	public void shouldCollectActivityFromIntervalTree() {
		JMemoryBuddy.memoryTest(checker -> {
			IntervalTree tree = new IntervalTree() {};
			checker.setAsReferenced(tree);

			ActivityBase notReferenced = new ActivityBase();
			tree.add(notReferenced);
			tree.remove(notReferenced);

			checker.assertCollectable(notReferenced);
		});
	}

	@Test
	public void should() {
		// given
		IntervalTree<Activity> tree = new IntervalTree<>();
		for (int i = 0; i <= 1000; i += 100) {
			Activity activity = new ActivityBase<>("Activity " + i, Instant.ofEpochMilli(i), Instant.ofEpochMilli(i + 99));
			tree.add(activity);
		}

		// when
		Collection<Activity> result = tree.getIntersectingObjects(new TimeInterval(Instant.ofEpochMilli(0), Instant.ofEpochMilli(1001)));

		// then
		assertThat(result.size(), is(equalTo(11)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant.ofEpochMilli(0), Instant.ofEpochMilli(0)));

		// then
		assertThat(result.size(), is(equalTo(1)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant.ofEpochMilli(1000), Instant.ofEpochMilli(1000)));

		// then
		assertThat(result.size(), is(equalTo(1)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant.ofEpochMilli(0), Instant.ofEpochMilli(200)));

		// then
		assertThat(result.size(), is(equalTo(2)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant.ofEpochMilli(500), Instant.ofEpochMilli(500)));

		// then
		assertThat(result.size(), is(equalTo(1)));

		// when
		result = tree.getIntersectingObjects(new TimeInterval(Instant.ofEpochMilli(500), Instant.ofEpochMilli(1000)));

		// then
		assertThat(result.size(), is(equalTo(5)));

		for (int i = 0; i <= 1000; i += 1) {
			// when
			result = tree.getIntersectingObjects(new TimeInterval(Instant.ofEpochMilli(i), Instant.ofEpochMilli(i)));

			// then
			assertThat(result.size(), is(equalTo(1)));
		}
	}
}
