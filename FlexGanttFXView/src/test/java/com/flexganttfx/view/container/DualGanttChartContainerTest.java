package com.flexganttfx.view.container;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.view.GanttChart;
import de.sandec.jmemorybuddy.JMemoryBuddy;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeoutException;

public class DualGanttChartContainerTest extends ApplicationTest {

    private Stage stage;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        FlexGanttFX.setLicenseKey("LIC=;VEN=DLSC;VER=11_11;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02144D3694C44BED5892BB980F53121FC3E9D83303AB0214572C7C38F1F389AEC0DE72D29796803364F4B217");
    }

    @Test
    public void shouldCollectGanttChart() {
        JMemoryBuddy.memoryTest(checker -> {

            // given
            GanttChart primaryGC = new GanttChart();
            GanttChart secondaryGC_1 = new GanttChart();
            GanttChart secondaryGC_2 = new GanttChart();


            DualGanttChartContainer container = new DualGanttChartContainer(primaryGC, secondaryGC_1);

            // when
            try {
                FxToolkit.setupScene(() -> new Scene(container));
                container.setSecondaryGanttChart(secondaryGC_2);
                FxToolkit.showStage();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            // then
            checker.assertCollectable(secondaryGC_1);
            checker.assertNotCollectable(secondaryGC_2);
        });
    }
}
