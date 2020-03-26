/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.LassoEvent;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A specialized background layer for the agenda editor. The layer is used to
 * visualize copy / paste locations and a "time cursor".
 *
 * @param <R>
 */
public class AgendaEditorBackgroundLayer<R extends Row<?, ?, ?>> extends
        SystemLayer<R> {

    private boolean lassoActive;

    private AgendaEditorContext<R> context;

    private AgendaController<R> controller;

    /**
     * Constructs a new background layer.
     *
     * @param context
     *            the context for which the layer will be used
     * @param controller
     *            the controller used for editing the agenda entries (supplies
     *            list of copied activities)
     */
    public AgendaEditorBackgroundLayer(AgendaEditorContext<R> context,
                                       AgendaController<R> controller) {
        super("Agenda Editor Background", context.getGraphics());

        this.context = requireNonNull(context);
        this.controller = requireNonNull(controller);

        GraphicsBase<?> graphics = context.getGraphics();

        graphics.addEventHandler(MouseEvent.MOUSE_EXITED, evt -> redraw(evt));

        graphics.addEventHandler(MouseEvent.MOUSE_MOVED, evt -> redraw(evt));

        graphics.addEventHandler(LassoEvent.SELECTION_STARTED, evt -> {
            lassoActive = true;
            redraw();
        });

        graphics.addEventHandler(LassoEvent.SELECTION_FINISHED, evt -> {
            lassoActive = false;
            redraw();
        });

        redrawObservable(showPasteLocations);
    }

    private double mouseX;

    private double mouseY;

    private void redraw(MouseEvent evt) {
        mouseX = evt.getX();
        mouseY = evt.getY();
        super.redraw();
    }

    private final BooleanProperty showPasteLocations = new SimpleBooleanProperty(
            this, "showPasteLocations", true);

    public final BooleanProperty showPasteLocationsProperty() {
        return showPasteLocations;
    }

    public final void setShowPasteLocations(boolean b) {
        showPasteLocationsProperty().set(b);
    }

    public final boolean isShowPasteLocations() {
        return showPasteLocationsProperty().get();
    }

    @Override
    public void drawLayer(RowCanvas<R> canvas, Instant startTime,
                          Instant endTime) {

        GraphicsBase<R> graphics = getGraphics();

        Row<?, ?, ?> row = graphics.getRowAt(mouseY);

        if (row == null) {
            return;
        }

        if (!row.equals(canvas.getRow())) {
            return;
        }

        if (lassoActive) {
            return;
        }

        List<TimeInterval> pasteLocations = context.getPasteLocations(mouseX,
                mouseY, controller.getCopiedActivities());

        if (isShowPasteLocations() && pasteLocations != null) {
            for (TimeInterval interval : pasteLocations) {

                ZonedDateTime st = ZonedDateTime.ofInstant(
                        interval.getStartTime(), canvas.getRow().getZoneId());

                ZonedDateTime et = ZonedDateTime.ofInstant(
                        interval.getEndTime(), canvas.getRow().getZoneId());

                AgendaLayout layout = (AgendaLayout) canvas.getRow()
                        .getLayout();

                long days = et.toLocalDate().toEpochDay() - st.toLocalDate().toEpochDay() + 1;

                for (int day = 0; day < days; day++) {

                    double y1 = layout.getPadding()
                            + calculateVerticalTimeLocation(
                            st.toLocalTime(),
                            layout,
                            canvas.getHeight() - 2
                                    * layout.getPadding());
                    double y2 = 0;

                    if (et.toLocalDate().equals(st.toLocalDate())) {
                        y2 = layout.getPadding()
                                + calculateVerticalTimeLocation(
                                et.toLocalTime(),
                                layout,
                                canvas.getHeight() - 2
                                        * layout.getPadding());
                    } else {
                        y2 = layout.getPadding()
                                + calculateVerticalTimeLocation(
                                LocalTime.MAX,
                                layout,
                                canvas.getHeight() - 2
                                        * layout.getPadding());
                    }

                    st = st.truncatedTo(ChronoUnit.DAYS);

                    TimelineModel<?> model = canvas.getGraphics().getTimeline().getModel();
                    double x1 = model.calculateLocationForTime(Instant.from(st)) + getGraphics().getCanvasBuffer() - canvas.getTranslateX();

                    st = st.plusDays(1);
                    double x2 = model.calculateLocationForTime(Instant.from(st)) + getGraphics().getCanvasBuffer() - canvas.getTranslateX();

                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.setFill(new Color(0, 0.5, 0, .1));
                    gc.fillRect(x1 + 2, y1, x2 - x1 - 4, y2 - y1);
                }
            }
        } else if (graphics.getHoverActivity() == null) {
            drawTimeCursor(canvas, graphics);
        }
    }

    private void drawTimeCursor(RowCanvas<R> canvas, GraphicsBase<R> graphics) {
        Instant time = graphics.getTimeAt(mouseX);
        VirtualGrid<?> grid = graphics.getVirtualGrid();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(time,
                canvas.getRow().getZoneId()).truncatedTo(ChronoUnit.DAYS);
        time = zonedDateTime.toInstant();

        AgendaLayout layout = (AgendaLayout) canvas.getRow().getLayout();

        LocalTime startLocalTime = graphics.getLocalTimeAt(mouseY);
        LocalTime endLocalTime = startLocalTime.plus(layout.getMinDuration());

        if (grid != null) {
            startLocalTime = grid.adjustTime(startLocalTime, false);
            endLocalTime = grid.adjustTime(startLocalTime, true);
        }

        double y1 = layout.getPadding()
                + calculateVerticalTimeLocation(startLocalTime, layout,
                canvas.getHeight() - 2 * layout.getPadding());
        double y2 = layout.getPadding()
                + calculateVerticalTimeLocation(endLocalTime, layout,
                canvas.getHeight() - 2 * layout.getPadding());

        TimelineModel<?> model = canvas.getGraphics().getTimeline().getModel();

        double x1 = model.calculateLocationForTime(zonedDateTime.toInstant()) + getGraphics().getCanvasBuffer() - canvas.getTranslateX();
        double x2 = model.calculateLocationForTime(zonedDateTime.plusDays(1).toInstant()) + getGraphics().getCanvasBuffer() - canvas.getTranslateX();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new Color(0, 0, 0, .1));
        gc.fillRect(x1, y1, x2 - x1, y2 - y1);
    }

    private double calculateVerticalTimeLocation(LocalTime time, AgendaLayout layout, double availableHeight) {

        LocalTime st = layout.getStartTime();
        LocalTime et = layout.getEndTime();

        long millis = st.until(et, ChronoUnit.MILLIS);
        double mpp = millis / availableHeight;

        return Math.min(
                availableHeight,
                Math.max(
                        0,
                        (time.get(ChronoField.MILLI_OF_DAY) - st
                                .get(ChronoField.MILLI_OF_DAY)) / mpp));
    }
}
