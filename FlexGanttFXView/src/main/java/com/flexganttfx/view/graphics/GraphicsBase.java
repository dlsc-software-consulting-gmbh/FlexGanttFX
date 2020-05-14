/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.Calendar;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.ActivityBase;
import com.flexganttfx.model.activity.ChartActivity;
import com.flexganttfx.model.activity.ChartActivityBase;
import com.flexganttfx.model.activity.CompletableActivity;
import com.flexganttfx.model.activity.CompletableActivityBase;
import com.flexganttfx.model.activity.MutableActivity;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.activity.MutableChartActivityBase;
import com.flexganttfx.model.activity.MutableCompletableActivityBase;
import com.flexganttfx.model.activity.MutableHighLowChartActivityBase;
import com.flexganttfx.model.calendar.CalendarActivity;
import com.flexganttfx.model.calendar.CalendarActivityBase;
import com.flexganttfx.model.calendar.WeekendCalendar;
import com.flexganttfx.model.dateline.ChronoUnitGrid;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.repository.RepositoryEvent;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.IntervalTree;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.layer.AgendaLinesLayer;
import com.flexganttfx.view.graphics.layer.CalendarLayer;
import com.flexganttfx.view.graphics.layer.ChartLinesLayer;
import com.flexganttfx.view.graphics.layer.DSTLineLayer;
import com.flexganttfx.view.graphics.layer.GridLinesLayer;
import com.flexganttfx.view.graphics.layer.HoverTimeIntervalLayer;
import com.flexganttfx.view.graphics.layer.InnerLinesLayer;
import com.flexganttfx.view.graphics.layer.LayoutLayer;
import com.flexganttfx.view.graphics.layer.NowLineLayer;
import com.flexganttfx.view.graphics.layer.RowLayer;
import com.flexganttfx.view.graphics.layer.SelectedTimeIntervalsLayer;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import com.flexganttfx.view.graphics.layer.ZoomTimeIntervalLayer;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.graphics.renderer.ChartActivityRenderer;
import com.flexganttfx.view.graphics.renderer.CompletableActivityRenderer;
import com.flexganttfx.view.graphics.renderer.CurvedLinkRenderer;
import com.flexganttfx.view.graphics.renderer.LinkRenderer;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.FlexGanttFXControl;
import com.flexganttfx.view.util.Messages;
import com.flexganttfx.view.util.Position;
import com.sun.javafx.css.converters.PaintConverter;
import impl.com.flexganttfx.skin.graphics.GraphicsBaseSkin;
import impl.com.flexganttfx.skin.graphics.LinksCanvas;
import impl.com.flexganttfx.skin.graphics.RowPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.Duration;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Objects.requireNonNull;

/**
 * The graphics view control is responsible for the rendering of activities and
 * system layers, the editing of activities, the event notifications, the hit
 * detection, system layer management, and for context menu support.
 * <h2>Rendering</h2>
 * The graphics view uses the canvas API of JavaFX. This is due to the complex
 * nature of a Gantt chart and due to the large data volumes often observed in
 * Gantt charts. Directly rendering large quantities of activities into a bitmap
 * is much faster than constantly updating the scene graph and reapplying CSS
 * styling. FlexGanttFX implements a pluggable renderer architecture where
 * renderer instances can be mapped to activity types, very similar to the way
 * Swing was doing it. The following code is an example of how to register a
 * custom renderer for a given "Flight" activity type. Please note that the
 * graphics view is capable of displaying activities in three different layouts,
 * hence the layout type must also be passed to the method.
 * <p>
 * <code>
 * setActivityRenderer(Flight.class, GanttLayout.class, new FlightRenderer());
 * </code>
 * </p>
 * <h2>System Layers</h2>
 * Activities are not the only thing that need to be rendered. There are also
 * the current time ("now"), grid lines, inner lines, agenda / chart lines,
 * etc... All of these things are rendered by so-called system layers (see
 * {@link SystemLayer}). The graphics view manages two lists of these layers.
 * One list for background layers ({@link #getBackgroundSystemLayers()}) and one
 * list for foreground layers ({@link #getForegroundSystemLayers()}).
 * <br>
 * Background layers are drawn "behind" activities, foreground layers are drawn
 * "in front of" activities. Each one of these lists are already pre-populated
 * but can be changed by the application. For more information on the available
 * system layers, please refer to their individual documentation.
 * <br>
 * System layers can be turned on and off directly via the API of
 * {@link GraphicsBase}. There is a boolean property for each layer that ships
 * with FlexGanttFX. The value of these properties can be set by calling the
 * methods that follow the pattern <code>setShowXYZLayer</code>. System layers
 * that are controlled like this will appear and disappear with a fade in / fade
 * out animation, while calling {@link SystemLayer#setVisible(boolean)} directly
 * will be without any animation.
 * <h2>Editing Customization</h2>
 * Two different callbacks are used to control the editing behaviour of
 * activities. The first maps a mouse event / mouse location to an
 * {@link EditMode} and can be registered by calling
 * {@link #setEditModeCallback(Class, Class, Callback)}. The second callback is
 * used to determine whether a given editing mode / operation can be applied to
 * an activity at all. This callback is registered by calling
 * {@link #setActivityEditingCallback(Class, Callback)}. Most applications will
 * only need to work with the second callback and keep the defaults for the edit
 * mode locations (for example: right edge used to change end time, left edge
 * used to change start time).
 * <h2>Notifications / Events</h2>
 * Events of type {@link ActivityEvent} are sent whenever the user performs a
 * change inside the graphics view. Applications that want to receive these
 * events can either call any one of the <code>setOnActivityXYZEvent()</code>
 * methods or by adding an event handler directly via
 * <code>addEventHandler(ActionEvent.ACTIVITY_XYZ, ...)</code>. Events are fired
 * while the change is being performed and once it has been completed. For this
 * the {@link ActivityEvent} class lists event types with the two different
 * endings CHANGING and CHANGED.
 * <h2>Filtering</h2>
 * The data displayed by the graphics control can be filtered in two ways: first
 * by showing / hiding rows, second by showing / hiding activities. Row filtering
 * is done by the parent GanttChart controls while activity filtering is done by
 * the graphics control via an activity filter predicate:
 * <p>
 * <code>
 * setActivityFilter(Predicate&lt;Activity&gt; filter);
 * </code>
 * </p>
 * <h2>Finding / Lookup / Hitpoint Detection</h2>
 * The graphics view provides support for finding out information about a given
 * position. Activities can be found by calling
 * {@link #getActivityBoundsAt(double, double)} or
 * {@link #getActivityRefAt(double, double)}. The time at an x-coordinate can be
 * looked up by calling {@link #getTimeAt(double)}. The opposite direction is
 * also available: a location can be found for a given time by calling
 * {@link #getLocation(Instant)}.
 * <h2>Context Menu</h2>
 * Context menus can be set on any control in JavaFX but due to the complexitiy
 * of the graphics view it does make sense to provide additional built-in
 * support. By calling {@link #setContextMenuCallback(Callback)} a context menu
 * specific callback can be registered with the graphics control. This callback
 * will be invoked when the user triggers the context menu. A callback parameter
 * object (see {@link ContextMenuParameter}) will be passed to the callback
 * already populated with the most important values that might be relevant for
 * building a context menu.
 *
 * @param <R> the type of the rows shown by the graphics
 * @since 1.0
 */
public abstract class GraphicsBase<R extends Row<?, ?, ?>> extends FlexGanttFXControl {

    private static final String DEFAULT_STYLE_CLASS = "graphics";

    private Instant zoomTime;

    private final InvalidationListener redrawListener = observable -> {
        if (observable instanceof ReadOnlyProperty) {
            if (LoggingDomain.RENDERING.isLoggable(Level.FINE)) {
                LoggingDomain.RENDERING.fine("redraw because of property change, property = " + ((ReadOnlyProperty<?>) observable).getName());
            }
        }
        redraw("property in GraphicsBase has changed");
    };

    private final WeakInvalidationListener weakRedrawListener = new WeakInvalidationListener(redrawListener);

    private final ChangeListener<Instant> redrawNowListener = new ChangeListener<Instant>() {
        @Override
        public void changed(ObservableValue<? extends Instant> observable, Instant oldNow, Instant newNow) {

            Instant visibleStart = getTimeline().getVisibleStartTime();
            Instant visibleEnd = getTimeline().getVisibleEndTime();

            if (visibleStart != null && visibleEnd != null) {
                if (inRange(oldNow, visibleStart, visibleEnd) || inRange(newNow, visibleStart, visibleEnd)) {
                    redraw("'now' changed");
                }
            }
        }

        private boolean inRange(Instant time, Instant visibleStart, Instant visibleEnd) {
            return visibleStart.equals(time) || visibleEnd.equals(time) || (visibleStart.isBefore(time) && visibleEnd.isAfter(time));
        }
    };

    private final WeakChangeListener<Instant> weakRedrawNowListener = new WeakChangeListener<>(redrawNowListener);

    /**
     * Constructs a new graphics view and initializes the following:
     * <ul>
     * <li>Virtual grid settings (1, 5, 10, 15, 30, 60 Minutes)</li>
     * <li>Activity renderers for several of the default model classes.</li>
     * <li>Edit mode callbacks for several of the default model classes.</li>
     * <li>Activity editing callbacks.</li>
     * <li>Background and foreground layers.</li>
     * <li>Calendars (e. g. weekend calendar).</li>
     * </ul>
     *
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public GraphicsBase() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);

        // Virtual grids
        ObservableList<VirtualGrid<?>> grids = getVirtualGrids();
        grids.add(new ChronoUnitGrid(Messages.getString("GraphicsBase.GRID_MINUTES_1"), MINUTES, 1));
        grids.add(new ChronoUnitGrid(Messages.getString("GraphicsBase.GRID_MINUTES_5"), MINUTES, 5));
        grids.add(new ChronoUnitGrid(Messages.getString("GraphicsBase.GRID_MINUTES_10"), MINUTES, 10));
        grids.add(new ChronoUnitGrid(Messages.getString("GraphicsBase.GRID_MINUTES_15"), MINUTES, 15));
        grids.add(new ChronoUnitGrid(Messages.getString("GraphicsBase.GRID_MINUTES_30"), MINUTES, 30));
        grids.add(new ChronoUnitGrid(Messages.getString("GraphicsBase.GRID_MINUTES_60"), MINUTES, 60));

        timelineProperty().addListener((obs, oldTimeline, newTimeline) -> timelineChanged(oldTimeline, newTimeline));

        // pinch zoom

        addEventHandler(ZoomEvent.ZOOM_STARTED, event -> {
            Timeline timeline = getTimeline();
            TimelineModel<?> model = timeline.getModel();
            zoomTime = model.calculateTimeForLocation(event.getX());
        });

        addEventHandler(ZoomEvent.ZOOM, event -> {
            Timeline timeline = getTimeline();
            timeline.zoom(Math.abs(event.getZoomFactor() - 1), event.getZoomFactor() > 1, zoomTime);
        });

        // scroll zoom and horizontal scrolling with SHIFT
        addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isShortcutDown()) {
                Timeline timeline = getTimeline();
                TimelineModel<?> model = timeline.getModel();
                zoomTime = model.calculateTimeForLocation(event.getX());
                timeline.zoom(.1, event.getDeltaY() > 0, zoomTime);
                event.consume();
            } else if (event.isShiftDown()) {
                Timeline timeline = getTimeline();
                TimelineModel<?> model = timeline.getModel();

                Instant newTime = model.getStartTime();

                if (event.getDeltaY() < 0) {
                    newTime = model.calculateTimeForLocation(20);
                } else if (event.getDeltaY() > 0) {
                    newTime = model.calculateTimeForLocation(-20);
                }

                boolean animated = timeline.isMoveAnimated();
                timeline.setMoveAnimated(false);
                timeline.showTime(newTime);
                timeline.setMoveAnimated(animated);

                event.consume();
            }
        });

        debugModeProperty().addListener(weakRedrawListener);

        selectionModeProperty().addListener(observable -> getSelectedActivities().clear());

        setContextMenuCallback(GraphicsViewMenu::new);

        // GanttLayout renderers

        setActivityRenderer(ActivityBase.class, GanttLayout.class, new ActivityRenderer<ActivityBase<?>>(this, "Activities (Gantt Layout)"));
        setActivityRenderer(CompletableActivityBase.class, GanttLayout.class, new CompletableActivityRenderer<>(this, "Completable Activity (Gantt Layout)"));
        setActivityRenderer(CalendarActivityBase.class, GanttLayout.class, new ActivityRenderer<>(this, "Calendars (Gantt Layout)"));

        // ChartLayout renderers

        setActivityRenderer(ActivityBase.class, ChartLayout.class, new ActivityRenderer<ActivityBase<?>>(this, "Activities (Chart Layout)"));
        setActivityRenderer(CompletableActivityBase.class, ChartLayout.class, new CompletableActivityRenderer<>(this, "Completable Activity (Chart Layout)"));
        setActivityRenderer(ChartActivityBase.class, ChartLayout.class, new ChartActivityRenderer<>(this, "Chart Activity (Chart Layout)"));
        setActivityRenderer(CalendarActivityBase.class, ChartLayout.class, new ActivityRenderer<>(this, "Calendars (Chart Layout)"));

        // AgendaLayout renderers

        setActivityRenderer(MutableActivityBase.class, AgendaLayout.class, new ActivityRenderer<>(this, "Activities (Agenda Layout)"));

        // Activity link renderer
        setLinkRenderer(ActivityLink.class, new CurvedLinkRenderer<>(this, "Default Link Renderer"));

        // Edit mode controllers

        /*
         * Normal activities, capacities, and high low activities all use the
         * same editing behaviour as long as they are being displayed with a
         * gantt layout.
         */
        setEditModeCallback(MutableActivityBase.class, GanttLayout.class, new ActivityEditModeCallback());
        setEditModeCallback(MutableChartActivityBase.class, GanttLayout.class, new ActivityEditModeCallback());
        setEditModeCallback(MutableHighLowChartActivityBase.class, GanttLayout.class, new ActivityEditModeCallback());
        setEditModeCallback(MutableCompletableActivityBase.class, GanttLayout.class, new CompletableActivityEditModeCallback());

        setEditModeCallback(MutableActivityBase.class, ChartLayout.class, new ActivityEditModeCallback());
        setEditModeCallback(MutableCompletableActivityBase.class, ChartLayout.class, new CompletableActivityEditModeCallback());
        setEditModeCallback(MutableChartActivityBase.class, ChartLayout.class, new ChartActivityEditModeCallback());
        setEditModeCallback(MutableHighLowChartActivityBase.class, ChartLayout.class, new ChartHighLowEditModeCallback());
        setEditModeCallback(MutableActivityBase.class, AgendaLayout.class, new ActivityInAgendaLayoutEditModeCallback());

        setActivityEditingCallback(MutableActivityBase.class, input -> true);
        setActivityEditingCallback(MutableChartActivityBase.class, input -> true);
        setActivityEditingCallback(MutableCompletableActivityBase.class, input -> true);

        setRowDragAndDropCallback(Row.class, param -> true);

        rendererLayoutMap.addListener(weakRedrawListener);

        getBackgroundSystemLayers().add(new RowLayer<>(this));
        getBackgroundSystemLayers().add(new CalendarLayer<>(this));
        getBackgroundSystemLayers().add(new ChartLinesLayer<>(this));
        getBackgroundSystemLayers().add(new AgendaLinesLayer<>(this));
        getBackgroundSystemLayers().add(new InnerLinesLayer<>(this));
        getForegroundSystemLayers().add(new NowLineLayer<>(this));
        getBackgroundSystemLayers().add(new HoverTimeIntervalLayer<>(this));
        getBackgroundSystemLayers().add(new SelectedTimeIntervalsLayer<>(this));
        getBackgroundSystemLayers().add(new ZoomTimeIntervalLayer<>(this));
        getBackgroundSystemLayers().add(new GridLinesLayer<>(this));
        getBackgroundSystemLayers().add(new DSTLineLayer<>(this));
        getBackgroundSystemLayers().addListener(weakRedrawListener);

        getForegroundSystemLayers().add(new LayoutLayer<>(this));
        getForegroundSystemLayers().addListener(weakRedrawListener);

        addRedrawObservable(showGridLineLayer);
        addRedrawObservable(showNowLineLayer);
        addRedrawObservable(showCalendarLayer);
        addRedrawObservable(showInnerLinesLayer);
        addRedrawObservable(showHoverTimeIntervalLayer);
        addRedrawObservable(showSelectedTimeIntervalsLayer);
        addRedrawObservable(showZoomTimeIntervalLayer);
        addRedrawObservable(showAgendaLinesLayer);
        addRedrawObservable(showChartLinesLayer);
        addRedrawObservable(showRowHeaders);
        addRedrawObservable(maxGridLevel);
        addRedrawObservable(rowHeadersWidth);
        addRedrawObservable(activityFilter);

        rowEditingModeProperty().addListener(it -> {
            switch (getRowEditingMode()) {
                case NONE:
                case SINGLE_ROW:
                    (new ArrayList<>(getRowsEditing()))
                            .forEach(this::stopRowEditing);
                    break;
                case MULTIPLE_ROWS:
                    break;
            }
        });

        timeline.addListener((observable, oldTimeline, newTimeline) -> {
            if (oldTimeline != null) {
                removeRedrawObservable(
                        oldTimeline.getDateline().firstDayOfWeekProperty());
            }
            if (newTimeline != null) {
                addRedrawObservable(
                        newTimeline.getDateline().firstDayOfWeekProperty());
            }
        });

        //
        // Highlighting
        //

        InvalidationListener highlightListener = observable -> {
            if (!(getHighlightedActivities().isEmpty() && getHighlightedRows().isEmpty())) {
                startHighlighting();
            } else {
                stopHighlighting();
            }
        };

        getHighlightedActivities().addListener(highlightListener);
        getHighlightedRows().addListener(highlightListener);

        highlightedProperty().addListener(weakRedrawListener);

        //
        // Layers
        //

        getLayers().addListener(weakRedrawListener);

        //
        // Calendars
        //

        getCalendars().addListener(weakRedrawListener);
        getCalendars().addListener((Change<? extends Calendar<?>> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Calendar<?> calendar : change.getAddedSubList()) {
                        calendar.visibleProperty().addListener(weakRedrawListener);
                        calendar.addEventHandler(weakRepositoryListener);
                    }
                } else if (change.wasRemoved()) {
                    for (Calendar<?> calendar : change.getRemoved()) {
                        calendar.visibleProperty().removeListener(weakRedrawListener);
                        calendar.removeEventHandler(weakRepositoryListener);
                    }
                }
            }
        });

        getCalendars().add(new WeekendCalendar());

        final ListChangeListener<Layer> layerListListener = change -> {
            while (change.next()) {
                change.getRemoved().forEach(this::removeListenersFromLayer);

                change.getAddedSubList().forEach(this::addListenersToLayer);
            }
        };

        getLayers().addListener(layerListListener);

        getRows().addListener((Observable it) -> Platform.runLater(() -> redraw("row list changed")));

        automaticRedraw.addListener(weakRedrawListener);

        layers.forEach(this::addListenersToLayer);

        autoGridEnabled.addListener(it -> updateGridProperty());
        virtualGrid.addListener(it -> updateGridProperty());

        /*
         * We are "abusing" the properties map to pass new values of read-only
         * properties from the skin to the control.
         */
        getProperties().addListener((javafx.collections.MapChangeListener.Change<?, ?> change) -> {
            if (change.getKey().equals("com.flexganttfx.currenteditmode")) {
                if (change.getValueAdded() != null) {
                    EditMode mode = (EditMode) change.getValueAdded();
                    editMode.set(mode);
                } else {
                    editMode.set(EditMode.NONE);
                }
            } else if (change.getKey().equals("com.flexganttfx.currentlyeditedactivity")) {
                Object valueAdded = change.getValueAdded();
                if (valueAdded != null) {
                    if (valueAdded instanceof ActivityRef) {
                        ActivityRef<?> activity = (ActivityRef<?>) change.getValueAdded();
                        editedActivity.set(activity);
                    } else {
                        editedActivity.set(null);
                    }
                } else {
                    editedActivity.set(null);
                }
            } else if (change.getKey().equals("com.flexganttfx.draganddropinfo")) {
                Object valueAdded = change.getValueAdded();
                if (valueAdded != null) {
                    if (valueAdded instanceof DragAndDropInfo) {
                        DragAndDropInfo info = (DragAndDropInfo) change
                                .getValueAdded();
                        dragAndDropInfo.set(info);
                    } else {
                        dragAndDropInfo.set(null);
                    }
                    /*
                     * Do NOT remove value from map as this would trigger the
                     * code below and set the info property to null.
                     */
                } else {
                    dragAndDropInfo.set(null);
                }
            } else if (change.getKey().equals("com.flexganttfx.lassoActive")) {
                Object valueAdded = change.getValueAdded();
                if (valueAdded != null) {
                    if (valueAdded instanceof Boolean) {
                        lassoActive.set((Boolean) valueAdded);
                    } else {
                        lassoActive.set(Boolean.FALSE);
                    }
                } else {
                    lassoActive.set(Boolean.FALSE);
                }
            } else if (change.getKey().equals("com.flexganttfx.hover.row")) {
                Object valueAdded = change.getValueAdded();
                if (valueAdded != null) {
                    if (valueAdded instanceof Row<?, ?, ?>) {
                        hoverRow.set((R) valueAdded);
                    } else {
                        hoverRow.set(null);
                    }
                } else {
                    hoverRow.set(null);
                }
            } else if (change.getKey().equals("com.flexganttfx.hover.activity")) {
                Object valueAdded = change.getValueAdded();
                if (valueAdded != null) {
                    if (valueAdded instanceof ActivityRef) {
                        hoverActivity.set((ActivityRef<?>) valueAdded);
                    } else {
                        hoverActivity.set(null);
                    }
                } else {
                    hoverActivity.set(null);
                }
            } else if (change.getKey().equals("com.flexganttfx.hover.layout")) {
                Object valueAdded = change.getValueAdded();
                if (valueAdded != null) {
                    if (valueAdded instanceof Layout) {
                        hoverLayout.set((Layout) valueAdded);
                    } else {
                        hoverLayout.set(null);
                    }
                } else {
                    hoverLayout.set(null);
                }
            } else if (change.getKey().equals("com.flexganttfx.pressed.activity")) {
                Object valueAdded = change.getValueAdded();
                if (valueAdded != null) {
                    if (valueAdded instanceof ActivityRef) {
                        pressedActivity.set((ActivityRef<?>) valueAdded);
                    } else {
                        pressedActivity.set(null);
                    }
                } else {
                    pressedActivity.set(null);
                }
            }
        });

        addEventHandler(ActivityEvent.ACTIVITY_CHANGE, evt -> {
            final ActivityRef<?> activityRef = evt.getActivityRef();
            if (evt.getOldTimeInterval() != null) {
                final Collection<ActivityLink> links = getLinks().getIntersectingObjects(evt.getOldTimeInterval());
                for (ActivityLink link : links) {
                    if (link.getSourceActivityRef().equals(activityRef) || link.getTargetActivityRef().equals(activityRef)) {
                        getLinks().remove(link);
                        getLinks().add(link);
                    }
                }
            }
        });

        setRowHeaderFactory(graphics -> new ScaleRowHeader<>(this));

