/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model;

import com.flexganttfx.model.activity.ActivityBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActivityRefTest {

    @Test
    public void shouldRequireNonNullRow() {
        assertThrows(NullPointerException.class,
                () -> new ActivityRef<>(null, null, new ActivityBase<>()));
    }
}
