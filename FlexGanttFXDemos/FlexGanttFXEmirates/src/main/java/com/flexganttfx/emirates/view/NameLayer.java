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
package com.flexganttfx.emirates.view;

import com.flexganttfx.emirates.model.Group;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.time.Instant;

/**
 * Created by dirk on 21.06.16.
 */
public class NameLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

    private final Color backgroundColor = Color.color(0, 0, 0, .5);
    private final Color foregroundColor = Color.WHITE;

    public NameLayer(GraphicsBase graphics) {
        super("Name Layer", graphics);
    }

    @Override
    public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
        R row = canvas.getRow();
        if (row != null && !(row instanceof Group)) {

            String name = row.getName();
            GraphicsContext gc = canvas.getGraphicsContext2D();

            double textWidth = 80;
            double textHeight = 20;

            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.BOTTOM);
            gc.setFill(backgroundColor);
            gc.fillRect(0, 0, textWidth + 4, textHeight);
            gc.setFill(foregroundColor);
            gc.fillText(name, 2, textHeight);
        }
    }
}
