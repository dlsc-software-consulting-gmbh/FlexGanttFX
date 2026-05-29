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
import com.flexganttfx.view.timeline.Dateline;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SelectionMode;
import org.controlsfx.control.PropertySheet.Item;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatelineItemProvider implements ItemProvider<Dateline> {

    private static final String DATELINE_PROPERTIES_CATEGORY = "Control: Dateline";

    @Override
    public List<Item> getPropertySheetItems(Dateline target) {
        List<Item> items = new ArrayList<>();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.datelineBufferProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setDatelineBuffer((Double) value);
            }

            @Override
            public Object getValue() {
                return target.getDatelineBuffer();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Dateline Buffer";
            }

            @Override
            public String getDescription() {
                return "Increases the dateline width to reduce redraws";
            }

            @Override
            public String getCategory() {
                return DATELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.selectionModeProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setSelectionMode((SelectionMode) value);
            }

            @Override
            public Object getValue() {
                return target.getSelectionMode();
            }

            @Override
            public Class<?> getType() {
                return SelectionMode.class;
            }

            @Override
            public String getName() {
                return "Selection Mode";
            }

            @Override
            public String getDescription() {
                return "Single or multiple selections of dateline cells / time intervals.";
            }

            @Override
            public String getCategory() {
                return DATELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.zoneIdProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setZoneId((ZoneId) value);
            }

            @Override
            public Object getValue() {
                return target.getZoneId();
            }

            @Override
            public Class<?> getType() {
                return ZoneId.class;
            }

            @Override
            public String getName() {
                return "Timezone";
            }

            @Override
            public String getDescription() {
                return "The timezone that will be displayed by the dateline.";
            }

            @Override
            public String getCategory() {
                return DATELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.firstDayOfWeekProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setFirstDayOfWeek((DayOfWeek) value);
            }

            @Override
            public Object getValue() {
                return target.getFirstDayOfWeek();
            }

            @Override
            public Class<?> getType() {
                return DayOfWeek.class;
            }

            @Override
            public String getName() {
                return "First Day of Week";
            }

            @Override
            public String getDescription() {
                return "The day representing the beginning of the week.";
            }

            @Override
            public String getCategory() {
                return DATELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.zoomLassoEnabledProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setZoomLassoEnabled((Boolean) value);
            }

            @Override
            public Object getValue() {
                return target.isZoomLassoEnabled();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Support Zoom Lasso";
            }

            @Override
            public String getDescription() {
                return "If enabled the user can perform a zoom by selecting a time range with a lasso.";
            }

            @Override
            public String getCategory() {
                return DATELINE_PROPERTIES_CATEGORY;
            }
        });

        return items;
    }
}
