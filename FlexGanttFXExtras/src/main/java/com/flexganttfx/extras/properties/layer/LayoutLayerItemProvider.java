/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.layer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.layer.LayoutLayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link LayoutLayer}.
 */
public class LayoutLayerItemProvider implements ItemProvider<LayoutLayer> {

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(LayoutLayer layer) {
        SystemLayerItemProvider provider = new SystemLayerItemProvider();
        List<Item> items = provider.getPropertySheetItems(layer);

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.paddingFillProperty());
            }
            @Override
            public void setValue(Object value) {
                layer.setPaddingFill((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getPaddingFill();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Padding Fill";
            }

            @Override
            public String getDescription() {
                return "The color used for filling the background of the padding area.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        return items;
    }
}
