/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.extras.util.Messages;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.ThemingUtil;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import org.controlsfx.control.StatusBar;

import java.text.MessageFormat;

import static java.util.Objects.requireNonNull;

/**
 * A statusbar implementation that can be used in combination with the Gantt
 * chart control. Please note that this statusbar is used for rapid prototyping
 * and does not present a feature-complete implementation that could be used for
 * any kind of application. An entire framework could be written just for that
 * purpose.
 *
 * @param <R> the type of the rows in the Gantt chart
 * @since 1.0
 */
public class GanttChartStatusBar<R extends Row<?, ?, ?>> extends StatusBar {

    private final Label gridLabel;

    /**
     * Constructs a new statusbar control. The Gantt chart has to be set later
     * by calling {@link #setGanttChart(GanttChartBase)}.
     *
     * @since 1.0
     */
    public GanttChartStatusBar() {
        getStyleClass().add("gantt-chart-status-bar");

        gridLabel = new Label();
        gridLabel.getStyleClass().add("grid-label");

        getRightItems().add(gridLabel);

        updateGridLabel();

        ganttChartProperty().addListener((observable, oldValue, newValue) -> {

            if (oldValue != null) {
                GraphicsBase<?> graphicsView = oldValue.getGraphics();
                graphicsView.hoverActivityProperty().removeListener(weakFocusedActivityListener);
                graphicsView.virtualGridProperty().removeListener(weakVirtualGridListener);

            }

            if (newValue != null) {
                GraphicsBase<?> graphicsView = newValue.getGraphics();
                graphicsView.hoverActivityProperty().addListener(weakFocusedActivityListener);
                graphicsView.virtualGridProperty().addListener(weakVirtualGridListener);
            }

            updateGridLabel();
        });

        if (ThemingUtil.isAtlantaFXActive(getScene())) {
            getStylesheets().add(requireNonNull(GanttChartStatusBar.class.getResource("statusbar-atlantafx.css")).toExternalForm());
        } else {
            getStylesheets().add(requireNonNull(GanttChartStatusBar.class.getResource("statusbar.css")).toExternalForm());
        }
    }

    /**
     * Constructs a new statusbar control.
     *
     * @param ganttChart the Gantt chart for which the statusbar will be used
     * @since 1.0
     */
    public GanttChartStatusBar(GanttChartBase<R> ganttChart) {
        this();

        setGanttChart(ganttChart);
    }

    private final InvalidationListener focusedActivityListener = observable -> {
        GraphicsBase<?> graphicsView = getGanttChart().getGraphics();
        ActivityRef<?> focusedActivity = graphicsView.getHoverActivity();
        if (focusedActivity != null) {
            Activity activity = focusedActivity.getActivity();
            setText(activity.getName());
        }
    };

    private final WeakInvalidationListener weakFocusedActivityListener = new WeakInvalidationListener(focusedActivityListener);

    private final InvalidationListener virtualGridListener = observable -> updateGridLabel();

    private final WeakInvalidationListener weakVirtualGridListener = new WeakInvalidationListener(virtualGridListener);

    private void updateGridLabel() {
        GanttChartBase<R> gc = getGanttChart();
        if (gc == null) {
            return;
        }
        GraphicsBase<?> graphicsView = gc.getGraphics();
        VirtualGrid<?> grid = graphicsView.getVirtualGrid();
        if (grid != null) {
            gridLabel.setText(MessageFormat.format(Messages.getString("GanttChartStatusBar.MESSAGE_GRID_NAME"), grid.getName()));
        } else {
            gridLabel.setText(Messages.getString("GanttChartStatusBar.MESSAGE_GRID_OFF"));
        }
    }

    private final ObjectProperty<GanttChartBase<R>> ganttChart = new SimpleObjectProperty<>(this, "ganttChart");

    /**
     * A property used to store the reference to the Gantt chart that will be
     * watched by this statusbar.
     *
     * @return the Gantt chart property
     * @since 1.0
     */
    public final ObjectProperty<GanttChartBase<R>> ganttChartProperty() {
        return ganttChart;
    }

    /**
     * Returns the value of {@link #ganttChartProperty()}.
     *
     * @return the property used for the Gantt chart reference
     * @since 1.0
     */
    public final GanttChartBase<R> getGanttChart() {
        return ganttChartProperty().get();
    }

    /**
     * Sets the value of {@link #ganttChartProperty()}.
     *
     * @param ganttChart the Gantt chart
     * @since 1.0
     */
    public final void setGanttChart(GanttChartBase<R> ganttChart) {
        requireNonNull(ganttChart);
        ganttChartProperty().set(ganttChart);
    }
}
