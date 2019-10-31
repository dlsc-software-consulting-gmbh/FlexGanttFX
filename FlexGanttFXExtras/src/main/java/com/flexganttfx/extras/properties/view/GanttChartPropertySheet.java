/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.view;

import com.flexganttfx.extras.properties.ItemFactory;
import com.flexganttfx.model.Row;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;

import java.util.ArrayList;
import java.util.List;

public class GanttChartPropertySheet<R extends Row<?, ?, ?>> extends PropertySheet {

    public GanttChartPropertySheet() {
        setMode(Mode.CATEGORY);
        this.targets.addListener((Observable it) -> update());
    }

    public GanttChartPropertySheet(Object target) {
        this();
        getTargets().add(target);
    }

    private final ObservableList<Object> targets = FXCollections.observableArrayList();

    public final ObservableList<Object> getTargets() {
        return targets;
    }

    private void update() {
        ItemFactory itemFactory = new ItemFactory();
        List<Item> targetItems = new ArrayList<>();

        for (Object target : targets) {
            targetItems.addAll(itemFactory.getItems(target));
        }

        getItems().setAll(targetItems);
    }

}
