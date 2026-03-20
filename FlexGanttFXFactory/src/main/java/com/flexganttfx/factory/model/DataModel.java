/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.factory.model;

import com.flexganttfx.model.Layer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates synthetic manufacturing data: 8 production lines with 5 machines
 * each. Jobs are packed consecutively with short setup gaps so machines appear
 * busy across the full window (one week before today → four weeks after today).
 * All four statuses (SCHEDULED, IN_PROGRESS, DONE, DELAYED) appear naturally
 * based on each job's position relative to now.
 * The random seed is fixed (42) so the data is deterministic.
 *
 * <p>Approximate total: ~3 000 jobs (~75 per machine).
 */
public class DataModel {

    // Window: -7 days … +28 days relative to now (35 days total, 840 hours)
    private static final int DAYS_BEFORE = 7;
    private static final int DAYS_AFTER  = 28;

    private static final String[] LINE_NAMES = {
            "Assembly Line A", "Assembly Line B",
            "Machining Line 1", "Machining Line 2",
            "Stamping Line", "Welding Line",
            "Finishing Line", "Packaging Line"
    };

    private static final String[][] MACHINE_NAMES = {
            {"Robot Arm 1", "Robot Arm 2", "Conveyor A", "Torque Station", "QA Scanner"},
            {"Robot Arm 3", "Robot Arm 4", "Conveyor B", "Press Fit", "Vision System"},
            {"CNC-101", "CNC-102", "Lathe-1", "Mill-1", "Drill Press"},
            {"CNC-201", "CNC-202", "Lathe-2", "Mill-2", "Grinder"},
            {"Stamper A", "Stamper B", "Blanker", "Trimmer", "Deburr Cell"},
            {"MIG Welder 1", "MIG Welder 2", "TIG Welder", "Spot Welder", "Weld Inspector"},
            {"Grinder A", "Polisher", "Paint Booth 1", "Paint Booth 2", "Coating Unit"},
            {"Wrapper 1", "Wrapper 2", "Labeller", "Palletiser", "Shrink Tunnel"}
    };

    private static final String[] JOB_PREFIXES = {
            "MFG", "WO", "OP", "JOB", "TASK", "PO", "SO", "RUN"
    };

    private final Layer layer = new Layer("Jobs");
    private final Machine root = new Machine("Factory");
    private final List<ProductionLine> productionLines = new ArrayList<>();

    public DataModel() {
        Random rng = new Random(42);
        Instant now         = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant windowStart = now.minus(DAYS_BEFORE, ChronoUnit.DAYS);
        Instant windowEnd   = now.plus(DAYS_AFTER,   ChronoUnit.DAYS);

        for (int l = 0; l < LINE_NAMES.length; l++) {
            ProductionLine line = new ProductionLine(LINE_NAMES[l]);
            productionLines.add(line);

            for (int m = 0; m < MACHINE_NAMES[l].length; m++) {
                Machine machine = new Machine(MACHINE_NAMES[l][m]);

                // Pack jobs consecutively: 4–10 h job + 1–3 h setup gap
                Instant cursor = windowStart;
                int jobIndex = 0;
                while (cursor.isBefore(windowEnd)) {
                    long durationHours = 4 + rng.nextInt(7);  // 4–10 h
                    long gapHours      = 1 + rng.nextInt(3);  // 1–3 h setup

                    Instant start = cursor;
                    Instant end   = start.plus(durationHours, ChronoUnit.HOURS);
                    if (end.isAfter(windowEnd)) {
                        end = windowEnd;
                    }

                    JobStatus status = pickStatus(start, end, now, rng);
                    double    pct    = pickPercentage(rng, status);
                    String    prefix = JOB_PREFIXES[rng.nextInt(JOB_PREFIXES.length)];
                    String    name   = prefix + "-" + (1000 + l * 100 + m * 10 + (jobIndex % 10) + 1);
                    machine.addActivity(layer, new Job(name, start, end, status, pct));

                    cursor = end.plus(gapHours, ChronoUnit.HOURS);
                    jobIndex++;
                }

                line.getChildren().add(machine);
            }

            root.getChildren().add(line);
        }
    }

    private JobStatus pickStatus(Instant start, Instant end, Instant now, Random rng) {
        if (end.isBefore(now)) {
            // Entirely in the past — done (80 %) or delayed (20 %)
            return rng.nextInt(10) < 8 ? JobStatus.DONE : JobStatus.DELAYED;
        } else if (start.isBefore(now)) {
            // Straddling now — in-progress (70 %) or delayed (30 %)
            return rng.nextInt(10) < 7 ? JobStatus.IN_PROGRESS : JobStatus.DELAYED;
        } else {
            // Entirely in the future — scheduled
            return JobStatus.SCHEDULED;
        }
    }

    private double pickPercentage(Random rng, JobStatus status) {
        switch (status) {
            case DONE:        return 100.0;
            case IN_PROGRESS: return 10 + rng.nextInt(80); // 10–89 %
            case DELAYED:     return  5 + rng.nextInt(45); //  5–49 %
            case SCHEDULED:   return 0.0;
            default:          return 0.0;
        }
    }

    public Layer getLayer() {
        return layer;
    }

    /** Root row used as the invisible tree root for GanttChart. */
    public Machine getRoot() {
        return root;
    }

    public List<ProductionLine> getProductionLines() {
        return productionLines;
    }
}

