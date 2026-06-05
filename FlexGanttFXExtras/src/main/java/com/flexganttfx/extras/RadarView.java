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
package com.flexganttfx.extras;

import impl.com.flexganttfx.extras.skin.RadarViewSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.util.FlexGanttFXControl;
import com.flexganttfx.view.graphics.GraphicsBase;

/**
 * A control used for rendering an overview of all activities within a Gantt
 * chart or, to be more precise, a {@link GraphicsBase}.<br>
 * <img src="doc-files/radar-view.png" alt="Radar View">
 *
 * @param <R> the type of the rows
 * @since 1.0
 */
public class RadarView<R extends Row<?, ?, ?>> extends FlexGanttFXControl {

    /**
     * Constructs a new control.
     *
     * @since 1.0
     */
    public RadarView() {
        getStylesheets().add(
                RadarView.class.getResource("radar-view.css").toExternalForm());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RadarViewSkin<>(this);
    }

    private final ObjectProperty<GraphicsBase<R>> graphics = new SimpleObjectProperty<>(
            this, "graphics");

    /**
     * The graphics property. Stores the graphics view for which an overview of
     * all activities is rendered.
     *
     * @see GraphicsBase#getRows()
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

    // width support

    private final DoubleProperty radarWidth = new SimpleDoubleProperty(this,
            "width", 300);

    /**
     * The radarWidth property. Controls the pixel width of the canvas inside
     * the radar view.
     *
     * @return the radarWidth property
     * @since 1.0
     */
    public final DoubleProperty radarWidthProperty() {
        return radarWidth;
    }

    public final double getRadarWidth() {
        return radarWidth.get();
    }

    public final void setRadarWidth(double width) {
        if (width <= 0) {
            throw new IllegalArgumentException(
                    "width must be larger than 0 but was " + width);
        }
        radarWidth.set(width);
    }

    // height support

    private final DoubleProperty radarHeight = new SimpleDoubleProperty(this,
            "height", 200);

    /**
     * The radarHeight property. Controls the pixel height of the canvas inside
     * the radar view.
     *
     * @return the radarHeight property
     * @since 1.0
     */
    public final DoubleProperty radarHeightProperty() {
        return radarHeight;
    }

    public final double getRadarHeight() {
        return radarHeight.get();
    }

    public final void setRadarHeight(double height) {
        if (height <= 0) {
            throw new IllegalArgumentException(
                    "height must be larger than 0 but was " + height);
        }
        radarHeight.set(height);
    }
}
