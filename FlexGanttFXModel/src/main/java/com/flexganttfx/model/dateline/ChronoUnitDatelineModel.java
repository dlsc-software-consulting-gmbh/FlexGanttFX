/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import java.time.temporal.ChronoUnit;

import static com.flexganttfx.model.dateline.Resolution.Position.*;
import static java.time.temporal.ChronoUnit.*;

/**
 * The chrono unit dateline model is a specialization of the dateline model that works
 * in combination with the {@link ChronoUnit}. The chrono unit basically represents standard
 * calendar units ranging from milliseconds to thousands of years.
 *
 * @since 1.0
 */
public final class ChronoUnitDatelineModel extends DatelineModel<ChronoUnit> {

    /**
     * Constructs a new dateline model with a long list of predefined
     * resolutions of type {@link ChronoUnitResolution}.
     *
     * @since 1.0
     */
    public ChronoUnitDatelineModel() {
        addResolution(new ChronoUnitResolution(MILLIS, "EEEE, dd. MMMM yyyy, HH:mm:ss:SSS", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(MILLIS, "EEEE, dd.MM.yy, HH:mm:ss:SSS", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(MILLIS, "E, dd.MM.yy, HH:mm:ss:SSS", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(MILLIS, "dd.MM.yy, HH:mm:ss:SSS", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(MILLIS, "dd.MM, HH:mm:ss:SSS", 1, TOP));
        addResolution(new ChronoUnitResolution(MILLIS, "SSS", 1, BOTTOM));
        addResolution(new ChronoUnitResolution(MILLIS, "SSS", 5, BOTTOM));
        addResolution(new ChronoUnitResolution(MILLIS, "SSS", 10, BOTTOM));
        addResolution(new ChronoUnitResolution(MILLIS, "SSS", 15, BOTTOM));

        addResolution(new ChronoUnitResolution(SECONDS, "EEEE, dd. MMMM yyyy, HH:mm:ss", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(SECONDS, "EEEE, dd.MM.yy, HH:mm:ss", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(SECONDS, "E, dd.MM.yy, HH:mm:ss", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(SECONDS, "dd.MM.yy, HH:mm:ss", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(SECONDS, "dd.MM, HH:mm:ss", 1, MIDDLE));
        addResolution(new ChronoUnitResolution(SECONDS, "HH:mm:ss", 1, MIDDLE));
        addResolution(new ChronoUnitResolution(SECONDS, "ss", 1, BOTTOM));
        addResolution(new ChronoUnitResolution(SECONDS, "ss", 5, BOTTOM));
        addResolution(new ChronoUnitResolution(SECONDS, "ss", 10, BOTTOM));
        addResolution(new ChronoUnitResolution(SECONDS, "ss", 15, BOTTOM));

        addResolution(new ChronoUnitResolution(MINUTES, "EEEE, dd. MMMM yyyy, HH:mm", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(MINUTES, "EEEE, dd.MM.yy, HH:mm", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(MINUTES, "E, dd.MM.yy, HH:mm", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(MINUTES, "dd.MM.yy, HH:mm", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(MINUTES, "dd.MM, HH:mm", 1, TOP));
        addResolution(new ChronoUnitResolution(MINUTES, "HH:mm", 1, MIDDLE));
        addResolution(new ChronoUnitResolution(MINUTES, "mm", 1, BOTTOM));
        addResolution(new ChronoUnitResolution(MINUTES, "mm", 5, BOTTOM));
        addResolution(new ChronoUnitResolution(MINUTES, "mm", 10, BOTTOM));
        addResolution(new ChronoUnitResolution(MINUTES, "mm", 15, BOTTOM));

        addResolution(new ChronoUnitResolution(HOURS, "EEEE, dd. MMMM yyyy, HH:mm", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(HOURS, "EEEE, dd.MM.yy, HH:mm", 1, TOP, BOTTOM, ONLY));
        addResolution(new ChronoUnitResolution(HOURS, "E, dd.MM.yy, HH:mm", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(HOURS, "dd.MM.yy, HH:mm", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(HOURS, "dd.MM, HH:mm", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(HOURS, "H:mm", 1, MIDDLE, BOTTOM));
        addResolution(new ChronoUnitResolution(HOURS, "H:mm", 3, MIDDLE, BOTTOM));
        addResolution(new ChronoUnitResolution(HOURS, "H:mm", 6, MIDDLE, BOTTOM));

        addResolution(new ChronoUnitResolution(DAYS, "EEEE d. MMMM yyyy", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(DAYS, "EEEE d. MMMM yy", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(DAYS, "E, d. MMMM yy", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(DAYS, "E, d. MMMM", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(DAYS, "E, dd.MM.yy", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(DAYS, "EEEE dd", 1, MIDDLE, BOTTOM));
        addResolution(new ChronoUnitResolution(DAYS, "E dd", 1, MIDDLE, BOTTOM));
        addResolution(new ChronoUnitResolution(DAYS, "dd.MM", 1, MIDDLE, BOTTOM));
        addResolution(new ChronoUnitResolution(DAYS, "dd", 1, BOTTOM));

        addResolution(new ChronoUnitResolution(WEEKS, "'W' w, EEEE d. MMMM yy", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(WEEKS, "'W' w, d. MMMM yy", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(WEEKS, "'W' w, d. MMMM", 1));
        addResolution(new ChronoUnitResolution(WEEKS, "'W' w, E, dd.MM.yy", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(WEEKS, "'W' w, dd.MM.yy", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(WEEKS, "'W' w, dd.MM", 1, BOTTOM));
        addResolution(new ChronoUnitResolution(WEEKS, "'W' w", 1, MIDDLE, BOTTOM));

        addResolution(new ChronoUnitResolution(MONTHS, "MMMM yyyy", 1, TOP, ONLY));
        addResolution(new ChronoUnitResolution(MONTHS, "MMMM", 1, MIDDLE, BOTTOM));
        addResolution(new ChronoUnitResolution(MONTHS, "MMM", 1, MIDDLE, BOTTOM));
        addResolution(new ChronoUnitResolution(MONTHS, "M", 1, MIDDLE, BOTTOM));

        addResolution(new ChronoUnitResolution(YEARS, "yyyy", 1));
        addResolution(new ChronoUnitResolution(DECADES, "yyyy", 1));
        addResolution(new ChronoUnitResolution(CENTURIES, "yyyy", 1));
        addResolution(new ChronoUnitResolution(MILLENNIA, "yyyy", 1));
    }

    @Override
    public ChronoUnit nextTemporalUnit(ChronoUnit unit) {
        switch (unit) {
            case NANOS:
                return MICROS;
            case MICROS:
                return MILLIS;
            case MILLIS:
                return SECONDS;
            case SECONDS:
                return MINUTES;
            case MINUTES:
                return HOURS;
            case HOURS:
                return DAYS;
            case DAYS:
                return WEEKS;
            case WEEKS:
                return MONTHS;
            case MONTHS:
                return YEARS;
            case YEARS:
                return DECADES;
            case DECADES:
                return CENTURIES;
            case CENTURIES:
                return MILLENNIA;
            default:
            /*
			 * We are ignoring HALF DAYS.
			 */
                return null;
        }
    }
}
