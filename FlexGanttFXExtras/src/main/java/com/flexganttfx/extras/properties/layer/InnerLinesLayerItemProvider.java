/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
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
 * A property sheet item provider for {@link InnerLinesLayer}.
 */
public class InnerLinesLayerItemProvider implements ItemProvider<InnerLinesLayer> {

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
