package com.flexganttfx.covid;

import com.flexganttfx.covid.CovidUI.Cases;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.ChartLayout;

import java.util.HashMap;
import java.util.Map;

class LocationRow extends Row<LocationRow, LocationRow, Cases> {

    private Map<View, Double> maxCases = new HashMap<>();

    private final ChartLayout chartLayout = new ChartLayout();

    public LocationRow(String name) {
        super(name);

        setHeight(200);
        setMinHeight(50);

        chartLayout.setPadding(0);
        setLayout(chartLayout);
    }

    public void setMax(View view, Double cases) {
        maxCases.put(view, cases);
    }

    public double getMax(View view) {
        return maxCases.getOrDefault(view, 0.0);
    }

    public void updateMaxValue(View view) {
        final double max = getMax(view);
        chartLayout.setMaxValue(max * 1.25);
        chartLayout.getMajorTicks().setAll(max);
    }


}