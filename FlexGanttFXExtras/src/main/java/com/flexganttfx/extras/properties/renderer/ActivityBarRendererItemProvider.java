/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.renderer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

public class ActivityBarRendererItemProvider implements ItemProvider<ActivityBarRenderer> {

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(ActivityBarRenderer renderer) {
        ActivityRendererItemProvider support = new ActivityRendererItemProvider();
        List<Item> items = support.getPropertySheetItems(renderer);

        // Text Fill

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.textFillProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setTextFill((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getTextFill();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Text Fill";
            }

            @Override
            public String getDescription() {
                return "The color used for the text.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Text Fill Selected

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.textFillSelectedProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setTextFillSelected((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getTextFillSelected();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Text Fill Selected";
            }

            @Override
            public String getDescription() {
                return "The color used for the text when the activity is selected.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Text Fill Hover

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.textFillHighlightProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setTextFillHover((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getTextFillHover();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Text Fill Hover";
            }

            @Override
            public String getDescription() {
                return "The color used for the text when the mouse cursor hovers over the activity.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Text Fill

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.textFillHighlightProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setTextFillHighlight((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getTextFillHighlight();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Text Fill Highlight";
            }

            @Override
            public String getDescription() {
                return "The color used for the text when the activity is highlighted / blinking.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Text Fill

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.textFillPressedProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setTextFillPressed((Paint) value);
            }

            @Override
            public Object getValue() {
                return renderer.getTextFillPressed();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Text Fill Pressed";
            }

            @Override
            public String getDescription() {
                return "The color used for the text when the activity is pressed.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Text Gap

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.textGapProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setTextGap((double) value);
            }

            @Override
            public Object getValue() {
                return renderer.getTextGap();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Text Gap";
            }

            @Override
            public String getDescription() {
                return "The gap between the activity and the text.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Glossy

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.glossyProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setGlossy((boolean) value);
            }

            @Override
            public Object getValue() {
                return renderer.isGlossy();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Glossy";
            }

            @Override
            public String getDescription() {
                return "Shows / hides a glossy effect on the activity.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Auto fix text

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.autoFixTextProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setAutoFixText((boolean) value);
            }

            @Override
            public Object getValue() {
                return renderer.isAutoFixText();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Fix Text";
            }

            @Override
            public String getDescription() {
                return "Controls if text stays within the viewport as long as possible.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Bar height

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.barHeightProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setBarHeight((double) value);
            }

            @Override
            public Object getValue() {
                return renderer.getBarHeight();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Bar Height";
            }

            @Override
            public String getDescription() {
                return "The size of the bar representing an activity.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        // Font

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(renderer.fontProperty());
            }

            @Override
            public void setValue(Object value) {
                renderer.setFont((Font) value);
            }

            @Override
            public Object getValue() {
                return renderer.getFont();
            }

            @Override
            public Class<?> getType() {
                return Font.class;
            }

            @Override
            public String getName() {
                return "Font";
            }

            @Override
            public String getDescription() {
                return "The font used for any text.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + renderer.getName();
            }
        });

        return items;
    }
}
