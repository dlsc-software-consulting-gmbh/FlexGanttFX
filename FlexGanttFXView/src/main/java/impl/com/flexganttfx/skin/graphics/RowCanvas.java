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
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.*;
import com.flexganttfx.model.activity.ChartActivity;
import com.flexganttfx.model.activity.HighLowChartActivity;
import com.flexganttfx.model.exception.IllegalLineIndexException;
import com.flexganttfx.model.exception.RepositoryException;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.layout.AgendaLayout.LayoutStrategy;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.ActivityHelper;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.EditMode;
import com.flexganttfx.view.graphics.GraphicsBase.LassoSelectionBehaviour;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.Position;
import impl.com.flexganttfx.skin.util.Placement;
import impl.com.flexganttfx.skin.util.Resolver;
import impl.com.flexganttfx.skin.util.ResolverResult;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import java.text.MessageFormat;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;

import static com.flexganttfx.model.layout.AgendaLayout.LayoutStrategy.PARALLEL_OVERLAPPING;
import static com.flexganttfx.view.util.Position.*;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.requireNonNull;

/**
 * Canvas that renders the activities of a single row. It manages per-row layout state,
 * selection visuals, and redraw requests for the row content.
 *
 * @param <R> the type of the rows
 */
public final class RowCanvas<R extends Row<?, ?, ?>> extends Canvas {

    private final GraphicsBase<R> graphics;

    private final List<ActivityBounds> activityBounds = new ArrayList<>();

    private Map<Integer, List<ActivityEntry>> agendaColumnMap;

    private Map<LocalDate, List<Activity>> dateActivitiesMap;

    private Map<LocalDate, Map<Activity, Placement<Activity>>> datePlacementsMap;

    private Rectangle2D debugRectangle;

    private RowCanvasBehaviour<?> rowCanvasBehaviour;

    /**
     * Constructs a new row canvas for the given graphics control.
     *
     * @param graphics
     *            the graphics control
     */
    public RowCanvas(GraphicsBase<R> graphics) {
        requireNonNull(graphics);

        this.graphics = graphics;

        getStyleClass().add("row-canvas");

        widthProperty().addListener(redrawListener);
        heightProperty().addListener(redrawListener);

        rowCanvasBehaviour = new RowCanvasBehaviour<>(this);

        rowProperty().addListener(evt -> requestRedraw("row model object changed"));

        ChangeListener<ActivityRef<?>> weakActivityRedrawListener = new WeakChangeListener<>(activityRedrawListener);
        graphics.editModeProperty().addListener(new WeakInvalidationListener(editModeListener));
        graphics.hoverActivityProperty().addListener(weakActivityRedrawListener);
        graphics.pressedActivityProperty().addListener(weakActivityRedrawListener);
        graphics.getSelectedActivities().addListener(new WeakListChangeListener<>(selectedActivitiesListener));

        InvalidationListener pseudoStateRedrawListener = observable -> requestRedraw("pseudo state changed");

        hoverProperty().addListener(pseudoStateRedrawListener);
        pressedProperty().addListener(pseudoStateRedrawListener);
        focusedProperty().addListener(pseudoStateRedrawListener);

        graphics.canvasBufferProperty().addListener(it -> {
            randomTranslateX(true);
            requestRedraw("canvas buffer size changed");
        });

        randomTranslateX(true);
    }

    private void randomTranslateX(boolean scrollingRight) {
        final double canvasBuffer = graphics.getCanvasBuffer();
        final double offset = Math.random() * canvasBuffer / 4;

        if (scrollingRight) {
            setTranslateX(canvasBuffer - offset);
        } else {
            setTranslateX(-canvasBuffer + offset);
        }
    }

    /**
     * Requests a redraw for the given reason.
     *
     * @param reason the redraw reason
     * @param oldTime the previous time
     */
    public void requestRedraw(String reason, Instant oldTime) {
        final Timeline timeline = graphics.getTimeline();

        final double rowHeadersWidth = graphics.isShowRowHeaders() ? graphics.getRowHeadersWidth() : 0;
        final double x = timeline.getModel().calculateLocationForTime(oldTime) - rowHeadersWidth;
        final double newTranslateX = getTranslateX() + x;

        if (Math.abs(newTranslateX) < graphics.getCanvasBuffer()) {
            setTranslateX(newTranslateX);
        } else {
            if (graphics.getCanvasBuffer() > 0) {
                randomTranslateX(newTranslateX - getTranslateX() < 0);
            }
            requestRedraw(reason);
        }
    }

    private final ChangeListener<ActivityRef<?>> activityRedrawListener = (observable, oldRef, newRef) -> {
        if ((oldRef != null && oldRef.getRow() == getRow()) || (newRef != null && newRef.getRow() == getRow())) {

            if (observable instanceof ReadOnlyProperty) {
                if (LoggingDomain.RENDERING.isLoggable(Level.FINE)) {
                    LoggingDomain.RENDERING.fine("redraw because of property change, property = " + ((ReadOnlyProperty<?>) observable).getName() + ", row = " + (getRow() != null ? getRow().getName() : "(empty row)"));
                }
            }

            requestRedraw("activity redraw listener fired");
        }
    };

