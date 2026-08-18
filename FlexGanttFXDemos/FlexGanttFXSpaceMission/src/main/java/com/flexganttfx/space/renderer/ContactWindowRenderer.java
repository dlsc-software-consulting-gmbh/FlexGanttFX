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

import com.flexganttfx.space.model.ContactWindow;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.scene.paint.Color;

/** Renders contact windows as steel-blue bars. */
public class ContactWindowRenderer extends ActivityBarRenderer<ContactWindow> {

    public ContactWindowRenderer(GraphicsBase<?> graphics) {
        super(graphics, "Contact Window");
        setFill(Color.STEELBLUE);
        setFillSelected(Color.STEELBLUE.darker());
        setFillHover(Color.STEELBLUE.brighter());
        setFillHighlight(Color.STEELBLUE.deriveColor(0, 1, 1.3, 0.8));
        setFillPressed(Color.STEELBLUE.darker().darker());
        setStroke(Color.STEELBLUE.darker());
        setTextFill(Color.WHITE);
        setTextFillSelected(Color.WHITE);
        setTextFillHover(Color.WHITE);
        setTextFillHighlight(Color.WHITE);
        setTextFillPressed(Color.WHITE);
    }
}
