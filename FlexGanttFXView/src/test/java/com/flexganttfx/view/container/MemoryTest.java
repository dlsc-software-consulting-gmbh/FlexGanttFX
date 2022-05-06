package com.flexganttfx.view.container;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import de.sandec.jmemorybuddy.JMemoryBuddy;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeoutException;

public class MemoryTest extends ApplicationTest {

    private Stage stage;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302D021442068CF635B84BFC157478E2D60932F52AFBD59E021500952B8FD690A764EC20AE70A3D8655029BDD66532");
    }

    @Test
    public void shouldGarbageCollect() {
        JMemoryBuddy.memoryTest(memoryTestAPI -> {
            GanttChart<TestRow> gc = new GanttChart<>();

            Label label = new Label("test");

            // when
            try {
                FxToolkit.setupScene(() -> new Scene(label));
                FxToolkit.showStage();
                FxToolkit.hideStage();
                memoryTestAPI.assertCollectable(label);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

        });
    }

    class TestRow extends Row<TestRow,TestRow, Activity> {}
}