    private final InvalidationListener editModeListener = it -> {
        if (getGraphics().getEditMode().equals(EditMode.NONE)) {
            rowCanvasBehaviour.stopEdit();
        }
    };

    private final ListChangeListener<ActivityRef<?>> selectedActivitiesListener = (Change<? extends ActivityRef<?>> change) -> {
        while (change.next()) {
            for (ActivityRef<?> ref : change.getAddedSubList()) {
                if (ref.getRow() == getRow()) {
                    requestRedraw("selected activities listener fired after activities were added");
                    return;
                }
            }
            for (ActivityRef<?> ref : change.getRemoved()) {
                if (ref.getRow() == getRow()) {
                    requestRedraw("selected activities listener fired after activities were removed");
                    return;
                }
            }
        }
    };

    /**
     * Returns the graphics control.
     *
     * @return the graphics control
     */
    public GraphicsBase<R> getGraphics() {
        return graphics;
    }

    private final ChangeListener<Number> redrawListener = (value, oldSize, newSize) -> requestRedraw("redraw listener fired");

    private final ObjectProperty<R> row = new SimpleObjectProperty<>(this, "row");

    /**
     * The row property.
     *
     * @return the row property
     */
    public ObjectProperty<R> rowProperty() {
        return row;
    }

    public void setRow(R row) {
        rowProperty().set(row);
    }

    public R getRow() {
        return rowProperty().get();
    }

    /**
     * Returns the timeline model.
     *
     * @return the timeline model
     */
    public TimelineModel<?> getTimelineModel() {
        return graphics.getTimeline().getModel();
    }

    /**
     * Returns whether this canvas is resizable.
     *
     * @return true if this canvas is resizable
     */
    @Override
    public boolean isResizable() {
        return true;
    }

    /**
     * Returns the preferred height for the given width.
     *
     * @param width the width
     *
     * @return the preferred height
     */
    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    /**
     * Returns the preferred width for the given height.
     *
     * @param height the height
     *
     * @return the preferred width
     */
    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    private boolean safeRendering;

    private boolean dirty;
    private String reason;

    private static int drawCounter;
    private static int doDrawCounter;

    /**
     * Returns whether a redraw is pending.
     *
     * @return true if a redraw is pending
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Requests a redraw for the given reason.
     *
     * @param reason the redraw reason
     */
    public void requestRedraw(String reason) {
        this.reason = reason;

        dirty = true;

        if (drawCounter < Integer.MAX_VALUE) {
            drawCounter++;
        } else {
            drawCounter = 1;
            doDrawCounter = 1;
        }

        //
        // Super important to also request a layout because the actual drawing only
        // happens after a layout pulse gets fired, which is not guaranteed if the
        // only thing that changed is the content of the canvas.
        //
        if (getParent() != null) {
            getParent().requestLayout();
        }
    }

    /**
     * Draws the canvas contents.
     */
    public void draw() {
        if (doDrawCounter < Integer.MAX_VALUE) {
            doDrawCounter++;
        } else {
            doDrawCounter = 1;
            drawCounter = 1;
        }

        dirty = false;

        if (LoggingDomain.RENDERING.isLoggable(Level.FINEST)) {
            LoggingDomain.RENDERING.finest("drawing canvas of row " + getRow() + ", reason: " + reason);
        }

        activityBounds.clear();

        double width = getWidth();
        double height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        GraphicsContext gc = getGraphicsContext2D();

        gc.clearRect(0, 0, width, height);

        TimelineModel<?> timelineModel = getTimelineModel();

        Instant drawingStartTime = timelineModel.calculateTimeForLocation(0 - graphics.getCanvasBuffer() + getTranslateX() + timelineModel.getOffset());
        Instant drawingEndTime = timelineModel.calculateTimeForLocation(0 - graphics.getCanvasBuffer() + getTranslateX() + getWidth() + timelineModel.getOffset());

        safeRendering = getGraphics().isSafeRendering();

        try {
            if (safeRendering) {
                gc.save();
            }

            for (SystemLayer layer : graphics.getBackgroundSystemLayers()) {
                if (layer.isVisible()) {
                    if (safeRendering) {
                        gc.save();
                    }

                    drawSystemLayer(layer, gc, drawingStartTime, drawingEndTime);

                    if (safeRendering) {
                        gc.restore();
                    }
                }
            }

            drawModelLayers(drawingStartTime, drawingEndTime);

            for (SystemLayer layer : graphics.getForegroundSystemLayers()) {
                if (layer.isVisible()) {
                    if (safeRendering) {
                        gc.save();
                    }

                    drawSystemLayer(layer, gc, drawingStartTime, drawingEndTime);

                    if (safeRendering) {
                        gc.restore();
                    }
                }
            }

        } catch (IllegalLineIndexException | MissingActivityBoundsException ex) {
            ex.printStackTrace();
        } finally {
            if (safeRendering) {
                gc.restore();
            }
        }

        if (lookupBounds != null && graphics.isDebugMode()) {
            gc.setStroke(Color.MAGENTA);
            gc.strokeRect(lookupBounds.getMinX(), lookupBounds.getMinY(), lookupBounds.getWidth(), lookupBounds.getHeight());
        }

        if (agendaColumnMap != null) {
            agendaColumnMap.clear();
        }

        if (graphics.isDebugMode()) {
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(3);
            gc.strokeRect(0, 0, getWidth(), getHeight());

            gc.setStroke(Color.BLUE);
            gc.strokeLine(calculateLocation(drawingStartTime), 5, calculateLocation(drawingStartTime), getHeight() - 10);
            gc.strokeLine(calculateLocation(drawingEndTime) - 2, 5, calculateLocation(drawingEndTime) - 2, getHeight() - 10);
        }

        LoggingDomain.RENDERING.fine("calls to draw = " + drawCounter + ", actual draws = " + doDrawCounter + ", saved draws = " + (drawCounter - doDrawCounter));
    }