//        final Runnable drawRunnable = () -> {
//            for (RowPane<R> pane : getRowPanes()) {
//                if (pane.getCanvas().isDirty()) {
//                    pane.getCanvas().draw();
//                }
//            }
//        };
//
//        final TKPulseListener tkPulseListener = () -> drawRunnable.run();
//
//        sceneProperty().addListener((obs, oldScene, newScene) -> {
//            if (oldScene != null) {
//                Toolkit.getToolkit().removeSceneTkPulseListener(tkPulseListener);
//            }
//
//            if (newScene != null) {
//                Toolkit.getToolkit().addSceneTkPulseListener(tkPulseListener);
//            }
//        });
    }

    @Override
    public String getUserAgentStylesheet() {
        return super.getUserAgentStylesheet(GraphicsBase.class, "graphics.css");
    }

    // Row filter support.

    private final ObjectProperty<Predicate<R>> rowFilter = new SimpleObjectProperty<>(this, "rowFilter", row -> true);

    /**
     * A predicate used to filter the rows.
     *
     * @return the filter predicate
     */
    public final ObjectProperty<Predicate<R>> rowFilterProperty() {
        return rowFilter;
    }

    /**
     * Sets the value of {@link #rowFilterProperty}.
     *
     * @param predicate the filter predicate
     */
    public final void setRowFilter(Predicate<R> predicate) {
        rowFilter.set(predicate);
    }

    /**
     * Returns the value of {@link #rowFilterProperty}.
     *
     * @return the filter predicate
     */
    public final Predicate getRowFilter() {
        return rowFilter.get();
    }

    // Activity filter support.

    private final ObjectProperty<Predicate<Activity>> activityFilter = new SimpleObjectProperty<>(this, "activityFilter");

    /**
     * A property used to store a filter function, which determines if an activity will be rendered or not.
     * An activity will be drawn if the function returns "true".
     *
     * @return the predicate / the filter function for activities
     * @since 1.6
     */
    public final ObjectProperty<Predicate<Activity>> activityFilterProperty() {
        return activityFilter;
    }

    /**
     * Returns the value of {@link #activityFilterProperty()}.
     *
     * @return the predicate / the filter function for activities
     * @since 1.6
     */
    public final Predicate<Activity> getActivityFilter() {
        return activityFilter.get();
    }

    /**
     * Sets the value of {@link #activityFilterProperty()}.
     *
     * @param filter the filter function
     * @since 1.6
     */
    public final void setActivityFilter(Predicate<Activity> filter) {
        activityFilter.set(filter);
    }

    private final BooleanProperty lassoEnabled = new SimpleBooleanProperty(this, "lassoEnabled", true);

    /**
     * A property used to control whether the user can use the lasso for selecting multiple
     * activities at once.
     *
     * @return the lasso enabled property
     * @since 1.6
     */
    public final BooleanProperty lassoEnabledProperty() {
        return lassoEnabled;
    }

    /**
     * Sets the value of {@link #lassoEnabledProperty()}.
     *
     * @param enabled if true the lasso will be usable by the user
     * @since 1.6
     */
    public final void setLassoEnabled(boolean enabled) {
        lassoEnabled.set(enabled);
    }

    /**
     * Returns the value of {@link #lassoEnabledProperty()}.
     *
     * @return true if the user can use the lasso
     * @since 1.6
     */
    public final boolean isLassoEnabled() {
        return lassoEnabled.get();
    }

    // Automatic redraw support.

    private final BooleanProperty automaticRedraw = new SimpleBooleanProperty(this, "automaticRedraw", true);

    /**
     * A property used to determine if the graphics will be redrawn whenever the
     * data in any of the activity repository changes. The default value is
     * true. Applications can use this property to disable the redrawing when
     * they know that they have to add a lot of activities but do not want the
     * chart to perform a lot of redraws. The graphics will be redrawn right
     * away when the value of this property changes from true to false or vice
     * versa.
     *
     * @return the automatic redraw property
     * @since 1.5
     */
    public final ReadOnlyBooleanProperty automaticRedrawProperty() {
        return automaticRedraw;
    }

    /**
     * Returns the value of {@link #automaticRedrawProperty()}.
     *
     * @return true if automatic redrawing will be performed (default)
     * @since 1.5
     */
    public final boolean isAutomaticRedraw() {
        return automaticRedraw.get();
    }

    /**
     * Sets the value of {@link #automaticRedrawProperty()}.
     *
     * @param automatic if true then the graphics redraw after every repository change
     *                  event
     * @since 1.5
     */
    public final void setAutomaticRedraw(boolean automatic) {
        automaticRedraw.set(automatic);
    }

    private void removeListenersFromLayer(Layer layer) {
        layer.visibleProperty().removeListener(weakLayerVisibilityListener);
        layer.opacityProperty().removeListener(weakRedrawListener);
        layer.nameProperty().removeListener(weakRedrawListener);
        layer.fadeInOutOpacityProperty().removeListener(weakRedrawListener);
    }

    private void addListenersToLayer(Layer layer) {
        if (!layer.isVisible()) {
            layer.setFadeInOutOpacity(0);
        }
        layer.visibleProperty().addListener(weakLayerVisibilityListener);
        layer.opacityProperty().addListener(weakRedrawListener);
        layer.nameProperty().addListener(weakRedrawListener);
        layer.fadeInOutOpacityProperty().addListener(weakRedrawListener);
    }

    private final DoubleProperty canvasBuffer = new SimpleDoubleProperty(this, "canvasBuffer", 250);

    public final double getCanvasBuffer() {
        return canvasBuffer.get();
    }

    /**
     * A canvas buffer size that is larger than zero increases the rendering performance
     * of the Gantt chart substantially as fewer repaints of each row's canvas are needed.
     *
     * @return the canvas buffer size (default is 250 pixel)
     */
    public final DoubleProperty canvasBufferProperty() {
        return canvasBuffer;
    }

    public final void setCanvasBuffer(double canvasBuffer) {
        this.canvasBuffer.set(canvasBuffer);
    }

    private final ChangeListener<Instant> startTimeChangedListener = (obs, oldTime, newTime) -> redraw("start time changed", oldTime);

    private final WeakChangeListener weakStartTimeChangedListener = new WeakChangeListener(startTimeChangedListener);

    private final ChangeListener<TimelineModel<?>> timelineModelChangedListener = (observable, oldModel, newModel) -> {

        if (oldModel != null) {
            oldModel.nowProperty().removeListener(weakRedrawNowListener);
            removeRedrawObservable(oldModel.millisPerPixelProperty());
        }

        if (newModel != null) {
            newModel.nowProperty().addListener(weakRedrawNowListener);
            addRedrawObservable(newModel.millisPerPixelProperty());
        }
    };

    private final WeakChangeListener<TimelineModel<?>> weakTimelineModelChangedListener = new WeakChangeListener<>(timelineModelChangedListener);

    private void timelineChanged(Timeline oldTimeline, Timeline newTimeline) {
        if (oldTimeline != null) {
            disconnectFromTimeline(oldTimeline);
        }

        if (newTimeline != null) {
            connectToTimeline(newTimeline);
        }
    }

    private void disconnectFromTimeline(Timeline timeline) {
        timeline.getModel().startTimeProperty().removeListener(weakStartTimeChangedListener);
        timeline.getModel().nowProperty().removeListener(weakRedrawNowListener);
        timeline.modelProperty().removeListener(weakTimelineModelChangedListener);

        // IMPORTANT: do not "unbind" the offset() property as this disconnects the timeline's
        // offset property from the timeline model's offset property.

        removeRedrawObservable(timeline.getModel().millisPerPixelProperty());

        // dateline (un)listening

        Dateline dateline = timeline.getDateline();
        removeRedrawObservable(dateline.primaryTemporalUnitProperty());
        removeRedrawObservable(dateline.hoverTimeIntervalProperty());
        removeRedrawObservable(dateline.zoneIdProperty());
        removeRedrawObservable(dateline.selectedTimeIntervalProperty());

        dateline.getSelectedIntervals().removeListener(weakRedrawListener);

        // eventline (un)listening

        Eventline eventline = timeline.getEventline();
        final SingleRowGraphics<Row<?, ?, ?>> eventlineGraphics = eventline.getGraphics();
        eventlineGraphics.rowHeadersWidthProperty().unbind();
        eventlineGraphics.showRowHeadersProperty().unbind();
    }

    private void connectToTimeline(Timeline timeline) {
        timeline.getModel().startTimeProperty().addListener(weakStartTimeChangedListener);
        timeline.getModel().nowProperty().addListener(weakRedrawNowListener);
        timeline.modelProperty().addListener(weakTimelineModelChangedListener);
        timeline.offsetProperty().bind(Bindings.createDoubleBinding(() -> isShowRowHeaders() ? getRowHeadersWidth() : 0, rowHeadersWidthProperty(), showRowHeadersProperty()));

        addRedrawObservable(timeline.getModel().millisPerPixelProperty());

        // dateline listening

        final Dateline dateline = timeline.getDateline();
        addRedrawObservable(dateline.primaryTemporalUnitProperty());
        addRedrawObservable(dateline.hoverTimeIntervalProperty());
        addRedrawObservable(dateline.zoneIdProperty());
        addRedrawObservable(dateline.selectedTimeIntervalProperty());

        dateline.getSelectedIntervals().addListener(weakRedrawListener);

        // eventline listening

        final Eventline eventline = timeline.getEventline();
        final SingleRowGraphics<Row<?, ?, ?>> eventlineGraphics = eventline.getGraphics();
        if (eventlineGraphics != this) { // check, or we get a stack overflow
            eventlineGraphics.rowHeadersWidthProperty().bind(rowHeadersWidthProperty());
            eventlineGraphics.showRowHeadersProperty().bind(showRowHeadersProperty());
        }
    }

    private void updateGridProperty() {
        gridEnabled.set(isAutoGridEnabled() || getVirtualGrid() != null);
    }

    private final EventHandler<RepositoryEvent> repositoryListener = evt -> {
        /*
         * Do not redraw immediately after each repository event if automatic redraw
         * is set to false. Can be used to fine-tune application when adding a lot of data
         * in a batch.
         */
        if (isAutomaticRedraw()) {
            redraw("repository listener fired");
        }
    };

    private final WeakEventHandler<RepositoryEvent> weakRepositoryListener = new WeakEventHandler<>(repositoryListener);

    private void addRedrawObservable(Observable property) {
        property.addListener(weakRedrawListener);
    }

    private void removeRedrawObservable(Observable property) {
        property.removeListener(weakRedrawListener);
    }

    // Lasso active support.

    private final ReadOnlyBooleanWrapper lassoActive = new ReadOnlyBooleanWrapper(this, "lassoActive", false);

    /**
     * A boolean property used to indicate whether the lasso selection tool is
     * currently in use or not.
     *
     * @return true if the user is currently performing a lasso selection
     * operation
     * @since 1.0
     */
    public final ReadOnlyBooleanProperty lassoActiveProperty() {
        return lassoActive.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #lassoActiveProperty()}.
     *
     * @return true if the user is currently performing a lasso selection
     * operation.
     * @since 1.0
     */
    public final boolean isLassoActive() {
        return lassoActiveProperty().get();
    }

    // Lasso grid snapped support.

    private final BooleanProperty lassoSnapsToGrid = new SimpleBooleanProperty(this, "lassoSnapsToGrid", false);

    /**
     * A boolean property used to indicate whether the lasso selection tool is
     * using the currently active grid settings.
     *
     * @return the lasso snaps property
     * @since 1.1
     */
    public final BooleanProperty lassoSnapsToGridProperty() {
        return lassoSnapsToGrid;
    }

    /**
     * Returns the value of {@link #lassoSnapsToGridProperty()}.
     *
     * @return true if the lasso is obeying the current virtual grid settings
     * @since 1.1
     */
    public final boolean isLassoSnapsToGrid() {
        return lassoSnapsToGridProperty().get();
    }

    /**
     * Sets the value of {@link #lassoSnapsToGridProperty()}.
     *
     * @param snaps if true the lasso will obey the grid
     * @since 1.1
     */
    public final void setLassoSnapsToGrid(boolean snaps) {
        lassoSnapsToGridProperty().set(snaps);
    }

    private final LayerVisibilityListener layerVisibilityListener = new LayerVisibilityListener();

    private final WeakChangeListener<Boolean> weakLayerVisibilityListener = new WeakChangeListener<>(layerVisibilityListener);

    private class LayerVisibilityListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            Property<?> property = (Property<?>) observable;
            Layer layer = (Layer) property.getBean();

            if (newValue) {
                fade(layer, getOpacity());
            } else {
                fade(layer, 0);
            }
        }

        private void fade(Layer layer, double opacityTarget) {
            if (isFadeInOutVisibilityChanges()) {
                KeyValue keyValue = new KeyValue(
                        layer.fadeInOutOpacityProperty(), opacityTarget);
                KeyFrame keyFrame = new KeyFrame(
                        Duration.millis(
                                getFadeInOutVisibilityChangesDuration()),
                        keyValue);
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                        keyFrame);
                timeline.play();
            } else {
                layer.setFadeInOutOpacity(opacityTarget);
            }
        }
    }

    private final IntervalTree<ActivityLink> links = new IntervalTree<>();

    /**
     * Returns the interval tree that is used to store all activity links of the model.
     *
     * @return a list of activity links
     * @since 1.0
     */
    public final IntervalTree<ActivityLink> getLinks() {
        return links;
    }

    private final ObservableList<Layer> layers = FXCollections.observableArrayList();

    /**
     * Returns the list that is used to store all layers of the model.
     *
     * @return a list of layers
     * @since 1.0
     */
    public final ObservableList<Layer> getLayers() {
        return layers;
    }

    // Rows
    private final ListProperty<R> rows = new SimpleListProperty<>(this, "rows", FXCollections.observableArrayList());

    /**
     * Returns the property used to store the list of rows.
     *
     * @return the list of rows
     * @since 1.6
     */
    public final ListProperty<R> rowsProperty() {
        return rows;
    }

    /**
     * Sets the value of the {@link #rowsProperty()}.
     *
     * @param rows the new rows to display
     * @since 1.6
     */
    public final void setRows(ObservableList<R> rows) {
        Objects.requireNonNull(rows);
        this.rows.set(rows);
    }

    /**
     * Returns the list that is used to store all rows of the model.
     *
     * @return a list of rows
     * @since 1.6
     */
    public final ObservableList<R> getRows() {
        return rows.get();
    }

    // Timeline support.

    private final ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>(this, "timeline");

    /**
     * A property used to store a reference to the timeline control above the
     * graphics.
     *
     * @return the property used to store the timeline reference
     * @since 1.0
     */
    public final ObjectProperty<Timeline> timelineProperty() {
        return timeline;
    }

    /**
     * Sets the value of {@link #timelineProperty()}.
     *
     * @param timeline the timeline control above the graphics
     * @since 1.0
     */
    public final void setTimeline(Timeline timeline) {
        timelineProperty().set(timeline);
    }

    /**
     * Returns the value of {@link #timelineProperty()}.
     *
     * @return the timeline control above the graphics
     * @since 1.0
     */
    public final Timeline getTimeline() {
        return timeline.get();
    }

    // Fixed cell size support.

    private final DoubleProperty fixedCellSize = new SimpleDoubleProperty(this, "fixedCellSize", -1);

    /**
     * A property used to store a fixed cell size for controls that are based on
     * the virtual flow control. This value is not used by all subclasses of
     * this class. Using a fixed cell size can result in a performance gain.
     *
     * @return the property used to store a fixed cell size
     * @see ListViewGraphics
     * @since 1.0
     */
    public final DoubleProperty fixedCellSizeProperty() {
        return fixedCellSize;
    }

    /**
     * Returns the value of {@link #fixedCellSizeProperty()}.
     *
     * @return the fixed cell size (default is -1)
     * @since 1.0
     */
    public final double getFixedCellSize() {
        return fixedCellSize.get();
    }

    /**
     * Sets the value of {@link #fixedCellSizeProperty()}.
     *
     * @param size the fixed cell size, -1 to disable fixed cell size
     * @since 1.0
     */
    public final void setFixedCellSize(double size) {
        fixedCellSizeProperty().set(size);
    }

    /**
     * Returns the x coordinate for the given time.
     *
     * @param time the time for which to lookup a coordinate
     * @return the x coordinate for the given time
     * @see #getTimeAt(double)
     * @see TimelineModel#calculateLocationForTime(Instant)
     * @since 1.0
     */
    public final double getLocation(Instant time) {
        Timeline timeline = getTimeline();
        TimelineModel<?> timelineModel = timeline.getModel();
        return snapPosition(timelineModel.calculateLocationForTime(time));
    }

    /**
     * Returns the time at the given location.
     *
     * @param location the x-coordinate for which to retrieve the time
     * @return the time at the given location
     * @since 1.0
     */
    public final Instant getTimeAt(double location) {
        Timeline timeline = getTimeline();
        TimelineModel<?> timelineModel = timeline.getModel();
        return timelineModel.calculateTimeForLocation(location);
    }

    /**
     * Returns the local time at the given location. This method will only
     * return a valid value if the {@link AgendaLayout} is being used at the
     * given location (in graphics view coordinate space).
     *
     * @param y the y-coordinate in the coordinate space of the graphics view
     * @return the local time at the given location or null if location not
     * managed by an {@link AgendaLayout}
     * @since 1.0
     */
    public final LocalTime getLocalTimeAt(double y) {
        @SuppressWarnings("unchecked")
        GraphicsBaseSkin<?, R> skin = (GraphicsBaseSkin<?, R>) getSkin();
        return skin.getLocalTimeAt(y);
    }

    /**
     * Finds the row at the given y-coordinate.
     *
     * @param y the y-coordinate in the coordinate space of the graphics view
     *          for which to return a row model object
     * @return the row model object at the given y-coordinate
     * @since 1.0
     */
    public final R getRowAt(double y) {
        @SuppressWarnings("unchecked")
        GraphicsBaseSkin<?, R> skin = (GraphicsBaseSkin<?, R>) getSkin();
        return skin.getRowAt(y);
    }

    /**
     * Finds the layout that is being used at the given y-coordinate.
     *
     * @param y the y-coordinate in the coordinate space of the graphics view
     *          for which to return the layout
     * @return the layout used at the given location
     * @since 1.0
     */
    public final Layout getLayoutAt(double y) {
        @SuppressWarnings("unchecked")
        GraphicsBaseSkin<?, R> skin = (GraphicsBaseSkin<?, R>) getSkin();
        return skin.getLayoutAt(y);
    }

    /**
     * Finds the activity bounds at the given location. Returns the bounds of
     * the activity drawn last if several activities can be found at the given
     * location.
     *
     * @param x the x-coordinate in the coordinate space of the graphics view
     * @param y the y-coordinate in the coordinate space of the graphics view
     * @return the bounds of the activity found at the given location or null if
     * no activity can be found
     * @since 1.0
     */
    public final ActivityBounds getActivityBoundsAt(double x, double y) {
        @SuppressWarnings("unchecked")
        GraphicsBaseSkin<?, R> skin = (GraphicsBaseSkin<?, R>) getSkin();
        return skin.getActivityBoundsAt(x, y);
    }

    /**
     * Finds the activity reference at the given location. Returns the reference
     * of the activity drawn last if several activities can be found at the
     * given location.
     *
     * @param x the x-coordinate in the coordinate space of the graphics view
     * @param y the y-coordinate in the coordinate space of the graphics view
     * @return the reference of the activity found at the given location or null
     * if no activity can be found
     * @since 1.0
     */
    public final ActivityRef<?> getActivityRefAt(double x, double y) {
        @SuppressWarnings("unchecked")
        GraphicsBaseSkin<?, R> skin = (GraphicsBaseSkin<?, R>) getSkin();
        return skin.getActivityRefAt(x, y);
    }

    /**
     * Returns the bounds of all activities found at the given location.
     * Activities can be drawn on top of each other, hence several bounds can
     * exist at the same location.
     *
     * @param x the x-coordinate in the coordinate space of the graphics view
     * @param y the y-coordinate in the coordinate space of the graphics view
     * @return the bounds of the activities found at the given location or null
     * if no activities can be found
     * @since 1.0
     */
    public final List<ActivityBounds> getAllActivityBoundsAt(double x,
                                                             double y) {
        @SuppressWarnings("unchecked")
        GraphicsBaseSkin<?, R> skin = (GraphicsBaseSkin<?, R>) getSkin();
        return skin.getAllActivityBoundsAt(x, y);
    }

    /**
     * Returns the references to all activities found at the given location.
     * Activities can be drawn on top of each other, hence several references
     * can exist at the same location.
     *
     * @param x the x-coordinate in the coordinate space of the graphics view
     * @param y the y-coordinate in the coordinate space of the graphics view
     * @return the references of all activities found at the given location or
     * null if no activities can be found
     * @since 1.0
     */
    public final List<ActivityRef<?>> getAllActivityRefsAt(double x, double y) {
        @SuppressWarnings("unchecked")
        GraphicsBaseSkin<?, R> skin = (GraphicsBaseSkin<?, R>) getSkin();
        return skin.getAllActivityRefsAt(x, y);
    }

    /**
     * Finds all calendar activities at the given location.
     *
     * @param x the x-coordinate in the coordinate space of the graphics view
     * @param y the y-coordinate in the coordinate space of the graphics view
     * @return all calendar activities at the given location
     * @since 1.1
     */
    public final List<CalendarActivity> getAllCalendarActivitiesAt(double x,
                                                                   double y) {
        @SuppressWarnings("unchecked")
        GraphicsBaseSkin<?, R> skin = (GraphicsBaseSkin<?, R>) getSkin();
        return skin.getAllCalendarActivitiesAt(x, y);
    }

    // Layers

    /**
     * Moves the given layer to the front so that the activities located on it
     * will be drawn on top of all other activities.
     *
     * @param layer the layer to move
     * @see #getLayers()
     * @since 1.0
     */
    public final void moveLayerToFront(Layer layer) {
        requireNonNull(layer);

        if (!getLayers().contains(layer)) {
            throw new IllegalArgumentException(
                    "given layer is not a member of the model, can not move it");
        }

        getLayers().remove(layer);
        getLayers().add(layer);
    }

    /**
     * Moves the given layer to the back so that the activities located on it
     * will be drawn first and all other activities on other layers will be
     * drawn on top of them.
     *
     * @param layer the layer to move
     * @see #getLayers()
     * @since 1.0
     */
    public final void moveLayerToBack(Layer layer) {
        requireNonNull(layer);

        if (!getLayers().contains(layer)) {
            throw new IllegalArgumentException(
                    "given layer is not a member of the model, can not move it");
        }

        getLayers().remove(layer);
        getLayers().add(0, layer);
    }

    /**
     * Moves the given layer forward within the stack of layers.
     *
     * @param layer the layer to move
     * @see #getLayers()
     * @since 1.0
     */
    public final void moveLayerForward(Layer layer) {
        requireNonNull(layer);

        if (!getLayers().contains(layer)) {
            throw new IllegalArgumentException(
                    "given layer is not a member of the model, can not move it");
        }

        /*
         * Moving a layer forward, means moving it to a higher index location in
         * the models list.
         */
        ObservableList<Layer> layers = getLayers();
        int oldIndex = layers.indexOf(layer);
        int newIndex = Math.min(oldIndex + 1, layers.size() - 1);
        layers.remove(oldIndex);
        layers.add(newIndex, layer);
    }

    /**
     * Moves the given layer backward within the stack of layers.
     *
     * @param layer the layer to move
     * @see #getLayers()
     * @since 1.0
     */
    public final void moveLayerBackward(Layer layer) {
        requireNonNull(layer);

        if (!getLayers().contains(layer)) {
            throw new IllegalArgumentException(
                    "given layer is not a member of the model, can not move it");
        }

        /*
         * Moving a layer forward, means moving it to a lower index location in
         * the models list.
         */
        ObservableList<Layer> layers = getLayers();
        int oldIndex = layers.indexOf(layer);
        int newIndex = Math.max(oldIndex - 1, 0);
        layers.remove(oldIndex);
        layers.add(newIndex, layer);
    }

    private class ActivityEventHandlerProperty
            extends SimpleObjectProperty<EventHandler<ActivityEvent>> {

        private final EventType<ActivityEvent> eventType;

        public ActivityEventHandlerProperty(final String name,
                                            final EventType<ActivityEvent> eventType) {
            super(GraphicsBase.this, name);
            this.eventType = eventType;
        }

        @Override
        protected void invalidated() {
            setEventHandler(eventType, get());
        }
    }

    // On ACTIVITY_DELETED support.

    private ActivityEventHandlerProperty onActivityDeleted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityDeletedProperty() {
        if (onActivityDeleted == null) {
            onActivityDeleted = new ActivityEventHandlerProperty(
                    "onActivityDeleted", ActivityEvent.ACTIVITY_DELETED);
        }

        return onActivityDeleted;
    }

    public final void setOnActivityDeleted(EventHandler<ActivityEvent> value) {
        onActivityDeletedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityDeleted() {
        return onActivityDeleted == null ? null
                : onActivityDeletedProperty().get();
    }

    // On ACTIVITY_CHANGE support.

    private ActivityEventHandlerProperty onActivityChange;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChangeProperty() {
        if (onActivityChange == null) {
            onActivityChange = new ActivityEventHandlerProperty(
                    "onActivityChange", ActivityEvent.ACTIVITY_CHANGE);
        }

        return onActivityChange;
    }

    public final void setOnActivityChange(EventHandler<ActivityEvent> value) {
        onActivityChangeProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChange() {
        return onActivityChange == null ? null : onActivityChange.get();
    }

    // On ACTIVITY_CHANGE_STARTED support.

    private ActivityEventHandlerProperty onActivityChangeStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChangeStartedProperty() {
        if (onActivityChangeStarted == null) {
            onActivityChangeStarted = new ActivityEventHandlerProperty(
                    "onActivityChangeStarted",
                    ActivityEvent.ACTIVITY_CHANGE_STARTED);
        }

        return onActivityChangeStarted;
    }

    public final void setOnActivityChangeStarted(
            EventHandler<ActivityEvent> value) {
        onActivityChangeStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChangeStarted() {
        return onActivityChangeStarted == null ? null
                : onActivityChangeStarted.get();
    }

    // On ACTIVITY_CHANGE_ONGOING support.

    private ActivityEventHandlerProperty onActivityChangeOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChangeOngoingProperty() {
        if (onActivityChangeOngoing == null) {
            onActivityChangeOngoing = new ActivityEventHandlerProperty(
                    "onActivityChangeOngoing",
                    ActivityEvent.ACTIVITY_CHANGE_ONGOING);
        }

        return onActivityChangeOngoing;
    }

    public final void setOnActivityChangeOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityChangeOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChangeOngoing() {
        return onActivityChangeOngoing == null ? null
                : onActivityChangeOngoing.get();
    }

    // On ACTIVITY_CHANGE_FINISHED support.

    private ActivityEventHandlerProperty onActivityChangeFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChangeFinishedProperty() {
        if (onActivityChangeFinished == null) {
            onActivityChangeFinished = new ActivityEventHandlerProperty(
                    "onActivityChangeFinished",
                    ActivityEvent.ACTIVITY_CHANGE_FINISHED);
        }

        return onActivityChangeFinished;
    }

    public final void setOnActivityChangeFinished(
            EventHandler<ActivityEvent> value) {
        onActivityChangeFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChangeFinished() {
        return onActivityChangeFinished == null ? null
                : onActivityChangeFinished.get();
    }

    // On DRAG STARTED support.

    private ActivityEventHandlerProperty onActivityDragStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityDragStartedProperty() {
        if (onActivityDragStarted == null) {
            onActivityDragStarted = new ActivityEventHandlerProperty(
                    "onActivityDragStarted", ActivityEvent.DRAG_STARTED);
        }

        return onActivityDragStarted;
    }

    public final void setOnActivityDragStarted(
            EventHandler<ActivityEvent> value) {
        onActivityDragStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityDragStarted() {
        return onActivityDragStarted == null ? null
                : onActivityDragStarted.get();
    }

    // On DRAG ONGOING support.

    private ActivityEventHandlerProperty onActivityDragOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityDragOngoingProperty() {
        if (onActivityDragOngoing == null) {
            onActivityDragOngoing = new ActivityEventHandlerProperty(
                    "onActivityDragOngoing", ActivityEvent.DRAG_ONGOING);
        }

        return onActivityDragOngoing;
    }

    public final void setOnActivityDragOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityDragOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityDragOngoing() {
        return onActivityDragOngoing == null ? null
                : onActivityDragOngoing.get();
    }

    // On DRAG FINISHED support.

    private ActivityEventHandlerProperty onActivityDragFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityDragFinishedProperty() {
        if (onActivityDragFinished == null) {
            onActivityDragFinished = new ActivityEventHandlerProperty(
                    "onActivityDragFinished", ActivityEvent.DRAG_FINISHED);
        }

        return onActivityDragFinished;
    }

    public final void setOnActivityDragFinished(
            EventHandler<ActivityEvent> value) {
        onActivityDragFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityDragFinished() {
        return onActivityDragFinished == null ? null
                : onActivityDragFinished.get();
    }

    // On DRAG DONE support.

    private ActivityEventHandlerProperty onActivityDragDone;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityDragDoneProperty() {
        if (onActivityDragDone == null) {
            onActivityDragDone = new ActivityEventHandlerProperty(
                    "onActivityDragDone", ActivityEvent.DRAG_DONE);
        }

        return onActivityDragDone;
    }

    public final void setOnActivityDragDone(EventHandler<ActivityEvent> value) {
        onActivityDragDoneProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityDragDone() {
        return onActivityDragDone == null ? null : onActivityDragDone.get();
    }

    // On CHART VALUE CHANGE STARTED support.

    private ActivityEventHandlerProperty onActivityChartValueChangeStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChartValueChangeStartedProperty() {
        if (onActivityChartValueChangeFinished == null) {
            onActivityChartValueChangeStarted = new ActivityEventHandlerProperty(
                    "onActivityChartValueChangeStarted",
                    ActivityEvent.CHART_VALUE_CHANGE_STARTED);
        }

        return onActivityChartValueChangeStarted;
    }

    public final void setOnActivityChartValueChangeStarted(
            EventHandler<ActivityEvent> value) {
        onActivityChartValueChangeStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChartValueChangeStarted() {
        return onActivityChartValueChangeStarted == null ? null
                : onActivityChartValueChangeStarted.get();
    }

    // On CHART VALUE CHANGE ONGOING support.

    private ActivityEventHandlerProperty onActivityChartValueChangeOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChartValueChangeOngoingProperty() {
        if (onActivityChartValueChangeOngoing == null) {
            onActivityChartValueChangeOngoing = new ActivityEventHandlerProperty(
                    "onActivityChartValueChangeOngoing",
                    ActivityEvent.CHART_VALUE_CHANGE_ONGOING);
        }

        return onActivityChartValueChangeOngoing;
    }

    public final void setOnActivityChartValueChangeOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityChartValueChangeOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChartValueChangeOngoing() {
        return onActivityChartValueChangeOngoing == null ? null
                : onActivityChartValueChangeOngoing.get();
    }

    // On CHART VALUE CHANGE FINISHED support.

    private ActivityEventHandlerProperty onActivityChartValueChangeFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChartValueChangeFinishedProperty() {
        if (onActivityChartValueChangeFinished == null) {
            onActivityChartValueChangeFinished = new ActivityEventHandlerProperty(
                    "onActivityChartValueChangeFinished",
                    ActivityEvent.CHART_VALUE_CHANGE_FINISHED);
        }

        return onActivityChartValueChangeFinished;
    }

    public final void setOnActivityChartValueChangeFinished(
            EventHandler<ActivityEvent> value) {
        onActivityChartValueChangeFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChartValueChangeFinished() {
        return onActivityChartValueChangeFinished == null ? null
                : onActivityChartValueChangeFinished.get();
    }

    // On CHART HIGH VALUE STARTED support.

    private ActivityEventHandlerProperty onActivityChartHighValueChangeStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChartHighValueChangeStartedProperty() {
        if (onActivityChartHighValueChangeStarted == null) {
            onActivityChartHighValueChangeStarted = new ActivityEventHandlerProperty(
                    "onActivityChartHighValueChangeStarted",
                    ActivityEvent.CHART_HIGH_VALUE_CHANGE_STARTED);
        }

        return onActivityChartHighValueChangeStarted;
    }

    public final void setOnActivityChartHighValueChangeStarted(
            EventHandler<ActivityEvent> value) {
        onActivityChartHighValueChangeStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChartHighValueChangeStarted() {
        return onActivityChartHighValueChangeStarted == null ? null
                : onActivityChartHighValueChangeStarted.get();
    }

    // On CHART HIGH VALUE CHANGE ONGOING support.

    private ActivityEventHandlerProperty onActivityChartHighValueChangeOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChartHighValueChangeOngoingProperty() {
        if (onActivityChartHighValueChangeOngoing == null) {
            onActivityChartHighValueChangeOngoing = new ActivityEventHandlerProperty(
                    "onActivityChartHighValueChangeOngoing",
                    ActivityEvent.CHART_HIGH_VALUE_CHANGE_ONGOING);
        }

        return onActivityChartHighValueChangeOngoing;
    }

    public final void setOnActivityChartHighValueChangeOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityChartHighValueChangeOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChartHighValueChangeOngoing() {
        return onActivityChartHighValueChangeOngoing == null ? null
                : onActivityChartHighValueChangeOngoing.get();
    }

    // On CHART HIGH VALUE FINISHED support.

    private ActivityEventHandlerProperty onActivityChartHighValueChangeFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChartHighValueChangeFinishedProperty() {
        if (onActivityChartHighValueChangeFinished == null) {
            onActivityChartHighValueChangeFinished = new ActivityEventHandlerProperty(
                    "onActivityChartHighValueChangeFinished",
                    ActivityEvent.CHART_HIGH_VALUE_CHANGE_FINISHED);
        }

        return onActivityChartHighValueChangeFinished;
    }

    public final void setOnActivityChartHighValueChangeFinished(
            EventHandler<ActivityEvent> value) {
        onActivityChartHighValueChangeFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChartHighValueChangeFinished() {
        return onActivityChartHighValueChangeFinished == null ? null
                : onActivityChartHighValueChangeFinished.get();
    }

    // On CHART LOW VALUE CHANGE STARTED support.

    private ActivityEventHandlerProperty onActivityChartLowValueChangeStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChartLowValueChangeStartedProperty() {
        if (onActivityChartLowValueChangeStarted == null) {
            onActivityChartLowValueChangeStarted = new ActivityEventHandlerProperty(
                    "onActivityChartLowValueChangeStarted",
                    ActivityEvent.CHART_LOW_VALUE_CHANGE_STARTED);
        }

        return onActivityChartLowValueChangeStarted;
    }

    public final void setOnActivityChartLowValueChangeStarted(
            EventHandler<ActivityEvent> value) {
        onActivityChartLowValueChangeStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChartLowValueChangeStarted() {
        return onActivityChartLowValueChangeStarted == null ? null
                : onActivityChartLowValueChangeStarted.get();
    }

    // On CHART LOW VALUE CHANGE ONGOING support.

    private ActivityEventHandlerProperty onActivityChartLowValueChangeOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChartLowValueChangeOngoingProperty() {
        if (onActivityChartLowValueChangeOngoing == null) {
            onActivityChartLowValueChangeOngoing = new ActivityEventHandlerProperty(
                    "onActivityChartLowValueChangeOngoing",
                    ActivityEvent.CHART_LOW_VALUE_CHANGE_ONGOING);
        }

        return onActivityChartLowValueChangeOngoing;
    }

    public final void setOnActivityChartLowValueChangeOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityChartLowValueChangeOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChartLowValueChangeOngoing() {
        return onActivityChartLowValueChangeOngoing == null ? null
                : onActivityChartLowValueChangeOngoing.get();
    }

    // On CHART LOW VALUE CHANGE FINISHED support.

    private ActivityEventHandlerProperty onActivityChartLowValueChangeFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityChartLowValueChangeFinishedProperty() {
        if (onActivityChartLowValueChangeFinished == null) {
            onActivityChartLowValueChangeFinished = new ActivityEventHandlerProperty(
                    "onActivityChartLowValueChangeFinished",
                    ActivityEvent.CHART_LOW_VALUE_CHANGE_FINISHED);
        }

        return onActivityChartLowValueChangeFinished;
    }

    public final void setOnActivityChartLowValueChangeFinished(
            EventHandler<ActivityEvent> value) {
        onActivityChartLowValueChangeFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityChartLowValueChangeFinished() {
        return onActivityChartLowValueChangeFinished == null ? null
                : onActivityChartLowValueChangeFinished.get();
    }

    // On HORIZONTAL DRAG STARTED support.

    private ActivityEventHandlerProperty onActivityHorizontalDragStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityHorizontalDragStartedProperty() {
        if (onActivityHorizontalDragStarted == null) {
            onActivityHorizontalDragStarted = new ActivityEventHandlerProperty(
                    "onActivityHorizontalDragStarted",
                    ActivityEvent.HORIZONTAL_DRAG_STARTED);
        }

        return onActivityHorizontalDragStarted;
    }

    public final void setOnActivityHorizontalDragStarted(
            EventHandler<ActivityEvent> value) {
        onActivityHorizontalDragStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityHorizontalDragStarted() {
        return onActivityHorizontalDragStarted == null ? null
                : onActivityHorizontalDragStarted.get();
    }

    // On HORIZONTAL DRAG ONGOING support.

    private ActivityEventHandlerProperty onActivityHorizontalDragOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityHorizontalDragOngoingProperty() {
        if (onActivityHorizontalDragOngoing == null) {
            onActivityHorizontalDragOngoing = new ActivityEventHandlerProperty(
                    "onActivityHorizontalDragOngoing",
                    ActivityEvent.HORIZONTAL_DRAG_ONGOING);
        }

        return onActivityHorizontalDragOngoing;
    }

    public final void setOnActivityHorizontalDragOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityHorizontalDragOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityHorizontalDragOngoing() {
        return onActivityHorizontalDragOngoing == null ? null
                : onActivityHorizontalDragOngoing.get();
    }

    // On HORIZONTAL DRAG FINISHED support.

    private ActivityEventHandlerProperty onActivityHorizontalDragFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityHorizontalDragFinishedProperty() {
        if (onActivityHorizontalDragFinished == null) {
            onActivityHorizontalDragFinished = new ActivityEventHandlerProperty(
                    "onActivityHorizontalDragFinished",
                    ActivityEvent.HORIZONTAL_DRAG_FINISHED);
        }

        return onActivityHorizontalDragFinished;
    }

    public final void setOnActivityHorizontalDragFinished(
            EventHandler<ActivityEvent> value) {
        onActivityHorizontalDragFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityHorizontalDragFinished() {
        return onActivityHorizontalDragFinished == null ? null
                : onActivityHorizontalDragFinished.get();
    }

    // On VERTICAL DRAG STARTED support.

    private ActivityEventHandlerProperty onActivityVerticalDragStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityVerticalDragStartedProperty() {
        if (onActivityVerticalDragStarted == null) {
            onActivityVerticalDragStarted = new ActivityEventHandlerProperty(
                    "onActivityVerticalDragStarted",
                    ActivityEvent.VERTICAL_DRAG_STARTED);
        }

        return onActivityVerticalDragFinished;
    }

    public final void setOnActivityVerticalDragStarted(
            EventHandler<ActivityEvent> value) {
        onActivityVerticalDragStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityVerticalDragStarted() {
        return onActivityVerticalDragStarted == null ? null
                : onActivityVerticalDragStarted.get();
    }

    // On VERTICAL DRAG ONGOING support.

    private ActivityEventHandlerProperty onActivityVerticalDragOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityVerticalDragOngoingProperty() {
        if (onActivityVerticalDragOngoing == null) {
            onActivityVerticalDragOngoing = new ActivityEventHandlerProperty(
                    "onActivityVerticalDragOngoing",
                    ActivityEvent.VERTICAL_DRAG_ONGOING);
        }

        return onActivityVerticalDragOngoing;
    }

    public final void setOnActivityVerticalDragOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityVerticalDragOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityVerticalDragOngoing() {
        return onActivityVerticalDragOngoing == null ? null
                : onActivityVerticalDragOngoing.get();
    }

    // On VERTICAL DRAG FINISHED support.

    private ActivityEventHandlerProperty onActivityVerticalDragFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityVerticalDragFinishedProperty() {
        if (onActivityVerticalDragFinished == null) {
            onActivityVerticalDragFinished = new ActivityEventHandlerProperty(
                    "onActivityVerticalDragFinished",
                    ActivityEvent.VERTICAL_DRAG_FINISHED);
        }

        return onActivityVerticalDragFinished;
    }

    public final void setOnActivityVerticalDragFinished(
            EventHandler<ActivityEvent> value) {
        onActivityVerticalDragFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityVerticalDragFinished() {
        return onActivityVerticalDragFinished == null ? null
                : onActivityVerticalDragFinished.get();
    }

    // On VERTICAL DRAG DONE support.

    private ActivityEventHandlerProperty onActivityVerticalDragDone;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityVerticalDragDoneProperty() {
        if (onActivityVerticalDragDone == null) {
            onActivityVerticalDragDone = new ActivityEventHandlerProperty(
                    "onActivityVerticalDragDone",
                    ActivityEvent.VERTICAL_DRAG_DONE);
        }

        return onActivityVerticalDragDone;
    }

    public final void setOnActivityVerticalDragDone(
            EventHandler<ActivityEvent> value) {
        onActivityVerticalDragDoneProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityVerticalDragDone() {
        return onActivityVerticalDragDone == null ? null
                : onActivityVerticalDragDone.get();
    }

    // On END TIME CHANGE STARTED support.

    private ActivityEventHandlerProperty onActivityEndTimeChangeStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityEndTimeChangeStartedProperty() {
        if (onActivityEndTimeChangeStarted == null) {
            onActivityEndTimeChangeStarted = new ActivityEventHandlerProperty(
                    "onActivityEndTimeChangeStarted",
                    ActivityEvent.END_TIME_CHANGE_STARTED);
        }

        return onActivityEndTimeChangeStarted;
    }

    public final void setOnActivityEndTimeChangeStarted(
            EventHandler<ActivityEvent> value) {
        onActivityEndTimeChangeStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityEndTimeChangeStarted() {
        return onActivityEndTimeChangeStarted == null ? null
                : onActivityEndTimeChangeStarted.get();
    }

    // On END TIME CHANGE ONGOING support.

    private ActivityEventHandlerProperty onActivityEndTimeChangeOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityEndTimeChangeOngoingProperty() {
        if (onActivityEndTimeChangeOngoing == null) {
            onActivityEndTimeChangeOngoing = new ActivityEventHandlerProperty(
                    "onActivityEndTimeChangeOngoing",
                    ActivityEvent.END_TIME_CHANGE_ONGOING);
        }

        return onActivityEndTimeChangeOngoing;
    }

    public final void setOnActivityEndTimeChangeOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityEndTimeChangeOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityEndTimeChangeOngoing() {
        return onActivityEndTimeChangeOngoing == null ? null
                : onActivityEndTimeChangeOngoing.get();
    }

    // On END TIME CHANGE FINISHED support.

    private ActivityEventHandlerProperty onActivityEndTimeChangeFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityEndTimeChangeFinishedProperty() {
        if (onActivityEndTimeChangeFinished == null) {
            onActivityEndTimeChangeFinished = new ActivityEventHandlerProperty(
                    "onActivityEndTimeChangeFinished",
                    ActivityEvent.END_TIME_CHANGE_FINISHED);
        }

        return onActivityEndTimeChangeFinished;
    }

    public final void setOnActivityEndTimeChangeFinished(
            EventHandler<ActivityEvent> value) {
        onActivityEndTimeChangeFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityEndTimeChangeFinished() {
        return onActivityEndTimeChangeFinished == null ? null
                : onActivityEndTimeChangeFinished.get();
    }

    // On PERCENTAGE CHANGE STARTED support.

    private ActivityEventHandlerProperty onActivityPercentageChangeStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityPercentageChangeStartedProperty() {
        if (onActivityPercentageChangeStarted == null) {
            onActivityPercentageChangeStarted = new ActivityEventHandlerProperty(
                    "onActivityPercentageChangeStarted",
                    ActivityEvent.PERCENTAGE_CHANGE_STARTED);
        }

        return onActivityPercentageChangeStarted;
    }

    public final void setOnActivityPercentageChangeStarted(
            EventHandler<ActivityEvent> value) {
        onActivityPercentageChangeStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityPercentageChangeStarted() {
        return onActivityPercentageChangeStarted == null ? null
                : onActivityPercentageChangeStarted.get();
    }

    // On PERCENTAGE CHANGE ONGOING support.

    private ActivityEventHandlerProperty onActivityPercentageChangeOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityPercentageChangeOngoingProperty() {
        if (onActivityPercentageChangeOngoing == null) {
            onActivityPercentageChangeOngoing = new ActivityEventHandlerProperty(
                    "onActivityPercentageChangeOngoing",
                    ActivityEvent.PERCENTAGE_CHANGE_ONGOING);
        }

        return onActivityPercentageChangeOngoing;
    }

    public final void setOnActivityPercentageChangeOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityPercentageChangeOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityPercentageChangeOngoing() {
        return onActivityPercentageChangeOngoing == null ? null
                : onActivityPercentageChangeOngoing.get();
    }

    // On PERCENTAGE CHANGE FINISHED support.

    private ActivityEventHandlerProperty onActivityPercentageChangeFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityPercentageChangeFinishedProperty() {
        if (onActivityPercentageChangeFinished == null) {
            onActivityPercentageChangeFinished = new ActivityEventHandlerProperty(
                    "onActivityPercentageChangeFinished",
                    ActivityEvent.PERCENTAGE_CHANGE_FINISHED);
        }

        return onActivityPercentageChangeFinished;
    }

    public final void setOnActivityPercentageChangeFinished(
            EventHandler<ActivityEvent> value) {
        onActivityPercentageChangeFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityPercentageChangeFinished() {
        return onActivityPercentageChangeFinished == null ? null
                : onActivityPercentageChangeFinished.get();
    }

    // On START TIME CHANGE STARTED support.

    private ActivityEventHandlerProperty onActivityStartTimeChangeStarted;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityStartTimeChangeStartedProperty() {
        if (onActivityStartTimeChangeStarted == null) {
            onActivityStartTimeChangeStarted = new ActivityEventHandlerProperty(
                    "onActivityStartTimeChangeStarted",
                    ActivityEvent.START_TIME_CHANGE_STARTED);
        }

        return onActivityStartTimeChangeStarted;
    }

    public final void setOnActivityStartTimeChangeStarted(
            EventHandler<ActivityEvent> value) {
        onActivityStartTimeChangeStartedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityStartTimeChangeStarted() {
        return onActivityStartTimeChangeStarted == null ? null
                : onActivityStartTimeChangeStarted.get();
    }

    // On START TIME CHANGE ONGOING support.

    private ActivityEventHandlerProperty onActivityStartTimeChangeOngoing;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityStartTimeChangeOngoingProperty() {
        if (onActivityStartTimeChangeOngoing == null) {
            onActivityStartTimeChangeOngoing = new ActivityEventHandlerProperty(
                    "onActivityStartTimeChangeOngoing",
                    ActivityEvent.START_TIME_CHANGE_ONGOING);
        }

        return onActivityStartTimeChangeOngoing;
    }

    public final void setOnActivityStartTimeChangeOngoing(
            EventHandler<ActivityEvent> value) {
        onActivityStartTimeChangeOngoingProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityStartTimeChangeOngoing() {
        return onActivityStartTimeChangeOngoing == null ? null
                : onActivityStartTimeChangeOngoing.get();
    }

    // On START TIME CHANGE FINISHED support.

    private ActivityEventHandlerProperty onActivityStartTimeChangeFinished;

    public final ObjectProperty<EventHandler<ActivityEvent>> onActivityStartTimeChangeFinishedProperty() {
        if (onActivityStartTimeChangeFinished == null) {
            onActivityStartTimeChangeFinished = new ActivityEventHandlerProperty(
                    "onActivityStartTimeChangeFinished",
                    ActivityEvent.START_TIME_CHANGE_FINISHED);
        }

        return onActivityStartTimeChangeFinished;
    }

    public final void setOnActivityStartTimeChangeFinished(
            EventHandler<ActivityEvent> value) {
        onActivityStartTimeChangeFinishedProperty().set(value);
    }

    public final EventHandler<ActivityEvent> getOnActivityStartTimeChangeFinished() {
        return onActivityStartTimeChangeFinished == null ? null
                : onActivityStartTimeChangeFinished.get();
    }

    private class LassoEventHandlerProperty
            extends SimpleObjectProperty<EventHandler<LassoEvent>> {

        private final EventType<LassoEvent> eventType;

        public LassoEventHandlerProperty(final String name,
                                         final EventType<LassoEvent> eventType) {
            super(GraphicsBase.this, name);
            this.eventType = eventType;
        }

        @Override
        protected void invalidated() {
            setEventHandler(eventType, get());
        }
    }

    // On LASSO_SELECTION support.

    private LassoEventHandlerProperty onLassoSelection;

    public final ObjectProperty<EventHandler<LassoEvent>> onLassoSelectionProperty() {
        if (onLassoSelection == null) {
            onLassoSelection = new LassoEventHandlerProperty("onLassoSelection",
                    LassoEvent.ALL);
        }

        return onLassoSelection;
    }

    public final void setOnLassoSelection(EventHandler<LassoEvent> value) {
        onLassoSelectionProperty().set(value);
    }

    public final EventHandler<LassoEvent> getOnLassoSelection() {
        return onLassoSelection == null ? null : onLassoSelection.get();
    }

    // On LASSO_SELECTION_START support.

    private LassoEventHandlerProperty onLassoSelectionStarted;

    public final ObjectProperty<EventHandler<LassoEvent>> onLassoSelectionStartedProperty() {
        if (onLassoSelectionStarted == null) {
            onLassoSelectionStarted = new LassoEventHandlerProperty(
                    "onLassoSelectionStarted", LassoEvent.SELECTION_STARTED);
        }

        return onLassoSelectionStarted;
    }

    public final void setOnLassoSelectionStarted(
            EventHandler<LassoEvent> value) {
        onLassoSelectionStartedProperty().set(value);
    }

    public final EventHandler<LassoEvent> getOnLassoSelectionStarted() {
        return onLassoSelectionStarted == null ? null
                : onLassoSelectionStarted.get();
    }

    // On LASSO_SELECTION_ONGOING support.

    private LassoEventHandlerProperty onLassoSelectionOngoing;

    public final ObjectProperty<EventHandler<LassoEvent>> onLassoSelectionOngoingProperty() {
        if (onLassoSelectionOngoing == null) {
            onLassoSelectionOngoing = new LassoEventHandlerProperty(
                    "onLassoSelectionOngoing", LassoEvent.SELECTION_ONGOING);
        }

        return onLassoSelectionOngoing;
    }

    public final void setOnLassoSelectionOngoing(
            EventHandler<LassoEvent> value) {
        onLassoSelectionOngoingProperty().set(value);
    }

    public final EventHandler<LassoEvent> getOnLassoSelectionOngoing() {
        return onLassoSelectionOngoing == null ? null
                : onLassoSelectionOngoing.get();
    }

    // On LASSO_SELECTION_FINISH support.

    private LassoEventHandlerProperty onLassoSelectionFinished;

    public final ObjectProperty<EventHandler<LassoEvent>> onLassoSelectionFinishedProperty() {
        if (onLassoSelectionFinished == null) {
            onLassoSelectionFinished = new LassoEventHandlerProperty("onLassoSelectionFinished", LassoEvent.SELECTION_FINISHED);
        }

        return onLassoSelectionFinished;
    }

    public final void setOnLassoSelectionFinished(
            EventHandler<LassoEvent> value) {
        onLassoSelectionFinishedProperty().set(value);
    }

    public final EventHandler<LassoEvent> getOnLassoSelectionFinished() {
        return onLassoSelectionFinished == null ? null
                : onLassoSelectionFinished.get();
    }

    // Editing support.

    private final ReadOnlyObjectWrapper<EditMode> editMode = new ReadOnlyObjectWrapper<>(this, "editMode", EditMode.NONE);

    /**
     * A property used to store the currently active editing mode, e.g.
     * "changing start time", "changing end time", "dragging horizontally",
     * "dragging vertically", etc...<br>
     * The property is read-only as it can not be set from the outside. It is
     * being updated when the user moves the mouse cursor on top of an activity.
     * The edit mode depends on the location of the cursor (left or right edge,
     * center). See {@link #setActivityEditingCallback(Class, Callback)} for
     * mapping mouse events to editing operations.
     *
     * @return the currently active edit mode
     * @since 1.0
     */
    public final ReadOnlyObjectProperty<EditMode> editModeProperty() {
        return editMode.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #editModeProperty()}.
     *
     * @return the currently active edit mode
     * @since 1.0
     */
    public final EditMode getEditMode() {
        return editMode.get();
    }

    // Highlighting support.

    private final ObservableSet<Row<?, ?, ?>> highlightedRows = FXCollections
            .observableSet();

    /**
     * Returns a set that is used to store the currently highlighted rows. A row
     * added to this set will start blinking and draw the attention of the user
     * to it.
     *
     * @return the set of highlighted rows
     * @see #setHighlightDelay(long)
     * @since 1.0
     */
    public final ObservableSet<Row<?, ?, ?>> getHighlightedRows() {
        return highlightedRows;
    }

    private final ObservableSet<ActivityRef<?>> highlightedActivities = FXCollections
            .observableSet();

    /**
     * Returns a set that is used to store the currently highighted activities.
     * An activity added to this set will start blinking and draw the attention
     * of the user to it.
     *
     * @return the set of highlighted activities
     * @see #setHighlightDelay(long)
     * @since 1.0
     */
    public final ObservableSet<ActivityRef<?>> getHighlightedActivities() {
        return highlightedActivities;
    }

    private final LongProperty highlightDelay = new SimpleLongProperty(this,
            "highlightDelay", 500);

    /**
     * A property used to store the delay between two "blinks" of highlighted
     * rows or activities.
     *
     * @return the property used for the delay (in milliseconds)
     * @since 1.0
     */
    public final LongProperty highlightDelayProperty() {
        return highlightDelay;
    }

    /**
     * Sets the value of {@link #highlightDelayProperty()}.
     *
     * @param delay the highlight delay in milliseconds
     * @since 1.0
     */
    public final void setHighlightDelay(long delay) {
        if (delay < 100) {
            throw new IllegalArgumentException(
                    "delay must be at least 100 millis, but was " + delay);
        }
        highlightDelayProperty().set(delay);
    }

    /**
     * Returns the value of {@link #highlightDelayProperty()}.
     *
     * @return the highlight delay in milliseconds
     * @since 1.0
     */
    public final long getHighlightDelay() {
        return highlightDelay.get();
    }

    private final ReadOnlyBooleanWrapper highlighted = new ReadOnlyBooleanWrapper(
            this, "highlighted", false);

    /**
     * A read-only property used to control the highlighting effect. The value
     * of this property gets frequently toggled between true and false so that
     * is triggers a redraw of the graphics and a blink effect.
     *
     * @return a read-only property that signals if the highlight is on or off
     * (causes blinking)
     * @since 1.0
     */
    public final ReadOnlyBooleanProperty highlightedProperty() {
        return highlighted.getReadOnlyProperty();
    }

    private void setHighlighted(boolean on) {
        highlighted.set(on);
    }

    /**
     * Returns the value of {@link #highlightedProperty()}.
     *
     * @return a flag value used to toggle the highlighting effect
     * @since 1.0
     */
    public final boolean isHighlighted() {
        return highlightedProperty().get();
    }

    private HighlightThread highlightThread;

    private void startHighlighting() {
        if (highlightThread == null || !highlightThread.isRunning()) {
            highlightThread = new HighlightThread();
            highlightThread.start();
        }
    }

    private void stopHighlighting() {
        if (highlightThread != null) {
            highlightThread.stopRunning();
        }
    }

    private class HighlightThread extends Thread {

        private boolean running = true;

        public HighlightThread() {
            super("Highlight Thread");
            setDaemon(true);
        }

        @Override
        public final void run() {
            while (running) {
                highlight(!isHighlighted());

                try {
                    Thread.sleep(getHighlightDelay());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    highlight(false);
                }
            }

            highlight(false);
        }

        private void highlight(boolean highlight) {
            Platform.runLater(() -> setHighlighted(highlight));
        }

        public final void stopRunning() {
            running = false;
        }

        public final boolean isRunning() {
            return running;
        }
    }

    // Context menu factory support.

    /**
     * A callback parameter class used for displaying a context menu. Instances
     * of this class will be passed constructed based on the information
     * available at the location of the context menu trigger event.
     *
     * @param <R> the type of the rows used in the graphics view
     * @see GraphicsBase#setContextMenuCallback(Callback)
     * @since 1.0
     */
    public static final class ContextMenuParameter<R extends Row<?, ?, ?>> {

        private final GraphicsBase<R> graphics;

        private final R row;

        private final List<ActivityRef<?>> activities;

        private final Layout layout;

        private final Instant time;

        private final LocalTime localTime;

        /**
         * Constructs a new context menu parameter object.
         *
         * @param graphics   the view where the context menu has been requested
         * @param row        the row where the context menu has been requested
         * @param activities the activities found at the location within the row where
         *                   the context menu has been requested
         * @param layout     the layout found at the location within the row where the
         *                   context menu has been requested
         * @param time       the time found at the location within the row where the
         *                   context menu has been requested
         * @param localTime  the local time found at the location within the row where
         *                   the context menu has been requested
         * @since 1.0
         */
        public ContextMenuParameter(GraphicsBase<R> graphics, R row,
                                    List<ActivityRef<?>> activities, Layout layout, Instant time,
                                    LocalTime localTime) {

            this.graphics = requireNonNull(graphics);
            this.row = row;
            this.activities = activities;
            this.layout = layout;
            this.time = requireNonNull(time);
            this.localTime = localTime;
        }

        /**
         * Returns the graphics view where the context menu has been requested.
         *
         * @return the graphics view
         * @since 1.0
         */
        public final GraphicsBase<R> getGraphics() {
            return graphics;
        }

        /**
         * Returns the row where the context menu has been requested.
         *
         * @return the row
         * @since 1.0
         */
        public final R getRow() {
            return row;
        }

        /**
         * Returns the activities found at the location where the context menu
         * has been requested.
         *
         * @return the activities
         * @since 1.0
         */
        public final List<ActivityRef<?>> getActivities() {
            return activities;
        }

        /**
         * Returns the layout found at the location where the context menu has
         * been requested.
         *
         * @return the layout (e.g. GanttLayout)
         * @since 1.0
         */
        public Layout getLayout() {
            return layout;
        }

        /**
         * Returns the local time found at the location where the context menu
         * has been requested.
         *
         * @return the local time (e.g. 6pm)
         * @since 1.0
         */
        public LocalTime getLocalTime() {
            return localTime;
        }

        /**
         * Returns the time found at the location where the context menu has
         * been requested.
         *
         * @return the time
         * @since 1.0
         */
        public Instant getTime() {
            return time;
        }
    }

    private final ObjectProperty<Callback<ContextMenuParameter<R>, ContextMenu>> contextMenuCallback = new SimpleObjectProperty<>(
            this, "contextMenuCallback");

    /**
     * A property used to store a callback which is used for creating a context
     * menu. Context menus can also be used by simply calling
     * {@link Control#setContextMenu(ContextMenu)} but using this callback saves
     * you from collecting all the information and objects that can be found at
     * the location of the context menu trigger event.
     *
     * @return a callback for creating a context menu
     * @since 1.0
     */
    public final ObjectProperty<Callback<ContextMenuParameter<R>, ContextMenu>> contextMenuCallbackProperty() {
        return contextMenuCallback;
    }

    /**
     * Sets the value of {@link #contextMenuCallbackProperty()}.
     *
     * @param callback a callback for creating a parameterized context menu
     * @since 1.0
     */
    public final void setContextMenuCallback(
            Callback<ContextMenuParameter<R>, ContextMenu> callback) {
        if (callback != null) {
            LoggingDomain.CONFIG.fine("callback class: "
                    + callback.getClass().getName());
        } else {
            LoggingDomain.CONFIG.fine("callback: null");
        }

        contextMenuCallbackProperty().set(callback);
    }

    /**
     * Returns the value of {@link #contextMenuCallbackProperty()}.
     *
     * @return the callback for creating a parameterized context menu
     * @since 1.0
     */
    public final Callback<ContextMenuParameter<R>, ContextMenu> getContextMenuCallback() {
        return contextMenuCallbackProperty().get();
    }

    // Marked interval support.

    private final BooleanProperty autoMarkedTimeInterval = new SimpleBooleanProperty(
            this, "autoMarkedTimeInterval", true);

    /**
     * Controls whether the marked time interval property of the
     * {@link Eventline} will be automatically set when the user performs
     * certain editing operations (e.g. move an activity horizontally). The
     * default is "true".
     *
     * @return the auto marked time interval property
     * @see Eventline#markedTimeIntervalProperty()
     */
    public final BooleanProperty autoMarkedTimeIntervalProperty() {
        return autoMarkedTimeInterval;
    }

    /**
     * Returns the value of {@link #autoMarkedTimeIntervalProperty()}.
     *
     * @return true if the marked time interval gets updated automatically
     */
    public final boolean isAutoMarkedTimeInterval() {
        return autoMarkedTimeIntervalProperty().get();
    }

    /**
     * Sets the value of {@link #autoMarkedTimeIntervalProperty()}.
     *
     * @param auto if true the marked time interval will be updated automatically
     */
    public final void setAutoMarkedTimeInterval(boolean auto) {
        autoMarkedTimeIntervalProperty().set(auto);
    }

    // Support for grid levels.

    private final IntegerProperty maxGridLevel = new SimpleIntegerProperty(this,
            "maxGridLevel", 2);

    /**
     * A property used to store the number of grid levels that the user wants to
     * see in the graphics view. The value of this property must be between 1
     * and 5. The grid level depends on the number of scales shown by the
     * dateline (see {@link Dateline#getScaleResolutions()}). If the dateline is
     * currently showing two scales (e.g. days and weeks) then the graphics view
     * and the {@link GridLinesLayer} can also display two different grid lines,
     * for example a light gray one for days and a dark gray one for weeks.
     *
     * @return the maximum number of grid levels
     * @since 1.0
     */
    public final IntegerProperty maxGridLevelProperty() {
        return maxGridLevel;
    }

    /**
     * Returns the value of {@link #maxGridLevelProperty()}.
     *
     * @return the maximum number of grid levels
     * @since 1.0
     */
    public final int getMaxGridLevel() {
        return maxGridLevelProperty().get();
    }

    /**
     * Sets the value of {@link #maxGridLevelProperty()}.
     *
     * @param max the maximum number of grid levels, a value between 1 and 5
     * @since 1.0
     */
    public final void setMaxGridLevel(int max) {
        if (max < 1 || max > 5) {
            throw new IllegalArgumentException(
                    "max grid level must be within [1, 5] but was " + max);
        }

        maxGridLevelProperty().set(max);
    }

    // Cursor support.

    private final BooleanProperty showVerticalCursor = new SimpleBooleanProperty(this,
            "showVerticalCursor", false);

    /**
     * A property used to control wether a vertical cursor line will be shown by
     * the graphics view. The line will always follow the location of the mouse
     * cursor.
     *
     * @return a property used for controlling the visibility of a vertical
     * cursor line
     * @since 1.0
     */
    public final BooleanProperty showVerticalCursorProperty() {
        return showVerticalCursor;
    }

    /**
     * Returns the value of {@link #showVerticalCursorProperty()}.
     *
     * @return true if the cursor will be shown
     * @since 1.0
     */
    public final boolean isShowVerticalCursor() {
        return showVerticalCursorProperty().get();
    }

    /**
     * Sets the value of {@link #showVerticalCursorProperty()}.
     *
     * @param show if true a vertical cursor line will be shown
     * @since 1.0
     */
    public final void setShowVerticalCursor(boolean show) {
        showVerticalCursorProperty().set(show);
    }

    private final BooleanProperty showHorizontalCursor = new SimpleBooleanProperty(
            this, "showHorizontalCursor", false);

    /**
     * A property used to control wether a horizontal cursor line will be shown
     * by the graphics view. The line will always follow the location of the
     * mouse cursor.
     *
     * @return a property used for controlling the visibility of a horizontal
     * cursor line
     * @since 1.0
     */
    public final BooleanProperty showHorizontalCursorProperty() {
        return showHorizontalCursor;
    }

    /**
     * Returns the value of {@link #showHorizontalCursorProperty()}.
     *
     * @return true if the cursor will be shown
     * @since 1.0
     */
    public final boolean isShowHorizontalCursor() {
        return showHorizontalCursorProperty().get();
    }

    /**
     * Sets the value of {@link #showHorizontalCursorProperty()}.
     *
     * @param show if true a horizontal cursor line will be shown
     * @since 1.0
     */
    public final void setShowHorizontalCursor(boolean show) {
        showHorizontalCursorProperty().set(show);
    }

    private final BooleanProperty showMarkedTimeInterval = new SimpleBooleanProperty(
            this, "showMarkedTimeInterval", true);

    /**
     * A property used to control whether vertical lines will be shown for a
     * marked time interval (e.g. while dragging the marked interval will
     * display the new location of the dragged activity).
     *
     * @return a property used for controlling the visibility of a horizontal
     * cursor line
     * @see Eventline#markedTimeIntervalProperty
     * @since 1.1
     */
    public final BooleanProperty showMarkedTimeIntervalProperty() {
        return showMarkedTimeInterval;
    }

    /**
     * Returns the value of {@link #showMarkedTimeIntervalProperty()}.
     *
     * @return true if the marker lines will be shown
     * @see Eventline#markedTimeIntervalProperty
     * @since 1.1
     */
    public final boolean isShowMarkedTimeInterval() {
        return showMarkedTimeIntervalProperty().get();
    }

    /**
     * Sets the value of {@link #showMarkedTimeIntervalProperty()}.
     *
     * @param show if true marker lines will be drawn for the currently marked
     *             time interval
     * @see Eventline#markedTimeIntervalProperty
     * @since 1.1
     */
    public final void setShowMarkedTimeInterval(boolean show) {
        showMarkedTimeIntervalProperty().set(show);
    }

    // Debug mode.

    private final BooleanProperty debugMode = new SimpleBooleanProperty(this,
            "debugMode", false);

    /**
     * A property used to enable / disable the debug mode. The debug mode will
     * cause the object bounds of activities to be rendered in the graphics view
     * and also the bounds of the lasso selection tool. Other information might
     * get added in the future.
     *
     * @return a property used to enable / disable the debug mode
     * @since 1.0
     */
    public final BooleanProperty debugModeProperty() {
        return debugMode;
    }

    /**
     * Returns the value of {@link #debugModeProperty()}.
     *
     * @return true if the debug mode is enabled
     * @since 1.0
     */
    public final boolean isDebugMode() {
        return debugModeProperty().get();
    }

    /**
     * Sets the value of {@link #debugModeProperty()}.
     *
     * @param debug if true the debug mode is enabled
     * @since 1.0
     */
    public final void setDebugMode(boolean debug) {
        debugModeProperty().set(debug);
    }

    // Autogrid support.

    private final BooleanProperty autoGridEnabled = new SimpleBooleanProperty(this,
            "autoGrid", false);

    /**
     * A property used to enable / disable the autogrid mode. The autogrid mode
     * will cause activities to snap to times based on the currently shown
     * granularity of the dateline. If the dateline is showing "days" then the
     * activities will snap to the beginning and / or end of a day. If the
     * dateline is showing hours then the activities will snap to full hours.
     *
     * @return a property used to enable / disable the debug mode
     * @since 1.1
     */
    public final BooleanProperty autoGridEnabledProperty() {
        return autoGridEnabled;
    }

    /**
     * Returns the value of {@link #autoGridEnabledProperty()}.
     *
     * @return true if the autogrid mode is enabled
     * @since 1.1
     */
    public final boolean isAutoGridEnabled() {
        return autoGridEnabled.get();
    }

    /**
     * Sets the value of {@link #autoGridEnabledProperty()}.
     *
     * @param auto if true the autogrid mode is enabled
     * @since 1.1
     */
    public final void setAutoGridEnabled(boolean auto) {
        autoGridEnabled.set(auto);
    }

    private final ReadOnlyBooleanWrapper gridEnabled = new ReadOnlyBooleanWrapper(
            this, "gridEnabled");

    /**
     * A convenience read-only property to check whether any kind of grid is
     * active, either the automatic grid or a virtual grid.
     *
     * @return true if the graphics is using a grid for its editing operations
     * @see #autoGridEnabledProperty()
     * @see #getVirtualGrid()
     * @since 1.2
     */
    public final ReadOnlyBooleanProperty gridEnabledProperty() {
        return gridEnabled.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #gridEnabledProperty()}.
     *
     * @return true if any kind of grid support is enabled
     * @since 1.2
     */
    public final boolean isGridEnabled() {
        return gridEnabled.get();
    }

    // Selection support.

    /**
     * An enumerator used to control the selection behaviour of the graphics
     * view.
     *
     * @see GraphicsBase#setSelectionMode(SelectionMode)
     * @since 1.0
     */
    public enum SelectionMode {
        SINGLE, MULTIPLE, NONE
    }

    private final ObjectProperty<SelectionMode> selectionMode = new SimpleObjectProperty<>(
            this, "selectionMode", SelectionMode.MULTIPLE);

    /**
     * A property used to store the currently supported selection mode. The
     * graphics view supports single, multiple, and none.
     *
     * @return the property used to store the selection mode
     * @since 1.0
     */
    public final ObjectProperty<SelectionMode> selectionModeProperty() {
        return selectionMode;
    }

    /**
     * Returns the value of {@link #selectionModeProperty()}.
     *
     * @return the currently used selection mode (single, all, none)
     * @since 1.0
     */
    public final SelectionMode getSelectionMode() {
        return selectionMode.get();
    }

    /**
     * Sets the value of {@link #selectionModeProperty()}.
     *
     * @param mode the new selection mode
     * @since 1.0
     */
    public final void setSelectionMode(SelectionMode mode) {
        requireNonNull(mode);
        selectionModeProperty().set(mode);
    }

    private final ObservableList<ActivityRef<?>> selectedActivities = FXCollections
            .observableArrayList();

    /**
     * Returns the list of currently selected activities.
     *
     * @return the list of selected activities
     * @since 1.0
     */
    public final ObservableList<ActivityRef<?>> getSelectedActivities() {
        return selectedActivities;
    }

    /**
     * An enumerator used to control the selection behaviour of the lasso. In
     * some applications it is sufficient when the bounds of an activity
     * <b>intersect</b> with the bounds of the lasso, in others the activities
     * need to be completely <b>contained</b> within the lasso bounds.
     *
     * @see GraphicsBase#setLassoSelectionBehaviour(LassoSelectionBehaviour)
     * @since 1.0
     */
    public enum LassoSelectionBehaviour {

        /**
         * A value indicating to the lasso selection that a simple intersection
         * of the bounds of an activity with the bounds of the lasso is
         * sufficient for the selection of the activity.
         *
         * @since 1.0
         */
        INTERSECTION,

        /**
         * A value indicating to the lasso selection that the time interval of
         * an activity has to be completely contained within the time interval
         * defined by the lasso in order for the activity to become selected.
         *
         * @since 1.0
         */
        TIME_INTERVAL_CONTAINMENT,

        /**
         * A value indicating to the lasso selection that the bounds of an
         * activity need to be completely contained within the bounds of the
         * lasso in order for the activity to become selected.
         *
         * @since 1.0
         */
        BOUNDS_CONTAINMENT,
    }

    private final ObjectProperty<LassoSelectionBehaviour> lassoSelectionBehaviour = new SimpleObjectProperty<>(
            this, "selectionBehaviour", LassoSelectionBehaviour.INTERSECTION);

    /**
     * A property used to store the currently used lasso selection behaviour.
     * This value of this property controls when an activity is actually
     * considered selected by the lasso: does it need to be completely inside
     * the lasso bounds or is it enough when it gets touched by the lasso?
     *
     * @return the property used to store the lasso selection behaviour
     * @since 1.0
     */
    public final ObjectProperty<LassoSelectionBehaviour> lassoSelectionBehaviourProperty() {
        // TODO: add graphic from FlexGantt presentations to javadocs
        return lassoSelectionBehaviour;
    }

    /**
     * Sets the value of {@link #lassoSelectionBehaviourProperty()}.
     *
     * @param behaviour the lasso selection behaviour to use
     * @since 1.0
     */
    public final void setLassoSelectionBehaviour(
            LassoSelectionBehaviour behaviour) {
        requireNonNull(behaviour);
        lassoSelectionBehaviourProperty().set(behaviour);
    }

    /**
     * Returns the value of the {@link #lassoSelectionBehaviourProperty()}.
     *
     * @return the currently used lasso selection behaviour
     * @since 1.0
     */
    public final LassoSelectionBehaviour getLassoSelectionBehaviour() {
        return lassoSelectionBehaviourProperty().get();
    }

    // Global calendar support

    private final ObservableList<Calendar<? extends CalendarActivity>> calendars = FXCollections
            .observableArrayList();

    /**
     * Returns the list of calendars that are registered with the graphics view.
     * Calendars are used to render static information in the background of each
     * row. One example are the days that are considered weekend days (e.g.
     * saturday and sunday). They will be drawn with a gray background.
     *
     * @return the calendars drawn by the graphics view
     * @see CalendarLayer#setCalendarActivityRenderer(Class,
     * com.flexganttfx.view.graphics.renderer.CalendarActivityRenderer)
     * @since 1.0
     */
    public final ObservableList<Calendar<? extends CalendarActivity>> getCalendars() {
        return calendars;
    }

    // Show.... methods

    /**
     * Makes the {@link Timeline} start with the earliest time used by the
     * currently loaded rows.
     *
     * @see #getEarliestTimeUsed()
     * @see Timeline#showTime(Instant, boolean)
     * @see ActivityRepository#getEarliestTimeUsed()
     * @since 1.0
     */
    public final void showEarliestActivities() {
        Instant time = getEarliestTimeUsed();

        if (time != null) {
            getTimeline().showTime(time, false);
        }
    }

    /**
     * Makes the {@link Timeline} show the latest time used by the currently
     * loaded rows.
     *
     * @see #getLatestTimeUsed()
     * @see Timeline#showTime(Instant, boolean)
     * @see ActivityRepository#getLatestTimeUsed()
     * @since 1.0
     */
    public final void showLatestActivities() {
        Instant time = getLatestTimeUsed();

        if (time != null) {
            getTimeline().showTime(time, true);
        }
    }

    /**
     * Makes the {@link Timeline} show a time range starting with the earliest
     * time used and ending with the latest time used by all currently loaded
     * rows.
     *
     * @see Timeline#showRange(Instant, Instant)
     * @see #getLatestTimeUsed()
     * @see #getEarliestTimeUsed()
     * @see ActivityRepository#getEarliestTimeUsed()
     * @see ActivityRepository#getLatestTimeUsed()
     * @since 1.0
     */
    public final void showAllActivities() {
        Instant earliestTime = getEarliestTimeUsed();
        Instant latestTime = getLatestTimeUsed();

        if (earliestTime != null && latestTime != null) {
            getTimeline().showRange(earliestTime, latestTime);
        }
    }

    /**
     * Calculates and returns the earliest time used by all rows in the model.
     *
     * @return the earliest time used by the graphics view
     * @see Row#getEarliestTimeUsed()
     * @see ActivityRepository#getEarliestTimeUsed()
     * @since 1.0
     */
    public final Instant getEarliestTimeUsed() {
        Instant time = null;

        for (R row : getRows()) {

            if (row != null) {
                Instant earliest = row.getEarliestTimeUsed();
                if (earliest != null) {
                    if (time == null || earliest.isBefore(time)) {
                        time = earliest;
                    }
                }
            }

        }

        return time;
    }

    /**
     * Calculates and returns the latest time used by all rows in the model.
     *
     * @return the latest time used by the graphics view
     * @see Row#getLatestTimeUsed()
     * @see ActivityRepository#getLatestTimeUsed()
     * @since 1.0
     */
    public final Instant getLatestTimeUsed() {
        Instant time = null;

        for (R row : getRows()) {

            if (row != null) {
                Instant latest = row.getLatestTimeUsed();
                if (latest != null) {
                    if (time == null || latest.isAfter(time)) {
                        time = latest;
                    }
                }
            }

        }

        return time;
    }

    // Hover activity support.

    private final ReadOnlyObjectWrapper<ActivityRef<?>> hoverActivity = new ReadOnlyObjectWrapper<>(this, "hoverActivity");

    public final ReadOnlyObjectProperty<ActivityRef<?>> hoverActivityProperty() {
        return hoverActivity;
    }

    public final ActivityRef<?> getHoverActivity() {
        return hoverActivityProperty().get();
    }

    // Hover row support.

    private final ReadOnlyObjectWrapper<R> hoverRow = new ReadOnlyObjectWrapper<>(this, "hoverRow");

    public final ReadOnlyObjectProperty<R> hoverRowProperty() {
        return hoverRow;
    }

    public final R getHoverRow() {
        return hoverRowProperty().get();
    }

    // Hover layout support.

    private final ReadOnlyObjectWrapper<Layout> hoverLayout = new ReadOnlyObjectWrapper<>(this, "hoverLayout");

    public final ReadOnlyObjectProperty<Layout> hoverLayoutProperty() {
        return hoverLayout;
    }

    public final Layout getHoverLayout() {
        return hoverLayoutProperty().get();
    }

    // Edited activity support.

    private final ReadOnlyObjectWrapper<ActivityRef<?>> editedActivity = new ReadOnlyObjectWrapper<>(this, "editedActivity");

    public final ReadOnlyObjectProperty<ActivityRef<?>> editedActivityProperty() {
        return editedActivity.getReadOnlyProperty();
    }

    public final ActivityRef<?> getEditedActivity() {
        return editedActivityProperty().get();
    }

    // Pressed activity support.

    private final ReadOnlyObjectWrapper<ActivityRef<?>> pressedActivity = new ReadOnlyObjectWrapper<>(this, "pressedActivity");

    public final ReadOnlyObjectProperty<ActivityRef<?>> pressedActivityProperty() {
        return pressedActivity;
    }

    public final ActivityRef<?> getPressedActivity() {
        return pressedActivityProperty().get();
    }

    // Grid support.

    private final ObjectProperty<VirtualGrid<?>> virtualGrid = new SimpleObjectProperty<>(this, "virtualGrid");

    public final ObjectProperty<VirtualGrid<?>> virtualGridProperty() {
        return virtualGrid;
    }

    public final VirtualGrid<?> getVirtualGrid() {
        return virtualGridProperty().get();
    }

    public final void setVirtualGrid(VirtualGrid<?> grid) {
        virtualGridProperty().set(grid);
    }

    private final ObservableList<VirtualGrid<?>> virtualGrids = FXCollections
            .observableArrayList();

    public final ObservableList<VirtualGrid<?>> getVirtualGrids() {
        return virtualGrids;
    }

    // Placeholder support.

    private final ObjectProperty<Node> placeholder = new SimpleObjectProperty<>(this, "node");

    public final ObjectProperty<Node> placeholderProperty() {
        return placeholder;
    }

    public final Node getPlaceholder() {
        return placeholderProperty().get();
    }

    public final void setPlaceholder(Node node) {
        placeholderProperty().set(node);
    }

    private final ObservableList<RowPane<R>> rowPanes = FXCollections.observableArrayList();

    public final ObservableList<RowPane<R>> getRowPanes() {
        return rowPanes;
    }

    private LinksCanvas<R> linksCanvas;

    /**
     * Performs a redraw of the displayed activities. Also lays out the links
     * shown by the {@link LinksCanvas}.
     */
    public void redraw() {
        redraw("complete redraw", null);
    }

    /**
     * Performs a redraw of the displayed activities and logs the given reason. Also lays out the links
     * shown by the {@link LinksCanvas}.
     */
    public void redraw(String reason) {
        redraw(reason, null);
    }

    private void redraw(String reason, Instant oldTime) {
        if (LoggingDomain.RENDERING.isLoggable(Level.FINE)) {
            LoggingDomain.RENDERING.finer("row cells list size = " + getRowPanes().size());
        }

        if (LoggingDomain.PERFORMANCE.isLoggable(Level.FINE)) {

            Instant timeBefore = Instant.now();

            drawRows(reason, oldTime);
            drawLinks(reason);

            Instant timeAfter = Instant.now();
            java.time.Duration duration = java.time.Duration.between(timeBefore, timeAfter);
            LoggingDomain.PERFORMANCE.fine("redraw duration in millis: " + duration.toMillis());

        } else {

            drawRows(reason, oldTime);
            drawLinks(reason);

        }

    }

    public void drawLinks(String reason) {
        if (linksCanvas == null) {
            linksCanvas = (LinksCanvas<R>) lookup("LinksCanvas");
        }

        if (linksCanvas != null) {
            linksCanvas.requestRedraw(reason);
        }
    }

    private void drawRows(String reason, Instant oldTime) {
        if (getTimeline() != null && getTimeline().getModel() != null) {

            for (RowPane<R> pane : getRowPanes()) {
                if (pane.isVisible()) {

                    // Fix for FLEXFX-340: "Links are not being rendered properly after sorting rows"
                    final R row = pane.getRow();
                    if (row != null && !row.isShowing()) {
                        row.getProperties().put("com.flexganttfx.row.showing", true);
                    }

                    if (oldTime != null) {
                        pane.getCanvas().requestRedraw(reason, oldTime);
                    } else {
                        pane.getCanvas().requestRedraw(reason);
                    }
                }
            }
        }
    }

    // Activity Renderer support

    private final ObservableMap<Class<? extends Layout>, ObservableMap<Class<?>, ActivityRenderer<?>>> rendererLayoutMap = FXCollections.observableHashMap();

    private final ObservableMap<Class<? extends Layout>, ObservableMap<Class<?>, ActivityRenderer<?>>> cachedRendererMap = FXCollections.observableHashMap();

    private Map<Class<?>, ActivityRenderer<?>> getRendererMapForLayoutStrategy(Class<? extends Layout> layoutType) {
        return rendererLayoutMap.computeIfAbsent(layoutType, k -> {
            ObservableMap<Class<?>, ActivityRenderer<?>> map = FXCollections.observableHashMap();
            return map;
        });
    }

    /**
     * Returns a list of all currently registered activity renderers.
     *
     * @return all activity renderers
     * @since 8.9.0
     */
    public final List<ActivityRenderer<?>> getAllActivityRenderers() {
        List<ActivityRenderer<?>> list = new ArrayList<>();
        for (Map<Class<?>, ActivityRenderer<?>> rendererMap : rendererLayoutMap.values()) {
            list.addAll(rendererMap.values());
        }
        return list;
    }

    /**
     * Registers a renderer for the given activity and layout type. The renderer will
     * be used to "draw" any activity of the given type when the activity is laid out
     * via the given layout.
     *
     * @param activityType the type of the activity
     * @param layoutType   the type of the layout
     * @param renderer     the renderer instance
     * @param <A>          the type of the activity
     */
    public final <A extends Activity> void setActivityRenderer(
            Class<? extends A> activityType, Class<? extends Layout> layoutType,
            ActivityRenderer<? extends A> renderer) {

        requireNonNull(activityType);
        requireNonNull(layoutType);

        cachedRendererMap.clear();

        if (renderer != null) {
            LoggingDomain.CONFIG.fine("activity type = " + activityType + ", layout type " + layoutType + ", renderer = " + renderer.getClass().getName());
        } else {
            LoggingDomain.CONFIG.fine("activity type = " + activityType + ", layout type " + layoutType + ", renderer = null");
        }

        if (renderer != null) {
            for (ObservableMap<Class<?>, ActivityRenderer<?>> layoutMap : rendererLayoutMap.values()) {
                for (ActivityRenderer<?> r : layoutMap.values()) {
                    if (r != null && r.getName().equals(renderer.getName())) {
                        throw new IllegalArgumentException(
                                "a renderer with name "
                                        + renderer.getName()
                                        + " is already registered for the given layout type "
                                        + layoutType.getName());
                    }
                }
            }
        }

        Map<Class<?>, ActivityRenderer<?>> rendererMap = getRendererMapForLayoutStrategy(layoutType);
        rendererMap.put(activityType, renderer);
    }

    private final ActivityRenderer<?> defaultGanttActivityRenderer = new ChartActivityRenderer<>(this, "Default Gantt Activity Renderer");

    private final ActivityRenderer<?> defaultChartActivityRenderer = new ChartActivityRenderer<>(this, "Default Chart Activity Renderer");

    private final ActivityRenderer<?> defaultAgendaActivityRenderer = new ChartActivityRenderer<>(this, "Default Agenda Activity Renderer");

    @SuppressWarnings("unchecked")
    public final <A extends Activity> ActivityRenderer<? extends A> getActivityRenderer(Class<? extends A> activityType, Class<? extends Layout> layoutType) {

        requireNonNull(activityType);
        requireNonNull(layoutType);

        ObservableMap<Class<?>, ActivityRenderer<?>> cache = cachedRendererMap
                .computeIfAbsent(layoutType,
                        it -> FXCollections.observableHashMap());

        ActivityRenderer<? extends A> renderer = (ActivityRenderer<? extends A>) cache
                .computeIfAbsent(activityType, it -> doGetActivityRenderer(
                        getRendererMapForLayoutStrategy(layoutType),
                        activityType));

        if (renderer == null) {
            if (layoutType.equals(ChartLayout.class)) {
                return (ActivityRenderer<? extends A>) defaultChartActivityRenderer;
            } else if (layoutType.equals(GanttLayout.class)) {
                return (ActivityRenderer<? extends A>) defaultGanttActivityRenderer;
            } else if (layoutType.equals(AgendaLayout.class)) {
                return (ActivityRenderer<? extends A>) defaultAgendaActivityRenderer;
            }
        }

        return renderer;
    }

    private <A extends Activity> ActivityRenderer<A> doGetActivityRenderer(Map<Class<?>, ? extends ActivityRenderer<?>> map, Class<?> clazz) {
        if (clazz != null) {
            @SuppressWarnings("unchecked")
            ActivityRenderer<A> renderer = (ActivityRenderer<A>) map.get(clazz);
            if (renderer == null) {
                return doGetActivityRenderer(map, clazz.getSuperclass());
            }
            return renderer;
        }

        return null;
    }

    // Link renderer support.

    private final ObservableMap<Class<?>, LinkRenderer<?>> linkRendererMap = FXCollections.observableHashMap();

    private final ObservableMap<Class<?>, LinkRenderer<?>> linkRendererCache = FXCollections.observableHashMap();

    /**
     * Sets a custom link renderer for the given type of activity link.
     *
     * @param clazz    the activity type
     * @param renderer the renderer
     */
    public final void setLinkRenderer(Class<? extends Activity> clazz, LinkRenderer<?> renderer) {
        linkRendererCache.clear();

        if (renderer != null) {
            LoggingDomain.CONFIG.fine("class = " + clazz + ", renderer = " + renderer.getClass().getName());
        } else {
            LoggingDomain.CONFIG.fine("class = " + clazz + ", renderer = null");
        }

        requireNonNull(clazz);

        linkRendererMap.put(clazz, renderer);
    }

    /**
     * Returns a renderer for the given activity link type.
     *
     * @param clazz the activity link type
     * @param <AL>  the activity link type
     * @return the link renderer
     */
    public final <AL extends ActivityLink<?>> LinkRenderer<AL> getLinkRenderer(Class<AL> clazz) {
        LinkRenderer<AL> cachedRenderer = (LinkRenderer<AL>) linkRendererCache.get(clazz);
        if (cachedRenderer != null) {
            return cachedRenderer;
        }

        LinkRenderer<AL> renderer = (LinkRenderer<AL>) doGetLinkRenderer(clazz);
        linkRendererCache.put(clazz, renderer);
        return renderer;
    }

    private LinkRenderer<?> doGetLinkRenderer(Class<?> clazz) {
        if (clazz != null) {
            LinkRenderer<?> renderer = linkRendererMap.get(clazz);
            if (renderer == null) {
                return doGetLinkRenderer(clazz.getSuperclass());
            }

            return renderer;
        }

        return null;
    }

    // Editing policies

    /**
     * An enumeration of possible editing states that the graphics view can be
     * in. The state gets determined every time when a mouse move event gets
     * received. Depending on the location of the mouse cursor different states
     * are entered. This behaviour can be highly application-dependent. Some
     * apps want the user to be able to change the start time of a timeline
     * object when the mouse cursor is on the left edge of the object, other
     * apps want to be able to change the objects percentage complete value that
     * way.
     *
     * @since 1.0
     */
    public enum EditMode {
        NONE,

        DELETING,

        START_TIME_CHANGE,

        END_TIME_CHANGE,

        PERCENTAGE_COMPLETE_CHANGE,

        DRAGGING_HORIZONTAL,

        DRAGGING_VERTICAL,

        DRAGGING,

        AGENDA_START_TIME_CHANGE,

        AGENDA_END_TIME_CHANGE,

        AGENDA_DRAGGING,

        AGENDA_ASSIGNING,

        CHART_VALUE_CHANGE,

        CHART_VALUE_HIGH_CHANGE,

        CHART_VALUE_LOW_CHANGE
    }

    /**
     * A callback parameter object used for determining if the proposed
     * {@link EditMode} is currently allowed or not. The editing callback is
     * used to enable / disable specific editing operations.
     *
     * @see GraphicsBase#setActivityEditingCallback(Class, Callback)
     * @since 1.0
     */
    public static final class EditingCallbackParameter {

        private final ActivityRef<?> activityRef;

        private final EditMode editMode;

        public EditingCallbackParameter(ActivityRef<?> activityRef,
                                        EditMode editMode) {

            requireNonNull(activityRef);
            requireNonNull(editMode);

            this.activityRef = activityRef;
            this.editMode = editMode;
        }

        public final ActivityRef<?> getActivityRef() {
            return activityRef;
        }

        public final EditMode getEditMode() {
            return editMode;
        }
    }

    private final ObservableMap<Class<?>, Callback<EditingCallbackParameter, Boolean>> activityEditingCallbackMap = FXCollections
            .observableHashMap();

    /**
     * Registers a callback used to determine if a given editing operation can
     * be used for a given activity.
     *
     * @param activityType the type of the activity for which to use the callback
     * @param callback     the callback
     * @since 1.0
     */
    public final void setActivityEditingCallback(
            Class<? extends MutableActivity> activityType,
            Callback<EditingCallbackParameter, Boolean> callback) {

        requireNonNull(activityType);

        activityEditingCallbackMap.put(activityType, callback);
    }

    public final <A extends Activity> Callback<EditingCallbackParameter, Boolean> getActivityEditingCallback(
            Class<A> activityType) {
        return doGetEditingCallback(activityType);
    }

    private Callback<EditingCallbackParameter, Boolean> doGetEditingCallback(
            Class<?> activityType) {
        if (activityType != null) {
            Callback<EditingCallbackParameter, Boolean> callback = activityEditingCallbackMap
                    .get(activityType);
            if (callback == null) {
                return doGetEditingCallback(activityType.getSuperclass());
            }

            return callback;
        }

        return null;
    }

    // Drag & Drop Support

    private final ObservableMap<Class<?>, Callback<DragAndDropInfo, Boolean>> dragAndDropCallbackMap = FXCollections
            .observableHashMap();

    /**
     * Specifies a callback that will be invoked when the user drags an activity
     * over a row of the given type. The callback implementation then determines
     * if a drop would be accepted in the given row.
     *
     * @param rowType  the type of the row for which the callback gets registered
     * @param callback the callback implementation
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
    public final void setRowDragAndDropCallback(Class<? extends Row> rowType, Callback<DragAndDropInfo, Boolean> callback) {
        requireNonNull(rowType);
        dragAndDropCallbackMap.put(rowType, callback);
    }

    /**
     * Returns a callback that will be invoked when the user drags an activity
     * over a row of the given type. The callback implementation then determines
     * if a drop would be accepted in the given row.
     *
     * @param rowType the type of the row for which the callback gets registered
     * @return the callback implementation
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
    public final Callback<DragAndDropInfo, Boolean> getRowDragAndDropCallback(Class<? extends Row> rowType) {
        return doGetRowDragAndDropCallback(rowType);
    }

    private Callback<DragAndDropInfo, Boolean> doGetRowDragAndDropCallback(Class<?> rowType) {
        if (rowType != null) {
            Callback<DragAndDropInfo, Boolean> callback = dragAndDropCallbackMap.get(rowType);
            if (callback == null) {
                return doGetRowDragAndDropCallback(rowType.getSuperclass());
            }

            return callback;
        }

        return null;
    }

    private final ReadOnlyObjectWrapper<DragAndDropInfo> dragAndDropInfo = new ReadOnlyObjectWrapper<>(this, "dragAndDropInfo");

    /**
     * A property used to store the current drag and drop information. This
     * object stores data relevant to the current drag and drop operation.
     *
     * @return the property used to store the current drag and drop information
     * @since 1.0
     */
    public final ReadOnlyObjectProperty<DragAndDropInfo> dragAndDropInfoProperty() {
        return dragAndDropInfo.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #dragAndDropInfoProperty()}.
     *
     * @return the current drag and drop information
     * @since 1.0
     */
    public final DragAndDropInfo getDragAndDropInfo() {
        return dragAndDropInfoProperty().get();
    }

    private final ObjectProperty<Callback<ActivityRef<?>, Image>> dragImageProvider = new SimpleObjectProperty<>(this, "dragImageProvider");

    public final ObjectProperty<Callback<ActivityRef<?>, Image>> dragImageProviderProperty() {
        return dragImageProvider;
    }

    public final void setDragImageProvider(Callback<ActivityRef<?>, Image> provider) {
        dragImageProvider.set(provider);
    }

    public final Callback<ActivityRef<?>, Image> getDragImageProvider() {
        return dragImageProvider.get();
    }

    /**
     * The drag and drop info class aggregates the various pieces of information
     * that the application might be interested in while a drag and drop
     * operation is in progress.
     *
     * @see GraphicsBase#dragAndDropInfoProperty()
     * @since 1.0
     */
    public static class DragAndDropInfo {

        private final Row<?, ?, ?> row;

        private final DragEvent dragEvent;

        private final ActivityBounds activityBounds;

        private final TimeInterval dropInterval;

        private final Point2D offset;
        private final List<ActivityBounds> selectedActivities;

        public DragAndDropInfo(Row<?, ?, ?> row, ActivityBounds activityBounds, List<ActivityBounds> selectedActivities,
                               TimeInterval dropInterval, DragEvent dragEvent,
                               Point2D offset) {

            /*
             * Row can be NULL (empty row / unused row)
             */
            this.row = row;
            this.activityBounds = requireNonNull(activityBounds);
            this.selectedActivities = requireNonNull(selectedActivities);
            this.dropInterval = requireNonNull(dropInterval);
            this.dragEvent = requireNonNull(dragEvent);
            this.offset = requireNonNull(offset);
        }

        /**
         * The row where the drop might occur.
         *
         * @return the row where drop happens
         * @since 1.0
         */
        public Row<?, ?, ?> getRow() {
            return row;
        }

        /**
         * Returns the dragged / dropped activity (bounds).
         *
         * @return the activity bounds
         * @since 1.0
         */
        public ActivityBounds getActivityBounds() {
            return activityBounds;
        }

        /**
         * Returns the activities that were selected when the user initiated the drag.
         *
         * @return the selected activities
         * @since 1.6
         */
        public List<ActivityBounds> getSelectedActivities() {
            return selectedActivities;
        }

        /**
         * Returns the activity being dragged.
         *
         * @return the dragged activity
         * @since 1.2
         */
        public ActivityRef<?> getActivityRef() {
            return activityBounds.getActivityRef();
        }

        /**
         * Returns the drag event that triggered the callback.
         *
         * @return the drag event
         * @since 1.0
         */
        public DragEvent getDragEvent() {
            return dragEvent;
        }

        /**
         * Returns the time interval where the drop would take place.
         *
         * @return the the drop time interval
         * @since 1.0
         */
        public TimeInterval getDropInterval() {
            return dropInterval;
        }

        /**
         * Returns the mouse offset relative to the upper left corner of the
         * activity.
         *
         * @return the mouse offset
         * @since 1.1
         */
        public Point2D getOffset() {
            return offset;
        }

        @Override
        public String toString() {
            return "drag info = target row: "
                    + (row != null ? row.getName() : "<No Row>")
                    + ", source row: " + activityBounds.getRow().getName()
                    + ", activity: " + activityBounds.getActivity().getName()
                    + ", time interval: " + dropInterval.toString()
                    + ", drag event: " + dragEvent.toString();
        }
    }

    // Background & foreground Layers

    private final ObservableList<SystemLayer<R>> backgroundLayers = FXCollections.observableArrayList();
    private final ObservableList<SystemLayer<R>> foregroundLayers = FXCollections.observableArrayList();

    @SuppressWarnings("unchecked")
    private <SL extends SystemLayer<R>> SL doGetLayer(Class<SL> layerType, List<SystemLayer<R>> layers) {
        for (SystemLayer<R> layer : layers) {
            if (layer.getClass().isAssignableFrom(layerType)) {
                return (SL) layer;
            }
        }

        return null;
    }

    public final <SL extends SystemLayer<R>> SL getBackgroundSystemLayer(
            Class<SL> layerType) {
        return doGetLayer(layerType, backgroundLayers);
    }

    public final <SL extends SystemLayer<R>> SL getForegroundSystemLayer(
            Class<SL> layerType) {
        return doGetLayer(layerType, foregroundLayers);
    }

    public final <SL extends SystemLayer<R>> SL getSystemLayer(
            Class<SL> layerType) {
        SL layer = doGetLayer(layerType, backgroundLayers);
        if (layer == null) {
            layer = doGetLayer(layerType, foregroundLayers);
        }

        return layer;
    }

    public final ObservableList<SystemLayer<R>> getBackgroundSystemLayers() {
        return backgroundLayers;
    }

    public final ObservableList<SystemLayer<R>> getForegroundSystemLayers() {
        return foregroundLayers;
    }

    // Support for showing agenda lines

    private final BooleanProperty showAgendaLinesLayer = new SimpleBooleanProperty(
            this, "showAgendaLinesLayer", true);

    public final BooleanProperty showAgendaLinesLayerProperty() {
        return showAgendaLinesLayer;
    }

    public final void setShowAgendaLinesLayer(boolean show) {
        showAgendaLinesLayerProperty().set(show);
    }

    public final boolean isShowAgendaLinesLayer() {
        return showAgendaLinesLayerProperty().get();
    }

    // Support for showing calendars

    private final BooleanProperty showCalendarLayer = new SimpleBooleanProperty(this,
            "showCalendarLayer", true);

    public final BooleanProperty showCalendarLayerProperty() {
        return showCalendarLayer;
    }

    public final void setShowCalendarLayer(boolean show) {
        showCalendarLayerProperty().set(show);
    }

    public final boolean isShowCalendarLayer() {
        return showCalendarLayerProperty().get();
    }

    // Support for layout layer

    private final BooleanProperty showLayoutLayer = new SimpleBooleanProperty(this, "showLayoutLayer", true);

    public final BooleanProperty showLayoutLayerProperty() {
        return showLayoutLayer;
    }

    public final void setShowLayoutLayer(boolean show) {
        showLayoutLayerProperty().set(show);
    }

    public final boolean isShowLayoutLayer() {
        return showLayoutLayerProperty().get();
    }

    // Support for showing chart lines

    private final BooleanProperty showChartLinesLayer = new SimpleBooleanProperty(this, "showChartLinesLayer", true);

    public final BooleanProperty showChartLinesLayerProperty() {
        return showChartLinesLayer;
    }

    public final void setShowChartLinesLayer(boolean show) {
        showChartLinesLayerProperty().set(show);
    }

    public final boolean isShowChartLinesLayer() {
        return showChartLinesLayerProperty().get();
    }

    // Support for showing grid lines

    private final BooleanProperty showGridLineLayer = new SimpleBooleanProperty(this,
            "showGridLineLayer", true);

    public final BooleanProperty showGridLineLayerProperty() {
        return showGridLineLayer;
    }

    public final void setShowGridLineLayer(boolean show) {
        showGridLineLayerProperty().set(show);
    }

    public final boolean isShowGridLineLayer() {
        return showGridLineLayerProperty().get();
    }

    // Hover time interval support.

    private final BooleanProperty showHoverTimeIntervalLayer = new SimpleBooleanProperty(this, "showHoverTimeIntervalLayer", false);

    public final BooleanProperty showHoverTimeIntervalLayerProperty() {
        return showHoverTimeIntervalLayer;
    }

    public final void setShowHoverTimeIntervalLayer(boolean show) {
        showHoverTimeIntervalLayerProperty().set(show);
    }

    public final boolean isShowHoverTimeIntervalLayer() {
        return showHoverTimeIntervalLayerProperty().get();
    }

    // Support for showing inner lines

    private final BooleanProperty showInnerLinesLayer = new SimpleBooleanProperty(this, "showInnerLinesLayer", true);

    public final BooleanProperty showInnerLinesLayerProperty() {
        return showInnerLinesLayer;
    }

    public final void setShowInnerLinesLayer(boolean show) {
        showInnerLinesLayerProperty().set(show);
    }

    public final boolean isShowInnerLinesLayer() {
        return showInnerLinesLayerProperty().get();
    }

    // Support for showing now line

    private final BooleanProperty showNowLineLayer = new SimpleBooleanProperty(this, "showNowLineLayer", true);

    public final BooleanProperty showNowLineLayerProperty() {
        return showNowLineLayer;
    }

    public final void setShowNowLineLayer(boolean show) {
        showNowLineLayerProperty().set(show);
    }

    public final boolean isShowNowLineLayer() {
        return showNowLineLayerProperty().get();
    }

    // Support for showing DST line

    private final BooleanProperty showDSTLineLayer = new SimpleBooleanProperty(this, "showDSTLineLayer", true);

    public final BooleanProperty showDSTLineLayerProperty() {
        return showDSTLineLayer;
    }

    public final void setShowDSTLineLayer(boolean show) {
        showDSTLineLayerProperty().set(show);
    }

    public final boolean isShowDSTLineLayer() {
        return showDSTLineLayerProperty().get();
    }

    // Support for showing row layer.

    private final BooleanProperty showRowLayer = new SimpleBooleanProperty(this, "showRowLayer", true);

    public final BooleanProperty showRowLayerProperty() {
        return showRowLayer;
    }

    public final void setShowRowLayer(boolean show) {
        showRowLayerProperty().set(show);
    }

    public final boolean isShowRowLayer() {
        return showRowLayerProperty().get();
    }

    // Selected time intervals support.

    private final BooleanProperty showSelectedTimeIntervalsLayer = new SimpleBooleanProperty(this, "showSelectedTimeIntervals", true);

    public final BooleanProperty showSelectedTimeIntervalsLayerProperty() {
        return showSelectedTimeIntervalsLayer;
    }

    public final void setShowSelectedTimeIntervalsLayer(boolean show) {
        showSelectedTimeIntervalsLayerProperty().set(show);
    }

    public final boolean isShowSelectedTimeIntervalsLayer() {
        return showSelectedTimeIntervalsLayerProperty().get();
    }

    // Zoom time interval support.

    private final BooleanProperty showZoomTimeIntervalLayer = new SimpleBooleanProperty(this, "showZoomTimeIntervalLayer", true);

    public final BooleanProperty showZoomTimeIntervalLayerProperty() {
        return showZoomTimeIntervalLayer;
    }

    public final void setShowZoomTimeIntervalLayer(boolean show) {
        showZoomTimeIntervalLayerProperty().set(show);
    }

    public final boolean isShowZoomTimeIntervalLayer() {
        return showZoomTimeIntervalLayerProperty().get();
    }

    // Support for showing row time zones.

    private final BooleanProperty showZoneId = new SimpleBooleanProperty(this, "showZoneId", false);

    public final BooleanProperty showZoneIdProperty() {
        return showZoneId;
    }

    public final void setShowZoneId(boolean show) {
        showZoneIdProperty().set(show);
    }

    public final boolean isShowZoneId() {
        return showZoneIdProperty().get();
    }

    // Animate visibility changes.

    private final BooleanProperty fadeInOutVisibilityChanges = new SimpleBooleanProperty(this, "fadeInOutVisibilityChanges", true);

    public final BooleanProperty fadeInOutVisibilityChangesProperty() {
        return fadeInOutVisibilityChanges;
    }

    public final boolean isFadeInOutVisibilityChanges() {
        return fadeInOutVisibilityChangesProperty().get();
    }

    public final void setFadeInOutVisibilityChanges(boolean show) {
        fadeInOutVisibilityChangesProperty().set(show);
    }

    private final DoubleProperty fadeInOutVisibilityChangesDuration = new SimpleDoubleProperty(this, "fadeInOutVisibilityChangesDuration", 200);

    public final DoubleProperty fadeInOutVisibilityChangesDurationProperty() {
        return fadeInOutVisibilityChangesDuration;
    }

    public final double getFadeInOutVisibilityChangesDuration() {
        return fadeInOutVisibilityChangesDurationProperty().get();
    }

    public final void setFadeInOutVisibilityChangesDuration(double duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("duration must be larger or equal to 0 but was " + duration);
        }
        fadeInOutVisibilityChangesDurationProperty().set(duration);
    }

    // Support for links canvas

    private final BooleanProperty showLinks = new SimpleBooleanProperty(this, "showLinks", true);

    public final boolean isShowLinks() {
        return showLinks.get();
    }

    /**
     * Controls whether the {@link LinksCanvas} will be visible and links will be drawn.
     *
     * @return true if the links will be drawn
     */
    public final BooleanProperty showLinksProperty() {
        return showLinks;
    }

    public final void setShowLinks(boolean showLinks) {
        this.showLinks.set(showLinks);
    }

    // DRAG AND DROP FEEDBACK

    /**
     * An enumerator used to define how to visuzalize the dragged activity
     * during a drag and drop operation.
     *
     * @see GraphicsBase#setDragAndDropFeedback(DragAndDropFeedback)
     * @since 1.1
     */
    public enum DragAndDropFeedback {

        /**
         * A snapshot image of the activity will be taken and placed below the
         * mouse cursor. The image will be set at the moment the drag gesture
         * gets recognized. Optionally a drag image provider can be used. Note:
         * the size of the image might be different than the size of the
         * activity (platform-specific).
         *
         * @see GraphicsBase#setDragImageProvider(Callback)
         * @since 1.1
         */
        NATIVE,

        /**
         * The dragged activity will be constantly rendered on a separate canvas
         * on top of the graphics area. The activity is guaranteed to keep its
         * original size.
         *
         * @since 1.1
         */
        RENDERED,

        /**
         * The dragged activity will be constantly rendered on a separate canvas
         * on top of the graphics area. The activity is guaranteed to keep its
         * original size. The currently active {@link VirtualGrid} will be used
         * to make the dragged activity snap to the grid locations.
         *
         * @since 1.1
         */
        RENDERED_GRID_SNAPPED
    }

    // since 1.1
    private final ObjectProperty<DragAndDropFeedback> dragAndDropFeedback = new SimpleObjectProperty<>(
            this, "dragAndDropFeedback", DragAndDropFeedback.RENDERED);

    // since 1.1
    public final ObjectProperty<DragAndDropFeedback> dragAndDropFeedbackProperty() {
        return dragAndDropFeedback;
    }

    // since 1.1
    public final void setDragAndDropFeedback(DragAndDropFeedback feedback) {
        requireNonNull(feedback);
        dragAndDropFeedback.set(feedback);
    }

    // since 1.1
    public final DragAndDropFeedback getDragAndDropFeedback() {
        return dragAndDropFeedback.get();
    }

    // Row controls support.

    private final ObjectProperty<Callback<RowControlsParameter<R>, Node>> rowControlsFactory = new SimpleObjectProperty<>(this, "rowControls");

    public final ObjectProperty<Callback<RowControlsParameter<R>, Node>> rowControlsFactoryProperty() {
        return rowControlsFactory;
    }

    public final void setRowControlsFactory(Callback<RowControlsParameter<R>, Node> factory) {
        rowControlsFactory.set(factory);
    }

    public final Callback<RowControlsParameter<R>, Node> getRowControlsFactory() {
        return rowControlsFactory.get();
    }

    /**
     * A callback parameter object used to provide context for the row controls
     * factory.
     *
     * @param <R> the row type
     * @see #rowControlsFactoryProperty()
     */
    public static final class RowControlsParameter<R extends Row<?, ?, ?>> {

        private final R row;
        private final GraphicsBase<R> graphics;

        public RowControlsParameter(GraphicsBase<R> graphics, R row) {
            requireNonNull(graphics);
            requireNonNull(row);

            this.graphics = graphics;
            this.row = row;
        }

        public R getRow() {
            return row;
        }

        public GraphicsBase<R> getGraphics() {
            return graphics;
        }
    }

    // support for row resizing

    private final BooleanProperty enableRowResizing = new SimpleBooleanProperty(this, "enableRowResizing", true);

    /**
     * Controls whether the view allows the user to interactively resize the row / change
     * the row height.
     *
     * @return true if the rows can be resized
     * @since 11.12.0
     */
    public final BooleanProperty enableRowResizingProperty() {
        return enableRowResizing;
    }

    public final boolean isEnableRowResizing() {
        return enableRowResizing.get();
    }

    public final void setEnableRowResizing(boolean enableRowResizing) {
        this.enableRowResizing.set(enableRowResizing);
    }

    // Row header support.

    /**
     * A row header is a node that can be displayed to the left of each row inside
     * the graphics area. These headers can be used (for example) to display a scale
     * for the information shown in the canvas area to the right.
     *
     * @param <R> the row type
     */
    public static class RowHeader<R extends Row<?, ?, ?>> extends Label {

        private final GraphicsBase<R> graphics;

        private double startY;

        public RowHeader(GraphicsBase<R> graphics) {
            this.graphics = Objects.requireNonNull(graphics, "graphics can not be null");

            setAlignment(Pos.CENTER);
            getStyleClass().add("row-header");

            addEventHandler(MouseEvent.MOUSE_MOVED, evt -> {
                if (evt.getY() > getHeight() - 4 && getItem() != null) {
                    if (graphics.isEnableRowResizing()) {
                        setCursor(Cursor.V_RESIZE);
                    }
                } else {
                    setCursor(Cursor.DEFAULT);
                }

                // do not consume ... horizontal cursor might need it
            });

            addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
                if (evt.getY() > getHeight() - 4 && graphics.isEnableRowResizing()) {
                    startY = evt.getY();
                } else {
                    startY = -1;
                }
                evt.consume();
            });

            addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
                if (startY != -1) {
                    double delta = evt.getY() - startY;
                    startY = evt.getY();

                    final R row = getItem();

                    if (row != null) {
                        row.setHeight(Math.min(Math.max(row.getHeight() + delta, row.getMinHeight()), row.getMaxHeight()));
                    }
                }

                // consume, or we start scrolling
                evt.consume();
            });

            addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {
                if (evt.isShiftDown() || evt.isShortcutDown()) {
                    final R row = getItem();

                    if (row != null) {
                        double rowHeight = row.getHeight();

                        graphics.getRows().forEach(r -> {
                            if (rowHeight < r.getMinHeight()) {
                                r.setHeight(r.getMinHeight());
                            } else if (rowHeight > r.getMaxHeight()) {
                                r.setHeight(r.getMaxHeight());
                            } else {
                                r.setHeight(rowHeight);
                            }
                        });

                        evt.consume();
                    }
                }
            });
        }

        public final GraphicsBase<R> getGraphics() {
            return graphics;
        }

        private final ObjectProperty<R> item = new SimpleObjectProperty<>(this, "item");

        public final ObjectProperty<R> itemProperty() {
            return item;
        }

        public final R getItem() {
            return item.get();
        }

        public final void setItem(R item) {
            this.item.set(item);
        }
    }

    private final ObjectProperty<Callback<GraphicsBase<R>, RowHeader<R>>> rowHeaderFactory = new SimpleObjectProperty<>(this, "rowHeaderFactory");

    /**
     * A property used to store a callback for creating a node that will be
     * placed to the left of each row in the graphics view.
     *
     * @return the row header node callback property
     * @since 11.11.0
     */
    public final ObjectProperty<Callback<GraphicsBase<R>, RowHeader<R>>> rowHeaderFactoryProperty() {
        return rowHeaderFactory;
    }

    /**
     * Sets the value of {@link #rowHeaderFactoryProperty()}.
     *
     * @param factory the factory used for creating the row header nodes
     * @since 11.11.0
     */
    public final void setRowHeaderFactory(Callback<GraphicsBase<R>, RowHeader<R>> factory) {
        requireNonNull(factory);
        rowHeaderFactory.set(factory);
    }

    /**
     * Returns the value of {@link #rowHeaderFactoryProperty()}.
     *
     * @return the row header nodes factory
     * @since 11.11.0
     */
    public final Callback<GraphicsBase<R>, RowHeader<R>> getRowHeaderFactory() {
        return rowHeaderFactory.get();
    }

    private final BooleanProperty showRowHeaders = new SimpleBooleanProperty(this, "showRowHeaders", false);

    /**
     * Determines if the row headers will be shown to the user or not.
     *
     * @return true if the row headers will be visible
     * @since 11.11.0
     */
    public final BooleanProperty showRowHeadersProperty() {
        return showRowHeaders;
    }

    public final boolean isShowRowHeaders() {
        return showRowHeaders.get();
    }

    public final void setShowRowHeaders(boolean showRowHeaders) {
        this.showRowHeaders.set(showRowHeaders);
    }

    private final DoubleProperty rowHeadersWidth = new SimpleDoubleProperty(this, "rowHeaderWidths", 60);

    /**
     * Specifies the width of the so-called "row headers". These are custom nodes that can be placed
     * in front of every row inside the graphics area. For proper layout the width of all row headers
     * has to be the same.
     *
     * @see #setRowHeaderFactory(Callback)
     * @return the width in pixels used for all row headers
     * @since 11.11.0
     */
    public final DoubleProperty rowHeadersWidthProperty() {
        return rowHeadersWidth;
    }

    public final double getRowHeadersWidth() {
        return rowHeadersWidth.get();
    }

    public final void setRowHeadersWidth(double rowHeadersWidth) {
        this.rowHeadersWidth.set(rowHeadersWidth);
    }

    // Row editor support.

    private final ObjectProperty<Callback<RowEditorParameter<R>, Node>> rowEditorFactory = new SimpleObjectProperty<>(this, "rowEditor", param -> null);

    public final ObjectProperty<Callback<RowEditorParameter<R>, Node>> rowEditorFactoryProperty() {
        return rowEditorFactory;
    }

    public final void setRowEditorFactory(Callback<RowEditorParameter<R>, Node> factory) {
        requireNonNull(factory);
        rowEditorFactory.set(factory);
    }

    public final Callback<RowEditorParameter<R>, Node> getRowEditorFactory() {
        return rowEditorFactory.get();
    }

    /**
     * A callback parameter object used to provide context for the row editor
     * factory.
     *
     * @param <R> the row type
     * @see #rowEditorFactoryProperty()
     */
    public static final class RowEditorParameter<R extends Row<?, ?, ?>> {

        private final R row;
        private final GraphicsBase<R> graphics;

        public RowEditorParameter(GraphicsBase<R> graphics, R row) {
            requireNonNull(graphics);
            requireNonNull(row);

            this.graphics = graphics;
            this.row = row;
        }

        public R getRow() {
            return row;
        }

        public GraphicsBase<R> getGraphics() {
            return graphics;
        }

        public void stopEditing() {
            graphics.stopRowEditing(row);
        }
    }

    /**
     * An enumerator used to define how many rows can show their row editors at
     * the same time.
     *
     * @see GraphicsBase#setRowEditingMode(RowEditingMode)
     * @since 1.0
     */
    public enum RowEditingMode {
        NONE, SINGLE_ROW, MULTIPLE_ROWS
    }

    private final ObjectProperty<RowEditingMode> rowEditingMode = new SimpleObjectProperty<>(this, "rowEditingMode", RowEditingMode.SINGLE_ROW);

    public final ObjectProperty<RowEditingMode> rowEditingModeProperty() {
        return rowEditingMode;
    }

    public final void setRowEditingMode(RowEditingMode mode) {
        rowEditingMode.set(mode);
    }

    public final RowEditingMode getRowEditingMode() {
        return rowEditingMode.get();
    }

    private final ObservableList<R> rowsEditing = FXCollections.observableArrayList();

    public final ObservableList<R> getRowsEditing() {
        return rowsEditing;
    }

    public final void stopRowEditing() {
        rowsEditing.forEach(this::stopRowEditing);
    }

    public final void stopRowEditing(R row) {
        getRowPanes().stream().filter(pane -> pane.getRow() == row)
                .forEach(pane -> {
                    getRowsEditing().remove(row);
                    pane.stopEditing();
                });
    }

    public final void startRowEditing(R row) {
        switch (getRowEditingMode()) {
            case NONE:
                return;
            case SINGLE_ROW:
                (new ArrayList<>(getRowsEditing())).forEach(this::stopRowEditing);
                break;
            case MULTIPLE_ROWS:
            default:
                break;

        }

        getRowPanes().stream().filter(pane -> pane.getRow() == row).forEach(pane -> {
            getRowsEditing().add(row);
            pane.startEditing();
        });
    }

    private final BooleanProperty animateRowEditor = new SimpleBooleanProperty(this, "animateRowEditor", true);

    public final BooleanProperty animateRowEditorProperty() {
        return animateRowEditor;
    }

    public final void setAnimateRowEditor(boolean animate) {
        animateRowEditorProperty().set(animate);
    }

    public final boolean isAnimateRowEditor() {
        return animateRowEditorProperty().get();
    }

    // Edit mode support.

    /**
     * A callback parameter object used by the edit mode callback that provides information
     * about the context for which the edit mode will be determined.
     */
    public static final class EditModeCallbackParameter {

        private final ActivityBounds activityBounds;

        private final MouseEvent event;

        /**
         * Constructs a new callback parameter.
         *
         * @param activityBounds the activity / bounds for which to determine the edit mode
         * @param event          the mouse event triggering the edit mode lookup
         */
        public EditModeCallbackParameter(ActivityBounds activityBounds, MouseEvent event) {
            requireNonNull(activityBounds);
            requireNonNull(event);

            this.activityBounds = activityBounds;
            this.event = event;
        }

        /**
         * The activity / bounds for which to determine the edit mode.
         *
         * @return the activity
         */
        public ActivityBounds getActivityBounds() {
            return activityBounds;
        }

        /**
         * The event that triggered the lookup.
         *
         * @return the event causing the lookup
         */
        public MouseEvent getMouseEvent() {
            return event;
        }
    }

    private final ObservableMap<Class<? extends Layout>, ObservableMap<Class<?>, Callback<EditModeCallbackParameter, EditMode>>> editModeCallbackMap = FXCollections.observableHashMap();

    public final void setEditModeCallback(
            Class<? extends MutableActivity> activityType,
            Class<? extends Layout> layoutType,
            Callback<EditModeCallbackParameter, EditMode> callback) {

        if (callback != null) {
            LoggingDomain.CONFIG.fine("layout = " + layoutType + ", class = " + activityType + ", callback = " + callback.getClass().getName());
        } else {
            LoggingDomain.CONFIG.fine("class = " + activityType + ", callback = null");
        }

        ObservableMap<Class<?>, Callback<EditModeCallbackParameter, EditMode>> layoutMap = editModeCallbackMap.computeIfAbsent(layoutType, k -> FXCollections.observableHashMap());
        layoutMap.put(activityType, callback);
    }

    public final Callback<EditModeCallbackParameter, EditMode> getEditModeCallback(Class<? extends MutableActivity> activityType, Class<? extends Layout> layoutType) {
        return doGetEditModeCallback(activityType, layoutType);
    }

    private Callback<EditModeCallbackParameter, EditMode> doGetEditModeCallback(Class<?> activityType, Class<? extends Layout> layoutType) {
        if (activityType != null) {
            ObservableMap<Class<?>, Callback<EditModeCallbackParameter, EditMode>> modeMap = editModeCallbackMap.get(layoutType);
            if (modeMap != null) {
                Callback<EditModeCallbackParameter, EditMode> callback = modeMap.get(activityType);
                if (callback == null) {
                    return doGetEditModeCallback(activityType.getSuperclass(), layoutType);
                }

                return callback;
            }
        }

        return null;
    }

    class ActivityEditModeCallback implements Callback<EditModeCallbackParameter, EditMode> {

        @Override
        public EditMode call(EditModeCallbackParameter input) {
            ActivityBounds bounds = input.getActivityBounds();
            MouseEvent evt = input.getMouseEvent();

            if (evt.getX() < bounds.getMinX() + 3) {
                return EditMode.START_TIME_CHANGE;
            }

            if (evt.getX() > bounds.getMinX() + bounds.getWidth() - 3) {
                return EditMode.END_TIME_CHANGE;
            }

            if (evt.isShiftDown()) {
                return EditMode.DRAGGING;
            }

            if (evt.isShortcutDown()) {
                return EditMode.DRAGGING_VERTICAL;
            }

            return EditMode.DRAGGING_HORIZONTAL;
        }
    }

    class ActivityInAgendaLayoutEditModeCallback implements Callback<EditModeCallbackParameter, EditMode> {

        @Override
        public EditMode call(EditModeCallbackParameter input) {
            ActivityBounds bounds = input.getActivityBounds();

            MouseEvent evt = input.getMouseEvent();
            Layout layout = bounds.getLayout();

            if (evt.isShiftDown()) {
                return EditMode.DRAGGING;
            }

            if (evt.isShortcutDown()) {
                return EditMode.DRAGGING_VERTICAL;
            }

            if (layout instanceof AgendaLayout) {
                Position pos = bounds.getPosition();

                if (evt.getY() < bounds.getMinY() + 3) {
                    switch (pos) {
                        case FIRST:
                        case ONLY:
                            return EditMode.AGENDA_START_TIME_CHANGE;
                        case LAST:
                        case MIDDLE:
                        default:
                            break;
                    }
                }

                if (evt.getY() > bounds.getMinY() + bounds.getHeight() - 3) {
                    switch (pos) {
                        case LAST:
                        case ONLY:
                            return EditMode.AGENDA_END_TIME_CHANGE;
                        case FIRST:
                        case MIDDLE:
                        default:
                            break;
                    }
                }

                return EditMode.AGENDA_DRAGGING;
            }

            return EditMode.NONE;
        }
    }

    class CompletableActivityEditModeCallback extends ActivityEditModeCallback {

        @Override
        public EditMode call(EditModeCallbackParameter input) {
            ActivityBounds bounds = input.getActivityBounds();
            MouseEvent evt = input.getMouseEvent();

            CompletableActivity completableActivity = (CompletableActivity) bounds.getActivity();
            double xCompletion = bounds.getMinX() + (bounds.getWidth() / 100 * completableActivity.getPercentageComplete());

            /*
             * The percentage complete value can only be changed if the activity
             * is currently selected.
             */
            if (getSelectedActivities().contains(bounds.getActivityRef())
                    && evt.getX() > xCompletion - 3
                    && evt.getX() < xCompletion + 3) {
                return EditMode.PERCENTAGE_COMPLETE_CHANGE;
            }

            return super.call(input);
        }
    }

    class ChartActivityEditModeCallback extends ActivityEditModeCallback {

        @Override
        public EditMode call(EditModeCallbackParameter input) {
            ActivityBounds bounds = input.getActivityBounds();
            MouseEvent evt = input.getMouseEvent();
            Layout layout = bounds.getLayout();

            ChartActivity chartActivity = (ChartActivity) bounds.getActivity();
            if (chartActivity.getChartValue() >= 0) {
                if (evt.getY() < bounds.getMinY() + 3 && layout instanceof ChartLayout) {
                    return EditMode.CHART_VALUE_CHANGE;
                }
            } else {
                if (evt.getY() > bounds.getMinY() + bounds.getHeight() - 3 && layout instanceof ChartLayout) {
                    return EditMode.CHART_VALUE_CHANGE;
                }
            }

            return super.call(input);
        }
    }

    class ChartHighLowEditModeCallback extends ActivityEditModeCallback {

        @Override
        public EditMode call(EditModeCallbackParameter input) {
            ActivityBounds bounds = input.getActivityBounds();
            MouseEvent evt = input.getMouseEvent();
            Layout layout = bounds.getLayout();

            if (evt.getY() < bounds.getMinY() + 3 && layout instanceof ChartLayout) {
                return EditMode.CHART_VALUE_HIGH_CHANGE;
            }

            if (evt.getY() > bounds.getMinY() + bounds.getHeight() - 3 && layout instanceof ChartLayout) {
                return EditMode.CHART_VALUE_LOW_CHANGE;
            }

            return super.call(input);
        }
    }

    private class CalendarMenu extends Menu {

        private ObservableList<Calendar<?>> calendars;

        private final InvalidationListener calendarListListener = it -> buildMenu();

        private final WeakInvalidationListener weakCalendarListListener = new WeakInvalidationListener(calendarListListener);

        public CalendarMenu() {
        }

        public void setCalendars(ObservableList<Calendar<?>> calendars) {
            requireNonNull(calendars);

            getItems().clear();

            this.calendars = calendars;
            this.calendars.addListener(new WeakInvalidationListener(weakCalendarListListener));

            buildMenu();
        }

        private void buildMenu() {
            getItems().clear();

            for (Calendar<?> calendar : calendars) {
                final CheckMenuItem item = new CheckMenuItem(calendar.nameProperty().get());
                item.setSelected(calendar.visibleProperty().get());
                Bindings.bindBidirectional(item.textProperty(), calendar.nameProperty());
                Bindings.bindBidirectional(calendar.visibleProperty(), item.selectedProperty());
                getItems().add(item);
            }
        }
    }

    private class GridMenu extends Menu {

        private ObservableList<VirtualGrid<?>> grids;

        private final InvalidationListener gridsListener = (
                Observable observable) -> buildMenu();

        private final WeakInvalidationListener weakGridsListener = new WeakInvalidationListener(
                gridsListener);

        public GridMenu() {
        }

        public void setVirtualGrids(ObservableList<VirtualGrid<?>> grids) {
            requireNonNull(grids);

            getItems().clear();

            this.grids = grids;
            this.grids.addListener(weakGridsListener);

            buildMenu();
        }

        private void buildMenu() {
            CheckMenuItem offItem = new CheckMenuItem(
                    Messages.getString("GraphicsBase.GRID_OFF"));
            offItem.setSelected(!isGridEnabled());
            getItems().add(offItem);
            offItem.setOnAction(evt -> {
                setAutoGridEnabled(false);
                setVirtualGrid(null);
            });

            CheckMenuItem autoGrid = new CheckMenuItem("Auto");
            autoGrid.setOnAction(
                    evt -> setAutoGridEnabled(autoGrid.isSelected()));
            autoGrid.selectedProperty().set(isAutoGridEnabled());
            getItems().add(autoGrid);

            getItems().add(new SeparatorMenuItem());

            for (VirtualGrid<?> grid : grids) {
                CheckMenuItem item = new CheckMenuItem(grid.getName());

                item.selectedProperty().set(grid.equals(getVirtualGrid()));
                item.setOnAction(evt -> setVirtualGrid(grid));
                getItems().add(item);
            }
        }
    }

    private class GraphicsViewMenu extends ContextMenu {

        private final CalendarMenu calendarMenu;

        public GraphicsViewMenu(final ContextMenuParameter<R> input) {
            requireNonNull(input);

            MenuItem highlightOn = new MenuItem(Messages.getString("GraphicsBase.HIGHLIGHT_ON"));
            highlightOn.setOnAction(highlightOn(input));
            highlightOn.disableProperty().bind(Bindings.isEmpty(getSelectedActivities()));
            getItems().add(highlightOn);

            MenuItem highlightOff = new MenuItem(Messages.getString("GraphicsBase.HIGHLIGHT_OFF"));
            highlightOff.setOnAction(highlightOff(input));
            highlightOff.disableProperty().bind(Bindings.isEmpty(getHighlightedActivities()));
            getItems().add(highlightOff);

            calendarMenu = new CalendarMenu();
            calendarMenu.setText(Messages.getString("GraphicsBase.CALENDAR_MENU_TITLE"));

            getItems().add(calendarMenu);

            Row<?, ?, ?> row = input.getRow();

            if (row != null) {
                ObservableList<Calendar<?>> globalCalendars = input.getGraphics().getCalendars();
                ObservableList<Calendar<?>> localCalendars = row.getCalendars();
                ObservableList<Calendar<?>> allCalendars = FXCollections.observableArrayList();

                allCalendars.addAll(globalCalendars);
                allCalendars.addAll(localCalendars);

                calendarMenu.setCalendars(allCalendars);
                calendarMenu.setDisable(false);
            } else {
                calendarMenu.setDisable(true);
            }

            GridMenu gridMenu = new GridMenu();
            gridMenu.setText(Messages.getString("GraphicsBase.GRID_MENU_TITLE"));
            gridMenu.setVirtualGrids(getVirtualGrids());
            getItems().add(gridMenu);

        }

        private EventHandler<ActionEvent> highlightOff(
                final ContextMenuParameter<R> input) {
            return event -> {
                GraphicsBase<R> view = input.getGraphics();
                view.getHighlightedActivities().clear();
            };
        }

        private EventHandler<ActionEvent> highlightOn(
                final ContextMenuParameter<R> input) {
            return event -> {
                GraphicsBase<R> view = input.getGraphics();
                view.getHighlightedActivities().clear();
                view.getHighlightedActivities()
                        .addAll(view.getSelectedActivities());
            };
        }
    }

    // Drop layer provider support.

    private final ObjectProperty<Callback<DragAndDropInfo, Layer>> dropLayerProvider = new SimpleObjectProperty<>(
            this, "dropLayerProvider",
            info -> info.getActivityRef().getLayer());

    /**
     * A property used to store a callback that will return the layer on which a
     * dragged activity will be placed once the drop operation has finished. The
     * default provider returns the layer on which the activity is currently
     * shown.
     *
     * @return a property used to store the drop layer provider
     * @since 1.2
     */
    public final ObjectProperty<Callback<DragAndDropInfo, Layer>> dropLayerProviderProperty() {
        return dropLayerProvider;
    }

    /**
     * Returns the value of {@link #dropLayerProviderProperty()}.
     *
     * @return the drop layer provider used for DnD operations
     * @since 1.2
     */
    public final Callback<DragAndDropInfo, Layer> getDropLayerProvider() {
        return dropLayerProvider.get();
    }

    /**
     * Sets the value of {@link #dropLayerProviderProperty()}.
     *
     * @param provider the drop layer provider used for DnD operations
     * @since 1.2
     */
    public final void setDropLayerProvider(
            Callback<DragAndDropInfo, Layer> provider) {
        requireNonNull(provider);
        dropLayerProvider.set(provider);
    }

    private final BooleanProperty horizontalDragEnabled = new SimpleBooleanProperty(
            this, "horizontalDragEnabled", true);

    /**
     * Determines whether the user can perform a horizontal drag with a mouse
     * drag.
     *
     * @return true if the visible time range can be changed via a mouse drag
     * @since 1.3
     */
    public final BooleanProperty horizontalDragEnabledProperty() {
        return horizontalDragEnabled;
    }

    /**
     * Sets the value of {@link #horizontalDragEnabledProperty()}.
     *
     * @param enabled if true the user can perform horizontal scrolling
     */
    public final void setHorizontalDragEnabled(boolean enabled) {
        horizontalDragEnabled.set(enabled);
    }

    /**
     * Returns the value of {@link #horizontalDragEnabledProperty()}.
     *
     * @return true if the user can perform horizontal scrolling
     */
    public final boolean isHorizontalDragEnabled() {
        return horizontalDragEnabled.get();
    }

    // safe rendering support (shadow field approach because this gets called a lot!)

    private BooleanProperty safeRendering;

    private boolean _safeRendering = false;

    /**
     * Returns the property that specifies whether the various canvas API-based rendering parts inside
     * this framework will always call {@link GraphicsContext#save()} to save the current state of the
     * context before changing its state (followed by {@link GraphicsContext#restore()} to restore the
     * old state).
     * <p>
     * Using save / restore will ensure that the pluggable system layers and activity renderers will not
     * have any side effects on each other. Setting this property to true has an impact on performance.
     * The default value of this property is false.
     * <p>
     * <h3>Example</h3>
     * The following code shows how the property is used within the framework.
     * <pre>
     *     GraphicsContext gc = canvas.getGraphicsContext2D();
     *
     *     if (graphics.isSafeRendering()) {
     *          gc.save();
     *     }
     *
     *     gc.setTransform(...);
     *     gc.strokeLine(...);
     *
     *     if (graphics.isSafeRendering()) {
     *          gc.restore();
     *     }
     * </pre>
     *
     * @return the property to control safe rendering
     */
    public final BooleanProperty safeRenderingProperty() {
        if (safeRendering == null) {
            safeRendering = new SimpleBooleanProperty(this, "safeRendering", _safeRendering);
        }

        return safeRendering;
    }

    /**
     * Sets the value of {@link #safeRenderingProperty()}.
     *
     * @param safe if true the safe rendering mode will be used (the graphics context state will
     *             be saved before invoking renderers or drawing system layers).
     */
    public final void setSafeRendering(boolean safe) {
        if (safeRendering == null) {
            _safeRendering = safe;
        } else {
            safeRendering.set(safe);
        }
    }

    /**
     * Returns the value of {@link #safeRenderingProperty()}.
     *
     * @return "true" if the safe rendering mode will be used (the graphics context state will
     * be saved before invoking renderers or drawing system layers).
     */
    public final boolean isSafeRendering() {
        if (safeRendering == null) {
            return _safeRendering;
        }

        return safeRendering.get();
    }

    // Grid line color support
    private StyleableObjectProperty<Paint> gridLineColor1;

    public final StyleableObjectProperty<Paint> gridLineColor1Property() {
        if (gridLineColor1 == null) {
            gridLineColor1 = new StyleableObjectProperty() {

                @Override
                public CssMetaData<GraphicsBase, Paint> getCssMetaData() {
                    return StyleableProperties.GRID_LINE_COLOR1;
                }

                @Override
                public Object getBean() {
                    return GraphicsBase.this;
                }

                @Override
                public String getName() {
                    return "gridLineColor1";
                }
            };
        }
        return gridLineColor1;
    }

    public final Paint getGridLineColor1() {
        return gridLineColor1Property().get();
    }

    public void setGridLineColor1(Paint color) {
        gridLineColor1Property().set(color);
    }

    // grid line color 2

    private StyleableObjectProperty<Paint> gridLineColor2;

    public final StyleableObjectProperty<Paint> gridLineColor2Property() {
        if (gridLineColor2 == null) {
            gridLineColor2 = new StyleableObjectProperty() {

                @Override
                public CssMetaData<GraphicsBase, Paint> getCssMetaData() {
                    return StyleableProperties.GRID_LINE_COLOR2;
                }

                @Override
                public Object getBean() {
                    return GraphicsBase.this;
                }

                @Override
                public String getName() {
                    return "gridLineColor2";
                }
            };
        }
        return gridLineColor2;
    }

    public final Paint getGridLineColor2() {
        return gridLineColor2Property().get();
    }

    public void setGridLineColor2(Paint color) {
        gridLineColor2Property().set(color);
    }

    // grid line color 3

    private StyleableObjectProperty<Paint> gridLineColor3;

    public final StyleableObjectProperty<Paint> gridLineColor3Property() {
        if (gridLineColor3 == null) {
            gridLineColor3 = new StyleableObjectProperty() {

                @Override
                public CssMetaData<GraphicsBase, Paint> getCssMetaData() {
                    return StyleableProperties.GRID_LINE_COLOR3;
                }

                @Override
                public Object getBean() {
                    return GraphicsBase.this;
                }

                @Override
                public String getName() {
                    return "gridLineColor3";
                }
            };
        }
        return gridLineColor3;
    }

    public final Paint getGridLineColor3() {
        return gridLineColor3Property().get();
    }

    public void setGridLineColor3(Paint color) {
        gridLineColor3Property().set(color);
    }

    // weekend color

    private StyleableObjectProperty<Paint> weekendColor;

    public final StyleableObjectProperty<Paint> weekendColorProperty() {
        if (weekendColor == null) {
            weekendColor = new StyleableObjectProperty() {

                @Override
                public CssMetaData<GraphicsBase, Paint> getCssMetaData() {
                    return StyleableProperties.WEEKEND_COLOR;
                }

                @Override
                public Object getBean() {
                    return GraphicsBase.this;
                }

                @Override
                public String getName() {
                    return "weekendColor";
                }
            };
        }
        return weekendColor;
    }

    public final Paint getWeekendColor() {
        return weekendColorProperty().get();
    }

    public void setWeekendColor(Paint color) {
        weekendColorProperty().set(color);
    }

    // time now color

    private StyleableObjectProperty<Paint> timeNowColor;

    public final StyleableObjectProperty<Paint> timeNowColorProperty() {
        if (timeNowColor == null) {
            timeNowColor = new StyleableObjectProperty() {

                @Override
                public CssMetaData<GraphicsBase, Paint> getCssMetaData() {
                    return StyleableProperties.TIME_NOW_COLOR;
                }

                @Override
                public Object getBean() {
                    return GraphicsBase.this;
                }

                @Override
                public String getName() {
                    return "timeNowColor";
                }
            };
        }
        return timeNowColor;
    }

    public final Paint getTimeNowColor() {
        return timeNowColorProperty().get();
    }

    public void setTimeNowColor(Paint color) {
        timeNowColorProperty().set(color);
    }

    // inner lines color

    private StyleableObjectProperty<Paint> innerLinesColor;

    public final StyleableObjectProperty<Paint> innerLinesColorProperty() {
        if (innerLinesColor == null) {
            innerLinesColor = new StyleableObjectProperty() {

                @Override
                public CssMetaData<GraphicsBase, Paint> getCssMetaData() {
                    return StyleableProperties.INNER_LINES_COLOR;
                }

                @Override
                public Object getBean() {
                    return GraphicsBase.this;
                }

                @Override
                public String getName() {
                    return "innerLinesColor";
                }
            };
        }
        return innerLinesColor;
    }

    public final Paint getInnerLinesColor() {
        return innerLinesColorProperty().get();
    }

    public void setInnerLinesColor(Paint color) {
        innerLinesColorProperty().set(color);
    }

    private static class StyleableProperties {

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        private static final CssMetaData<GraphicsBase, Paint> INNER_LINES_COLOR = new CssMetaData<GraphicsBase, Paint>(
                "-fx-inner-lines-color", PaintConverter.getInstance(), Color.LIGHTGRAY) {

            @Override
            public Paint getInitialValue(GraphicsBase node) {
                return node.getGridLineColor1();
            }

            @Override
            public boolean isSettable(GraphicsBase n) {
                return n.innerLinesColor == null || !n.innerLinesColor.isBound();
            }

            @Override
            public StyleableObjectProperty<Paint> getStyleableProperty(GraphicsBase n) {
                return n.innerLinesColorProperty();
            }
        };

        private static final CssMetaData<GraphicsBase, Paint> GRID_LINE_COLOR1 = new CssMetaData<GraphicsBase, Paint>(
                "-fx-grid-line-color1", PaintConverter.getInstance(), Color.LIGHTGRAY) {

            @Override
            public Paint getInitialValue(GraphicsBase node) {
                return node.getGridLineColor1();
            }

            @Override
            public boolean isSettable(GraphicsBase n) {
                return n.gridLineColor1 == null || !n.gridLineColor1.isBound();
            }

            @Override
            public StyleableObjectProperty<Paint> getStyleableProperty(GraphicsBase n) {
                return n.gridLineColor1Property();
            }
        };

        private static final CssMetaData<GraphicsBase, Paint> GRID_LINE_COLOR2 = new CssMetaData<GraphicsBase, Paint>(
                "-fx-grid-line-color2", PaintConverter.getInstance(), Color.GRAY) {

            @Override
            public Paint getInitialValue(GraphicsBase node) {
                return node.getGridLineColor2();
            }

            @Override
            public boolean isSettable(GraphicsBase n) {
                return n.gridLineColor2 == null || !n.gridLineColor2.isBound();
            }

            @Override
            public StyleableObjectProperty<Paint> getStyleableProperty(GraphicsBase n) {
                return n.gridLineColor2Property();
            }
        };

        private static final CssMetaData<GraphicsBase, Paint> GRID_LINE_COLOR3 = new CssMetaData<GraphicsBase, Paint>(
                "-fx-grid-line-color3", PaintConverter.getInstance(), Color.DARKGRAY) {

            @Override
            public Paint getInitialValue(GraphicsBase node) {
                return node.getGridLineColor3();
            }

            @Override
            public boolean isSettable(GraphicsBase n) {
                return n.gridLineColor3 == null || !n.gridLineColor3.isBound();
            }

            @Override
            public StyleableObjectProperty<Paint> getStyleableProperty(GraphicsBase n) {
                return n.gridLineColor3Property();
            }
        };

        private static final CssMetaData<GraphicsBase, Paint> WEEKEND_COLOR = new CssMetaData<GraphicsBase, Paint>(
                "-fx-weekend-color", PaintConverter.getInstance(), Color.LIGHTGRAY) {

            @Override
            public Paint getInitialValue(GraphicsBase node) {
                return node.getWeekendColor();
            }

            @Override
            public boolean isSettable(GraphicsBase n) {
                return n.weekendColor == null || !n.weekendColor.isBound();
            }

            @Override
            public StyleableObjectProperty<Paint> getStyleableProperty(GraphicsBase n) {
                return n.weekendColorProperty();
            }
        };

        private static final CssMetaData<GraphicsBase, Paint> TIME_NOW_COLOR = new CssMetaData<GraphicsBase, Paint>(
                "-fx-time-now-color", PaintConverter.getInstance(), Color.RED) {

            @Override
            public Paint getInitialValue(GraphicsBase node) {
                return node.getTimeNowColor();
            }

            @Override
            public boolean isSettable(GraphicsBase n) {
                return n.timeNowColor == null || !n.timeNowColor.isBound();
            }

            @Override
            public StyleableObjectProperty<Paint> getStyleableProperty(GraphicsBase n) {
                return n.timeNowColorProperty();
            }
        };

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
                    Region.getClassCssMetaData());
            styleables.add(INNER_LINES_COLOR);
            styleables.add(GRID_LINE_COLOR1);
            styleables.add(GRID_LINE_COLOR2);
            styleables.add(GRID_LINE_COLOR3);
            styleables.add(WEEKEND_COLOR);
            styleables.add(TIME_NOW_COLOR);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return GraphicsBase.StyleableProperties.STYLEABLES;
    }

    @Override
    public final List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}
