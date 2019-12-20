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
                return Optional.of(target.showFrozenRowProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setShowFrozenRow((Boolean) value);
            }

            @Override
            public Object getValue() {
                return target.isShowFrozenRow();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Frozen Row";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the use of a frozen row.";
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
