/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.covid;

import org.apache.commons.text.WordUtils;

public enum View {
    TOTAL_CASES,
    TOTAL_CASES_PER_MILLIONS,

    NEW_CASES,
    NEW_CASES_PER_MILLIONS,

    TOTAL_DEATHS,
    TOTAL_DEATHS_PER_MILLIONS,

    NEW_DEATHS,
    NEW_DEATHS_PER_MILLIONS,

    NEW_TESTS,
    NEW_TESTS_PER_THOUSAND,

    TOTAL_TESTS,
    TOTAL_TESTS_PER_THOUSAND;

    public String getDisplayName() {
        return WordUtils.capitalizeFully(name().toLowerCase().replace("_", " "));
    }
}
