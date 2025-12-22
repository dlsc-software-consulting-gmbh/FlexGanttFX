/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.util;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

/**
 * A custom temporal unit used for showing numbered units (1, 2, 3, ... or 10,
 * 20, 30, ...).
 */
public enum SimpleUnit implements TemporalUnit {

    /**
     * A granularity representing one millisecond.
     *
     * @since 1.0
     */
    ONE(1L),

    /**
     * A granularity representing ten milliseconds.
     *
     * @since 1.0
     */
    TEN(10L),

    /**
     * A granularity representing one hundred milliseconds.
     *
     * @since 1.0
     */
    HUNDRED(100L),

    /**
     * A granularity representing one thousand milliseconds.
     *
     * @since 1.0
     */
    THOUSAND(1000L),

    /**
     * A granularity representing ten thousand milliseconds.
     *
     * @since 1.0
     */
    THOUSAND_TEN(10000L),

    /**
     * A granularity representing one hundred thousand milliseconds.
     *
     * @since 1.0
     */
    THOUSAND_HUNDRED(100000L),

    /**
     * A granularity representing one million milliseconds.
     *
     * @since 1.0
     */
    MILLION(1000000L),

    /**
     * A granularity representing ten million milliseconds.
     *
     * @since 1.0
     */
    MILLION_TEN(10000000L),

    /**
     * A granularity representing one hundred million milliseconds.
     *
     * @since 1.0
     */
    MILLION_HUNDRED(100000000L),

    /**
     * A granularity representing one billion milliseconds.
     *
     * @since 1.0
     */
    BILLION(1000000000L),

    /**
     * A granularity representing ten billion milliseconds.
     *
     * @since 1.0
     */
    BILLION_TEN(10000000000L),

    /**
     * A granularity representing one hundred billion milliseconds.
     *
     * @since 1.0
     */
    BILLION_HUNDRED(100000000000L),

    /**
     * A granularity representing one trillion milliseconds.
     *
     * @since 1.0
     */
    TRILLION(1000000000000L);

    /*
     * The total number of milliseconds represented by the granularity.
     */
    private final long millis;

    /**
     * Constructs a new enumerator value.
     *
     * @param millis
     *            the number of milliseconds represented by the value
     * @since 1.0
     */
    SimpleUnit(long millis) {
        this.millis = millis;
    }

    /**
     * Increments the given number of milliseconds with the milliseconds
     * represented by the enumerator value.
     *
     * @param time
     *            the time to increment
     * @return the given time plus the milliseconds represented by the value
     * @since 1.0
     */
    public long increment(long time) {
        return time + millis;
    }

    /**
     * Decrements the given number of milliseconds with the milliseconds
     * represented by the enumerator value.
     *
     * @param time
     *            the time to increment
     * @return the given time minus the milliseconds represented by the value
     * @since 1.0
     */
    public long decrement(long time) {
        return time - millis;
    }

    /**
     * Truncates the given time point by rounding it down to the nearest
     * multitude of the milliseconds represented by the granularity value.
     * Example: the granularity {@link #THOUSAND} would adjust the time point
     * 4366 to 4000.
     *
     * @param time
     *            the time that needs adjustment
     * @return a number of milliseconds that is a multitude of the number of
     *         milliseconds represented by the enumerator value and that is
     *         still smaller than the given time
     * @since 1.0
     */
    public long truncate(long time) {
        return time / millis * millis;
    }

    /**
     * Returns the number of milliseconds represented by the granularity.
     *
     * @return number of milliseconds represented by the granularity
     * @since 1.0
     */
    public long getMillis() {
        return millis;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Temporal> R addTo(R temporal, long periodToAdd) {
        Instant instant = Instant.from(temporal);
        long millisToAdd = instant.toEpochMilli();
        return (R) Instant.ofEpochMilli(millis + millisToAdd);
    }

    @Override
    public long between(Temporal t1, Temporal t2) {
        return Instant.from(t2).toEpochMilli()
                - Instant.from(t1).toEpochMilli();
    }

    @Override
    public Duration getDuration() {
        return Duration.ofMillis(millis);
    }

    @Override
    public boolean isDateBased() {
        return false;
    }

    @Override
    public boolean isDurationEstimated() {
        return false;
    }

    @Override
    public boolean isSupportedBy(Temporal temporal) {
        return (temporal instanceof Instant);
    }

    @Override
    public boolean isTimeBased() {
        return false;
    }
}
