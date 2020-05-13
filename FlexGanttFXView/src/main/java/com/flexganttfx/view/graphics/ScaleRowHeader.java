/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics;

import com.flexganttfx.model.Layout;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.graphics.GraphicsBase.RowHeader;
import impl.com.flexganttfx.skin.util.AgendaHelper;
import impl.com.flexganttfx.skin.util.AgendaHelper.AgendaLineLocation;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static javafx.geometry.VPos.CENTER;
import static javafx.scene.text.TextAlignment.RIGHT;

/**
 * Draws a scale for an entire row or for each line within the row. Scales vary
 * depending on the layout used for the row / line. The scale for the chart
 * layout displays the minimum and maximum values while the scale for the agenda
 * layout displays a time scale (8am, 9am, 10am, .....). The labels and dashes
 * in the scale layer have to align perfectly with the lines drawn by the agenda
 * lines layer and the chart lines layer.
 *
 * @param <R> the type of the rows
 * @since 1.0
 */
public class ScaleRowHeader<R extends Row<?, ?, ?>> extends RowHeader<R> {

    private final GraphicsBase<R> graphics;
    private final Canvas canvas;
    private final ObjectProperty<Paint> dividerLineStroke = new SimpleObjectProperty<>(this, "dividerLineStroke");
    private final InvalidationListener redrawListener = observable -> draw();
    private final BooleanProperty majorChartLabelsVisible = new SimpleBooleanProperty(this, "majorChartLabelsVisible", true);
    private final BooleanProperty minorChartLabelsVisible = new SimpleBooleanProperty(this, "minorChartLabelsVisible", true);
    private final BooleanProperty majorChartLinesVisible = new SimpleBooleanProperty(this, "majorChartLinesVisible", true);
    private final ObjectProperty<Paint> majorChartLinesStroke = new SimpleObjectProperty<>(this, "majorChartLinesStroke");
    private final DoubleProperty majorChartLinesLineWidth = new SimpleDoubleProperty(this, "majorChartLinesLineWidth");
    private final DoubleProperty majorChartLinesSize = new SimpleDoubleProperty(this, "majorChartLinesSize");
    private final BooleanProperty minorChartLinesVisible = new SimpleBooleanProperty(this, "minorChartLinesVisible", true);
    private final ObjectProperty<Paint> minorChartLinesStroke = new SimpleObjectProperty<>(this, "minorChartLinesStroke");
    private final DoubleProperty minorChartLinesLineWidth = new SimpleDoubleProperty(this, "minorChartLinesLineWidth");
    private final DoubleProperty minorChartLinesSize = new SimpleDoubleProperty(this, "minorChartLinesSize");
    private final BooleanProperty agendaLabelsVisible = new SimpleBooleanProperty(this, "agendaLabelsVisible", true);
    private final DoubleProperty agendaLinesLineWidth = new SimpleDoubleProperty(this, "agendaLinesLineWidth");
    private final DoubleProperty agendaLinesSize = new SimpleDoubleProperty(this, "agendaLinesSize");
    private final ObjectProperty<Paint> agendaLinesStroke = new SimpleObjectProperty<>(this, "agendaLinesStroke");
    private final BooleanProperty agendaLinesVisible = new SimpleBooleanProperty(this, "agendaLinesVisible", true);
    private final ObjectProperty<DateTimeFormatter> dateTimeFormatter = new SimpleObjectProperty<>(this, "dateTimeFormatter", DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
    private final ObjectProperty<NumberFormat> numberFormat = new SimpleObjectProperty<NumberFormat>(this, "numberFormat") {

        @Override
        public void set(NumberFormat newValue) {
            if (newValue == null) {
                throw new IllegalArgumentException("number format can not be null");
            }
            super.set(newValue);
        }

    };

    public ScaleRowHeader(GraphicsBase<R> graphics) {
        this.graphics = Objects.requireNonNull(graphics);
        this.canvas = new Canvas() {

            @Override
            public boolean isResizable() {
                return true;
            }

            @Override
            public double prefWidth(double height) {
                return getWidth();
            }

            @Override
            public double prefHeight(double width) {
                return getHeight();
            }
        };

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        canvas.widthProperty().addListener(it -> draw());
        canvas.heightProperty().addListener(it -> draw());

        setGraphic(canvas);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        setNumberFormat(NumberFormat.getIntegerInstance());
        setDividerLineStroke(new Color(0, 0, 0, .6));

        // major chart lines and labels
        setMajorChartLinesLineWidth(.5);
        setMajorChartLinesStroke(Color.GRAY);
        setMajorChartLinesVisible(true);
        setMajorChartLinesSize(15);
        setMajorChartLabelsVisible(true);

        // minor chart lines and labels
        setMinorChartLinesLineWidth(0.5);
        setMinorChartLinesStroke(Color.LIGHTGRAY);
        setMinorChartLinesVisible(true);
        setMinorChartLinesSize(8);
        setMinorChartLabelsVisible(false);

        // major agenda lines and labels
        setAgendaLinesLineWidth(.5);
        setAgendaLinesStroke(Color.GRAY);
        setAgendaLinesVisible(true);
        setAgendaLinesSize(15);
        setAgendaLabelsVisible(true);

        redrawObservable(dividerLineStroke);
        redrawObservable(dateTimeFormatter);
        redrawObservable(numberFormat);

        // major chart properties
        redrawObservable(majorChartLinesLineWidth);
        redrawObservable(majorChartLinesSize);
        redrawObservable(majorChartLinesStroke);
        redrawObservable(majorChartLinesVisible);
        redrawObservable(majorChartLabelsVisible);

        // minor chart properties
        redrawObservable(minorChartLinesVisible);
        redrawObservable(minorChartLinesSize);
        redrawObservable(minorChartLinesLineWidth);
        redrawObservable(minorChartLinesStroke);
        redrawObservable(minorChartLabelsVisible);

        // major agenda properties
        redrawObservable(agendaLinesLineWidth);
        redrawObservable(agendaLinesSize);
        redrawObservable(agendaLinesStroke);
        redrawObservable(agendaLinesVisible);
        redrawObservable(agendaLabelsVisible);

        itemProperty().addListener(it -> draw());
    }

    /**
     * Registers the given observable as something that requires
     * a redraw of the graphics area. E.g.: the stroke color has
     * changed.
     *
     * @param observable the observable to monitor for changes
     */
    protected void redrawObservable(Observable observable) {
        requireNonNull(observable);
        observable.addListener(redrawListener);
    }

    private void draw() {
        double canvasHeight = canvas.getHeight();

        if (getWidth() > 0) {

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFont(getFont());
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            Row<?, ?, ?> row = getItem();
            if (row != null) {
                double xOffset = 0;

                xOffset = drawLayoutSpecificHeader(xOffset, 0, canvasHeight, row.getLayout());

                for (int lineIndex = 0; lineIndex < row.getLineCount(); lineIndex++) {
                    Layout lineLayout = row.getLineLayout(lineIndex);
                    double lineOffset = row.getLineLocation(lineIndex);
                    double lineHeight = row.getLineHeight(lineIndex);
                    drawLayoutSpecificHeader(xOffset, lineOffset, lineHeight, lineLayout);

                    if (lineIndex < row.getLineCount() - 1) {
                        gc.setLineWidth(.5);
                        gc.setStroke(getDividerLineStroke());
                        final double y = snapPosition(((int) lineOffset + lineHeight) - .5);
                        gc.strokeLine(getInsets().getLeft(), y, getWidth() - getInsets().getRight() - 1, y);
                    }
                }
            }
        }
    }

    private double drawLayoutSpecificHeader(double xOffset, double yOffset, double height, Layout layout) {

        yOffset += layout.getPadding();
        height -= (layout.getPadding() * 2);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        try {
            if (graphics.isSafeRendering()) {
                gc.save();
            }

            if (layout instanceof GanttLayout) {
                return drawLayoutHeaderGantt();
            } else if (layout instanceof AgendaLayout) {
                return drawLayoutHeaderAgenda(yOffset, height, (AgendaLayout) layout);
            } else if (layout instanceof ChartLayout) {
                return drawLayoutHeaderChart(yOffset, height, (ChartLayout) layout);
            }
        } finally {
            if (graphics.isSafeRendering()) {
                gc.restore();
            }
        }

        return xOffset;
    }

    private double drawLayoutHeaderAgenda(double yOffset, double height, AgendaLayout layout) {

        GraphicsContext gc = canvas.getGraphicsContext2D();

        DateTimeFormatter formatter = getDateTimeFormatter();

        double width = getWidth() - getInsets().getLeft() - getInsets().getRight();

        List<AgendaLineLocation> lines = AgendaHelper.getLineLocations(layout, yOffset, height);

        lines.stream().filter(loc -> !loc.isMinor()).forEach(loc -> {
            if (isAgendaLinesVisible()) {
                gc.setLineWidth(getAgendaLinesLineWidth());
                gc.setStroke(getAgendaLinesStroke());
                gc.strokeLine(width - getAgendaLinesSize(), loc.getLocation(), width, loc.getLocation());
            }

            if (isAgendaLabelsVisible()) {

                LocalTime time = loc.getTime();
                if (time.equals(LocalTime.MAX)) {
                    time = LocalTime.MIDNIGHT; // will look prettier than 23:59
                }

                gc.setFill(getTextFill());
                gc.setTextAlign(RIGHT);
                gc.setTextBaseline(CENTER);
                gc.fillText(formatter.format(time), width - getAgendaLinesSize() - 3, loc.getLocation());
            }
        });

        return getWidth();
    }

    private double drawLayoutHeaderGantt() {
        return getWidth();
    }

    private double drawLayoutHeaderChart(double yOffset, double height, ChartLayout layout) {

        GraphicsContext gc = canvas.getGraphicsContext2D();

        double width = getWidth() - getInsets().getLeft() - getInsets().getRight();

        // minor lines

        if (isMinorChartLinesVisible()) {

            gc.setStroke(getMinorChartLinesStroke());
            gc.setLineWidth(getMinorChartLinesLineWidth());

            for (double value : layout.getMinorTicks()) {
                double y = getChartValueLocation(value, yOffset, height, layout);
                gc.strokeLine(width - getMinorChartLinesSize(), y, width, y);
            }
        }

        // minor labels

        if (isMinorChartLabelsVisible()) {

            NumberFormat format = getNumberFormat();

            for (double value : layout.getMinorTicks()) {
                double y = getChartValueLocation(value, yOffset, height, layout);
                gc.setTextAlign(RIGHT);
                gc.setTextBaseline(CENTER);
                gc.setFill(getTextFill());
                gc.fillText(format.format(value), width - getMinorChartLinesSize() - 3, y);
            }

        }

        // major lines

        if (isMajorChartLinesVisible()) {

            gc.setStroke(getMajorChartLinesStroke());
            gc.setLineWidth(getMajorChartLinesLineWidth());

            for (double value : layout.getMajorTicks()) {
                double y = getChartValueLocation(value, yOffset, height, layout);
                gc.strokeLine(width - getMajorChartLinesSize(), y, width, y);
            }
        }

        // major labels

        if (isMajorChartLabelsVisible()) {

            NumberFormat format = getNumberFormat();

            for (double value : layout.getMajorTicks()) {
                double y = getChartValueLocation(value, yOffset, height, layout);
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.setTextBaseline(VPos.CENTER);
                gc.setFill(getTextFill());
                gc.fillText(format.format(value), width - getMajorChartLinesSize() - 3, y);
            }

        }

        return getWidth();
    }

    private double getChartValueLocation(double value, double yOffset, double height, ChartLayout layout) {
        double minChart = layout.getMinValue();
        double maxChart = layout.getMaxValue();

        double range = maxChart - minChart;
        double ppv = height / range;

        double zeroLineLocation = yOffset + layout.getMaxValue() * ppv;

        return ((int) (zeroLineLocation - value * ppv)) + .5;
    }

    public final ObjectProperty<Paint> dividerLineStrokeProperty() {
        return dividerLineStroke;
    }

    public final Paint getDividerLineStroke() {
        return dividerLineStrokeProperty().get();
    }

    public final void setDividerLineStroke(Paint stroke) {
        requireNonNull(stroke);
        dividerLineStrokeProperty().set(stroke);
    }

    public final BooleanProperty majorChartLabelsVisibleProperty() {
        return majorChartLabelsVisible;
    }

    public final boolean isMajorChartLabelsVisible() {
        return majorChartLabelsVisibleProperty().get();
    }

    public final void setMajorChartLabelsVisible(boolean visible) {
        majorChartLabelsVisibleProperty().set(visible);
    }

    public final BooleanProperty minorChartLabelsVisibleProperty() {
        return minorChartLabelsVisible;
    }

    public final boolean isMinorChartLabelsVisible() {
        return minorChartLabelsVisibleProperty().get();
    }

    public final void setMinorChartLabelsVisible(boolean visible) {
        minorChartLabelsVisibleProperty().set(visible);
    }

    public final BooleanProperty majorChartLinesVisibleProperty() {
        return majorChartLinesVisible;
    }

    public final boolean isMajorChartLinesVisible() {
        return majorChartLinesVisibleProperty().get();
    }

    public final void setMajorChartLinesVisible(boolean visible) {
        majorChartLinesVisibleProperty().set(visible);
    }

    public final ObjectProperty<Paint> majorChartLinesStrokeProperty() {
        return majorChartLinesStroke;
    }

    public final Paint getMajorChartLinesStroke() {
        return majorChartLinesStrokeProperty().get();
    }

    public final void setMajorChartLinesStroke(Paint stroke) {
        requireNonNull(stroke);
        majorChartLinesStrokeProperty().set(stroke);
    }

    public final DoubleProperty majorChartLinesLineWidthProperty() {
        return majorChartLinesLineWidth;
    }

    public final double getMajorChartLinesLineWidth() {
        return majorChartLinesLineWidthProperty().get();
    }

    public final void setMajorChartLinesLineWidth(double lineWidth) {
        majorChartLinesLineWidthProperty().set(lineWidth);
    }

    public final DoubleProperty majorChartLinesSizeProperty() {
        return majorChartLinesSize;
    }

    public final double getMajorChartLinesSize() {
        return majorChartLinesSizeProperty().get();
    }

    public final void setMajorChartLinesSize(double size) {
        majorChartLinesSizeProperty().set(size);
    }

    public final BooleanProperty minorChartLinesVisibleProperty() {
        return minorChartLinesVisible;
    }

    public final boolean isMinorChartLinesVisible() {
        return minorChartLinesVisibleProperty().get();
    }

    public final void setMinorChartLinesVisible(boolean visible) {
        minorChartLinesVisibleProperty().set(visible);
    }

    public final ObjectProperty<Paint> minorChartLinesStrokeProperty() {
        return minorChartLinesStroke;
    }

    // Agenda settings.

    public final Paint getMinorChartLinesStroke() {
        return minorChartLinesStrokeProperty().get();
    }

    public final void setMinorChartLinesStroke(Paint stroke) {
        requireNonNull(stroke);
        minorChartLinesStrokeProperty().set(stroke);
    }

    public final DoubleProperty minorChartLinesLineWidthProperty() {
        return minorChartLinesLineWidth;
    }

    public final double getMinorChartLinesLineWidth() {
        return minorChartLinesLineWidthProperty().get();
    }

    public final void setMinorChartLinesLineWidth(double lineWidth) {
        minorChartLinesLineWidthProperty().set(lineWidth);
    }

    public final DoubleProperty minorChartLinesSizeProperty() {
        return minorChartLinesSize;
    }

    public final double getMinorChartLinesSize() {
        return minorChartLinesSizeProperty().get();
    }

    public final void setMinorChartLinesSize(double size) {
        minorChartLinesSizeProperty().set(size);
    }

    public final BooleanProperty agendaLabelsVisibleProperty() {
        return agendaLabelsVisible;
    }

    public final boolean isAgendaLabelsVisible() {
        return agendaLabelsVisibleProperty().get();
    }

    public final void setAgendaLabelsVisible(boolean visible) {
        agendaLabelsVisibleProperty().set(visible);
    }

    public final DoubleProperty agendaLinesLineWidthProperty() {
        return agendaLinesLineWidth;
    }

    public final double getAgendaLinesLineWidth() {
        return agendaLinesLineWidthProperty().get();
    }

    public final void setAgendaLinesLineWidth(double lineWidth) {
        agendaLinesLineWidthProperty().set(lineWidth);
    }

    public final DoubleProperty agendaLinesSizeProperty() {
        return agendaLinesSize;
    }

    public final double getAgendaLinesSize() {
        return agendaLinesSizeProperty().get();
    }

    public final void setAgendaLinesSize(double size) {
        agendaLinesSizeProperty().set(size);
    }

    public final ObjectProperty<Paint> agendaLinesStrokeProperty() {
        return agendaLinesStroke;
    }

    public final Paint getAgendaLinesStroke() {
        return agendaLinesStrokeProperty().get();
    }

    public final void setAgendaLinesStroke(Paint stroke) {
        requireNonNull(stroke);
        agendaLinesStrokeProperty().set(stroke);
    }

    public final BooleanProperty agendaLinesVisibleProperty() {
        return agendaLinesVisible;
    }

    // Date formatter support.

    public final boolean isAgendaLinesVisible() {
        return agendaLinesVisibleProperty().get();
    }

    public final void setAgendaLinesVisible(boolean visible) {
        agendaLinesVisibleProperty().set(visible);
    }

    public final ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty() {
        return dateTimeFormatter;
    }

    public final DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter.get();
    }

    // Number format support.

    public final void setDateTimeFormatter(DateTimeFormatter formatter) {
        dateTimeFormatterProperty().set(formatter);
    }

    /**
     * A property used to store a number format that will be used to format
     * the labels shown when a chart layout is being used.
     *
     * @return the number format
     * @since 1.4
     */
    public final ObjectProperty<NumberFormat> numberFormatProperty() {
        return numberFormat;
    }

    /**
     * Returns the value of {@link #numberFormatProperty()}.
     *
     * @return the number format for chart values
     */
    public final NumberFormat getNumberFormat() {
        return numberFormatProperty().get();
    }

    /**
     * Returns the value of {@link #getNumberFormat()}.
     *
     * @param format the number format to use for chart values
     */
    public final void setNumberFormat(NumberFormat format) {
        numberFormatProperty().set(format);
    }
}
