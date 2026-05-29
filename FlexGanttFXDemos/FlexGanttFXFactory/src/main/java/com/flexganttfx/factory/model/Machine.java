/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.factory.model;

import com.flexganttfx.model.Row;

/**
 * Represents a single machine on a production line. Each machine can have
 * multiple {@link Job} activities scheduled on it.
 * <p>
 * The self-referential generics allow {@link ProductionLine} (a subtype of
 * Machine) to appear as children of a root Machine, enabling a two-level tree
 * inside {@code GanttChart<Machine>}.
 */
public class Machine extends Row<Machine, Machine, Job> {

    public Machine(String name) {
        super(name);
    }
}
