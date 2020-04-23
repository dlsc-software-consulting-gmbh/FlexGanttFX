/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import com.flexganttfx.model.util.ChronoUnitUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static java.util.Objects.requireNonNull;

/**
 * A virtual grid implementation for {@link ChronoUnit}.
 *
 * @since 1.0
 */
public final class ChronoUnitGrid extends VirtualGrid<ChronoUnit> {

    /**
     * Constructs a new grid.
     *
     * @param name      the grid name as shown in the UI (e.g. "15 Minutes")
     * @param shortName the short name (e.g. "15 Min.")
     * @param unit      the chrono unit on which the grid will be based (e.g. "MINUTE")
     * @param amount    the amount of the chrono unit (e.g. "15")
     * @since 1.0
     */
    public ChronoUnitGrid(String name, String shortName, ChronoUnit unit, int amount) {
        super(name, shortName, unit, amount);
    }

    /**
     * Constructs a new grid. The short name will be the same as the long name.
     *
     * @param name      the grid name as shown in the UI (e.g. "15 Minutes")
     * @param unit      the chrono unit on which the grid will be based (e.g. "MINUTE")
     * @param amount    the amount of the chrono unit (e.g. "15")
     * @since 1.0
     */
    public ChronoUnitGrid(String name, ChronoUnit unit, int amount) {
        super(name, unit, amount);
    }

    @Override
    public Instant adjustTime(Instant instant, ZoneId zoneId, boolean roundUp, DayOfWeek firstDayOfWeek) {

        requireNonNull(instant);
        requireNonNull(zoneId);
        requireNonNull(firstDayOfWeek);

        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        if (roundUp) {
            zonedDateTime = zonedDateTime.plus(getAmount(), getUnit());
        }

        zonedDateTime = ChronoUnitUtils.truncate(zonedDateTime, getUnit(),
                getAmount(), firstDayOfWeek);

        return Instant.from(zonedDateTime);
    }

    @Override
    public LocalTime adjustTime(LocalTime time, boolean roundUp) {
        requireNonNull(time);

        if (roundUp) {
            time = time.plus(getAmount(), getUnit());
        }

        return ChronoUnitUtils.truncate(time, getUnit(), getAmount());
    }
}
