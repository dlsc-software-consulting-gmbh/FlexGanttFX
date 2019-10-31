/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.timeline;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * A specialized timeline model for the {@link ChronoUnit} temporal unit. This
 * model is the default model used by the Gantt chart.
 *
 * @since 1.0
 */
public class ChronoUnitTimelineModel extends TimelineModel<ChronoUnit> {

    /**
     * Constructs a new model with the lowest unit set to minutes and the
     * highest unit set to years. The start time gets set to
     * {@link Instant#now()}.
     *
     * @since 1.0
     */
    public ChronoUnitTimelineModel() {
        setStartTime(Instant.now());
        setZoomRange(ChronoUnit.MINUTES, 5, 100, ChronoUnit.MONTHS, 1, 50);
    }
}