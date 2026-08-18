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
package com.flexganttfx.space.renderer;

import com.flexganttfx.space.model.Maneuver;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.paint.Color;

/** Renders orbital maneuvers as orange bars with a darker stroke. */
public class ManeuverRenderer extends ActivityBarRenderer<Maneuver> {

    public ManeuverRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Maneuver");
        setFill(Color.ORANGE);
        setFillSelected(Color.DARKORANGE);
        setFillHover(Color.ORANGE.brighter());
        setFillHighlight(Color.ORANGE.deriveColor(0, 1, 1.2, 0.8));
        setFillPressed(Color.DARKORANGE.darker());
        setStroke(Color.DARKORANGE.darker());
        setTextFill(Color.BLACK);
        setTextFillSelected(Color.BLACK);
        setTextFillHover(Color.BLACK);
        setTextFillHighlight(Color.BLACK);
        setTextFillPressed(Color.BLACK);
    }
}
