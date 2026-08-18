/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
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

/**
 * A ControlsFX {@link org.controlsfx.control.PropertySheet} preconfigured for
 * FlexGanttFX. Populate {@link #getTargets()} with Gantt chart controls,
 * layers, or renderers; the sheet auto-updates its items via {@link ItemFactory}.
 *
 * @param <R> the row type
 * @since 1.0
 */
public class GanttChartPropertySheet<R extends Row<?, ?, ?>> extends PropertySheet {

    /**
     * Constructs an empty property sheet.
     *
     * @since 1.0
     */
    public GanttChartPropertySheet() {
        setMode(Mode.CATEGORY);
        this.targets.addListener((Observable it) -> update());
    }

    /**
     * Constructs a new sheet for the given target object.
     *
     * @param target the initial target object
     * @since 1.0
     */
    public GanttChartPropertySheet(Object target) {
        this();
        getTargets().add(target);
    }

    private final ObservableList<Object> targets = FXCollections.observableArrayList();

    /**
     * The list of target objects for which the properties will be displayed
     * inside the property sheet view.
     *
     * @return the target list
     */
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
