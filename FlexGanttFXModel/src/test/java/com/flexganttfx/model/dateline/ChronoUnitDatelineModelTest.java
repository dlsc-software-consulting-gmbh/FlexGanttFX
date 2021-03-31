/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.dateline;

import javafx.collections.ObservableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.CENTURIES;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLENNIA;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class ChronoUnitDatelineModelTest {

    private ChronoUnitDatelineModel datelineModel = new ChronoUnitDatelineModel();

    @Test
    public void shouldSetScaleCountWithinMinAndMaxBounds() {
        for (int count = datelineModel.getMinScaleCount(); count <= datelineModel
                .getMaxScaleCount(); count++) {
            // when
            datelineModel.setScaleCount(count);

            // then
            assertThat(datelineModel.getScaleCount(), is(equalTo(count)));
        }
    }

    @Test
    public void shouldSetMinAndMaxScaleCounts() {
        datelineModel.setScaleCount(datelineModel.getMaxScaleCount());
        datelineModel.setScaleCount(datelineModel.getMinScaleCount());
    }

    @Test
    public void shouldNotSetScaleCountWhenLargerThanMaxCount() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                datelineModel.setScaleCount(datelineModel.getMaxScaleCount() + 1));
    }

    @Test
    public void shouldNotSetScaleCountWhenLargerThanMinCount() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                datelineModel.setScaleCount(datelineModel.getMinScaleCount() - 1));
    }

    @Test
    public void shouldClearAllResolutions() {
        // when
        datelineModel.clearResolutions();

        // then
        assertThat(datelineModel.getResolutions(), is(empty()));

    }

    @Test
    public void shouldAddResolution() {
        // given
        ChronoUnitResolution resolution = new ChronoUnitResolution(HOURS,
                "xyz", 100);

        // when
        datelineModel.clearResolutions(HOURS);
        datelineModel.addResolution(resolution);

        // then
        assertThat(datelineModel.getResolutions(HOURS).hasNext(), is(true));
        assertThat(datelineModel.getResolutions(HOURS).next().hashCode(),
                is(equalTo(resolution.hashCode())));
    }

    @Test
    public void shouldReturnAvailableZoneIds() {

        // when
        ObservableSet<String> availableZoneIds = datelineModel
                .getAvailableZoneIds();

        // then
        assertThat(availableZoneIds, is(notNullValue()));
        assertThat(availableZoneIds, is(not(empty())));
    }

    @Test
    public void shouldAddZoneId() {
        // when
        datelineModel.addZoneId("xyz");

        // then
        assertThat(datelineModel.getAvailableZoneIds().contains("xyz"),
                is(true));
    }

    @Test
    public void shouldClearResolutions() {

        // when
        datelineModel.clearResolutions(HOURS);

        // then
        assertThat(datelineModel.getResolutions(HOURS).hasNext(), is(false));
    }

    @Test
    public void shouldReturnResolutionsForAllChronoUnits() {

        for (ChronoUnit unit : new ChronoUnit[]{SECONDS, MINUTES, HOURS,
                DAYS, WEEKS, MONTHS, YEARS, DECADES, CENTURIES, MILLENNIA}) {
            assertThat("no resolution for unit " + unit, datelineModel
                    .getResolutions(unit).hasNext(), is(true));
        }
    }
}
