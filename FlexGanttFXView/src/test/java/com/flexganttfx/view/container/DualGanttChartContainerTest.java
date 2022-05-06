/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.container;

import com.flexganttfx.core.FlexGanttFX;
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
        FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302D021442068CF635B84BFC157478E2D60932F52AFBD59E021500952B8FD690A764EC20AE70A3D8655029BDD66532");

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
