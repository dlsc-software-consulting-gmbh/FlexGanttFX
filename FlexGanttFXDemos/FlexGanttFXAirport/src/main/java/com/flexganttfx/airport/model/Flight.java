/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.airport.model;

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

/**
 * Represents a flight leg (arrival or departure) on an aircraft row.
 * The user object stores the flight number.
 */
public class Flight extends MutableActivityBase<String> {

    public Flight(String flightNumber, Instant start, Instant end) {
        setName(flightNumber);
        setUserObject(flightNumber);
        setStartTime(start);
        setEndTime(end);
    }
}
