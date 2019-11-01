/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties;

import java.util.List;

import static org.controlsfx.control.PropertySheet.Item;

/**
 * Item providers are used to return a list of items for the property sheet view of
 * ControlsFX.
 *
 * @param <T> the type for which to return items
 */
public interface ItemProvider<T> {

    /**
     * Returns a list of property sheet items for the given target object.
     *
     * @param target the target object
     * @return a list of property sheet view items
     */
    List<Item> getPropertySheetItems(T target);
}
