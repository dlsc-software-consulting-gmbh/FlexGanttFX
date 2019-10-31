/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.repository;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.repository.ListActivityRepository.BinarySearchActivityIterator;
import com.flexganttfx.model.repository.ListActivityRepository.IteratorType;
import com.flexganttfx.model.repository.ListActivityRepository.LinearSearchActivityIterator;
import javafx.event.EventHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.flexganttfx.model.repository.ListActivityRepository.IteratorType.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ListActivityRepositoryTest implements
        EventHandler<RepositoryEvent> {

    class TestRow extends Row<TestRow, TestRow, MutableActivityBase<String>> {
    }

    private ListActivityRepository<MutableActivityBase<String>> repository;
    private IteratorType iteratorType;
    private Layer layer = new Layer("Default");
    private TestRow row = new TestRow();
    private ActivityRef<MutableActivityBase<String>> activityRef;
    private RepositoryEvent event;

    public ListActivityRepositoryTest(IteratorType iteratorType) {
        this.iteratorType = iteratorType;
    }

    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{{SIMPLE_ITERATOR},
                {BINARY_ITERATOR}, {LINEAR_ITERATOR}};

        return Arrays.asList(data);
    }

    @Before
    public void setup() {
        repository = new ListActivityRepository<>(
                iteratorType);

        repository.addEventHandler(this);
        MutableActivityBase<String> activity = new MutableActivityBase<>();
        activityRef = new ActivityRef<>(row, layer,
                activity);
    }

    @Test
    public void shouldAddAndRemoveActivity() {

        // given
        MutableActivityBase<String> activity = new MutableActivityBase<>(
                "Activity");
        ActivityRef<MutableActivityBase<String>> activityRef = new ActivityRef<>(
                row, layer, activity);

        // when
        repository.addActivity(activityRef);

        // then
        assertThat(repository.getActivities(layer).size(), is(not(0)));
        assertThat(repository.getActivities(layer).get(0).getName(),
                is(equalTo(activity.getName())));

        // when
        repository.removeActivity(activityRef);

        // then
        assertThat(repository.getActivities(layer).size(), is(0));
    }

    @Test
    public void shouldAddActivitiesAndSortByTime() {

        // given
        MutableActivityBase<String> activity1 = new MutableActivityBase<>(
                "Activity1");
        MutableActivityBase<String> activity2 = new MutableActivityBase<>(
                "Activity2");
        MutableActivityBase<String> activity3 = new MutableActivityBase<>(
                "Activity3");
        MutableActivityBase<String> activity4 = new MutableActivityBase<>(
                "Activity4");
        MutableActivityBase<String> activity5 = new MutableActivityBase<>(
                "Activity5");

        Instant time = Instant.now();
        activity1.setStartTime(time);
        activity2.setStartTime(time.plus(Duration.ofDays(1)));
        activity3.setStartTime(time.plus(Duration.ofDays(2)));
        activity4.setStartTime(time.plus(Duration.ofDays(3)));
        activity5.setStartTime(time.plus(Duration.ofDays(4)));

        // add in mixed order, sorting should happen
        List<MutableActivityBase<String>> activities = Arrays.asList(activity2,
                activity3, activity1, activity5, activity4);

        // when
        for (MutableActivityBase<String> activity : activities) {
            ActivityRef<MutableActivityBase<String>> activityRef = new ActivityRef<>(
                    row, layer, activity);
            repository.addActivity(activityRef);
        }

        // then
        assertThat(repository.getActivities(layer).size(), is(equalTo(5)));
        assertThat(repository.getActivities(layer).get(0).getName(),
                is(equalTo(activity1.getName())));
        assertThat(repository.getActivities(layer).get(1).getName(),
                is(equalTo(activity2.getName())));
        assertThat(repository.getActivities(layer).get(2).getName(),
                is(equalTo(activity3.getName())));
        assertThat(repository.getActivities(layer).get(3).getName(),
                is(equalTo(activity4.getName())));
        assertThat(repository.getActivities(layer).get(4).getName(),
                is(equalTo(activity5.getName())));
    }

    @Test
    public void shouldAddAtCorrectIndexBasedOnTime() {

        // given
        MutableActivityBase<String> activity1 = new MutableActivityBase<>(
                "Activity1");
        MutableActivityBase<String> activity2 = new MutableActivityBase<>(
                "Activity2");
        MutableActivityBase<String> activity3 = new MutableActivityBase<>(
                "Activity3");
        MutableActivityBase<String> activity4 = new MutableActivityBase<>(
                "Activity4");
        MutableActivityBase<String> activity5 = new MutableActivityBase<>(
                "Activity5");

        Instant time = Instant.now();
        activity1.setStartTime(time);
        activity2.setStartTime(time.plus(Duration.ofDays(1)));
        activity3.setStartTime(time.plus(Duration.ofDays(2)));
        activity4.setStartTime(time.plus(Duration.ofDays(3)));
        activity5.setStartTime(time.plus(Duration.ofDays(4)));

        // when

        repository.addActivity(new ActivityRef<>(
                row, layer, activity3));
        repository.addActivity(new ActivityRef<>(
                row, layer, activity1));
        repository.addActivity(new ActivityRef<>(
                row, layer, activity5));
        repository.addActivity(new ActivityRef<>(
                row, layer, activity4));
        repository.addActivity(new ActivityRef<>(
                row, layer, activity2));

        // then
        assertThat(repository.getActivities(layer).size(), is(equalTo(5)));
        assertThat(repository.getActivities(layer).get(0).getName(),
                is(equalTo(activity1.getName())));
        assertThat(repository.getActivities(layer).get(1).getName(),
                is(equalTo(activity2.getName())));
        assertThat(repository.getActivities(layer).get(2).getName(),
                is(equalTo(activity3.getName())));
        assertThat(repository.getActivities(layer).get(3).getName(),
                is(equalTo(activity4.getName())));
        assertThat(repository.getActivities(layer).get(4).getName(),
                is(equalTo(activity5.getName())));
    }

    @Test
    public void shouldReturnEmptyIterator() {

        // given
        Instant time = Instant.now();

        // when
        Iterator<? extends MutableActivityBase<?>> iter = repository
                .getActivities(layer, time, time.plus(Duration.ofDays(4)),
                        ChronoUnit.DAYS, null);

        // then
        assertThat(iter, is(notNullValue()));
        assertThat(iter.hasNext(), is(false));
    }

    @Test
    public void shouldReturnIterator() {

        // given
        MutableActivityBase<String> activity1 = new MutableActivityBase<>(
                "Activity1");
        MutableActivityBase<String> activity2 = new MutableActivityBase<>(
                "Activity2");
        MutableActivityBase<String> activity3 = new MutableActivityBase<>(
                "Activity3");
        MutableActivityBase<String> activity4 = new MutableActivityBase<>(
                "Activity4");
        MutableActivityBase<String> activity5 = new MutableActivityBase<>(
                "Activity5");

        Instant time = Instant.now();
        activity1.setStartTime(time);
        activity2.setStartTime(time.plus(Duration.ofDays(1)));
        activity3.setStartTime(time.plus(Duration.ofDays(2)));
        activity4.setStartTime(time.plus(Duration.ofDays(3)));
        activity5.setStartTime(time.plus(Duration.ofDays(4)));

        repository.addActivity(new ActivityRef<>(
                row, layer, activity3));
        repository.addActivity(new ActivityRef<>(
                row, layer, activity1));
        repository.addActivity(new ActivityRef<>(
                row, layer, activity5));
        repository.addActivity(new ActivityRef<>(
                row, layer, activity4));
        repository.addActivity(new ActivityRef<>(
                row, layer, activity2));

        // when
        Iterator<? extends MutableActivityBase<?>> iter = repository
                .getActivities(layer, time, time.plus(Duration.ofDays(5)),
                        ChronoUnit.DAYS, null);

        // then
        assertThat(iter, is(notNullValue()));
        assertThat(iter.next().getName(), is(equalTo(activity1.getName())));
        assertThat(iter.next().getName(), is(equalTo(activity2.getName())));
        assertThat(iter.next().getName(), is(equalTo(activity3.getName())));
        assertThat(iter.next().getName(), is(equalTo(activity4.getName())));
        assertThat(iter.next().getName(), is(equalTo(activity5.getName())));
        assertThat(iter.hasNext(), is(false));

        switch (iteratorType) {
            case BINARY_ITERATOR:
                assertThat(iter, is(instanceOf(BinarySearchActivityIterator.class)));
                break;
            case LINEAR_ITERATOR:
                assertThat(iter, is(instanceOf(LinearSearchActivityIterator.class)));
                break;
            case SIMPLE_ITERATOR:
            /*
			 * We have no clue what the default / standard iterator class is,
			 * but it should not be one of ours.
			 */
                assertThat(iter,
                        is(not(instanceOf(BinarySearchActivityIterator.class))));
                assertThat(iter,
                        is(not(instanceOf(LinearSearchActivityIterator.class))));
                break;
        }
    }

    @Test
    public void shouldFireActivityAddedEvent() {
        // given
        repository.addEventHandler(this);

        // when
        repository.addActivity(activityRef);

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getEventType(),
                is(equalTo(RepositoryEvent.ACTIVITY_ADDED)));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getRepository(), is(equalTo(repository)));
    }

    @Test
    public void shouldFireActivityRemovedEvent() {
        // given
        repository.addEventHandler(this);

        // when
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
