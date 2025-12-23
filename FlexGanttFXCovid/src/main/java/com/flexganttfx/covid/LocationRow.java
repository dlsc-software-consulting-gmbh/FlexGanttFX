/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.covid;

import com.flexganttfx.covid.CovidUI.Cases;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.ChartLayout;

import java.util.HashMap;
import java.util.Map;

class LocationRow extends Row<LocationRow, LocationRow, Cases> {

    private final Map<View, Double> maxCases = new HashMap<>();

    private final Map<View, Double> maxCasesGlobally = new HashMap<>();

    private final ChartLayout chartLayout = new ChartLayout();

    private String iso3CountryCode;

    public LocationRow(String name) {
        super(name);

        setHeight(200);
        setMinHeight(50);

        chartLayout.setPadding(0);
        setLayout(chartLayout);
    }

    public String getIso3CountryCode() {
        return iso3CountryCode;
    }

    public void setIso3CountryCode(String iso3CountryCode) {
        this.iso3CountryCode = iso3CountryCode;
    }

    public void setMax(View view, Double cases) {
        maxCases.put(view, cases);
    }

    public double getMax(View view) {
        return maxCases.getOrDefault(view, 0.0);
    }

    public void updateMaxValueAndTickLine(View view) {
        final double max = getMax(view);
        chartLayout.setMaxValue(max * 1.25);
        chartLayout.getMajorTicks().setAll(max);
    }

    public void setMaxGlobally(View view, Double cases) {
        maxCasesGlobally.put(view, cases);
    }

    public double getMaxGlobally(View view) {
        return maxCasesGlobally.getOrDefault(view, 0.0);
    }

    public void updateMaxValueGloballyAndTickLine(View view) {
        final double globalMax = getMaxGlobally(view);
        chartLayout.setMaxValue(globalMax * 1.25);
        chartLayout.getMajorTicks().setAll(globalMax);
    }
}