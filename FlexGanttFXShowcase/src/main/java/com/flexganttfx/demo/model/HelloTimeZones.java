/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.model;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.CompletableActivityBase;
import com.flexganttfx.view.GanttChart;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class HelloTimeZones extends FlexGanttFXSample {
    private final Layer layer = new Layer("Default");

    public HelloTimeZones() {
    }

    @Override
    protected GanttChart<?> createGanttChart() throws Exception {
        ZoneIdRow root = new ZoneIdRow(ZoneId.systemDefault());
        root.setExpanded(true);

        GanttChart<ZoneIdRow> gantt = new GanttChart<>(root);
        gantt.getTreeTable().setShowRoot(false);

        gantt.getLayers().add(layer);

        Instant st = Instant.now().plus(3, ChronoUnit.DAYS);
        Instant et = st.plus(7, ChronoUnit.DAYS);

        Activity activity = new CompletableActivityBase<String>(st, et);

        for (String zoneId : ZoneId.getAvailableZoneIds()) {
            ZoneId zone = ZoneId.of(zoneId);
            ZoneIdRow row = new ZoneIdRow(zone);
            root.getChildren().add(row);
            row.addActivity(layer, activity);
        }

        gantt.getTimeline().setZoneIdVisible(true);
        gantt.getGraphics().setShowZoneId(true);

        gantt.getTimeline().getDateline().setZoneId(ZoneId.of("Europe/Helsinki"));
        gantt.getTimeline().getModel().setStartTime(ZonedDateTime.of(2017, 10, 14, 0, 0, 0, 0, ZoneId.of("Europe/Helsinki")).toInstant());
        return gantt;
    }

    public class ZoneIdRow extends Row<ZoneIdRow, ZoneIdRow, Activity> {

        public ZoneIdRow(ZoneId zoneId) {
            super(zoneId.getDisplayName(TextStyle.FULL_STANDALONE,
                    Locale.getDefault()));

            setZoneId(zoneId);
        }
    }

    @Override
    public String getSampleName() {
        return "Time Zones";
    }

    @Override
    public String getSampleDescription() {
        return "The dateline at the top matches one specific timezone and each row can have its own timezone.";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
