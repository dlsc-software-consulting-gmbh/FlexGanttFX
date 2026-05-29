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
package com.flexganttfx.view;

import com.flexganttfx.core.FlexGanttFX;
import de.sandec.jmemorybuddy.JMemoryBuddy;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeoutException;

public class GanttChartTest extends ApplicationTest {

    @Start
    public void start(Stage stage) {
        FlexGanttFX.setLicenseKey("LIC=;VEN=DLSC;VER=11_11;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02144D3694C44BED5892BB980F53121FC3E9D83303AB0214572C7C38F1F389AEC0DE72D29796803364F4B217");
    }

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
