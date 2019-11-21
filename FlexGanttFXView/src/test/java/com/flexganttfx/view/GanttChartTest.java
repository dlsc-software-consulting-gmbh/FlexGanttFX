package com.flexganttfx.view;

import de.sandec.jmemorybuddy.JMemoryBuddy;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class GanttChartTest extends ApplicationTest {

    @Test
    public void shouldGetGarbageCollect1() {
        JMemoryBuddy.memoryTest(checker -> {
            GanttChart notReferenced = new GanttChart();
            checker.assertCollectable(notReferenced);
        });
    }

    @Test
    public void shouldGetGarbageCollect2() {
        JMemoryBuddy.memoryTest(checker -> {
            GanttChartLite notReferenced = new GanttChartLite();
            checker.assertCollectable(notReferenced);
        });
    }
}
