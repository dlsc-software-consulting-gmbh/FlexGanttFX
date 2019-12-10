/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.Row;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;

import static com.flexganttfx.model.layout.AgendaLayout.LayoutStrategy.PARALLEL_OVERLAPPING;
import static com.flexganttfx.view.util.Position.FIRST;
import static com.flexganttfx.view.util.Position.LAST;
import static com.flexganttfx.view.util.Position.MIDDLE;
import static com.flexganttfx.view.util.Position.ONLY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.requireNonNull;

public final class RowCanvas<R extends Row<?, ?, ?>> extends Canvas {

    private final GraphicsBase<R> graphics;

    private final List<ActivityBounds> activityBounds = new ArrayList<>();

    private Map<Integer, List<ActivityEntry>> agendaColumnMap;

    private Map<LocalDate, List<Activity>> dateActivitiesMap;

    private Map<LocalDate, Map<Activity, Placement<Activity>>> datePlacementsMap;

    private Rectangle2D debugRectangle;

    private RowCanvasBehaviour<?> rowCanvasBehaviour;

    private Instant drawingStartTime;

    private Instant drawingEndTime;

    public RowCanvas(GraphicsBase<R> graphics) {
        requireNonNull(graphics);

        this.graphics = graphics;

        getStyleClass().add("row-canvas");

        widthProperty().addListener(redrawListener);
        heightProperty().addListener(redrawListener);

        rowCanvasBehaviour = new RowCanvasBehaviour<>(this);

        rowProperty().addListener(evt -> draw());

        ChangeListener<ActivityRef<?>> weakActivityRedrawListener = new WeakChangeListener<>(activityRedrawListener);
        graphics.editModeProperty().addListener(new WeakInvalidationListener(editModeListener));
        graphics.hoverActivityProperty().addListener(weakActivityRedrawListener);
        graphics.pressedActivityProperty().addListener(weakActivityRedrawListener);
        graphics.getSelectedActivities().addListener(new WeakListChangeListener<>(selectedActivitiesListener));

        InvalidationListener pseudoStateRedrawListener = observable -> draw();

        hoverProperty().addListener(pseudoStateRedrawListener);
        pressedProperty().addListener(pseudoStateRedrawListener);
        focusedProperty().addListener(pseudoStateRedrawListener);

        connectToTimeline();
        graphics.timelineProperty().addListener(it -> connectToTimeline());

        graphics.canvasBufferProperty().addListener(it -> {
            setTranslateX(0);
            draw();
        });

        graphics.canvasBufferProperty().addListener(it -> randomTranslateX(true));
        randomTranslateX(true);
    }

    private void randomTranslateX(boolean scrollingRight) {
        final double canvasBuffer = graphics.getCanvasBuffer();
        final double offset = Math.random() * canvasBuffer / 4;

        if (scrollingRight) {
            setTranslateX(snapPosition(canvasBuffer - offset));
        } else {
            setTranslateX(snapPosition(-canvasBuffer + offset));
        }
    }

    private void connectToTimeline() {
        Timeline timeline = graphics.getTimeline();

        final ChangeListener<Instant> startTimeListener = (obs, oldTime, newTime) -> {
            double x = timeline.getModel().calculateLocationForTime(oldTime);

            double newTranslateX = getTranslateX() + x;

            if (Math.abs(newTranslateX) < graphics.getCanvasBuffer()) {
                setTranslateX(newTranslateX);

                Instant st = graphics.getTimeAt(0);
                Instant et = graphics.getTimeAt(graphics.getWidth());

                boolean contained = (st.equals(drawingStartTime) || st.isAfter(drawingStartTime)) && (et.equals(drawingEndTime) || et.isBefore(drawingEndTime));

                if (!contained) {
                    draw();
                }
            } else {
                //System.out.println("BANG");
                randomTranslateX((newTranslateX - getTranslateX()) < 0);
                draw();
            }
        };

        timeline.getModel().startTimeProperty().addListener(startTimeListener);

        timeline.modelProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                timeline.getModel().startTimeProperty().removeListener(startTimeListener);
            }

