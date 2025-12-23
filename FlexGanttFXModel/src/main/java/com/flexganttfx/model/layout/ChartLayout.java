/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.layout;

import com.flexganttfx.model.Layout;
import com.flexganttfx.model.LinesManager;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.ChartActivity;
import com.flexganttfx.model.activity.HighLowChartActivity;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Using the chart layout class results in activities being laid out as chart
 * bars. A series of such bars can for example be used to form a capacity
 * profile. Activities of type {@link ChartActivity} will be placed on a
 * zeroline between {@link #getMinValue()} and {@link #getMaxValue()}. The
 * height of the activity will be based on the value returned by
 * {@link ChartActivity#getChartValue()}. Activities of type
 * {@link HighLowChartActivity} will appear as floating bars. The layout also
 * supports the definition of minor and major chart lines drawn in the row
 * background.
 *
 * <img src="doc-files/layout-capacity.png" alt="Capacity Layout">
 *
 * @see ChartActivity
 * @see HighLowChartActivity
 *
 * @see Row#setLayout(Layout)
 * @see Row#getLineLayout(int)
 * @see LinesManager#getLineLayout(int)
 *
 * @since 1.0
 */
public class ChartLayout extends Layout {
    // TODO: add HighLow screenshot to javadoc

    /**
     * Constructs a new chart layout with a range of 0 to 100.
     *
     * @since 1.0
     */
    public ChartLayout() {
        setPadding(10);
    }

    // Max value support.

    private DoubleProperty maxValue;

    /**
     * Returns the property used to store the maximum value that will be used
     * for the scale and the layout of the row.
     *
     * @return the maximum value displayed by the row / line.
     * @since 1.0
     */
    public final DoubleProperty maxValueProperty() {
        if (maxValue == null) {
            maxValue = new SimpleDoubleProperty(this, "maxValue", 100);
        }

        return maxValue;
    }

    /**
     * Returns the value of the {@link #maxValueProperty()}.
     *
     * @return the maximum value
     * @since 1.0
     */
    public final double getMaxValue() {
        return maxValue == null ? 100 : maxValue.get();
    }

    /**
     * Sets the value of the {@link #maxValueProperty()}.
     *
     * @param value
     *            the new maximum value
     * @since 1.0
     */
    public final void setMaxValue(double value) {
        if (maxValue == null && value == 0) {
            return;
        }

        maxValueProperty().set(value);
    }

    // Min value support.

    private DoubleProperty minValue;

    /**
     * Returns the property used to store the minimum value that will be used
     * for the scale and the layout of the row.
     *
     * @return the minimum value displayed by the row / line.
     * @since 1.0
     */
    public final DoubleProperty minValueProperty() {
        if (minValue == null) {
            minValue = new SimpleDoubleProperty(this, "minValue", 0);
        }

        return minValue;
    }

    /**
     * Returns the value of {@link #minValueProperty()}.
     *
     * @return the minimum value
     * @since 1.0
     */
    public final double getMinValue() {
        return minValue == null ? 0 : minValue.get();
    }

    /**
     * Sets the value of {@link #minValueProperty()}.
     *
     * @param value
     *            the new minimum value
     * @since 1.0
     */
    public final void setMinValue(double value) {
        if (minValue == null && value == 0) {
            return;
        }

        minValueProperty().set(value);
    }

    // Chart lines support.

    private ObservableList<Double> majorTicks;

    /**
     * Returns the major ticks to be displayed in the row background and by the
     * row scale.
     * <p>
     *     <img src="doc-files/scale-capacity.png" alt="Chart Scale">
     * </p>
     *
     * @return a list of major tick values
     * @since 1.0
     */
    public final ObservableList<Double> getMajorTicks() {
        if (majorTicks == null) {
            majorTicks = FXCollections.observableArrayList();
        }

        return majorTicks;
    }

    private ObservableList<Double> minorTicks;

    /**
     * Returns the minor ticks to be displayed in the row background and by the
     * row scale.
     * <p>
     * <img src="doc-files/scale-capacity.png" alt="Chart Scale">
     * </p>
     *
     * @return a list of minor tick values
     * @since 1.0
     */
    public final ObservableList<Double> getMinorTicks() {
        if (minorTicks == null) {
            minorTicks = FXCollections.observableArrayList();
        }

        return minorTicks;
    }

    @Override
    public boolean isSupportingHorizontalCursorLine() {
        return true;
    }


    @Override
    public String toString() {
        return "ChartLayout [maxValue=" + getMaxValue() + ", minValue=" + getMinValue()
                + ", majorTicks=" + majorTicks + ", minorTicks=" + minorTicks
                + "]";
    }
}
