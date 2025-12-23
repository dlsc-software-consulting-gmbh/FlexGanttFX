/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.layer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link SystemLayer}.
 */
public class SystemLayerItemProvider implements ItemProvider<SystemLayer> {

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(SystemLayer layer) {
        List<Item> items = new ArrayList<>();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.visibleProperty());
            }
            @Override
            public void setValue(Object value) {
                layer.setVisible((boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Visible";
            }

            @Override
            public String getDescription() {
                return "Controls visibility of the system layer.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.snapToPixelProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setSnapToPixel((boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isSnapToPixel();
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
                return "System Layer: " + layer.getName();
            }
        });

        return items;
    }
}
