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

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.FlexGanttFXControl;
import impl.com.flexganttfx.extras.skin.LayersViewSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

/**
 * A control used for displaying the list of layers used by the
 * {@link GraphicsBase}. The user can manipulate the order of the layers, the
 * opacity of the layers, and also delete layers.<br>
 * <img src="doc-files/layers-view.png" alt="Layers View">
 *
 * <h2>Example</h2>
 * <pre>{@code
 * GanttChart<MyRow> gantt = new GanttChart<>(new MyRow("Root"));
 * gantt.getLayers().add(new Layer("Flights"));
 *
 * LayersView<MyRow> layersView = new LayersView<>();
 * layersView.setGraphics(gantt.getGraphics());
 * }</pre>
 *
 * @see GraphicsBase#getLayers()
 * @see RadarView
 *
 * @param <R> the type of the rows
 * @since 1.0
 */
public class LayersView<R extends Row<?, ?, ?>> extends FlexGanttFXControl {

    /**
     * Constructs a new layer view.
     */
    public LayersView() {
        getStylesheets().add(LayersView.class.getResource("layers-view.css").toExternalForm());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new LayersViewSkin<>(this);
    }

    private final ObjectProperty<GraphicsBase<R>> graphics = new SimpleObjectProperty<>(this, "graphics");

    /**
     * The graphics property. Stores the graphics view whose layer list is
     * managed by this control.
     *
     * @see GraphicsBase#getLayers()
     *
     * @return the graphics property
     * @since 1.0
     */
    public final ObjectProperty<GraphicsBase<R>> graphicsProperty() {
        return graphics;
    }

    public final GraphicsBase<R> getGraphics() {
        return graphics.get();
    }

    public final void setGraphics(GraphicsBase<R> graphics) {
        graphicsProperty().set(graphics);
    }
}
