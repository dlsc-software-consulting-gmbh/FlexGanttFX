/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.msproject;

import net.sf.mpxj.ProjectFile;

import java.util.function.Supplier;

/**
 * A named sample project entry shown in the project-selector ComboBox.
 * The {@link #getFactory()} supplier creates a fresh {@link ProjectFile}
 * on each call so the chart always gets a clean instance.
 */
public final class SampleProject {

    private final String name;
    private final Supplier<ProjectFile> factory;

    public SampleProject(String name, Supplier<ProjectFile> factory) {
        this.name    = name;
        this.factory = factory;
    }

    public String getName() {
        return name;
    }

    public Supplier<ProjectFile> getFactory() {
        return factory;
    }

    @Override
    public String toString() {
        return name;
    }
}
