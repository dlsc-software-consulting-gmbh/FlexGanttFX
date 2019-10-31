/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.layout;

import com.flexganttfx.model.Layout;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;

/**
 * The standard layout used by all rows and lines. Lays out activities
 * horizontally.
 *
 * @see Row#setLayout(Layout)
 * @see Row#getLineLayout(int)
 * @see LinesManager#getLineLayout(int)
 *
 * @since 1.0
 */
public class GanttLayout extends Layout {

    /**
     * Constructs a new layout.
     *
     * @since 1.0
     */
    public GanttLayout() {
    }

    @Override
    public boolean isSupportingHorizontalCursorLine() {
        return false;
    }

    @Override
    public String toString() {
        return "GanttLayout";
    }
}
