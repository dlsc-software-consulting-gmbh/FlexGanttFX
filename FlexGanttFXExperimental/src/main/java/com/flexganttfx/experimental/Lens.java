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
/**
 *
 */
package com.flexganttfx.experimental;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import static java.util.Objects.requireNonNull;

/**
 * @param <R>
 *            the type of the rows that are displayed by the Gantt chart.
 */
public class Lens<R extends Row<?, ?, ?>> extends Control {

    private final GraphicsBase<R> graphics;

    public Lens(GraphicsBase<R> graphics) {
        this.graphics = requireNonNull(graphics);

        getStyleClass().add("graphics-lens");

        prefWidthProperty().bind(graphics.widthProperty());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new LensSkin<>(this);
    }

    /**
     * @return the graphics
     * @since 1.1
     */
    public GraphicsBase<R> getGraphics() {
        return graphics;
    }

    private final ObservableList<R> rows = FXCollections.observableArrayList();

    public final ObservableList<R> getRows() {
        return rows;
    }

    private final IntegerProperty startIndex = new SimpleIntegerProperty(this,
            "startIndex", 0);

    public final IntegerProperty startIndexProperty() {
        return startIndex;
    }

    public final void setStartIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "index must be >= 0 but was " + index);
        }
        startIndexProperty().set(index);
    }

    public final int getStartIndex() {
        return startIndexProperty().get();
    }

    private final IntegerProperty rowCount = new SimpleIntegerProperty(this,
            "rowCount", 4);

    public final IntegerProperty rowCountProperty() {
        return rowCount;
    }

    public final void setRowCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "row count must be larger than 0 but was " + count);
        }
        rowCountProperty().set(count);
    }

    public final int getRowCount() {
        return rowCountProperty().get();
    }
}
