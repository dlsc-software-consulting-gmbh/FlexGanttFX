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
package com.flexganttfx.extras.properties.layer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.layer.InnerLinesLayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * Provides ControlsFX {@link org.controlsfx.control.PropertySheet.Item}
 * instances for configuring {@link InnerLinesLayer}.
 *
 * @since 1.0
 */
public class InnerLinesLayerItemProvider implements ItemProvider<InnerLinesLayer> {

    /**
     * Constructs a new provider.
     */
    public InnerLinesLayerItemProvider() {
    }

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(InnerLinesLayer layer) {

        SystemLayerItemProvider provider = new SystemLayerItemProvider();
        List<Item> items = provider.getPropertySheetItems(layer);

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.strokeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setStroke((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getStroke();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for the inner lines";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.lineWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getLineWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Line Width";
            }

            @Override
            public String getDescription() {
                return "The width of the inner lines";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.drawLastDividerLineProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setDrawLastDividerLine((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isDrawLastDividerLine();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Draw Last Divider Line";
            }

            @Override
            public String getDescription() {
                return "Controls if a divider line is drawn for the last inner line";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        return items;
    }
}
