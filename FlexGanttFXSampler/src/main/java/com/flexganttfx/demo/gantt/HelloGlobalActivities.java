/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.extras.properties.view.GanttChartConfigurationView;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.calendar.CalendarBase;
import com.flexganttfx.model.calendar.MutableCalendarActivityBase;
import com.flexganttfx.model.dateline.ChronoUnitGrid;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.repository.RepositoryEvent;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.LassoEvent;
import com.flexganttfx.view.graphics.layer.CalendarLayer;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.graphics.renderer.CalendarActivityRenderer;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.Position;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class HelloGlobalActivities extends FlexGanttFXSample {

    private GanttChart<HelloRow> gc;
    private final EventlineCalendar calendar = new EventlineCalendar();
    private final Layer layer = new Layer("Default Layer");
    private final PhaseRow frozenRow = new PhaseRow();
    private final ChronoUnitGrid dayGrid = new ChronoUnitGrid("Day Grid", ChronoUnit.DAYS, 1);

    @Override
    public void dispose() {
        super.dispose();
        gc = null;
    }

    @Override
    protected GanttChart<?> createGanttChart() {
        gc = new GanttChart<>();
        gc.setRoot(new HelloRow("root"));

        Timeline timeline = gc.getTimeline();

        Eventline eventline = timeline.getEventline();
        eventline.setFrozenRow(frozenRow);

        gc.getGraphics().getCalendars().add(calendar);

        eventline.getGraphics().getLayers().add(layer);
        eventline.getGraphics().setVirtualGrid(dayGrid);
        eventline.getGraphics().setOnLassoSelectionFinished(evt -> {
            LassoEvent.LassoInfo info = evt.getInfo();
            Instant st = info.getStartTime();
            Instant et = info.getEndTime();
            TextInputDialog  dialog = new TextInputDialog("New Phase");
            dialog.setTitle("Phase Name");
            dialog.setHeaderText("Phase Name");
            dialog.setContentText("Enter a name for the new phase.");
            final Optional<String> nameOptional = dialog.showAndWait();
            if (nameOptional.isPresent()) {
                addPhase(nameOptional.get(), st, et);
            }
        });

        final CalendarLayer calendarLayer = gc.getGraphics().getSystemLayer(CalendarLayer.class);
        calendarLayer.setCalendarActivityRenderer(Phase.class, new PhaseCalendarActivityRenderer(gc.getGraphics()));

        eventline.getGraphics().setActivityRenderer(Phase.class, GanttLayout.class, new PhaseActivityRenderer(eventline.getGraphics()));

        addPhase("Design", Instant.now().plus(1, ChronoUnit.DAYS), Instant.now().plus(5, ChronoUnit.DAYS));
        addPhase("Implementation", Instant.now().plus(8, ChronoUnit.DAYS), Instant.now().plus(16, ChronoUnit.DAYS));
        addPhase("Testing", Instant.now().plus(19, ChronoUnit.DAYS), Instant.now().plus(25, ChronoUnit.DAYS));

        GanttChartConfigurationView view = (GanttChartConfigurationView) getControlPanel();
        view.update(); // show the newly registered renderers

        return gc;
    }

    @Override
    public Node getControlPanel() {
        return new GanttChartConfigurationView(gc);
    }

    private void addPhase(String title, Instant st, Instant et) {
        st = dayGrid.adjustTime(st, ZoneId.systemDefault(), true, DayOfWeek.MONDAY);
        et = dayGrid.adjustTime(et, ZoneId.systemDefault(), true, DayOfWeek.MONDAY);

        Phase phase = new Phase(title);
        phase.setStartTime(st);
        phase.setEndTime(et);
        frozenRow.addActivity(layer, phase);
        calendar.addPhase(phase);
    }

    private final ObjectProperty<Paint> phaseColor = new SimpleObjectProperty<>(Color.ORANGE);

    private final ObjectProperty<Paint> phaseTextColor = new SimpleObjectProperty<>(Color.WHITE);

    class PhaseActivityRenderer extends ActivityRenderer {

        public PhaseActivityRenderer(GraphicsBase graphics) {
            super(graphics, "Phase Activity Renderer");
            fillProperty().bindBidirectional(phaseColor);
            setStroke(Color.TRANSPARENT);
        }

        @Override
        protected ActivityBounds drawActivity(ActivityRef activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {
            ActivityBounds bounds = super.drawActivity(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);

            Phase phase = (Phase) activityRef.getActivity();
            String name = phase.getName();

            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.BOTTOM);

            gc.setFill(phaseTextColor.get());
            gc.fillText(name, x + 2, h);

            return bounds;
        }
    }

    class PhaseCalendarActivityRenderer extends CalendarActivityRenderer {

        public PhaseCalendarActivityRenderer(GraphicsBase graphics) {
            super(graphics, "Phase Calendar Renderer");
            strokeProperty().bindBidirectional(phaseColor);
        }

        @Override
        protected ActivityBounds drawActivity(ActivityRef activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {
            gc.setStroke(getStroke());

            gc.setLineWidth(2);
            gc.strokeLine(x, 0, x, h);

            return null;
        }
    }


    class EventlineCalendar extends CalendarBase<Phase> {

        private final List<Phase> phases = new ArrayList<>();

        protected EventlineCalendar() {
            super("Eventline Calendar");
        }

        @Override
        public Iterator<Phase> getActivities(Layer layer, Instant startTime, Instant endTime, TemporalUnit temporalUnit, ZoneId zoneId) {
            return phases.iterator();
        }

        public void addPhase(Phase phase) {
            phases.add(phase);
            fireEvent(new RepositoryEvent(this));
        }
    }

    class PhaseRow extends Row<PhaseRow, PhaseRow, Phase> {

    }

    class Phase extends MutableCalendarActivityBase<String> {


        public Phase(String name) {
            super(name);
        }
    }

    @Override
    public String getSampleName() {
        return "Global Activities";
    }

    @Override
    public String getSampleDescription() {
        return "This demo shows how the graphics node inside the eventline can be used to display global events. To add your own event, simply use the lasso inside the eventline (press and wait or SHIFT and drag).";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
