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
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.DemoActivity;
import com.flexganttfx.demo.DemoBase;
import com.flexganttfx.demo.DemoRow;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.calendar.CalendarBase;
import com.flexganttfx.model.calendar.MutableCalendarActivityBase;
import com.flexganttfx.model.dateline.ChronoUnitGrid;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.repository.RepositoryEvent;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.VBoxGraphics;
import com.flexganttfx.view.graphics.layer.CalendarLayer;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.graphics.renderer.CalendarActivityRenderer;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.Position;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CanvasBufferDemo extends DemoBase {

    private final CanvasBufferDemo.EventlineCalendar calendar = new CanvasBufferDemo.EventlineCalendar();
    private final Layer layer = new Layer("Default Layer");
    private final PhaseRow frozenRow = new PhaseRow();
    private final ChronoUnitGrid dayGrid = new ChronoUnitGrid("Day Grid", ChronoUnit.DAYS, 1);
    private VBoxGraphics<DemoRow> vboxGraphics;
    private Timeline timeline;

    @Override
    public void dispose() {
        super.dispose();
        vboxGraphics = null;
        timeline = null;
        System.setProperty("timeline.no.clip", "false");
        System.setProperty("rowpane.no.clip", "false");
    }

    @Override
    public Node getPanel(Stage stage) {
        System.setProperty("timeline.no.clip", "true");
        System.setProperty("rowpane.no.clip", "true");

        timeline = new Timeline();
        vboxGraphics = new VBoxGraphics<>();

        timeline.getDateline().setDatelineBuffer(200);

        Eventline eventline = timeline.getEventline();
        eventline.setFrozenRow(frozenRow);
        eventline.getGraphics().getLayers().add(layer);
        eventline.getGraphics().setVirtualGrid(dayGrid);

        vboxGraphics.setTimeline(timeline);
        vboxGraphics.setActivityRenderer(DemoActivity.class, GanttLayout.class, new ActivityBarRenderer<>(vboxGraphics, "DemoActivityRenderer"));
        vboxGraphics.getLayers().add(DemoRow.layer);
        vboxGraphics.getCalendars().add(calendar);

        List<DemoRow> rows = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            DemoRow row = new DemoRow("Row " + i, 10);
            rows.add(row);
        }

        vboxGraphics.getRows().setAll(rows);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(timeline);
        borderPane.setCenter(vboxGraphics);
        borderPane.setStyle("-fx-background-color: gray;");

        StackPane stackPane = new StackPane(borderPane);
        stackPane.setStyle("-fx-padding: 250px; -fx-background-color: gray;");

        final CalendarLayer calendarLayer = vboxGraphics.getSystemLayer(CalendarLayer.class);
        calendarLayer.setCalendarActivityRenderer(Phase.class, new PhaseCalendarActivityRenderer(vboxGraphics));

        timeline.getEventline().getGraphics().setActivityRenderer(Phase.class, GanttLayout.class, new PhaseActivityRenderer(timeline.getEventline().getGraphics()));

        addPhase("Design", Instant.now().plus(1, ChronoUnit.DAYS), Instant.now().plus(5, ChronoUnit.DAYS));
        addPhase("Implementation", Instant.now().plus(8, ChronoUnit.DAYS), Instant.now().plus(16, ChronoUnit.DAYS));
        addPhase("Testing", Instant.now().plus(19, ChronoUnit.DAYS), Instant.now().plus(25, ChronoUnit.DAYS));

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(stackPane.widthProperty());
        clip.heightProperty().bind(stackPane.heightProperty());
        stackPane.setClip(clip);

        return stackPane;
    }

    @Override
    public Node getControlPanel() {
        CheckBox debugMode = new CheckBox("Debug Mode");
        debugMode.selectedProperty().addListener(it -> {
            vboxGraphics.setDebugMode(debugMode.isSelected());
            timeline.getEventline().getGraphics().setDebugMode(debugMode.isSelected());
            if (debugMode.isSelected()) {
                vboxGraphics.setStyle("-fx-border-color: red; -fx-border-width: 3px;");
            } else {
                vboxGraphics.setStyle("");
            }
        });

        CheckBox showScale = new CheckBox("Show Row Headers");
        showScale.selectedProperty().bindBidirectional(vboxGraphics.showRowHeadersProperty());

        // canvas buffer
        Label canvasBufferSizeLabel = new Label("Canvas Buffer:");
        Slider canvasBufferSlider = new Slider(0, 250, vboxGraphics.getCanvasBuffer());
        canvasBufferSlider.valueProperty().bindBidirectional(vboxGraphics.canvasBufferProperty());

        Label canvasBufferValueLabel = new Label();
        canvasBufferValueLabel.setMinWidth(Region.USE_PREF_SIZE);
        canvasBufferValueLabel.textProperty().bind(Bindings.createStringBinding(() -> DecimalFormat.getIntegerInstance().format(vboxGraphics.getCanvasBuffer()), vboxGraphics.canvasBufferProperty()));
        HBox canvasBufferBox = new HBox(10, canvasBufferSlider, canvasBufferValueLabel);
        canvasBufferBox.setAlignment(Pos.CENTER_LEFT);

        // eventline canvas buffer
        Label eventLineCanvasBufferSizeLabel = new Label("Eventline Canvas Buffer:");
        Slider eventlineCanvasBufferSlider = new Slider(0, 250, vboxGraphics.getCanvasBuffer());
        eventlineCanvasBufferSlider.valueProperty().bindBidirectional(timeline.getEventline().getGraphics().canvasBufferProperty());

        Label eventlineCanvasBufferValueLabel = new Label();
        eventlineCanvasBufferValueLabel.setMinWidth(Region.USE_PREF_SIZE);
        eventlineCanvasBufferValueLabel.textProperty().bind(Bindings.createStringBinding(() -> DecimalFormat.getIntegerInstance().format(timeline.getEventline().getGraphics().getCanvasBuffer()), timeline.getEventline().getGraphics().canvasBufferProperty()));
        HBox eventlineBufferBox = new HBox(10, eventlineCanvasBufferSlider, eventlineCanvasBufferValueLabel);
        eventlineBufferBox.setAlignment(Pos.CENTER_LEFT);

        // dateline buffer
        Label datelineBufferSizeLabel = new Label("Dateline Buffer:");
        Slider datelineBufferSlider = new Slider(0, 250, vboxGraphics.getCanvasBuffer());
        datelineBufferSlider.valueProperty().bindBidirectional(timeline.getDateline().datelineBufferProperty());

        Label datelineBufferValueLabel = new Label();
        datelineBufferValueLabel.setMinWidth(Region.USE_PREF_SIZE);
        datelineBufferValueLabel.textProperty().bind(Bindings.createStringBinding(() -> DecimalFormat.getIntegerInstance().format(timeline.getDateline().getDatelineBuffer()), timeline.getDateline().datelineBufferProperty()));
        HBox datelineBufferBox = new HBox(10, datelineBufferSlider, datelineBufferValueLabel);
        datelineBufferBox.setAlignment(Pos.CENTER_LEFT);

        FlowPane box = new FlowPane(
                debugMode,
                showScale,
                canvasBufferSizeLabel,
                canvasBufferBox,
                datelineBufferSizeLabel,
                datelineBufferBox,
                eventLineCanvasBufferSizeLabel,
                eventlineBufferBox);

        box.setVgap(10);
        box.setHgap(10);

        box.setAlignment(Pos.CENTER_LEFT);

        return box;
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
    public String getDescription() {
        return "This demo illustrates how the canvas and dateline buffer work. Not everything gets recreated or redrawn when scrolling. Instead larger views are simply moved left or right via their translate-x property.";
    }

    @Override
    public String getName() {
        return "Canvas Buffer";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
