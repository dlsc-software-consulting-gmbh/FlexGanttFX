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
import com.flexganttfx.view.graphics.renderer.CompletableActivityRenderer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Paint;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link CompletableActivityRenderer}.
 */
public class CompletableActivityRendererItemProvider implements ItemProvider<CompletableActivityRenderer> {

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(CompletableActivityRenderer renderer) {
        ActivityBarRendererItemProvider support = new ActivityBarRendererItemProvider();

        List<Item> list = support.getPropertySheetItems(renderer);

        list.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillCompletionProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFillCompletion((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFillCompletion();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Completion";
            }

            @Override
            public String getDescription() {
                return "The paint used for drawing that segment of the activity that represents the completed part.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        list.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillCompletionHoverProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFillCompletionHover((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFillCompletionHover();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Completion Hover";
            }

            @Override
            public String getDescription() {
                return "The paint used for drawing that segment of the activity that represents the completed part.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        list.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillCompletionPressedProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFillCompletionPressed((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFillCompletionPressed();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Completion Pressed";
            }

            @Override
            public String getDescription() {
                return "The paint used for drawing that segment of the activity that represents the completed part.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        list.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillCompletionSelectedProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFillCompletionSelected((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFillCompletionSelected();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Completion Selected";
            }

            @Override
            public String getDescription() {
                return "The paint used for drawing that segment of the activity that represents the completed part.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        list.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillCompletionHighlightProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFillCompletionHighlight((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFillCompletionHighlight();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Completion Highlight";
            }

            @Override
            public String getDescription() {
                return "The paint used for drawing that segment of the activity that represents the completed part.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });
        return list;
    }
}
