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
package com.flexganttfx.space.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;

/**
 * Represents a single spacecraft. Self-referential generics allow
 * {@link SpaceFleet} (a subtype) to act as the invisible tree root inside
 * {@code GanttChart<Spacecraft>}.
 */
public class Spacecraft extends Row<Spacecraft, Spacecraft, Activity> {

    public Spacecraft(String name) {
        super(name);
    }
}
