/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.sprint.view;

import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.extras.LayersView;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.sprint.model.BugActivity;
import com.flexganttfx.sprint.model.BurnDownActivity;
import com.flexganttfx.sprint.model.EngineerFleet;
import com.flexganttfx.sprint.model.MilestoneActivity;
import com.flexganttfx.sprint.model.SampleDataGenerator;
import com.flexganttfx.sprint.model.TaskActivity;
import com.flexganttfx.sprint.model.TechDebtActivity;
import com.flexganttfx.sprint.model.TeamRoot;
import com.flexganttfx.sprint.model.UserStory;
import com.flexganttfx.sprint.renderer.BugRenderer;
import com.flexganttfx.sprint.renderer.BurnDownRenderer;
import com.flexganttfx.sprint.renderer.MilestoneRenderer;
import com.flexganttfx.sprint.renderer.TechDebtRenderer;
import com.flexganttfx.sprint.renderer.UserStoryRenderer;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.container.DualGanttChartContainer;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.temporal.ChronoUnit;

public class SprintView extends BorderPane {

    private final GanttChart<TeamRoot> sprintChart;

    public SprintView() {
        // --- Layers ---
        Layer featuresLayer  = new Layer("Features");
        Layer bugsLayer      = new Layer("Bugs");
        Layer techDebtLayer  = new Layer("TechDebt");
        Layer milestonesLayer = new Layer("Milestones");
        Layer burnDownLayer  = new Layer("Burn-Down");

        // --- Sprint chart ---
        sprintChart = new GanttChart<>();
        sprintChart.getLayers().addAll(featuresLayer, bugsLayer, techDebtLayer, milestonesLayer, burnDownLayer);
        sprintChart.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 30);
        sprintChart.setScrollBarType(GanttChartBase.ScrollBarType.FIXED_HORIZON);

        // --- Team-load chart ---
        GanttChart<EngineerFleet> teamLoadChart = new GanttChart<>();
        teamLoadChart.getLayers().addAll(featuresLayer, bugsLayer, techDebtLayer, milestonesLayer, burnDownLayer);
        teamLoadChart.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 30);

        // --- Sample data ---
        SampleDataGenerator data = new SampleDataGenerator(
                featuresLayer, bugsLayer, techDebtLayer, milestonesLayer, burnDownLayer);

        sprintChart.setRoot(data.getTeamRoot());
        teamLoadChart.setRoot(data.getEngineerFleet());

        // --- Register renderers on sprint chart ---
        GraphicsBase<TeamRoot> sg = sprintChart.getGraphics();
        sg.setActivityRenderer(UserStory.class,       GanttLayout.class, new UserStoryRenderer(sg));
        sg.setActivityRenderer(BugActivity.class,     GanttLayout.class, new BugRenderer(sg));
        sg.setActivityRenderer(TechDebtActivity.class, GanttLayout.class, new TechDebtRenderer(sg));
        sg.setActivityRenderer(MilestoneActivity.class, GanttLayout.class, new MilestoneRenderer(sg));
        sg.setActivityRenderer(BurnDownActivity.class, ChartLayout.class, new BurnDownRenderer(sg));

        // --- Register renderers on team-load chart ---
        GraphicsBase<EngineerFleet> tg = teamLoadChart.getGraphics();
        tg.setActivityRenderer(TaskActivity.class, GanttLayout.class, new UserStoryRenderer(tg));

        // --- Activity links ---
        for (ActivityLink<?> link : data.getActivityLinks()) {
            sprintChart.getLinks().add(link);
        }

        // --- DualGanttChartContainer ---
        DualGanttChartContainer dualContainer = new DualGanttChartContainer(sprintChart, teamLoadChart);
        HBox.setHgrow(dualContainer, Priority.ALWAYS);

        // --- Toolbar and StatusBar ---
        GanttChartToolBar<TeamRoot> toolBar = new GanttChartToolBar<>(sprintChart);
        GanttChartStatusBar<TeamRoot> statusBar = new GanttChartStatusBar<>(sprintChart);

        setTop(toolBar);
        setCenter(dualContainer);
        setBottom(statusBar);

        sprintChart.expandRows();
        teamLoadChart.expandRows();

        // Navigate to today's activities on first show
        Platform.runLater(() -> {
            sprintChart.getGraphics().showAllActivities();
        });
    }

    public GanttChart<TeamRoot> getSprintChart() {
        return sprintChart;
    }
}
