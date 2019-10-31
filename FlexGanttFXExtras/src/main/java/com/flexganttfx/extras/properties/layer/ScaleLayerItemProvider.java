/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.layer;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.graphics.layer.ScaleLayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

public class ScaleLayerItemProvider implements ItemProvider<ScaleLayer> {

    @Override
    public List<PropertySheet.Item> getPropertySheetItems(ScaleLayer layer) {
        SystemLayerItemProvider provider = new SystemLayerItemProvider();
        List<Item> items = provider.getPropertySheetItems(layer);

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorChartLabelsVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorChartLabelsVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isMajorChartLabelsVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Major Chart Labels Visible";
            }

            @Override
            public String getDescription() {
                return "Controls whether the labels for the major chart lines will be visible.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorChartLinesVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorChartLinesVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isMajorChartLinesVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Major Chart Lines Visible";
            }

            @Override
            public String getDescription() {
                return "Determines if major chart lines will be shown.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorChartLinesStrokeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorChartLinesStroke((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getMajorChartLinesStroke();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Major Chart Lines Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for the major chart lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " +layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorChartLabelsFillProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorChartLabelsFill((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getMajorChartLabelsFill();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Major Chart Labels Fill";
            }

            @Override
            public String getDescription() {
                return "The color used for the major chart labels.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorChartLabelsFillProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorChartLabelsFill((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getMinorChartLabelsFill();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Minor Chart Labels Fill";
            }

            @Override
            public String getDescription() {
                return "The color used for the minor chart labels.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " +layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorChartLinesLineWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorChartLinesLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getMajorChartLinesLineWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Major Chart Lines Width";
            }

            @Override
            public String getDescription() {
                return "The width of the major chart lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.majorChartLinesSizeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMajorChartLinesSize((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getMajorChartLinesSize();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Major Chart Lines Size";
            }

            @Override
            public String getDescription() {
                return "The size of the major chart lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorChartLabelsVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorChartLabelsVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isMinorChartLabelsVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Minor Chart Labels Visible";
            }

            @Override
            public String getDescription() {
                return "Controls whether the labels for the major chart lines will be visible.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorChartLinesVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorChartLinesVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isMinorChartLinesVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Minor Chart Lines Visible";
            }

            @Override
            public String getDescription() {
                return "Determines if minor chart lines will be shown.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorChartLinesStrokeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorChartLinesStroke((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getMinorChartLinesStroke();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Minor Chart Lines Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for the minor chart lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorChartLabelsFillProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorChartLabelsFill((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getMinorChartLabelsFill();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Minor Chart Labels Fill";
            }

            @Override
            public String getDescription() {
                return "The color used for the minor chart labels.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorChartLinesLineWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorChartLinesLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getMinorChartLinesLineWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Minor Chart Lines Width";
            }

            @Override
            public String getDescription() {
                return "The width of the minor chart lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.minorChartLinesSizeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setMinorChartLinesSize((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getMinorChartLinesSize();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Minor Chart Lines Size";
            }

            @Override
            public String getDescription() {
                return "The size of the minor chart lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.agendaLinesVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setAgendaLinesVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isAgendaLinesVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Major Agenda Lines Visible";
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
                return Optional.of(layer.agendaLinesStrokeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setAgendaLinesStroke((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getAgendaLinesStroke();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Major Agenda Lines Stroke";
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
                return Optional.of(layer.agendaLabelsFillProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setAgendaLabelsFill((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getAgendaLabelsFill();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Major Agenda Labels Fill";
            }

            @Override
            public String getDescription() {
                return "The color used for the major agenda labels.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.agendaLabelsVisibleProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setAgendaLabelsVisible((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isAgendaLabelsVisible();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Major Agenda Labels Visible";
            }

            @Override
            public String getDescription() {
                return "Determines if the labels for major agenda lines will be shown or not.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.agendaLinesLineWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setAgendaLinesLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getAgendaLinesLineWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Major Agenda Lines Width";
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
                return Optional.of(layer.agendaLinesSizeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setAgendaLinesSize((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getAgendaLinesSize();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Major Agenda Lines Size";
            }

            @Override
            public String getDescription() {
                return "The size of the major agenda lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.backgroundFillProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setBackgroundFill((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getBackgroundFill();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Background Fill";
            }

            @Override
            public String getDescription() {
                return "The color used for the background.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.dividerLineStrokeProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setDividerLineStroke((Color) value);
            }

            @Override
            public Object getValue() {
                return layer.getDividerLineStroke();
            }

            @Override
            public Class<?> getType() {
                return Color.class;
            }

            @Override
            public String getName() {
                return "Divider Line Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for the divider line between rows and lines.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.bluredBackgroundProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setBluredBackground((Boolean) value);
            }

            @Override
            public Object getValue() {
                return layer.isBluredBackground();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Blurred Background";
            }

            @Override
            public String getDescription() {
                return "Controls whether a blur effect will be applied on the background.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(layer.prefWidthProperty());
            }

            @Override
            public void setValue(Object value) {
                layer.setPrefWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return layer.getPrefWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Pref Width";
            }

            @Override
            public String getDescription() {
                return "The preferred width of the scale.";
            }

            @Override
            public String getCategory() {
                return "System Layer: " + layer.getName();
            }
        });

        return items;
    }
}
