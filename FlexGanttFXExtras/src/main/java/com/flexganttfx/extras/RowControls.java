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
package com.flexganttfx.extras;

import com.flexganttfx.extras.util.Messages;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * A simple row controls view with only one button called "Edit". Pressing the
 * button invokes {@link GraphicsBase#startRowEditing(Row)}.
 *
 * @param <R> the row type
 * @since 1.0
 */
public class RowControls<R extends Row<?, ?, ?>> extends HBox {

    /**
     * Constructs row controls for the given row.
     *
     * @param graphics the target graphics view
     * @param row the row for which the controls will be used
     * @since 1.0
     */
    public RowControls(GraphicsBase<R> graphics, R row) {
        setPickOnBounds(false);
        setMinSize(0, 0);
        setAlignment(Pos.TOP_RIGHT);
        setFillHeight(true);

        Button editButton = new Button(Messages.getString("RowControls.BUTTON_EDIT"));
        editButton.getStyleClass().add("row-controls-button");
        editButton.setOnAction(evt -> graphics.startRowEditing(row));
        getChildren().add(editButton);
    }
}