/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.layer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.layer.HoverTimeIntervalLayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link HoverTimeIntervalLayer}.
 */
public class HoverTimeIntervalLayerItemProvider implements ItemProvider<HoverTimeIntervalLayer> {

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(HoverTimeIntervalLayer layer) {
        SystemLayerItemProvider provider = new SystemLayerItemProvider();
        List<Item> items = provider.getPropertySheetItems(layer);

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.hoverTimeIntervalFillProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setHoverTimeIntervalFill((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getHoverTimeIntervalFill();
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
                return "The color used for visualizing the hover time interval of the dateline.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        return items;
    }
}
