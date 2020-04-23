/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.GanttChart;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet.Item;

import java.util.List;
import java.util.Optional;

/**
 * A property sheet item provider for {@link GanttChart}.
 *
 * @param <R> the row type
 */
public class GanttChartItemProvider<R extends Row<?, ?, ?>> implements ItemProvider<GanttChart<R>> {

    private static final String GANTT_CHART_PROPERTIES_CATEGORY = "Control: Gantt Chart";

    @Override
    public List<Item> getPropertySheetItems(GanttChart<R> gc) {

        GanttChartBaseItemProvider<R> baseItems = new GanttChartBaseItemProvider<>();
        List<Item> items = baseItems.getPropertySheetItems(gc);

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(gc.displayModeProperty());
            }

            @Override
            public void setValue(Object value) {
                gc.setDisplayMode((GanttChart.DisplayMode) value);
            }

            @Override
            public Object getValue() {
                return gc.getDisplayMode();
            }

            @Override
            public Class<?> getType() {
                return GanttChart.DisplayMode.class;
            }

            @Override
            public String getName() {
                return "Display Mode";
            }

            @Override
            public String getDescription() {
                return "Standard Gantt, Table Only, Graphics Only";
            }

            @Override
            public String getCategory() {
                return GANTT_CHART_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(gc.showTreeTableProperty());
            }

            @Override
            public void setValue(Object value) {
                gc.setShowTreeTable((Boolean) value);
            }

            @Override
            public Object getValue() {
                return gc.isShowTreeTable();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Tree Table";
            }

            @Override
            public String getDescription() {
                return "Enables / disables display of the tree table view";
            }

            @Override
            public String getCategory() {
                return GANTT_CHART_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(gc.rowHeaderTypeProperty());
            }

            @Override
            public void setValue(Object value) {
                gc.setRowHeaderType((GanttChart.RowHeaderType) value);
            }

            @Override
            public Object getValue() {
                return gc.getRowHeaderType();
            }

            @Override
            public Class<?> getType() {
                return GanttChart.RowHeaderType.class;
            }

            @Override
            public String getName() {
                return "Row Header Mode";
            }

            @Override
            public String getDescription() {
                return "The type of content shown by the row header.";
            }

            @Override
            public String getCategory() {
                return GANTT_CHART_PROPERTIES_CATEGORY;
            }
        });

        return items;
    }
}