    private void drawSystemLayer(SystemLayer<R> layer, GraphicsContext gc, Instant startTime, Instant endTime) {
        double opacity = layer.getOpacity();
        if (opacity > 0) {
            gc.setGlobalAlpha(opacity);
            layer.drawLayer(this, startTime, endTime);
        }
    }

    private void drawModelLayers(Instant startTime, Instant endTime) throws IllegalLineIndexException, MissingActivityBoundsException {

        R row = getRow();

        dateActivitiesMap = null;
        datePlacementsMap = null;

        if (row != null) {

            GraphicsContext gc = getGraphicsContext2D();

            for (Layer layer : graphics.getLayers()) {
                if (layer.getFadeInOutOpacity() > 0) {
                    try {
                        if (safeRendering) {
                            gc.save();
                        }
                        drawLayer(row, layer, startTime, endTime, false);
                    } finally {
                        if (safeRendering) {
                            gc.restore();
                        }
                    }
                }
            }

            if (dateActivitiesMap != null) {
                for (LocalDate date : dateActivitiesMap.keySet()) {
                    List<Activity> activities = dateActivitiesMap.get(date);
                    ResolverResult<Activity> result = Resolver.resolve(activities);
                    Map<Activity, Placement<Activity>> placements = result.getPlacements();
                    if (datePlacementsMap == null) {
                        datePlacementsMap = new HashMap<>();
                    }
                    datePlacementsMap.put(date, placements);
                }

                for (Layer layer : graphics.getLayers()) {
                    if (layer.getFadeInOutOpacity() > 0) {
                        try {
                            if (safeRendering) {
                                gc.save();
                            }
                            drawLayer(row, layer, startTime, endTime, true);
                        } finally {
                            if (safeRendering) {
                                gc.restore();
                            }
                        }
                    }
                }
            }

            switch (graphics.getEditMode()) {
                case DRAGGING:
                case DRAGGING_VERTICAL:
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void drawLayer(Row row, Layer layer, Instant startTime, Instant endTime, boolean secondPass) throws IllegalLineIndexException, MissingActivityBoundsException {

        Timeline timeline = graphics.getTimeline();
        TimelineModel<?> timelineModel = timeline.getModel();
        TemporalUnit temporalUnit = timeline.getDateline().getPrimaryTemporalUnit();
        ZoneId zoneId = row.getZoneId();

        /*
         * When using agenda layout anywhere in this row then we have to expand
         * the query horizon to make sure all activities for the first and last
         * day will be visible even when only parts of those days are currently
         * in the viewport.
         */
        if (isUsingAgendaLayout(row)) {
            Dateline dateline = timeline.getDateline();
            ZoneId datelineZoneId = dateline.getZoneId();

            ZonedDateTime st = ZonedDateTime.ofInstant(startTime, datelineZoneId);
            ZonedDateTime et = ZonedDateTime.ofInstant(endTime, datelineZoneId);

            st = st.truncatedTo(DAYS);
            et = et.truncatedTo(DAYS).plusDays(1).minusNanos(1);

            startTime = Instant.from(st);
            endTime = Instant.from(et);
        }

        ActivityRepository repository = row.getRepository();
        Iterator<Activity> activities = repository.getActivities(layer, startTime, endTime, temporalUnit, zoneId);

        if (activities == null) {
            throw new RepositoryException(MessageFormat.format(
                    "the repository of type {0} returned a NULL iterator for its activities, this is not allowed.",
                    repository.getClass().getName()));
        }

        double rowHeight = getHeight();

        final Predicate<Activity> activityFilter = graphics.getActivityFilter();

        while (activities.hasNext()) {

            Activity activity = activities.next();
            if (activityFilter != null && !activityFilter.test(activity)) {
                /*
                 * The activity has been filtered out.
                 */
                continue;
            }

            int lineIndex = row.getLineIndex(activity);

            if (lineIndex >= 0 && lineIndex >= row.getLineCount()) {
                /*
                 * The activity is placed on a line, but the line is not shown,
                 * so do nothing.
                 */
                continue;
            }

            ActivityRef ref = new ActivityRef(row, layer, activity);

            List<ActivityBounds> bounds = drawActivity(ref, timelineModel, zoneId, rowHeight, secondPass);

            if (bounds != null && !bounds.isEmpty()) {
                activityBounds.addAll(bounds);

                if (graphics.isDebugMode()) {
                    for (ActivityBounds b : bounds) {
                        getGraphicsContext2D().setStroke(Color.MAGENTA);
                        getGraphicsContext2D().strokeRect(b.getMinX(),
                                b.getMinY(), b.getWidth(), b.getHeight());
                    }

                    if (debugRectangle != null) {
                        getGraphicsContext2D().setStroke(Color.CYAN);
                        getGraphicsContext2D().strokeRect(
                                debugRectangle.getMinX(),
                                debugRectangle.getMinY(),
                                debugRectangle.getWidth(),
                                debugRectangle.getHeight());
                    }
                }
            }
        }

        if (secondPass) {
            ActivityRef<?> editedActivity = graphics.getEditedActivity();
            if (editedActivity != null && editedActivity.getRow() == row) {
                List<ActivityBounds> bounds = drawActivity(editedActivity, timelineModel, zoneId, rowHeight, secondPass);

                if (bounds != null && !bounds.isEmpty()) {
                    activityBounds.addAll(bounds);

                    if (graphics.isDebugMode()) {
                        for (ActivityBounds b : bounds) {
                            getGraphicsContext2D().setStroke(Color.MAGENTA);
                            getGraphicsContext2D().strokeRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
                        }
                    }
                }
            }
        }
    }

    private boolean isUsingAgendaLayout(Row<?, ?, ?> row) throws IllegalLineIndexException {
        if (row.getLayout() instanceof AgendaLayout) {
            return true;
        }

        int lineCount = row.getLineCount();
        if (lineCount > 0) {
            for (int index = 0; index < lineCount; index++) {
                Layout layout = row.getLineLayout(index);
                if (layout instanceof AgendaLayout) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<ActivityBounds> drawActivity(ActivityRef ref, TimelineModel<?> timelineModel, ZoneId zoneId, double rowHeight, boolean secondPass)
            throws IllegalLineIndexException, MissingActivityBoundsException {

        List<ActivityBounds> boundsList = new ArrayList<>();

        Row row = ref.getRow();
        Layout layout = row.getLayout();
        Activity activity = ref.getActivity();

        int lineIndex = row.getLineIndex(activity);

        double yOffset = 0;
        double availableHeight = rowHeight;

        if (lineIndex >= 0) {
            yOffset = row.getLineLocation(lineIndex);
            availableHeight = row.getLineHeight(lineIndex);
            layout = row.getLineLayout(lineIndex);
        }

        /*
         * A second pass is only relevant for agenda layout as it uses the
         * placement information from the first pass to render its activities in
         * multiple columns per day.
         */
        if (secondPass && !(layout instanceof AgendaLayout)) {
            return Collections.emptyList();
        }

        double x1 = calculateLocation(activity.getStartTime());
        double x2 = calculateLocation(activity.getEndTime());

        boolean selected = graphics.getSelectedActivities().contains(ref);
        boolean focused = ref.equals(graphics.getHoverActivity());
        boolean pressed = ref.equals(graphics.getPressedActivity());

        boolean highlighted = false;
        if (!graphics.getHighlightedActivities().isEmpty() && graphics.isHighlighted()) {
            highlighted = graphics.getHighlightedActivities().contains(ref);
        }

        yOffset += layout.getPadding();
        availableHeight -= 2 * layout.getPadding();

        if (availableHeight <= 0) {
            return null;
        }

        ActivityRenderer renderer = graphics
                .getActivityRenderer(activity.getClass(), layout.getClass());
        if (renderer == null) {
            throw new IllegalStateException("no renderer found for activity of type " + activity.getClass() + " and layout of type " + layout.getClass());
        }
        if (!renderer.isEnabled()) {
            return Collections.emptyList();
        }

        final GraphicsContext gc = getGraphicsContext2D();

        double layerOpacity = ref.getLayer().getOpacity();
        double fadeInOutOpacity = ref.getLayer().getFadeInOutOpacity();
        double totalOpacity = layerOpacity * fadeInOutOpacity;

        double alpha = gc.getGlobalAlpha();

        gc.setGlobalAlpha(totalOpacity);

        if (layout instanceof AgendaLayout) {
            AgendaLayout agendaLayout = (AgendaLayout) layout;

            if (agendaColumnMap == null) {
                agendaColumnMap = new HashMap<>();
            }

            ZonedDateTime zonedStartDateTime = ZonedDateTime.ofInstant(activity.getStartTime(), zoneId);
            ZonedDateTime zonedEndDateTime = ZonedDateTime.ofInstant(activity.getEndTime(), zoneId);
            ZonedDateTime startOfDayTime = zonedStartDateTime.with(LocalTime.MIN);
            ZonedDateTime endOfDayTime = zonedEndDateTime.with(LocalTime.MAX);

            long repeats = startOfDayTime.until(endOfDayTime, DAYS) + 1;

            for (int column = 0; column < repeats; column++) {

                startOfDayTime = startOfDayTime.with(agendaLayout.getStartTime());
                endOfDayTime = startOfDayTime.with(agendaLayout.getEndTime());

                Position position;

                if (repeats == 1) {
                    position = ONLY;
                } else if (column == 0) {
                    position = FIRST;
                } else if (column == repeats - 1) {
                    position = LAST;
                } else {
                    position = MIDDLE;
                }

                if (ActivityHelper.intersect(zonedStartDateTime.toInstant(), zonedEndDateTime.toInstant(), startOfDayTime.toInstant(), endOfDayTime.toInstant())) {

                    /*
                     * The activity might not be visible at all because the
                     * agenda layout defines a start and end time other than
                     * from midnight till midnight.
                     */

                    ZonedDateTime truncatedDateTime = zonedStartDateTime.truncatedTo(DAYS).plusDays(column);

                    x1 = calculateLocation(Instant.from(truncatedDateTime));
                    x2 = calculateLocation(Instant.from(truncatedDateTime.plusDays(1)));

                    LayoutStrategy layoutStrategy = agendaLayout.getLayoutStrategy();

                    switch (layoutStrategy) {
                        case OVERLAPPING:
                            List<ActivityEntry> columnActivities = agendaColumnMap.get((int) x1);

                            if (columnActivities == null) {
                                columnActivities = new ArrayList<>();
                                agendaColumnMap.put((int) x1, columnActivities);
                            }

                            int insetLevel = computeInsetLevel(activity, columnActivities);

                            ActivityEntry entry = new ActivityEntry();
                            entry.activity = activity;
                            entry.indentLevel = insetLevel;
                            columnActivities.add(entry);

                            x1 += (insetLevel * agendaLayout.getOverlapOffset());
                            break;
                        case PARALLEL:
                        case PARALLEL_OVERLAPPING:
                            if (!secondPass) {
                                /*
                                 * The first pass is used to collect all activities
                                 * per day. Rendering happens in the second pass.
                                 */
                                if (dateActivitiesMap == null) {
                                    dateActivitiesMap = new HashMap<>();
                                }

                                LocalDate date = startOfDayTime.toLocalDate();
                                List<Activity> dateActivities = dateActivitiesMap.computeIfAbsent(date, it -> new ArrayList<>());
                                dateActivities.add(activity);
                            }

                            break;
                    }

                    if (layoutStrategy.equals(LayoutStrategy.OVERLAPPING) || secondPass) {
                        double yy1 = yOffset;

                        if (column == 0) {
                            yy1 = yOffset + calculateVerticalTimeLocation(
                                    zonedStartDateTime.toLocalTime(),
                                    agendaLayout, availableHeight);
                        }

                        double yy2 = yOffset + availableHeight;

                        if (column == repeats - 1) {
                            yy2 = yOffset + calculateVerticalTimeLocation(
                                    zonedEndDateTime.toLocalTime(),
                                    agendaLayout, availableHeight);
                        }

                        ActivityBounds bounds;

                        double columnWidth = x2 - x1;

                        switch (layoutStrategy) {
                            case PARALLEL:
                            case PARALLEL_OVERLAPPING:
                                if (datePlacementsMap != null) {

                                    LocalDate date = truncatedDateTime.toLocalDate();
                                    Map<Activity, Placement<Activity>> dateMap = datePlacementsMap.get(date);

                                    if (dateMap != null) {
                                        Placement placement = dateMap.get(activity);

                                        if (placement != null) {

                                            columnWidth = columnWidth / placement.getColumnCount();
                                            x1 += placement.getColumnIndex() * columnWidth;

                                            if (layoutStrategy.equals(PARALLEL_OVERLAPPING)) {

                                                double offset = Math.min(.5, agendaLayout.getOverlapOffset());
                                                double extraWidth = columnWidth * offset / 2;

                                                if (placement.getColumnCount() > 1) {
                                                    columnWidth += extraWidth;
                                                }

                                                if (placement.getColumnIndex() > 0) {
                                                    x1 -= (column + 1) * extraWidth;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            case OVERLAPPING:
                                break;
                        }

                        bounds = renderer.draw(ref, position, gc,
                                snapPositionX(x1),
                                snapPositionY(yy1),
                                snapSizeX(columnWidth),
                                Math.max(snapSizeX(1), snapSizeY(yy2 - yy1 - 1 - 1)),
                                selected, focused, highlighted, pressed);

                        if (bounds == null) {
                            throw new MissingActivityBoundsException(renderer, activity, row, lineIndex);
                        }

                        bounds.setPosition(position);
                        bounds.setLayout(layout);
                        boundsList.add(bounds);
                    }
                }

                startOfDayTime = startOfDayTime.plusDays(1);
            }

        } else if (layout instanceof GanttLayout) {

            ActivityBounds bounds = renderer.draw(ref, ONLY, gc, x1 + .25,
                    yOffset + .5, x2 - x1, availableHeight - 1, selected,
                    focused, highlighted, pressed);

            if (bounds == null) {
                throw new MissingActivityBoundsException(renderer, activity, row, lineIndex);
            }

            bounds.setPosition(ONLY);
            bounds.setLayout(layout);
            boundsList.add(bounds);

        } else if (layout instanceof ChartLayout) {
            if (activity instanceof ChartActivity) {

                yOffset += calculateChartOffset((ChartActivity) activity, (ChartLayout) layout, availableHeight);
                availableHeight = calculateChartActivityHeight((ChartActivity) activity, (ChartLayout) layout, availableHeight);

            } else if (activity instanceof HighLowChartActivity) {

                HighLowChartActivity highLow = (HighLowChartActivity) activity;
                double offsetHigh = calculateChartValueOffset(highLow.getHigh(), (ChartLayout) layout, availableHeight);
                double offsetLow = calculateChartValueOffset(highLow.getLow(), (ChartLayout) layout, availableHeight);
                yOffset += offsetHigh;
                availableHeight = offsetLow - offsetHigh;

            }

            ActivityBounds bounds = renderer.draw(ref, ONLY, gc, x1 + .25,
                    yOffset + .5, x2 - x1, availableHeight - 1, selected,
                    focused, highlighted, pressed);

            if (bounds == null) {
                throw new MissingActivityBoundsException(renderer, activity, row, lineIndex);
            }

            bounds.setPosition(ONLY);
            bounds.setLayout(layout);
            boundsList.add(bounds);
        }

        if (!safeRendering) {
            gc.setGlobalAlpha(alpha);
        }

        return boundsList;
    }

    private double calculateLocation(Instant startTime) {
        double rowHeaderWidth = graphics.isShowRowHeaders() ? graphics.getRowHeadersWidth() : 0;
        return getTimelineModel().calculateLocationForTime(startTime) + graphics.getCanvasBuffer() - getTranslateX() - rowHeaderWidth;
    }

    private double calculateChartValueOffset(double value, ChartLayout layout, double availableHeight) {
        double range = layout.getMaxValue() - layout.getMinValue();
        double ppv = availableHeight / range;
        double zeroLineLocation = layout.getMaxValue() * ppv;

        return zeroLineLocation - (value * ppv);
    }

    private double calculateChartOffset(ChartActivity chartActivity, ChartLayout layout, double availableHeight) {
        double range = layout.getMaxValue() - layout.getMinValue();
        double ppv = availableHeight / range;
        double zeroLineLocation = layout.getMaxValue() * ppv;

        if (chartActivity.getChartValue() < 0) {
            return zeroLineLocation;
        }

        return zeroLineLocation - (chartActivity.getChartValue() * ppv);
    }

    private double calculateChartActivityHeight(ChartActivity chartActivity, ChartLayout layout, double availableHeight) {
        double range = layout.getMaxValue() - layout.getMinValue();
        double ppv = availableHeight / range;
        return Math.abs(chartActivity.getChartValue()) * ppv;
    }

    private int computeInsetLevel(Activity activity, List<ActivityEntry> columnActivities) {
        int level = 0;
        for (ActivityEntry entry : columnActivities) {
            if (ActivityHelper.intersect(entry.activity, activity)) {
                level = entry.indentLevel + 1;
            }
        }

        return level;
    }

    class ActivityEntry {
        int indentLevel;
        Activity activity;
    }

    private double calculateVerticalTimeLocation(LocalTime time,
                                                 AgendaLayout layout, double availableHeight) {

        LocalTime st = layout.getStartTime();
        LocalTime et = layout.getEndTime();

        long millis = st.until(et, ChronoUnit.MILLIS);
        double mpp = millis / availableHeight;

        return Math.min(availableHeight, Math.max(0, (time.get(ChronoField.MILLI_OF_DAY) - st.get(ChronoField.MILLI_OF_DAY)) / mpp));
    }

    /**
     * Returns all activity bounds.
     *
     * @return all activity bounds
     */
    public List<ActivityBounds> getAllActivityBounds() {
        return activityBounds;
    }

    /**
     * Returns all activity bounds at the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the activity bounds at the given coordinates
     */
    public List<ActivityBounds> getAllActivityBounds(double x, double y) {
        List<ActivityBounds> result = new ArrayList<>();
        for (ActivityBounds bounds : activityBounds) {
            if (bounds.contains(x, y)) {
                result.add(bounds);
            }
        }
        return result;
    }

    /**
     * Returns the activity bounds at the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the activity bounds at the given coordinates
     */
    public ActivityBounds getActivityBounds(double x, double y) {
        List<ActivityBounds> allBounds = getAllActivityBounds(x, y);

        int s = allBounds.size();
        if (s > 0) {
            return allBounds.get(s - 1);
        }

        return null;
    }

    /**
     * Returns the activity bounds for the given activity reference.
     *
     * @param activityRef the activity reference
     *
     * @return the activity bounds for the activity reference
     */
    public ActivityBounds getActivityBounds(ActivityRef<?> activityRef) {
        if (activityRef.getRow().equals(getRow())) {
            for (ActivityBounds bounds : activityBounds) {
                if (bounds.getActivityRef().equals(activityRef)) {
                    return bounds;
                }
            }

            /*
             * We haven't found any bounds but the activity clearly belongs to
             * this row. So let's draw the activity right now. The user will not
             * see it because it seems to be outside the currently visible time
             * range.
             */
            try {
                List<ActivityBounds> firstPassBounds = drawActivity(activityRef,
                        getTimelineModel(), activityRef.getRow().getZoneId(),
                        activityRef.getRow().getHeight(), false);

                /*
                 * If the activity is located on a row or a line with
                 * AgendaLayout then a second pass is necessary. The second pass
                 * will return an empty list for anything that is not on an
                 * AgendaLayout.
                 */
                List<ActivityBounds> secondPassBounds = drawActivity(
                        activityRef, getTimelineModel(),
                        activityRef.getRow().getZoneId(),
                        activityRef.getRow().getHeight(), true);

                if (secondPassBounds != null && !secondPassBounds.isEmpty()) {
                    return secondPassBounds.get(0);
                }

                if (firstPassBounds != null && !firstPassBounds.isEmpty()) {
                    return firstPassBounds.get(0);
                }
            } catch (IllegalLineIndexException | MissingActivityBoundsException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Rectangle2D lookupBounds;

    /**
     * Returns the activity bounds within the given area.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h the height
     *
     * @return the activity bounds within the given area
     */
    public List<ActivityBounds> getActivityBounds(double x, double y, double w, double h) {

        if (graphics.isDebugMode()) {
            lookupBounds = new Rectangle2D(x, y, w, h);
        } else {
            lookupBounds = null;
        }

        LassoSelectionBehaviour behaviour = graphics.getLassoSelectionBehaviour();
        Rectangle2D selectionRectangle = new Rectangle2D(x, y, w, h);

        Timeline timeline = graphics.getTimeline();
        TimelineModel<?> timelineModel = timeline.getModel();

        Instant st = timelineModel.calculateTimeForLocation(x);
        Instant et = timelineModel.calculateTimeForLocation(x + w);

        List<ActivityBounds> result = new ArrayList<>();
        for (ActivityBounds bounds : activityBounds) {

            Rectangle2D adjustedBounds = new Rectangle2D(bounds.getMinX() - graphics.getCanvasBuffer() + getTranslateX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());

            switch (behaviour) {
                case INTERSECTION:
                    if (selectionRectangle.intersects(adjustedBounds)) {
                        result.add(bounds);
                    }
                    break;
                case BOUNDS_CONTAINMENT:
                    if (selectionRectangle.contains(adjustedBounds)) {
                        result.add(bounds);
                    }
                    break;
                case TIME_INTERVAL_CONTAINMENT:
                    Activity activity = bounds.getActivity();
                    Instant activityStart = activity.getStartTime();
                    Instant activityEnd = activity.getEndTime();
                    if ((st.equals(activityStart) || st.isBefore(activityStart))
                            && (et.equals(activityEnd)
                            || et.isAfter(activityEnd))) {
                        result.add(bounds);
                    }
                    break;
            }
        }
        return result;
    }

    /**
     * Returns the layout at the given y coordinate.
     *
     * @param y the y coordinate
     *
     * @return the layout at the given y coordinate
     */
    public Layout getLayoutAt(double y) {
        Layout layout = null;

        Row<?, ?, ?> row = getRow();
        if (row != null) {
            int lineCount = row.getLineCount();
            if (lineCount <= 0) {
                layout = row.getLayout();
            } else {
                for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
                    double lineLocation = row.getLineLocation(lineIndex);
                    double lineHeight = row.getLineHeight(lineIndex);
                    if (y >= lineLocation && y <= lineLocation + lineHeight) {
                        layout = row.getLineLayout(lineIndex);
                        break;
                    }
                }
            }
        }

        return layout;
    }

    /**
     * Returns the layout bounds at the given y coordinate.
     *
     * @param y the y coordinate
     *
     * @return the layout bounds at the given y coordinate
     */
    public Rectangle2D getLayoutBoundsAt(double y) {
        Rectangle2D bounds = null;

        Row<?, ?, ?> row = getRow();
        if (row != null) {
            int lineCount = row.getLineCount();
            if (lineCount <= 0) {
                bounds = new Rectangle2D(0, 0, getWidth(), getHeight());
            } else {
                for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
                    double lineLocation = row.getLineLocation(lineIndex);
                    double lineHeight = row.getLineHeight(lineIndex);
                    if (y >= lineLocation && y <= lineLocation + lineHeight) {
                        bounds = new Rectangle2D(0, lineLocation, getWidth(),
                                lineHeight);
                        break;
                    }
                }
            }
        }

        if (graphics.isDebugMode()) {
            debugRectangle = bounds;
            requestRedraw("layout bounds lookup in debug mode");
        }

        return bounds;
    }

    // snap to pixel

    private final BooleanProperty snapToPixel = new SimpleBooleanProperty(this, "snapToPixel", true);

    /**
     * The snap to pixel property.
     *
     * @return the snap to pixel property
     */
    public BooleanProperty snapToPixelProperty() {
        return snapToPixel;
    }

    public void setSnapToPixel(boolean snap) {
        snapToPixel.set(snap);
    }

    public boolean isSnapToPixel() {
        return snapToPixel.get();
    }

    /**
     * If this canvas' snapToPixel property is true, returns a value rounded
     * to the nearest pixel in the horizontal direction, else returns the
     * same value.
     * @param value the space value to be snapped
     * @return value rounded to nearest pixel
     */
    public double snapSpaceX(double value) {
        return snapSpaceX(value, isSnapToPixel());
    }

    /**
     * If this canvas' snapToPixel property is true, returns a value rounded
     * to the nearest pixel in the vertical direction, else returns the
     * same value.
     * @param value the space value to be snapped
     * @return value rounded to nearest pixel
     */
    public double snapSpaceY(double value) {
        return snapSpaceY(value, isSnapToPixel());
    }

    /**
     * If this canvas' snapToPixel property is true, returns a value ceiled
     * to the nearest pixel in the horizontal direction, else returns the
     * same value.
     * @param value the size value to be snapped
     * @return value ceiled to nearest pixel
     */
    public double snapSizeX(double value) {
        return snapSizeX(value, isSnapToPixel());
    }

    /**
     * If this canvas' snapToPixel property is true, returns a value ceiled
     * to the nearest pixel in the vertical direction, else returns the
     * same value.
     * @param value the size value to be snapped
     * @return value ceiled to nearest pixel
     */
    public double snapSizeY(double value) {
        return snapSizeY(value, isSnapToPixel());
    }

    /**
     * If this canvas' snapToPixel property is true, returns a value rounded
     * to the nearest pixel in the horizontal direction, else returns the
     * same value.
     * @param value the position value to be snapped
     * @return value rounded to nearest pixel
     */
    public double snapPositionX(double value) {
        return snapPositionX(value, isSnapToPixel());
    }

    /**
     * If this canvas' snapToPixel property is true, returns a value rounded
     * to the nearest pixel in the vertical direction, else returns the
     * same value.
     * @param value the position value to be snapped
     * @return value rounded to nearest pixel
     */
    public double snapPositionY(double value) {
        return snapPositionY(value, isSnapToPixel());
    }

    private static double getSnapScaleXImpl(Scene scene) {
        if (scene == null) return 1.0;
        Window window = scene.getWindow();
        if (window == null) return 1.0;
        return window.getRenderScaleX();
    }

    private static double getSnapScaleYImpl(Scene scene) {
        if (scene == null) return 1.0;
        Window window = scene.getWindow();
        if (window == null) return 1.0;
        return window.getRenderScaleY();
    }

    private double getSnapScaleX() {
        return getSnapScaleXImpl(getScene());
    }

    private double getSnapScaleY() {
        return getSnapScaleYImpl(getScene());
    }

    private double scaledRound(double value, double scale) {
        return Math.round(value * scale) / scale;
    }

    private double scaledCeil(double value, double scale) {
        return Math.ceil(value * scale) / scale;
    }

    /**
     * If snapToPixel is true, then the value is rounded using Math.round. Otherwise,
     * the value is simply returned. This method will surely be JIT'd under normal
     * circumstances, however on an interpreter it would be better to inline this
     * method. However the use of Math.round here, and Math.ceil in snapSize is
     * not obvious, and so for code maintenance this logic is pulled out into
     * a separate method.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or rounded based on snapToPixel
     */
    private double snapSpaceX(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleX()) : value;
    }

    private double snapSpaceY(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleY()) : value;
    }

    /**
     * If snapToPixel is true, then the value is ceil'd using Math.ceil. Otherwise,
     * the value is simply returned.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or ceil'd based on snapToPixel
     */
    private double snapSizeX(double value, boolean snapToPixel) {
        return snapToPixel ? scaledCeil(value, getSnapScaleX()) : value;
    }

    private double snapSizeY(double value, boolean snapToPixel) {
        return snapToPixel ? scaledCeil(value, getSnapScaleY()) : value;
    }

    /**
     * If snapToPixel is true, then the value is rounded using Math.round. Otherwise,
     * the value is simply returned.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or rounded based on snapToPixel
     */
    private double snapPositionX(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleX()) : value;
    }

    private double snapPositionY(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleY()) : value;
    }
}
