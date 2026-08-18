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
package com.flexganttfx.view;

import de.sandec.jmemorybuddy.JMemoryBuddy;
import javafx.scene.Scene;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.TimeoutException;

public class GanttChartTest extends ApplicationTest {

    @Test
    public void shouldCollectGanttChart() {
        JMemoryBuddy.memoryTest(checker -> {

            // given
            GanttChart collectibleGanttChart = new GanttChart();
            GanttChart notCollectibleGanttChart = new GanttChart();

            // when
            try {
                FxToolkit.setupScene(() -> new Scene(collectibleGanttChart));
                FxToolkit.setupScene(() -> new Scene(notCollectibleGanttChart));
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            // then
            checker.assertCollectable(collectibleGanttChart);
            checker.assertNotCollectable(notCollectibleGanttChart);
        });
    }
}
