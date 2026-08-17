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
package com.flexganttfx.view.container;

import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartLite;
import de.sandec.jmemorybuddy.JMemoryBuddy;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class DualGanttChartContainerTest {

    @BeforeEach
    public void initFX() throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        Platform.setImplicitExit(false);
        Platform.startup(startupLatch::countDown);
        startupLatch.await(10000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldCollectBoundListProperty() {
        JMemoryBuddy.memoryTest(checker -> {

            // given
            ListProperty<Object> listA = new SimpleListProperty<>(FXCollections.observableArrayList());
            ListProperty<Object> listB = new SimpleListProperty<>(FXCollections.observableArrayList());

            listB.bind(listA);

            // when
            listB.unbind();

            // then
            checker.setAsReferenced(listB);
            checker.assertCollectable(listA);
        });
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

    @Test
    public void shouldCollectGraphics() {

        JMemoryBuddy.memoryTest(checker -> {
            CountDownLatch showingLatch = new CountDownLatch(1);
            AtomicReference<Stage> stage = new AtomicReference<>();

            GanttChartLite ganttChart = new GanttChartLite();

            Platform.runLater(() -> {
                stage.set(new Stage());
                Group root = new Group();
                root.setVisible(false);
                root.getChildren().add(ganttChart);
                stage.get().setScene(new Scene(root));
                stage.get().setOnShown(l -> Platform.runLater(() -> showingLatch.countDown()));
                stage.get().show();
            });

            try {
                showingLatch.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {
                stage.get().close();
            });

            checker.assertCollectable(ganttChart);
        });
    }
}
