/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.f1.model;

import javafx.scene.paint.Color;

public enum TireCompound {

    SOFT("Soft", Color.web("#E8002D")),
    MEDIUM("Medium", Color.web("#FFF200")),
    HARD("Hard", Color.web("#FFFFFF")),
    INTERMEDIATE("Inter", Color.web("#39B54A")),
    WET("Wet", Color.web("#0067FF")),
    UNKNOWN("?", Color.GRAY);

    private final String displayName;
    private final Color color;

    TireCompound(String displayName, Color color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    public static TireCompound fromString(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }

        String normalized = value.trim().replace(' ', '_').toUpperCase();
        for (TireCompound compound : values()) {
            if (compound.name().equals(normalized)) {
                return compound;
            }
        }

        return UNKNOWN;
    }
}
