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
import com.flexganttfx.view.graphics.layer.AgendaLinesLayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * Provides ControlsFX {@link org.controlsfx.control.PropertySheet.Item}
 * instances for configuring {@link AgendaLinesLayer}.
 *
 * @since 1.0
 */
public class AgendaLinesLayerItemProvider implements ItemProvider<AgendaLinesLayer> {

    /**
     * Constructs a new provider.
     */
    public AgendaLinesLayerItemProvider() {
    }

    @Override
    public List<Item> getPropertySheetItems(AgendaLinesLayer layer) {
        SystemLayerItemProvider provider = new SystemLayerItemProvider();
        List<Item> items = provider.getPropertySheetItems(layer);

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorLinesVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorLinesVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isMajorLinesVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Major Lines Visible";
            }

            @Override
            public String getDescription() {
                return "Determines if major agenda lines will be shown.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorLinesStrokeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorLinesStroke((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getMajorLinesStroke();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Major Lines Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for the major agenda lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorLinesLineWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorLinesLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getMajorLinesLineWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Major Lines Line Width";
            }

            @Override
            public String getDescription() {
                return "The width of the major agenda lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorLinesVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorLinesVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isMinorLinesVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Minor Lines Visible";
            }

            @Override
            public String getDescription() {
                return "Determines if minor agenda lines will be shown.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorLinesStrokeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorLinesStroke((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getMinorLinesStroke();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Minor Lines Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for the major agenda lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorLinesLineWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorLinesLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getMinorLinesLineWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Minor Lines Line Width";
            }

            @Override
            public String getDescription() {
                return "The width of the minor agenda lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });
        return items;
    }
}
