/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import com.flexganttfx.model.util.SimpleUnit;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * A virtual grid implementation for {@link SimpleUnit}.
 *
 * @since 1.1
 */
public final class SimpleUnitGrid extends VirtualGrid<SimpleUnit> {

    /**
     * Constructs a new grid.
     *
     * @param name      the grid name as shown in the UI (e.g. "50 Units")
     * @param shortName the short name (e.g. "50")
     * @param unit      the chrono unit on which the grid will be based (e.g. "TEN")
     * @param amount    the amount of the chrono unit (e.g. "5")
     * @since 1.0
     */
    public SimpleUnitGrid(String name, String shortName, SimpleUnit unit, int amount) {
        super(name, shortName, unit, amount);
    }

    /**
     * Constructs a new grid.
     *
     * @param name      the grid name as shown in the UI (e.g. "50")
     * @param unit      the chrono unit on which the grid will be based (e.g. "TEN")
     * @param amount    the amount of the chrono unit (e.g. "5")
     * @since 1.0
     */
    public SimpleUnitGrid(String name, SimpleUnit unit, int amount) {
        super(name, unit, amount);
    }

    @Override
    public Instant adjustTime(Instant instant, ZoneId zoneId, boolean roundUp, DayOfWeek firstDayOfWeek) {

        long millis = instant.toEpochMilli();

        millis -= millis % getUnit().getMillis();

        if (roundUp) {
            millis += getUnit().getMillis();
        }

        return Instant.ofEpochMilli(millis);
    }

    @Override
    public LocalTime adjustTime(LocalTime time, boolean roundUp) {
        throw new UnsupportedOperationException("local time not supported by grid");
    }
}
