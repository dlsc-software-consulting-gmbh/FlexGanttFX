/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.experimental;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Created by dirk on 21.06.16.
 */
public class DateSlider extends Control {

    public DateSlider() {

        getStyleClass().add("date-slider");

        MapChangeListener<Object, Object> listener = change -> {
            if (change.wasAdded()) {
                Object key = change.getKey();
                if (key.equals("flexganttfx.dateslider.date")) {
                    LocalDate value = (LocalDate) change.getValueAdded();
                    date.set(value);
                }
            }
        };

        getProperties().addListener(listener);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DateSliderSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DateSlider.class.getResource("date-slider.css").toExternalForm();
    }

    // date time formatter

    private final ObjectProperty<DateTimeFormatter> dateTimeFormatter = new SimpleObjectProperty<>(this, "dateTimeFormatter", DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));

    public final ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty() {
       return dateTimeFormatter;
    }

    public final DateTimeFormatter getDateTimeFormatter() {
       return dateTimeFormatter.get();
    }

    public final void setDateTimeFormatter(DateTimeFormatter value) {
        dateTimeFormatter.set(value);
    }

    // earliestDate

    private final ObjectProperty<LocalDate> earliestDate = new SimpleObjectProperty<>(this, "earliestDate", LocalDate.now().minusMonths(1));

    public final ObjectProperty<LocalDate> earliestDateProperty() {
        return earliestDate;
    }

    public final LocalDate getEarliestDate() {
        return earliestDate.get();
    }

    public final void setEarliestDate(LocalDate value) {
        earliestDate.set(value);
    }

    // latestDate
    private final ObjectProperty<LocalDate> latestDate = new SimpleObjectProperty<>(this, "latestDate", LocalDate.now().plusMonths(1));

    public final ObjectProperty<LocalDate> latestDateProperty() {
        return latestDate;
    }

    public final LocalDate getLatestDate() {
        return latestDate.get();
    }

    public final void setLatestDate(LocalDate value) {
        latestDate.set(value);
    }

    // date

    private final ReadOnlyObjectWrapper<LocalDate> date = new ReadOnlyObjectWrapper<>(this, "date", LocalDate.now());

    public final ReadOnlyObjectProperty<LocalDate> dateProperty() {
        return date.getReadOnlyProperty();
    }

    public final LocalDate getDate() {
        return date.get();
    }

}
