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
package com.flexganttfx.model.util;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.activity.ActivityBase;
import de.sandec.jmemorybuddy.JMemoryBuddy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class IntervalTreeTest {

    private static final Instant START = Instant.ofEpochMilli(0);
    private static final Instant END = Instant.ofEpochMilli(10);

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

    @Test
    public void shouldReturnNullForEarliestAndLatestTimeWhenEmpty() {
        IntervalTree<Activity> tree = new IntervalTree<>();

        assertThat(tree.getEarliestTimeUsed(), is(nullValue()));
        assertThat(tree.getLatestTimeUsed(), is(nullValue()));
    }

    @Test
    public void shouldReturnEarliestAndLatestTimeUsed() {
        IntervalTree<Activity> tree = new IntervalTree<>();
        Activity first = new ActivityBase<>("first", START.plus(Duration.ofMillis(5)), END.plus(Duration.ofMillis(5)));
        Activity earliest = new ActivityBase<>("earliest", START.minus(Duration.ofMillis(10)), END.minus(Duration.ofMillis(10)));
        Activity latest = new ActivityBase<>("latest", START.plus(Duration.ofMillis(20)), END.plus(Duration.ofMillis(20)));

        tree.add(first);
        tree.add(earliest);
        tree.add(latest);

        assertThat(tree.getEarliestTimeUsed(), is(equalTo(START.minus(Duration.ofMillis(10)))));
        assertThat(tree.getLatestTimeUsed(), is(equalTo(END.plus(Duration.ofMillis(20)))));
    }

    @Test
    public void shouldClearTreeAndResetTimes() {
        IntervalTree<Activity> tree = new IntervalTree<>();
        tree.add(new ActivityBase<>("activity", START, END));

        tree.clear();

        assertThat(tree.size(), is(equalTo(0L)));
        assertThat(tree.getEarliestTimeUsed(), is(nullValue()));
        assertThat(tree.getLatestTimeUsed(), is(nullValue()));
        assertThat(tree.getIntersectingObjects(START.toEpochMilli(), END.toEpochMilli()).isEmpty(), is(true));
    }

    @Test
    public void shouldReturnFalseWhenRemovingUnknownActivity() {
        IntervalTree<Activity> tree = new IntervalTree<>();
        Activity unknown = new ActivityBase<>("unknown", START, END);

        assertThat(tree.remove(unknown), is(false));
    }

    @Test
    public void shouldThrowWhenAddingNullActivity() {
        IntervalTree<Activity> tree = new IntervalTree<>();

        Assertions.assertThrows(IllegalArgumentException.class, () -> tree.add(null));
    }

    @Test
    public void shouldReturnFalseWhenRemoveIfDoesNotMatchAnything() {
        IntervalTree<Activity> tree = new IntervalTree<>();
        tree.add(new ActivityBase<>("keep", START, END));

        boolean removed = tree.removeIf(activity -> activity.getName().startsWith("remove"));

        assertThat(removed, is(false));
        assertThat(tree.size(), is(equalTo(1L)));
    }

    @Test
    public void shouldRemovePeriodAndReturnRemovedActivities() {
        IntervalTree<Activity> tree = new IntervalTree<>();
        Activity left = new ActivityBase<>("left", Instant.ofEpochMilli(0), Instant.ofEpochMilli(9));
        Activity middle = new ActivityBase<>("middle", Instant.ofEpochMilli(10), Instant.ofEpochMilli(19));
        Activity right = new ActivityBase<>("right", Instant.ofEpochMilli(20), Instant.ofEpochMilli(29));

        tree.add(left);
        tree.add(middle);
        tree.add(right);

        Collection<Activity> removed = tree.removePeriod(new TimeInterval(Instant.ofEpochMilli(5), Instant.ofEpochMilli(20)));

        assertThat(removed, is(notNullValue()));
        assertThat(removed.size(), is(equalTo(2)));
        assertThat(new ArrayList<>(removed), containsInAnyOrder(left, middle));
        assertThat(tree.size(), is(equalTo(1L)));
        assertThat(new ArrayList<>(tree.getIntersectingObjects(Long.MIN_VALUE, Long.MAX_VALUE)), contains(right));
    }

    @Test
    public void shouldAddActivitiesWhenOnlyHashCodeCollides() {
        IntervalTree<Activity> tree = new IntervalTree<>();
        TestActivity first = new TestActivity("first", "id-1", 42);
        TestActivity secondWithSameHash = new TestActivity("second", "id-2", 42);

        assertThat(tree.add(first), is(true));
        assertThat(tree.add(secondWithSameHash), is(true));
        assertThat(tree.size(), is(equalTo(2L)));
    }

    @Test
    public void shouldNotAddDuplicateWhenHashIdNameAndBoundsAreEqual() {
        IntervalTree<Activity> tree = new IntervalTree<>();
        TestActivity first = new TestActivity("same", "same-id", 42);
        TestActivity duplicateByTreeKey = new TestActivity("same", "same-id", 42);

        assertThat(tree.add(first), is(true));
        assertThat(tree.add(duplicateByTreeKey), is(false));
        assertThat(tree.size(), is(equalTo(1L)));
    }

    @Test
    public void shouldSupportActivitiesWithNullBounds() {
        IntervalTree<Activity> tree = new IntervalTree<>();
        Activity unboundedStart = new NullBoundActivity("unbounded-start", null, END);
        Activity unboundedEnd = new NullBoundActivity("unbounded-end", START, null);
        Activity bounded = new ActivityBase<>("bounded", START.plus(Duration.ofMillis(1)), END.minus(Duration.ofMillis(1)));

        tree.add(unboundedStart);
        tree.add(unboundedEnd);
        tree.add(bounded);

        Collection<Activity> activities = tree.getIntersectingObjects(Long.MIN_VALUE, Long.MAX_VALUE);

        assertThat(activities.size(), is(equalTo(3)));
        assertThat(new ArrayList<>(activities), containsInAnyOrder(unboundedStart, unboundedEnd, bounded));
    }

    @Test
    public void shouldHandleHashCodeComparisonWithoutIntegerOverflow() {
        IntervalTree<Activity> tree = new IntervalTree<>();

        Activity lowHashActivity = new TestActivity("low", Integer.MIN_VALUE);
        Activity rootActivity = new TestActivity("root", 1);

        tree.add(rootActivity);
        tree.add(lowHashActivity);

        assertThat(tree.remove(lowHashActivity), is(true));
        assertThat(tree.size(), is(equalTo(1L)));
    }

    @Test
    public void shouldRemoveMatchingActivitiesViaRemoveIf() {
        IntervalTree<Activity> tree = new IntervalTree<>();
        TestActivity keep = new TestActivity("keep", 1);
        TestActivity removeFirst = new TestActivity("remove-1", 2);
        TestActivity removeSecond = new TestActivity("remove-2", 3);

        tree.add(keep);
        tree.add(removeFirst);
        tree.add(removeSecond);

        boolean removed = tree.removeIf(activity -> activity.getName().startsWith("remove"));

        assertThat(removed, is(true));
        assertThat(tree.size(), is(equalTo(1L)));

        List<Activity> remaining = new ArrayList<>(tree.getIntersectingObjects(START.toEpochMilli(), END.toEpochMilli()));
        assertThat(remaining, contains(keep));
    }

    private static final class TestActivity extends ActivityBase<String> {

        private final int hashCode;

        private TestActivity(String name, int hashCode) {
            super(name, START, END);
            this.hashCode = hashCode;
        }

        private TestActivity(String name, String id, int hashCode) {
            this(name, hashCode);
            this.id = id;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private static final class NullBoundActivity implements Activity {

        private final String name;
        private final Instant startTime;
        private final Instant endTime;

        private NullBoundActivity(String name, Instant startTime, Instant endTime) {
            this.name = name;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getId() {
            return name;
        }

        @Override
        public Instant getStartTime() {
            return startTime;
        }

        @Override
        public Instant getEndTime() {
            return endTime;
        }
    }
}
