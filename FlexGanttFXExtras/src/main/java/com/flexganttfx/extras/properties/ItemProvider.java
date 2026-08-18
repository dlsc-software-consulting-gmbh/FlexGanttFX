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
package com.flexganttfx.extras.properties;

import java.util.List;

import static org.controlsfx.control.PropertySheet.Item;

/**
 * Strategy interface that produces a list of ControlsFX
 * {@link org.controlsfx.control.PropertySheet.Item} instances for a target
 * object of type {@code T}.
 *
 * @param <T> the target type
 * @since 1.0
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
