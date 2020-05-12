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
