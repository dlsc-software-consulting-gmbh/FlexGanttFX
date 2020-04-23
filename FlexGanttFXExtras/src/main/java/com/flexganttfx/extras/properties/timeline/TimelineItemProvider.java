/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.properties.timeline;

import com.flexganttfx.extras.properties.ItemProvider;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.timeline.Timeline.ZoomMode;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;
import org.controlsfx.control.PropertySheet.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TimelineItemProvider implements ItemProvider<Timeline> {

    private static final String TIMELINE_PROPERTIES_CATEGORY = "Control: Timeline";

    @Override
    public List<Item> getPropertySheetItems(Timeline target) {
        List<Item> items = new ArrayList<>();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.zoomAnimatedProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setZoomAnimated((Boolean) value);
            }

            @Override
            public Object getValue() {
                return target.isZoomAnimated();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Animated Zoom";
            }

            @Override
            public String getDescription() {
                return "Zoom operations can be performed with or without animation.";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.zoomFactorProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setZoomFactor((Double) value);
            }

            @Override
            public Object getValue() {
                return target.getZoomFactor();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Zoom Factor";
            }

            @Override
            public String getDescription() {
                return "The factor used for zooming in or out, default = .5";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.zoomModeProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setZoomMode((ZoomMode) value);
            }

            @Override
            public Object getValue() {
                return target.getZoomMode();
            }

            @Override
            public Class<?> getType() {
                return ZoomMode.class;
            }

            @Override
            public String getName() {
                return "Zoom Mode";
            }

            @Override
            public String getDescription() {
                return "The method of zooming in (keep start, keep end, keep center time).";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.zoomDurationProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setZoomDuration((Duration) value);
            }

            @Override
            public Object getValue() {
                return target.getZoomDuration();
            }

            @Override
            public Class<?> getType() {
                return Duration.class;
            }

            @Override
            public String getName() {
                return "Zoom Duration";
            }

            @Override
            public String getDescription() {
                return "The duration of the zoom animation.";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.moveAnimatedProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setMoveAnimated((Boolean) value);
            }

            @Override
            public Object getValue() {
                return target.isMoveAnimated();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Animated Move";
            }

            @Override
            public String getDescription() {
                return "Move operations can be performed with or without animation.";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(target.moveDurationProperty());
            }

            @Override
            public void setValue(Object value) {
                target.setMoveDuration((Duration) value);
            }

            @Override
            public Object getValue() {
                return target.getMoveDuration();
            }

            @Override
            public Class<?> getType() {
                return Duration.class;
            }

            @Override
            public String getName() {
                return "Move Duration";
            }

            @Override
            public String getDescription() {
                return "The duration of the move animation.";
            }

            @Override
            public String getCategory() {
                return TIMELINE_PROPERTIES_CATEGORY;
            }
        });

        return items;
    }
}
