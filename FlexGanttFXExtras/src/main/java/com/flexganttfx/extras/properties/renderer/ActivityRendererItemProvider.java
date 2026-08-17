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
package com.flexganttfx.extras.properties.renderer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Paint;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * Provides ControlsFX {@link org.controlsfx.control.PropertySheet.Item}
 * instances for configuring {@link ActivityRenderer}.
 *
 * @since 1.0
 */
public class ActivityRendererItemProvider implements ItemProvider<ActivityRenderer> {

    /**
     * Constructs a new provider.
     */
    public ActivityRendererItemProvider() {
    }


    @Override
    public List<PropertySheet.Item> getPropertySheetItems(ActivityRenderer renderer) {

        RendererItemProvider support = new RendererItemProvider();
        List<Item> items = support.getPropertySheetItems(renderer);

        // Stroke

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.strokeProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setStroke((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getStroke();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for drawing the activity border.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Stroke Highlight

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.strokeHighlightProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setStrokeHighlight((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getStrokeHighlight();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Stroke Highlight";
            }

            @Override
            public String getDescription() {
                return "The color used for drawing the activity border when the activity is currently drawn highlighted.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Stroke Hover

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.strokeHoverProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setStrokeHover((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getStrokeHover();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Stroke Hover";
            }

            @Override
            public String getDescription() {
                return "The color used for filling the activity border when the mouse cursor hovers over the activity.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Stroke Selected

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.strokeSelectedProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setStrokeSelected((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getStrokeSelected();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Stroke Selected";
            }

            @Override
            public String getDescription() {
                return "The color used for drawing the activity border when the activity is currently selected.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Corners Rounded

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.cornersRoundedProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setCornersRounded((Boolean) value);
            }

            @Override
            public Object getValue() {
                return renderer.isCornersRounded();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Corners Rounded";
            }

            @Override
            public String getDescription() {
                return "Determines if the corners of the activity will be drawn rounded or not.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Corner Radius

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.cornerRadiusProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setCornerRadius((Double) value);
            }

            @Override
            public Object getValue() {
                return renderer.getCornerRadius();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Corner Radius";
            }

            @Override
            public String getDescription() {
                return "The radius used for the activity corners when rounded corners are used.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Line Width

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.lineWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return renderer.getLineWidth();
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
                return "The line width used for the border.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        return items;
    }
}
