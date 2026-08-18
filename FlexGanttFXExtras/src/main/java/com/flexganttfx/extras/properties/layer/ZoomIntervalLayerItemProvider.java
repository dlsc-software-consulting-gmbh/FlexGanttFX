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
package com.flexganttfx.extras.properties.layer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.layer.ZoomTimeIntervalLayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * Provides ControlsFX {@link org.controlsfx.control.PropertySheet.Item}
 * instances for configuring {@link ZoomTimeIntervalLayer}.
 *
 * @since 1.0
 */
public class ZoomIntervalLayerItemProvider implements ItemProvider<ZoomTimeIntervalLayer> {

    /**
     * Constructs a new provider.
     */
    public ZoomIntervalLayerItemProvider() {
    }

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(ZoomTimeIntervalLayer layer) {
        SystemLayerItemProvider provider = new SystemLayerItemProvider();
        List<Item> items = provider.getPropertySheetItems(layer);

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.zoomTimeIntervalFillProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setZoomTimeIntervalFill((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getZoomTimeIntervalFill();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Focused Time Fill";
            }

            @Override
            public String getDescription() {
                return "The color used for visualizing the focused time interval of the dateline.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        return items;
    }
}
