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
package com.flexganttfx.model.layout;

import com.flexganttfx.model.Layout;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;

/**
 * The standard layout used by all rows and lines. Lays out activities
 * horizontally.
 *
 * <h2>Code Example</h2>
 *
 * <pre>
 * Aircraft aircraft = new Aircraft("D-ABCD");
 * aircraft.setLayout(new GanttLayout());
 *
 * graphics.setActivityRenderer(Flight.class, GanttLayout.class, flightRenderer);
 * </pre>
 *
 * @see AgendaLayout
 * @see ChartLayout
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
