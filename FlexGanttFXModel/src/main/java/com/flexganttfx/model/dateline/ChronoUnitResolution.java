/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import com.flexganttfx.model.util.ChronoUnitUtils;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * A resolution type for {@link ChronoUnit} that can be used in combination with
 * the {@link ChronoUnitDatelineModel}.
 *
 * @see ChronoUnitDatelineModel#addResolution(Resolution)
 * @since 1.0
 */
public final class ChronoUnitResolution extends Resolution<ChronoUnit> {

    /**
     * Constructs a new resolution for the given unit, format, step rate, and positions.
     *
     * @param unit               the chrono unit supported by this resolution (e.g. DAY)
     * @param format             the format how the unit will be shown on the screen
     * @param stepRate           the step rate (e.g. "1" Minute, "5" Minutes, "15" Minutes)
     * @param supportedPositions the position inside the dateline where this resolution can be displayed (top, middle, bottom)
     * @since 1.0
     */
    public ChronoUnitResolution(ChronoUnit unit, String format, int stepRate, Position... supportedPositions) {
        super(unit, format, stepRate, supportedPositions);
    }

    /**
     * Constructs a new resolution for the given unit, format, step rate, and positions.
     *
     * @param unit     the chrono unit supported by this resolution (e.g. DAY)
     * @param format   the format how the unit will be shown on the screen
     * @param stepRate the step rate (e.g. "1" Minute, "5" Minutes, "15" Minutes)
     * @since 1.0
     */
    public ChronoUnitResolution(ChronoUnit unit, String format, int stepRate) {
        super(unit, format, stepRate);
    }

    @Override
    public String format(Instant instant, ZoneId zoneId) {
        if (getFormat().equals("M")) {
            return ZonedDateTime.ofInstant(instant, zoneId).getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    .substring(0, 1);
        }

        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);

		/*
         * We can't cache the formatter as the zone id displayed by the dateline
		 * might change.
		 */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getFormat())
                .withZone(zoneId);
        return formatter.format(zonedDateTime);
    }

    @Override
    public Instant truncate(Instant instant, ZoneId zoneId, DayOfWeek firstDayOfWeek) {
        ZonedDateTime truncatedTime = ChronoUnitUtils.truncate(instant.atZone(zoneId), getTemporalUnit(), getStepRate(), firstDayOfWeek);
        return Instant.from(truncatedTime);
    }


    private boolean dstStart;

    /**
     * Returns true if the last call to {@link #increment(Instant, ZoneId)} was affected
     * by a DST start. Example: if the resolution is "3 hours" and the current time is midnight,
     * then the new local time might actually be 4am, instead of 3am. Because on DST start
     * the clocks will be advanced by one hour.
     *
     * @return true if the resolution's increment method was affected by the start of DST
     */
    public boolean isDSTStartIncrement() {
        return dstStart;
    }

    private boolean dstEnd;

    /**
     * Returns true if the last call to {@link #increment(Instant, ZoneId)} was affected
     * by a DST end. Example: if the resolution is "3 hours" and the current time is midnight,
     * then the new local time might actually be 2am, instead of 3am. Because on DST end
     * the clocks will be turned back by one hour.
     *
     * @return true if the resolution's increment method was affected by the end of DST
     */
    public boolean isDSTEndIncrement() {
        return dstEnd;
    }

    @Override
    public Instant increment(Instant instant, ZoneId zoneId) {
        dstStart = false;
        dstEnd = false;

        ChronoUnit unit = getTemporalUnit();
        ZonedDateTime time = ZonedDateTime.ofInstant(instant, zoneId);

        final int stepRate = getStepRate();

        switch (unit) {
            case NANOS:
                time = time.plusNanos(stepRate);
                break;
            case SECONDS:
                time = time.plusSeconds(stepRate);
                break;
            case MINUTES:
                time = time.plusMinutes(stepRate);
                break;
            case HOURS:
                int hourBefore = Math.max(0, time.getHour());
                int dayBefore = time.getDayOfYear();

                time = time.plusHours(stepRate);
                int hourAfter = Math.min(23, time.getHour());
                int dayAfter = time.getDayOfYear();

                final int deltaHours = hourAfter - hourBefore;
                if (dayBefore == dayAfter) {
                    if (deltaHours < stepRate) {
                        dstEnd = true;
                    } else if (deltaHours > stepRate) {
                        dstStart = true;
                    }
                }

                break;
            case DAYS:
                time = time.plusDays(stepRate);
                break;
            case WEEKS:
                time = time.plusWeeks(stepRate);
                break;
            case MONTHS:
                time = time.plusMonths(stepRate);
                break;
            case YEARS:
                time = time.plusYears(stepRate);
                break;
            case DECADES:
                time = time.plusYears(stepRate * 10L);
                break;
            case CENTURIES:
                time = time.plusYears(stepRate * 100L);
                break;
            case MILLENNIA:
                time = time.plusYears(stepRate * 1000L);
                break;
            default:
                return instant.plus(getTemporalUnit().getDuration().multipliedBy(stepRate));
        }

        return Instant.from(time);
    }

    @Override
    public Instant decrement(Instant instant, ZoneId zoneId) {
        dstStart = false;
        dstEnd = false;

        ChronoUnit unit = getTemporalUnit();
        ZonedDateTime time = ZonedDateTime.ofInstant(instant, zoneId);

        final int stepRate = getStepRate();

        switch (unit) {
            case NANOS:
                time = time.minusNanos(stepRate);
                break;
            case SECONDS:
                time = time.minusSeconds(stepRate);
                break;
            case MINUTES:
                time = time.minusMinutes(stepRate);
                break;
            case HOURS:
                int hourBefore = Math.max(0, time.getHour());
                int dayBefore = time.getDayOfYear();

                time = time.minusHours(stepRate);
                int hourAfter = Math.min(23, time.getHour());
                int dayAfter = time.getDayOfYear();

                final int deltaHours = hourAfter - hourBefore;
                if (dayBefore == dayAfter) {
                    if (deltaHours < stepRate) {
                        dstEnd = true;
                    } else if (deltaHours > stepRate) {
                        dstStart = true;
                    }
                }

                break;
            case DAYS:
                time = time.minusDays(stepRate);
                break;
            case WEEKS:
                time = time.minusWeeks(stepRate);
                break;
            case MONTHS:
                time = time.minusMonths(stepRate);
                break;
            case YEARS:
                time = time.minusYears(stepRate);
                break;
            case DECADES:
                time = time.minusYears(stepRate * 10L);
                break;
            case CENTURIES:
                time = time.minusYears(stepRate * 100L);
                break;
            case MILLENNIA:
                time = time.minusYears(stepRate * 1000L);
                break;
            default:
                return instant.minus(getTemporalUnit().getDuration().multipliedBy(stepRate));
        }

        return Instant.from(time);
    }

    @Override
    public VirtualGrid<ChronoUnit> createGrid() {
        return new ChronoUnitGrid("Auto", getTemporalUnit(), getStepRate());
    }
}
