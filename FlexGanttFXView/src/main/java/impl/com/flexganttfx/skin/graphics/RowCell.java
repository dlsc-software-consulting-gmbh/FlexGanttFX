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
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

public class RowCell<R extends Row<?, ?, ?>> extends ListCell<R> {

    private static final String DEFAULT_STYLE_CLASS = "row-cell";

    private final RowPane<R> rowPane;

    public RowCell(GraphicsBase<R> graphics) {
        requireNonNull(graphics);

        this.rowPane = new RowPane<>(graphics);

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        rowPane.prefWidthProperty().bind(widthProperty());
        rowPane.rowProperty().bind(itemProperty());

        /*
         * The pref height of the row pane is bound to the height of the row. So
         * when the row pane grows the cell will also grow.
         */
        Bindings.bindBidirectional(prefHeightProperty(), rowPane.prefHeightProperty());

        /*
         * We might have to redraw activity links.
         */
        heightProperty().addListener((obs, oldHeight, newHeight) -> ((GraphicsBaseSkin<?, ?>) graphics.getSkin()).getLinksCanvas().requestRedraw("height of row " + (getItem() != null ? getItem().getName() : "(empty row)") + " changed from " + oldHeight + " to " + newHeight));

        setPrefWidth(0);
        setGraphic(rowPane);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        visibleProperty().addListener(it -> {
            boolean visible = isVisible();

            Row<?, ?, ?> row = getItem();
            if (row != null) {
                row.getProperties().put("com.flexganttfx.row.showing", visible);
            }

            if (visible) {
                if (LoggingDomain.RENDERING.isLoggable(Level.FINE)) {
                    LoggingDomain.RENDERING.fine("redrawing canvas because of row cell visibility changing to true");
                }
                rowPane.getCanvas().requestRedraw("row cell became visible");
            }
        });
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        rowPane.resizeRelocate(0, 0, getWidth(), getHeight());
    }

    public final RowPane<R> getRowPane() {
        return rowPane;
    }
}
