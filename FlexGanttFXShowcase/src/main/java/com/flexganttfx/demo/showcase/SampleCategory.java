/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.showcase;

import com.flexganttfx.demo.Sample;
import org.kordamp.ikonli.Ikon;

import java.util.List;
import java.util.function.Supplier;

/**
 * Describes a category of showcase samples.
 */
public class SampleCategory {

    private final String name;
    private final Ikon icon;
    private final String accentColor;
    private final List<Supplier<Sample>> sampleSuppliers;

    public SampleCategory(String name, Ikon icon, String accentColor, List<Supplier<Sample>> sampleSuppliers) {
        this.name = name;
        this.icon = icon;
        this.accentColor = accentColor;
        this.sampleSuppliers = List.copyOf(sampleSuppliers);
    }

    public String getName() {
        return name;
    }

    public Ikon getIcon() {
        return icon;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public List<Supplier<Sample>> getSampleSuppliers() {
        return sampleSuppliers;
    }

    @Override
    public String toString() {
        return name;
    }
}
