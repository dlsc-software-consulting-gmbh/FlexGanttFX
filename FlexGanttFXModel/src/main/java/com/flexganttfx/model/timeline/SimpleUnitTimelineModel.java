/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.timeline;

import java.time.Instant;

import com.flexganttfx.model.util.SimpleUnit;

/**
 * A specialized timeline model for the {@link SimpleUnit} temporal unit.
 *
 * @since 1.0
 */
public class SimpleUnitTimelineModel extends TimelineModel<SimpleUnit> {

    /**
     * Constructs a new model with the lowest unit set to {@link SimpleUnit#ONE}
     * and the highest unit set to {@link SimpleUnit#BILLION_HUNDRED}. The start
     * time, the "now" time, and the horizon start time all get set to 0
     *
     * @since 1.0
     */
    public SimpleUnitTimelineModel() {
        setNow(Instant.ofEpochMilli(0));
        setMillisPerPixel(1.0 / 30.0);
        setStartTime(Instant.ofEpochMilli(0));
        setHorizonStartTime(Instant.ofEpochMilli(0));
        setZoomRange(SimpleUnit.ONE, 1, 100, SimpleUnit.BILLION_HUNDRED, 1, 20);
    }
}