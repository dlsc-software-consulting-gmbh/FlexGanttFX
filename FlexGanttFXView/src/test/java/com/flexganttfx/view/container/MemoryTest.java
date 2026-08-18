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
package com.flexganttfx.view.container;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import de.sandec.jmemorybuddy.JMemoryBuddy;
import javafx.scene.Group;
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
                FxToolkit.setupScene(() -> new Scene(new Group()));
                memoryTestAPI.assertCollectable(label);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

        });
    }

    class TestRow extends Row<TestRow,TestRow, Activity> {}
}
