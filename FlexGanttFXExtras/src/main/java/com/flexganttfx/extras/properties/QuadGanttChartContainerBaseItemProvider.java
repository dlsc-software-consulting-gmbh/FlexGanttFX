/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties;

import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.container.QuadGanttChartContainerBase;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet.Item;

import java.util.Optional;

/**
 * A property sheet item provider for {@link GanttChartBase}.
 */
public class QuadGanttChartContainerBaseItemProvider<T extends GanttChartBase<?>> implements ItemProvider<QuadGanttChartContainerBase<T>> {

    private static final String QUAD_GANTT_CHART_CONTAINER_PROPERTIES_CATEGORY = "Control: Quad Gantt Chart Container";

    /**
     * Returns property sheet items.
     *
     * @return property sheet items
     */
    public final ObservableList<Item> getPropertySheetItems(QuadGanttChartContainerBase<T> container) {
        ObservableList<Item> items = FXCollections.observableArrayList();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(container.showLowerProperty());
            }

            @Override
            public void setValue(Object value) {
                container.setShowLower((boolean) value);
            }

            @Override
            public Object getValue() {
                return container.isShowLower();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Lower";
            }

            @Override
            public String getDescription() {
                return "Show lower charts";
            }

            @Override
            public String getCategory() {
                return QUAD_GANTT_CHART_CONTAINER_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(container.animatedProperty());
            }

            @Override
            public void setValue(Object value) {
                container.setAnimated((boolean) value);
            }

            @Override
            public Object getValue() {
                return container.isAnimated();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Animated";
            }

            @Override
            public String getDescription() {
                return "Open / close animations";
            }

            @Override
            public String getCategory() {
                return QUAD_GANTT_CHART_CONTAINER_PROPERTIES_CATEGORY;
            }
        });

        return items;
    }
}
