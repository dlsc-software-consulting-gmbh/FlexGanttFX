/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.layer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.layer.DSTLineLayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link DSTLineLayer}.
 */
public class DSTLineLayerItemProvider implements ItemProvider<DSTLineLayer> {

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(DSTLineLayer layer) {

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
                return "The color used for the DST line";
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
                return "The width of the DST line";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        return items;
    }
}
