/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.model.util;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.activity.ActivityBase;
import de.sandec.jmemorybuddy.JMemoryBuddy;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
