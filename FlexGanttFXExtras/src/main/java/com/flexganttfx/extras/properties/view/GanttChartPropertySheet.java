/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
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
 * A property sheet implementation for use with the property sheet view of
 * ControlsFX.
 *
 * @param <R> the row type
 */
public class GanttChartPropertySheet<R extends Row<?, ?, ?>> extends PropertySheet {

    /**
     * Constructs a new sheet.
     */
    public GanttChartPropertySheet() {
        setMode(Mode.CATEGORY);
        this.targets.addListener((Observable it) -> update());
    }

    /**
     * Constructs a new sheet for the given target object.
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