            if (newValue != null) {
                timeline.getModel().startTimeProperty().addListener(startTimeListener);
            }
        });
    }

    private final ChangeListener<ActivityRef<?>> activityRedrawListener = (observable, oldRef, newRef) -> {

        if ((oldRef != null && oldRef.getRow() == getRow())
                || (newRef != null && newRef.getRow() == getRow())) {

            if (observable instanceof ReadOnlyProperty) {
                if (LoggingDomain.RENDERING.isLoggable(Level.FINE)) {
                    LoggingDomain.RENDERING.fine(
                            "redraw because of property change, property = "
                                    + ((ReadOnlyProperty<?>) observable)
                                    .getName());
                }
            }

            draw();
        }
    };

    private final InvalidationListener editModeListener = it -> {
        if (getGraphics().getEditMode().equals(EditMode.NONE)) {
            rowCanvasBehaviour.stopEdit();
        }
    };

    private final ListChangeListener<ActivityRef<?>> selectedActivitiesListener = (
            Change<? extends ActivityRef<?>> change) -> {
        while (change.next()) {
            for (ActivityRef<?> ref : change.getAddedSubList()) {
                if (ref.getRow() == getRow()) {
                    draw();
                    return;
                }
            }
            for (ActivityRef<?> ref : change.getRemoved()) {
                if (ref.getRow() == getRow()) {
                    draw();
                    return;
                }
            }
        }
    };

    public final GraphicsBase<R> getGraphics() {
        return graphics;
    }

    private final ChangeListener<Number> redrawListener = (value, oldSize, newSize) -> draw();

    private final ObjectProperty<R> row = new SimpleObjectProperty<>(this, "row");

    public final ObjectProperty<R> rowProperty() {
        return row;
    }

    public final void setRow(R row) {
        rowProperty().set(row);
    }

    public final R getRow() {
        return rowProperty().get();
    }

    public final TimelineModel<?> getTimelineModel() {
        return graphics.getTimeline().getModel();
    }

    @Override
    public final boolean isResizable() {
        return true;
    }

    @Override
    public final double prefHeight(double width) {
        return getHeight();
    }

    @Override
    public final double prefWidth(double height) {
        return getWidth();
    }

    private boolean safeRendering;

    public final void draw() {
        if (LoggingDomain.RENDERING.isLoggable(Level.FINEST)) {
            LoggingDomain.RENDERING.finest("drawing canvas of row " + getRow());
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

        drawingStartTime = timelineModel.calculateTimeForLocation(0 - graphics.getCanvasBuffer());
        drawingEndTime = timelineModel.calculateTimeForLocation(getWidth() + graphics.getCanvasBuffer());

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
            gc.strokeRect(lookupBounds.getMinX(), lookupBounds.getMinY(),
                    lookupBounds.getWidth(), lookupBounds.getHeight());
        }

        if (agendaColumnMap != null) {
            agendaColumnMap.clear();
        }
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
                List<ActivityBounds> bounds = drawActivity(editedActivity,
                        timelineModel, zoneId, rowHeight, secondPass);

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

                    ZonedDateTime truncatedDateTime = zonedStartDateTime.truncatedTo(DAYS).plus(column, DAYS);

                    x1 = calculateLocation(Instant.from(truncatedDateTime));
                    x2 = calculateLocation(Instant.from(truncatedDateTime.plus(1, DAYS)));

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
                                    LocalDate date = truncatedDateTime
                                            .toLocalDate();
                                    Map<Activity, Placement<Activity>> dateMap = datePlacementsMap
                                            .get(date);

                                    if (dateMap != null) {
                                        Placement placement = dateMap.get(activity);

                                        if (placement != null) {
                                            columnWidth = columnWidth
                                                    / placement.getColumnCount();
                                            x1 += placement.getColumnIndex()
                                                    * columnWidth;

                                            if (layoutStrategy
                                                    .equals(PARALLEL_OVERLAPPING)) {

                                                double offset = Math.min(.5,
                                                        agendaLayout
                                                                .getOverlapOffset());

                                                double extraWidth = columnWidth
                                                        * offset / 2;

                                                if (placement
                                                        .getColumnCount() > 1) {
                                                    columnWidth += extraWidth;
                                                }

                                                if (placement
                                                        .getColumnIndex() > 0) {
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
                                snapPosition(x1), snapPosition(yy1),
                                snapPosition(columnWidth),
                                Math.max(1, (snapPosition(yy2) - snapPosition(yy1) - 1)),
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
                throw new MissingActivityBoundsException(renderer, activity,
                        row, lineIndex);
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
        return getTimelineModel().calculateLocationForTime(startTime) + graphics.getCanvasBuffer() - getTranslateX();
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

    public final List<ActivityBounds> getAllActivityBounds() {
        return activityBounds;
    }

    public final List<ActivityBounds> getAllActivityBounds(double x, double y) {
        List<ActivityBounds> result = new ArrayList<>();
        for (ActivityBounds bounds : activityBounds) {
            if (bounds.contains(x, y)) {
                result.add(bounds);
            }
        }
        return result;
    }

    public final ActivityBounds getActivityBounds(double x, double y) {
        List<ActivityBounds> allBounds = getAllActivityBounds(x, y);

        int s = allBounds.size();
        if (s > 0) {
            return allBounds.get(s - 1);
        }

        return null;
    }

    public final ActivityBounds getActivityBounds(ActivityRef<?> activityRef) {
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

    public final List<ActivityBounds> getActivityBounds(double x, double y, double w, double h) {

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

    public final Layout getLayoutAt(double y) {
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

    public final Rectangle2D getLayoutBoundsAt(double y) {
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
            draw();
        }

        return bounds;
    }

    private final BooleanProperty snapToPixel = new SimpleBooleanProperty(this, "snapToPixel", true);

    public final void setSnapToPixel(boolean snap) {
        snapToPixel.set(snap);
    }

    public final boolean isSnapToPixel() {
        return snapToPixel.get();
    }

    protected double snapPosition(double value) {
        return snapPosition(value, isSnapToPixel());
    }

    protected double snapSpace(double value) {
        return snapSpace(value, isSnapToPixel());
    }

    protected double snapSize(double value) {
        return snapSize(value, isSnapToPixel());
    }

    /**
     * If snapToPixel is true, then the value is rounded using Math.round.
     * Otherwise, the value is simply returned.
     *
     * @param value       The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or rounded based on snapToPixel
     */
    private double snapSpace(double value, boolean snapToPixel) {
        return snapToPixel ? Math.round(value) : value;
    }

    /**
     * If snapToPixel is true, then the value is ceil'd using Math.ceil.
     * Otherwise, the value is simply returned.
     *
     * @param value       The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or ceil'd based on snapToPixel
     */
    private double snapSize(double value, boolean snapToPixel) {
        return snapToPixel ? Math.ceil(value) : value;
    }

    /**
     * If snapToPixel is true, then the value is rounded using Math.round.
     * Otherwise, the value is simply returned.
     *
     * @param value       The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or rounded based on snapToPixel
     */
    private double snapPosition(double value, boolean snapToPixel) {
        return snapToPixel ? Math.round(value) + .5 : value;
    }
}
