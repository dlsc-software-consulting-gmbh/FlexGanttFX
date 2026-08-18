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
package com.flexganttfx.model.repository;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.repository.ListActivityRepository.BinarySearchActivityIterator;
import com.flexganttfx.model.repository.ListActivityRepository.IteratorType;
import com.flexganttfx.model.repository.ListActivityRepository.LinearSearchActivityIterator;
import javafx.event.EventHandler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class ListActivityRepositoryTest implements EventHandler<RepositoryEvent> {

    class TestRow extends Row<TestRow, TestRow, MutableActivityBase<String>> {}

    private ListActivityRepository<MutableActivityBase<String>> repository;
    private final Layer layer = new Layer("Default");
    private final TestRow row = new TestRow();
    private ActivityRef<MutableActivityBase<String>> activityRef;
    private RepositoryEvent event;

    public void setup(IteratorType iteratorType) {
        repository = new ListActivityRepository<>(iteratorType);
        repository.addEventHandler(this);
        MutableActivityBase<String> activity = new MutableActivityBase<>();
        activityRef = new ActivityRef<>(row, layer, activity);
    }

    @ParameterizedTest
    @EnumSource(IteratorType.class)
    public void shouldAddAndRemoveActivity(IteratorType iteratorType) {
        setup(iteratorType);

        // given
        MutableActivityBase<String> activity = new MutableActivityBase<>("Activity");
        ActivityRef<MutableActivityBase<String>> activityRef = new ActivityRef<>(row, layer, activity);

        // when
        repository.addActivity(activityRef);

        // then
        assertThat(repository.getActivities(layer).size(), is(not(0)));
        assertThat(repository.getActivities(layer).get(0).getName(), is(equalTo(activity.getName())));

        // when
        repository.removeActivity(activityRef);

        // then
        assertThat(repository.getActivities(layer).size(), is(0));
    }

    @ParameterizedTest
    @EnumSource(IteratorType.class)
    public void shouldAddActivitiesAndSortByTime(IteratorType iteratorType) {
        setup(iteratorType);

        // given
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

        // add in mixed order, sorting should happen
        List<MutableActivityBase<String>> activities = Arrays.asList(activity2, activity3, activity1, activity5, activity4);

        // when
        for (MutableActivityBase<String> activity : activities) {
            ActivityRef<MutableActivityBase<String>> activityRef = new ActivityRef<>(row, layer, activity);
            repository.addActivity(activityRef);
        }

        // then
        assertThat(repository.getActivities(layer).size(), is(equalTo(5)));
        assertThat(repository.getActivities(layer).get(0).getName(), is(equalTo(activity1.getName())));
        assertThat(repository.getActivities(layer).get(1).getName(), is(equalTo(activity2.getName())));
        assertThat(repository.getActivities(layer).get(2).getName(), is(equalTo(activity3.getName())));
        assertThat(repository.getActivities(layer).get(3).getName(), is(equalTo(activity4.getName())));
        assertThat(repository.getActivities(layer).get(4).getName(), is(equalTo(activity5.getName())));
    }

    @ParameterizedTest
    @EnumSource(IteratorType.class)
    public void shouldAddAtCorrectIndexBasedOnTime(IteratorType iteratorType) {
        setup(iteratorType);

        // given
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

        // when

        repository.addActivity(new ActivityRef<>(row, layer, activity3));
        repository.addActivity(new ActivityRef<>(row, layer, activity1));
        repository.addActivity(new ActivityRef<>(row, layer, activity5));
        repository.addActivity(new ActivityRef<>(row, layer, activity4));
        repository.addActivity(new ActivityRef<>(row, layer, activity2));

        // then
        assertThat(repository.getActivities(layer).size(), is(equalTo(5)));
        assertThat(repository.getActivities(layer).get(0).getName(), is(equalTo(activity1.getName())));
        assertThat(repository.getActivities(layer).get(1).getName(), is(equalTo(activity2.getName())));
        assertThat(repository.getActivities(layer).get(2).getName(), is(equalTo(activity3.getName())));
        assertThat(repository.getActivities(layer).get(3).getName(), is(equalTo(activity4.getName())));
        assertThat(repository.getActivities(layer).get(4).getName(), is(equalTo(activity5.getName())));
    }

    @ParameterizedTest
    @EnumSource(IteratorType.class)
    public void shouldReturnEmptyIterator(IteratorType iteratorType) {
        setup(iteratorType);

        // given
        Instant time = Instant.now();

        // when
        Iterator<? extends MutableActivityBase<?>> iter = repository.getActivities(layer, time, time.plus(Duration.ofDays(4)), ChronoUnit.DAYS, null);

        // then
        assertThat(iter, is(notNullValue()));
        assertThat(iter.hasNext(), is(false));
    }

    @ParameterizedTest
    @EnumSource(IteratorType.class)
    public void shouldReturnIterator(IteratorType iteratorType) {
        setup(iteratorType);

        // given
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

        repository.addActivity(new ActivityRef<>(row, layer, activity3));
        repository.addActivity(new ActivityRef<>(row, layer, activity1));
        repository.addActivity(new ActivityRef<>(row, layer, activity5));
        repository.addActivity(new ActivityRef<>(row, layer, activity4));
        repository.addActivity(new ActivityRef<>(row, layer, activity2));

        // when
        Iterator<? extends MutableActivityBase<?>> iter = repository.getActivities(layer, time, time.plus(Duration.ofDays(5)), ChronoUnit.DAYS, null);

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
                assertThat(iter, is(not(instanceOf(BinarySearchActivityIterator.class))));
                assertThat(iter, is(not(instanceOf(LinearSearchActivityIterator.class))));
                break;
        }
    }

    @ParameterizedTest
    @EnumSource(IteratorType.class)
    public void shouldFireActivityAddedEvent(IteratorType iteratorType) {
        setup(iteratorType);

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

    @ParameterizedTest
    @EnumSource(IteratorType.class)
    public void shouldFireActivityRemovedEvent(IteratorType iteratorType) {
        setup(iteratorType);

        // given
        repository.addEventHandler(this);

        // when
        repository.removeActivity(activityRef);

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getEventType(), is(equalTo(RepositoryEvent.ACTIVITY_REMOVED)));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getRepository(), is(equalTo(repository)));
    }

    @ParameterizedTest
    @EnumSource(IteratorType.class)
    public void shouldFireRepositoryChangedEventAfterClearingActivities(IteratorType iteratorType) {
        setup(iteratorType);

        // given
        repository.addEventHandler(this);

        // when
        repository.clearActivities();

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getEventType(), is(equalTo(RepositoryEvent.REPOSITORY_CHANGED)));
        assertThat(event.getActivityRef(), is(nullValue()));
        assertThat(event.getRepository(), is(equalTo(repository)));
    }

    @ParameterizedTest
    @EnumSource(IteratorType.class)
    public void shouldFireRepositoryChangedEventAfterClearingActivitiesOnGivenLayer(IteratorType iteratorType) {
        setup(iteratorType);

        // given
        repository.addEventHandler(this);

        // when
        repository.clearActivities(layer);

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getEventType(), is(equalTo(RepositoryEvent.REPOSITORY_CHANGED)));
        assertThat(event.getActivityRef(), is(nullValue()));
        assertThat(event.getRepository(), is(equalTo(repository)));
    }

    @Override
    public void handle(RepositoryEvent event) {
        this.event = event;
    }
}
