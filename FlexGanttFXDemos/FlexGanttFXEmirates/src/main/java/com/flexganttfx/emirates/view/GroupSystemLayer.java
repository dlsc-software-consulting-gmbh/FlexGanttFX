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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.time.Instant;

/**
 * Created by dirk on 08/07/16.
 */
public class GroupSystemLayer extends SystemLayer {

    public GroupSystemLayer(GraphicsBase<?> graphics) {
        super("Group Layer", graphics);
    }

    @Override
    public void drawLayer(RowCanvas canvas, Instant startTime, Instant endTime) {
        Row<?,?,?> row = canvas.getRow();
        if (row instanceof Group) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.GRAY);
            gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        }
    }
}
