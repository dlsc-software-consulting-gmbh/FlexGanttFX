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
package com.flexganttfx.sprint.renderer;

import com.flexganttfx.sprint.model.BurnDownActivity;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.paint.Color;

public class BurnDownRenderer extends ActivityBarRenderer<BurnDownActivity> {

    public BurnDownRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Burn-Down");
        setFill(Color.LIGHTCORAL.deriveColor(0, 1, 1, 0.7));
        setFillSelected(Color.LIGHTCORAL.darker());
        setFillHover(Color.LIGHTCORAL.brighter());
        setStroke(Color.INDIANRED);
        setCornersRounded(false);
    }
}
