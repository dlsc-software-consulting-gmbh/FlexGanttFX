/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.renderer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.renderer.Renderer;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.paint.Paint;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link Renderer}.
 */
public class RendererItemProvider implements ItemProvider<Renderer> {

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(Renderer renderer) {
        ObservableList<Item> items = FXCollections.observableArrayList();

        // Enabled

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.enabledProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setEnabled((boolean) value);
            }

            @Override
            public Object getValue() {
                return renderer.isEnabled();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Enabled";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the renderer (if disabled activities using this renderer will not be shown at all).";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.snapToPixelProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setSnapToPixel((boolean) value);
            }

            @Override
            public Object getValue() {
                return renderer.isSnapToPixel();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Snap To Pixel";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the snap to pixel feature.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Padding
        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.paddingProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setPadding((Insets) value);
            }

            @Override
            public Object getValue() {
                return renderer.getPadding();
            }

            @Override
            public Class<?> getType() {
                return Insets.class;
            }

            @Override
            public String getName() {
                return "Padding";
            }

            @Override
            public String getDescription() {
                return "Specifies a padding to be applied (not applicable for all renderers).";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Fill

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFill((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFill();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Fill";
            }

            @Override
            public String getDescription() {
                return "The color used for filling the activity background.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Fill Highlight

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillHighlightProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFillHighlight((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFillHighlight();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Fill Highlight";
            }

            @Override
            public String getDescription() {
                return "The color used for filling the activity background when the activity is currently drawn highlighted.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Fill Hover

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillHoverProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFillHover((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFillHover();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Fill Hover";
            }

            @Override
            public String getDescription() {
                return "The color used for filling the activity background when the mouse cursor hovers over the activity.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Fill Selected

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillSelectedProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFillSelected((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFillSelected();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Fill Selected";
            }

            @Override
            public String getDescription() {
                return "The color used for filling the activity background when the activity is selected.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Fill pressed.

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fillPressedProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFillPressed((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFillPressed();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Fill Pressed";
            }

            @Override
            public String getDescription() {
                return "The color used for filling the activity background when the user presses on it.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Alpha

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.alphaProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setAlpha((Double) value);
            }

            @Override
            public Object getValue() {
                return renderer.getAlpha();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Opacity / Alpha";
            }

            @Override
            public String getDescription() {
                return "The alpha value used when drawing the activity (opaque, transparent).";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        return items;
    }
}
