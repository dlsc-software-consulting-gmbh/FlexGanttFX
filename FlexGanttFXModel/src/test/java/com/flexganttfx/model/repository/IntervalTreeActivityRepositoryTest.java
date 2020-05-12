/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.repository;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.ActivityBase;
import javafx.event.EventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class IntervalTreeActivityRepositoryTest implements
		EventHandler<RepositoryEvent> {

	private IntervalTreeActivityRepository<Activity> repository;
	private Layer layer;
	private ActivityRef<Activity> activityRef;

	private RepositoryEvent event;

	@BeforeEach
	public void setup() {
		repository = new IntervalTreeActivityRepository<>();
		repository.addEventHandler(this);
		Activity activity = new ActivityBase<>();
		Row<?, ?, Activity> row = new Row<Row<?, ?, Activity>, Row<?, ?, ?>, Activity>() {
		};
		layer = new Layer("Test");
		activityRef = new ActivityRef<>(row, layer, activity);
	}

	@Test
	public void shouldFireActivityAddedEvent() {
		// given
		repository.addEventHandler(this);

		// when
		repository.addActivity(activityRef);

		// then
		assertThat(event, is(notNullValue()));
		assertThat(event.getEventType(), is(equalTo(RepositoryEvent.ACTIVITY_ADDED)));
		assertThat(event.getActivityRef(), is(equalTo(activityRef)));
		assertThat(event.getRepository(), is(equalTo(repository)));
	}

	@Test
	public void shouldFireActivityRemovedEvent() {
		// given
		repository.addEventHandler(this);

		// when
		repository.addActivity(activityRef);
		repository.removeActivity(activityRef);

		// then
		assertThat(event, is(notNullValue()));
		assertThat(event.getEventType(),
				is(equalTo(RepositoryEvent.ACTIVITY_REMOVED)));
		assertThat(event.getActivityRef(), is(equalTo(activityRef)));
		assertThat(event.getRepository(), is(equalTo(repository)));
	}

	@Test
	public void shouldFireRepositoryChangedEventAfterClearingActivities() {
		// given
		repository.addEventHandler(this);

		// when
		repository.clearActivities();

		// then
		assertThat(event, is(notNullValue()));
		assertThat(event.getEventType(),
				is(equalTo(RepositoryEvent.REPOSITORY_CHANGED)));
		assertThat(event.getActivityRef(), is(nullValue()));
		assertThat(event.getRepository(), is(equalTo(repository)));
	}

	@Test
	public void shouldFireRepositoryChangedEventAfterClearingActivitiesOnGivenLayer() {
		// given
		repository.addEventHandler(this);

		// when
		repository.clearActivities(layer);

		// then
		assertThat(event, is(notNullValue()));
		assertThat(event.getEventType(),
				is(equalTo(RepositoryEvent.REPOSITORY_CHANGED)));
		assertThat(event.getActivityRef(), is(nullValue()));
		assertThat(event.getRepository(), is(equalTo(repository)));
	}

	@Override
	public void handle(RepositoryEvent event) {
		this.event = event;
	}
}
