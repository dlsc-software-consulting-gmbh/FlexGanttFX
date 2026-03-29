/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.space.model;

import com.flexganttfx.model.activity.MutableChartActivityBase;

import java.time.Instant;

/**
 * A telemetry data point rendered as a chart bar. {@code chartValue} holds
 * signal strength (0.0–1.0); {@code batteryLevel} holds the battery percentage
 * (0.0–1.0) for optional use by the renderer.
 */
public class TelemetryActivity extends MutableChartActivityBase<String> {

    private double batteryLevel;

    public TelemetryActivity(String name, Instant start, Instant end,
                             double signalStrength, double batteryLevel) {
        setName(name);
        setStartTime(start);
        setEndTime(end);
        setChartValue(signalStrength);
        this.batteryLevel = batteryLevel;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}
