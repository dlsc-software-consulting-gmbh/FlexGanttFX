/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.layer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.layer.GridLinesLayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

public class GridLinesLayerItemProvider implements ItemProvider<GridLinesLayer> {

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(GridLinesLayer layer) {

        SystemLayerItemProvider provider = new SystemLayerItemProvider();
        List<Item> items = provider.getPropertySheetItems(layer);

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.lineStroke1Property());
            }

            @Override
            public void setValue(Object value) {
                layer.setLineStroke1((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getLineStroke1();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Line Stroke 1";
            }

            @Override
            public String getDescription() {
                return "The color used for the first grid line level";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.lineWidth1Property());
            }

            @Override
            public void setValue(Object value) {
                layer.setLineWidth1((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getLineWidth1();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Line Width 1";
            }

            @Override
            public String getDescription() {
                return "The line width used for the first grid line level";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.lineStroke2Property());
            }

            @Override
            public void setValue(Object value) {
                layer.setLineStroke2((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getLineStroke2();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Line Stroke 2";
            }

            @Override
            public String getDescription() {
                return "The color used for the first grid line level";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.lineWidth2Property());
            }

            @Override
            public void setValue(Object value) {
                layer.setLineWidth2((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getLineWidth2();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Line Width 2";
            }

            @Override
            public String getDescription() {
                return "The line width used for the second grid line level";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.lineStroke3Property());
            }

            @Override
            public void setValue(Object value) {
                layer.setLineStroke3((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getLineStroke3();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Line Stroke 3";
            }

            @Override
            public String getDescription() {
                return "The color used for the third grid line level";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.lineWidth3Property());
            }

            @Override
            public void setValue(Object value) {
                layer.setLineWidth3((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getLineWidth3();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Line Width 3";
            }

            @Override
            public String getDescription() {
                return "The line width used for the third grid line level";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        return items;
    }
}
