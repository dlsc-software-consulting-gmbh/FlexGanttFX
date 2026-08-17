This demo focuses on global activities displayed in the eventline rather than inside individual rows. It demonstrates how chart-wide events can be created and visualized independently of the regular row-based activity layout.

```java
public class GlobalActivitiesDemo extends GanttChartDemoBase {

    private GanttChart<DemoRow> gc;
    private final EventlineCalendar calendar = new EventlineCalendar();
    private final Layer layer = new Layer("Default Layer");
    private final PhaseRow frozenRow = new PhaseRow();
    private final ChronoUnitGrid dayGrid = new ChronoUnitGrid("Day Grid", ChronoUnit.DAYS, 1);
    
    @Override
    protected GanttChart<?> createGanttChart() {
        gc = new GanttChart<>();
        gc.setRoot(new DemoRow("root"));

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
            nameOptional.ifPresent(s -> addPhase(s, st, et));
        });

        final CalendarLayer calendarLayer = gc.getGraphics().getSystemLayer(CalendarLayer.class);
        calendarLayer.setCalendarActivityRenderer(Phase.class, new PhaseCalendarActivityRenderer(gc.getGraphics()));

        eventline.getGraphics().setActivityRenderer(Phase.class, GanttLayout.class, new PhaseActivityRenderer(eventline.getGraphics()));

        addPhase("Design", Instant.now().plus(1, ChronoUnit.DAYS), Instant.now().plus(5, ChronoUnit.DAYS));
        addPhase("Implementation", Instant.now().plus(8, ChronoUnit.DAYS), Instant.now().plus(16, ChronoUnit.DAYS));
        addPhase("Testing", Instant.now().plus(19, ChronoUnit.DAYS), Instant.now().plus(25, ChronoUnit.DAYS));

        return gc;
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
}
```
