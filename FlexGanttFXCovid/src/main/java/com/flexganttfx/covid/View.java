package com.flexganttfx.covid;

import org.apache.commons.text.WordUtils;

public enum View {
    TOTAL_CASES,
    NEW_CASES,
    TOTAL_DEATHS;

    public String getDisplayName() {
        return WordUtils.capitalizeFully(name().toLowerCase().replace("_", " "));
    }
}
