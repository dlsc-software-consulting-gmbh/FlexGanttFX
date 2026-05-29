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
package com.flexganttfx.extras.properties.timeline;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.timeline.Eventline;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventlineItemProvider implements ItemProvider<Eventline> {

    private static final String EVENTLINE_PROPERTIES_CATEGORY = "Control: Eventline";

    @Override
    public List<Item> getPropertySheetItems(Eventline target) {
        List<Item> items = new ArrayList<>();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.showTimeCursorProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setShowTimeCursor((Boolean) value);
            }

            @Override
            public Object getValue() {
                return target.isShowTimeCursor();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Time Cursor";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the display of the time cursor.";
            }

            @Override
            public String getCategory() {
                return EVENTLINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.showMarkedTimeIntervalProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setShowMarkedTimeInterval((Boolean) value);
            }

            @Override
            public Object getValue() {
                return target.isShowMarkedTimeInterval();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Marked Time Intervals";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the display of a marked time interval.";
            }

            @Override
            public String getCategory() {
                return EVENTLINE_PROPERTIES_CATEGORY;
            }
        });

        return items;
    }
}
