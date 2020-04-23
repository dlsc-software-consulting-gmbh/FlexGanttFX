/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model;

import com.flexganttfx.model.exception.IllegalLineIndexException;
import org.junit.Test;

import java.util.function.Predicate;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RowTest {

	class TestRow extends Row<TestRow, TestRow, Activity> {

	}

	@Test
	public void shouldReturnParentAndPath() {
		// given
		TestRow row = new TestRow();
		TestRow root = new TestRow();
		TestRow sub1 = new TestRow();
		TestRow sub2 = new TestRow();

		root.getChildren().add(sub1);
		sub1.getChildren().add(sub2);
		sub2.getChildren().add(row);

		// when
		Row<?, ?, ?>[] path = row.getPath();

		// then
		assertThat(path, is(notNullValue()));
		assertThat(path[0].hashCode(), is(equalTo(root.hashCode())));
		assertThat(path[1].hashCode(), is(equalTo(sub1.hashCode())));
		assertThat(path[2].hashCode(), is(equalTo(sub2.hashCode())));

		assertThat(row.getParent().hashCode(), is(equalTo(sub2.hashCode())));
		assertThat(sub2.getParent().hashCode(), is(equalTo(sub1.hashCode())));
		assertThat(sub1.getParent().hashCode(), is(equalTo(root.hashCode())));
		assertThat(root.getParent(), is(nullValue()));

		// when
		sub2.getChildren().remove(row);
		assertThat(row.getParent(), is(nullValue()));
	}

	@Test
	public void shouldReturnLineLocations() throws IllegalLineIndexException {
		// given
		TestRow row = new TestRow();

		// when
		row.setHeight(100);
		row.setLineCount(10);

		// then
		for (int i = 0; i < 10; i++) {
			assertThat(row.getLineLocation(i), is(equalTo((double) i * 10)));
		}
	}

	@Test
	public void shouldReturnLineHeights() throws IllegalLineIndexException {
		// given
		TestRow row = new TestRow();

		// when
		row.setHeight(100);
		row.setLineCount(10);

		// then
		for (int i = 0; i < 10; i++) {
			assertThat(row.getLineHeight(i), is(equalTo((double) 10)));
		}
	}

	@Test(expected = IllegalLineIndexException.class)
	public void shouldThrowIllegalLineIndexExceptionWhenAskingForLineLocationWithNoLines()
			throws IllegalLineIndexException {
		// given
		TestRow row = new TestRow();

		// when
		row.getLineLocation(5);
	}

	@Test(expected = IllegalLineIndexException.class)
	public void shouldThrowIllegalLineIndexExceptionWhenAskingForLineHeightWithNoLines()
			throws IllegalLineIndexException {
		// given
		TestRow row = new TestRow();

		// when
		row.getLineHeight(5);
	}

	@Test(expected = IllegalLineIndexException.class)
	public void shouldThrowIllegalLineIndexExceptionWhenAskingForLineLocationWithLines()
			throws IllegalLineIndexException {
		// given
		TestRow row = new TestRow();
		row.setLineCount(4);

		// when
		row.getLineLocation(5);
	}

	@Test(expected = IllegalLineIndexException.class)
	public void shouldThrowIllegalLineIndexExceptionWhenAskingForLineHeightWithLines()
			throws IllegalLineIndexException {
		// given
		TestRow row = new TestRow();
		row.setLineCount(4);

		// when
		row.getLineHeight(5);
	}

	@Test
	public void shouldHaveFilteredChild() {
		// given
		TestRow row = new TestRow();

		TestRow child1 = new TestRow();
		child1.setName("mychild1");
		row.getChildren().add(child1);

		TestRow child2 = new TestRow();
		child2.setName("mychild2");
		row.getChildren().add(child2);

		Predicate<TestRow> filter1 = r -> r.getName().equals("mychild1");
		Predicate<TestRow> filter2 = r -> r.getName().equals("mychild2");

		// when
		boolean hasChild1 = row.hasChildren(filter1);
		boolean hasChild2 = row.hasChildren(filter2);

		// then
		assertThat(hasChild1, is(true));
		assertThat(hasChild2, is(true));
	}

	@Test
	public void shouldNotHaveFilteredChild() {
		// given
		TestRow row = new TestRow();

		TestRow child1 = new TestRow();
		child1.setName("mychild1");
		row.getChildren().add(child1);

		TestRow child2 = new TestRow();
		child2.setName("mychild2");
		row.getChildren().add(child2);

		Predicate<TestRow> filter1 = r -> r.getName().equals("unknown");
		Predicate<TestRow> filter2 = r -> r.getName().equals("unknown");

		// when
		boolean hasChild1 = row.hasChildren(filter1);
		boolean hasChild2 = row.hasChildren(filter2);

		// then
		assertThat(hasChild1, is(false));
		assertThat(hasChild2, is(false));
	}
}
