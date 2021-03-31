/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties;

import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.container.DualGanttChartContainerBase;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet.Item;

import java.util.Optional;

/**
 * A property sheet item provider for {@link GanttChartBase}.
 */
public class DualGanttChartContainerBaseItemProvider<T extends GanttChartBase<?>> implements ItemProvider<DualGanttChartContainerBase<T>> {

    private static final String DUAL_GANTT_CHART_CONTAINER_PROPERTIES_CATEGORY = "Control: Dual Gantt Chart Container";

    /**
     * Returns property sheet items.
     *
     * @return property sheet items
     */
    public final ObservableList<Item> getPropertySheetItems(DualGanttChartContainerBase<T> container) {
        ObservableList<Item> items = FXCollections.observableArrayList();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(container.showSecondaryProperty());
            }

            @Override
            public void setValue(Object value) {
                container.setShowSecondary((boolean) value);
            }

            @Override
            public Object getValue() {
                return container.isShowSecondary();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Secondary";
            }

            @Override
            public String getDescription() {
                return "Show secondary chart";
            }

            @Override
            public String getCategory() {
                return DUAL_GANTT_CHART_CONTAINER_PROPERTIES_CATEGORY;
            }
        });

        return items;
    }
}
