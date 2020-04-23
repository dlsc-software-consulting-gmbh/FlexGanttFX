/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.layer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.layer.AgendaLinesLayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link AgendaLinesLayer}.
 */
public class AgendaLinesLayerItemProvider implements ItemProvider<AgendaLinesLayer> {

    @Override
    public List<Item> getPropertySheetItems(AgendaLinesLayer layer) {
        SystemLayerItemProvider provider = new SystemLayerItemProvider();
        List<Item> items = provider.getPropertySheetItems(layer);

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorLinesVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorLinesVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isMajorLinesVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Major Lines Visible";
            }

            @Override
            public String getDescription() {
                return "Determines if major agenda lines will be shown.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorLinesStrokeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorLinesStroke((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getMajorLinesStroke();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Major Lines Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for the major agenda lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorLinesLineWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorLinesLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getMajorLinesLineWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Major Lines Line Width";
            }

            @Override
            public String getDescription() {
                return "The width of the major agenda lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorLinesVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorLinesVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isMinorLinesVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Minor Lines Visible";
            }

            @Override
            public String getDescription() {
                return "Determines if minor agenda lines will be shown.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorLinesStrokeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorLinesStroke((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getMinorLinesStroke();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Minor Lines Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for the major agenda lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorLinesLineWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorLinesLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getMinorLinesLineWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Minor Lines Line Width";
            }

            @Override
            public String getDescription() {
                return "The width of the minor agenda lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });
        return items;
    }
}
