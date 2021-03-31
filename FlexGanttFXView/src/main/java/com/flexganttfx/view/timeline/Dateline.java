/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.timeline;

import com.flexganttfx.model.Calendar;
import com.flexganttfx.model.dateline.ChronoUnitDatelineModel;
import com.flexganttfx.model.dateline.DatelineModel;
import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.SimpleUnit;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.util.FlexGanttFXControl;
import com.flexganttfx.view.util.Messages;
import impl.com.flexganttfx.skin.timeline.ChronoUnitDatelineCell;
import impl.com.flexganttfx.skin.timeline.DatelineSkin;
import impl.com.flexganttfx.skin.timeline.SimpleUnitDatelineCell;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static javafx.scene.control.SelectionMode.MULTIPLE;

/**
 * The dateline is a control that displays the actual dates (Mo, Tu, We, ...) in
 * cells in one or more rows. The dateline is timezone aware (see
 * {@link #setZoneId(ZoneId)}) and keeps track of currently selected time intervals
 * ({@link #getSelectedIntervals()}). Furthermore the dateline
 * control constantly updates the {@link #hoverTimeIntervalProperty()} whenever
 * the mouse moves over it.
 * <p>&nbsp;</p>
 *
 * <img src="doc-files/dateline.png" alt="Dateline">
 *
 *
 * @since 1.0
 */
public class Dateline extends FlexGanttFXControl {

    private static final String DEFAULT_STYLE_CLASS = "dateline";

    private final Timeline timeline;

    private Instant selectionStart;
    private Instant selectionEnd;
    private double mouseStartX;

    /**
     * Constructs a new dateline.
     *
     * @param timeline the parent timeline
     * @since 1.0
     */
    protected Dateline(Timeline timeline) {
        requireNonNull(timeline);

        this.timeline = timeline;

        setModel(new ChronoUnitDatelineModel());
        setFocusTraversable(true);
        setPrefWidth(0);
        setMinWidth(0);

        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> requestFocus());

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        setCellFactory(SimpleUnit.class, unit -> new SimpleUnitDatelineCell());
        setCellFactory(ChronoUnit.class, unit -> new ChronoUnitDatelineCell());

        getSelectedIntervals().addListener((javafx.beans.Observable it) -> requestLayout());
        selectionModeProperty().addListener(it -> getSelectedIntervals().clear());

        buildContextMenu();

        EventHandler<MouseEvent> mousePressedHandler = evt -> {
            if (evt.isShiftDown() || !timeline.isScrollDragEnabled()) {
                /*
                 * Time interval selection.
				 */
                TimelineModel<?> timelineModel = timeline.getModel();

                selectionStart = timelineModel.calculateTimeForLocation(evt.getX() - getDatelineBuffer() + getTranslateX());
                selectedTimeInterval.set(new TimeInterval(selectionStart, selectionStart));
            } else {
                /*
                 * Begin of a drag sequence.
				 */
                mouseStartX = evt.getScreenX();
            }
        };

        EventHandler<MouseEvent> mouseDraggedHandler = evt -> {
            if (evt.isShiftDown() || !timeline.isScrollDragEnabled()) {
                /*
                 * Time interval selection. While dragging some of the events
				 * are also coming from the dateline cells and we need to ignore
				 * them because otherwise the x-coordinate would be relative to
				 * the cell and not relative to the timeline.
				 */
                if (isZoomLassoEnabled() && !timeline.isScrollDragEnabled() && evt.getSource().equals(Dateline.this) && selectionStart != null) {
                    TimelineModel<?> timelineModel = timeline.getModel();
                    selectionEnd = timelineModel.calculateTimeForLocation(evt.getX() - getDatelineBuffer() + getTranslateX());

                    if (selectionStart.isBefore(selectionEnd) || selectionStart.equals(selectionEnd)) {
                        selectedTimeInterval.set(new TimeInterval(selectionStart, selectionEnd));
                    } else {
                        selectedTimeInterval.set(new TimeInterval(selectionEnd, selectionStart));
                    }
                }
            } else {
                /*
                 * Continued drag sequence.
				 */
                double scrollX = evt.getScreenX();
                double delta = mouseStartX - scrollX;

                TimelineModel<?> timelineModel = timeline.getModel();
                Instant newStartTime = timelineModel.calculateTimeForLocation(delta);

				/*
                 * Hack for the case when the dateline displays SimpleUnit.ONE.
				 * In this case the same instant is shown across several pixels
				 * and the dateline will hardly move.
				 */
                if (newStartTime.equals(timelineModel.getStartTime())) {
                    if (delta > 0) {
                        newStartTime = newStartTime.plusMillis(1);
                    } else {
                        newStartTime = newStartTime.minusMillis(1);
                    }
                }
                timelineModel.setStartTime(newStartTime);
                mouseStartX = scrollX;
            }
        };

        EventHandler<MouseEvent> mouseReleasedHandler = evt -> {

            if (getSelectedTimeInterval() != null) {
                TimeInterval interval = getSelectedTimeInterval();
                java.time.Duration duration = interval.getDuration();
                if (!duration.isZero()) {
                    if (evt.isShiftDown()) {
                        // zoom out
                        Instant st = timeline.getVisibleStartTime().minusMillis(duration.toMillis() / 2);
                        Instant et = timeline.getVisibleEndTime().plusMillis(duration.toMillis() / 2);
                        timeline.showRange(st, et);
                    } else {
                        // zoom in
                        timeline.showRange(interval);
                    }
                }
                selectedTimeInterval.set(null);
            }
        };

        addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);

		/*
		 * We are "abusing" the properties map to pass new values of read-only
		 * properties from the skin to the control.
		 */
        getProperties().addListener(
                (MapChangeListener.Change<?, ?> change) -> {
                    if (change.getKey().equals("com.flexganttfx.primaryUnit")) {
                        if (change.getValueAdded() != null) {
                            TemporalUnit mode = (TemporalUnit) change.getValueAdded();
                            primaryTemporalUnit.set(mode);
                        }
                    } else if (change.getKey().equals("com.flexganttfx.dateline.hover.interval")) {
                        if (change.getValueAdded() != null) {
                            TimeInterval interval = (TimeInterval) change.getValueAdded();
                            hoverTimeInterval.set(interval);
                        } else {
                            hoverTimeInterval.set(null);
                        }
                    }
                });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DatelineSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return super.getUserAgentStylesheet(Dateline.class, "dateline.css");
    }

    // Time interval selection support.

    private final ReadOnlyObjectWrapper<TimeInterval> selectedTimeInterval = new ReadOnlyObjectWrapper<>(this, "selectedTimeInterval");

    /**
     * A read-only property used to store the currently selected time interval. The value of
     * this property gets updated whenever the user performs a time interval selection by dragging
     * the mouse inside the timeline.
     *
     * @return the currently selected time interval
     * @since 1.0
     */
    public final ReadOnlyObjectProperty<TimeInterval> selectedTimeIntervalProperty() {
        return selectedTimeInterval.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #selectedTimeIntervalProperty()}.
     *
     * @return the currently selected time interval
     * @since 1.0
     */
    public final TimeInterval getSelectedTimeInterval() {
        return selectedTimeInterval.get();
    }

    // Zoom lasso support.

    private final BooleanProperty zoomLassoEnabled = new SimpleBooleanProperty(this, "zoomLassoEnabled", true);

    /**
     * A property used to control whether the zoom lasso feature will be available to the user
     * or not. The zoom lasso allows the user to select a time range inside the dateline so that
     * the dateline will zoom in as much as needed in order to make the range completely fill the
     * visible area.
     *
     * @return true if the zoom lasso is enabled
     * @since 1.0
     */
    public final BooleanProperty zoomLassoEnabledProperty() {
        return zoomLassoEnabled;
    }

    /**
     * Returns the value of {@link #zoomLassoEnabledProperty()}.
     *
     * @return true if the zoom lasso is enabled
     * @since 1.0
     */
    public final boolean isZoomLassoEnabled() {
        return zoomLassoEnabledProperty().get();
    }

    /**
     * Sets the value of {@link #zoomLassoEnabledProperty()}.
     *
     * @param enabled if true then the zoom lasso is enabled
     * @since 1.0
     */
    public final void setZoomLassoEnabled(boolean enabled) {
        zoomLassoEnabledProperty().set(enabled);
    }

    // Cell factory support.

    private final Map<Class<?>, Callback<?, ?>> cellFactoryMap = new HashMap<>();

    /**
     * Sets a cell factory on the dateline used to create dateline cells for the given
     * temporal unit type (e.g. ChronoUnit, SimpleUnit).
     *
     * @param temporalUnitType the type of the temporal unit (e.g. ChronoUnit)
     * @param factory          the factory used for creating new cells
     * @param <T>              the type of the temporal unit (e.g. ChronoUnit)
     * @since 1.0
     */
    public final <T extends TemporalUnit> void setCellFactory(Class<T> temporalUnitType, Callback<T, DatelineCell<T>> factory) {
        cellFactoryMap.put(temporalUnitType, factory);
    }

    /**
     * Returns the cell factory used for the given temporal unit type (e.g. ChronoUnit).
     *
     * @param temporalUnitType the type of the temporal unit (e.g. ChronoUnit)
     * @return the factory callback
     * @since 1.0
     */
    public final Callback<TemporalUnit, DatelineCell> getCellFactory(Class<? extends TemporalUnit> temporalUnitType) {
        return (Callback<TemporalUnit, DatelineCell>) cellFactoryMap.get(temporalUnitType);
    }

    /**
     * Returns the parent timeline container / node.
     *
     * @return the parent timeline
     * @since 1.0
     */
    public final Timeline getTimeline() {
        return timeline;
    }

    // Dateline model support

    private final ObjectProperty<DatelineModel<? extends TemporalUnit>> model = new SimpleObjectProperty<>(this, "datelineModel");

    /**
     * A property used to store the model of the dateline control. The model provides information about
     * the list of supported resolutions, available time zones, number of scales inside the dateline.
     *
     * @return the dateline model
     * @since 1.0
     */
    public final ObjectProperty<DatelineModel<? extends TemporalUnit>> modelProperty() {
        return model;
    }

    /**
     * Returns the value of {@link #modelProperty()}.
     *
     * @return the dateline model
     * @since 1.0
     */
    public final DatelineModel<? extends TemporalUnit> getModel() {
        return modelProperty().get();
    }

    /**
     * Sets the value of {@link #modelProperty()}.
     *
     * @param model the dateline model
     * @since 1.0
     */
    public final void setModel(DatelineModel<? extends TemporalUnit> model) {
        modelProperty().set(model);
    }

    // First day of week support

    private final ObjectProperty<DayOfWeek> firstDayOfWeek = new SimpleObjectProperty<>(this, "firstDayOfWeek", DayOfWeek.MONDAY);

    /**
     * A property used to store the weekday that is considered the "first day of the week".
     * In Germany the first day of the week is "Monday", in the US it is "Sunday". This is,
     * for example, relevant for displaying grid lines correctly (between weeks).
     *
     * @return the first day of week
     * @since 1.1
     */
    public final ObjectProperty<DayOfWeek> firstDayOfWeekProperty() {
        return firstDayOfWeek;
    }

    /**
     * Returns the value of {@link #firstDayOfWeekProperty()}.
     *
     * @return the first day of week
     * @since 1.1
     */
    public final DayOfWeek getFirstDayOfWeek() {
        return firstDayOfWeekProperty().get();
    }

    /**
     * Sets the value of {@link #firstDayOfWeekProperty()}.
     *
     * @param day the first day of week
     * @since 1.1
     */
    public final void setFirstDayOfWeek(DayOfWeek day) {
        requireNonNull(day);
        firstDayOfWeekProperty().set(day);
    }

    // ZoneId support.

    private final ObjectProperty<ZoneId> zoneId = new SimpleObjectProperty<>(this, "zoneId", ZoneId.systemDefault());

    /**
     * A property used to store the time zone that is currently shown by the dateline.
     * In this framework the dateline and each row can have their own time zones.
     *
     * @return the time zone ID
     * @since 1.0
     */
    public final ObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId;
    }

    /**
     * Returns the value of {@link #zoneIdProperty()}.
     *
     * @return the time zone ID
     * @since 1.0
     */
    public final ZoneId getZoneId() {
        return zoneIdProperty().get();
    }

    /**
     * Sets the value of {@link #zoneIdProperty()}.
     *
     * @param zoneId the time zone ID
     * @since 1.0
     */
    public final void setZoneId(ZoneId zoneId) {
        zoneIdProperty().set(zoneId);
    }

    // Primary temporal unit support.

    private final ReadOnlyObjectWrapper<TemporalUnit> primaryTemporalUnit = new ReadOnlyObjectWrapper<>();

    /**
     * A read-only property used to store the "primary" temporal unit, which is the unit shown
     * at the bottom of the dateline. Example: the dateline shows "Year / Month" at the top and "Days"
     * at the bottom. In this case "Days" will be the primary temporal unit. The primary temporal unit
     * is always passed to the activity repositories when querying for the activities inside the
     * visible time range. This allows the repository to return more or less activities. Example:
     * calendars can decide to not return weekend days if the user is currently looking at "Years".
     *
     * @return the currently shown primary temporal unit
     * @since 1.0
     */
    public final ReadOnlyObjectProperty<TemporalUnit> primaryTemporalUnitProperty() {
        return primaryTemporalUnit.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #primaryTemporalUnitProperty()}.
     *
     * @return the currently shown primary temporal unit
     * @since 1.0
     */
    public final TemporalUnit getPrimaryTemporalUnit() {
        return primaryTemporalUnitProperty().get();
    }

    // Hover time interval support

    private final ReadOnlyObjectWrapper<TimeInterval> hoverTimeInterval = new ReadOnlyObjectWrapper<>(this, "hoverTimeInterval");

    /**
     * A read-only property that can be used to find out the time shown at the current
     * mouse hover location.
     *
     * @return the time point at the mouse cursor location
     * @since 1.0
     */
    public final ReadOnlyObjectProperty<TimeInterval> hoverTimeIntervalProperty() {
        return hoverTimeInterval.getReadOnlyProperty();
    }

    /**
     * Returns the value of {@link #hoverTimeIntervalProperty()}.
     *
     * @return the time at the current mouse location
     * @since 1.0
     */
    public final TimeInterval getHoverTimeInterval() {
        return hoverTimeIntervalProperty().get();
    }

    // Selection support

    private final ObservableList<TimeInterval> selectedIntervals = FXCollections.observableArrayList();

    /**
     * An observable list of the currently selected time intervals. This list is the
     * "selection model" of the dateline. The difference to the {@link #selectedTimeIntervalProperty()} is
     * that these selections are permanent while the single selected time interval is only used
     * to highlight a section within the dateline for zoom in / out operations. This list of
     * selected time intervals however represents days or weeks that the user clicked on while pressing
     * the command key (on Mac) or the CTRL key (on Windows / Linux).
     *
     * @return the list of selected time intervals
     * @since 1.0
     */
    public final ObservableList<TimeInterval> getSelectedIntervals() {
        return selectedIntervals;
    }

    private final ObjectProperty<SelectionMode> selectionMode = new SimpleObjectProperty<>(this, "selectionMode", MULTIPLE);

    /**
     * A property used to store the selection mode applied by the dateline when the user
     * adds time interval selections. The value of this property enables the application to switch
     * between a single selection model and a multi selection model.
     *
     * @return the current selection mode (single, multi)
     * @since 1.0
     */
    public final ObjectProperty<SelectionMode> selectionModeProperty() {
        return selectionMode;
    }

    /**
     * Sets the value of {@link #selectionModeProperty()}.
     *
     * @param mode the selection mode (single, multi)
     * @since 1.0
     */
    public final void setSelectionMode(SelectionMode mode) {
        selectionModeProperty().set(mode);
    }

    /**
     * Returns the value of {@link #selectionModeProperty()}.
     *
     * @return the selection mode (single, multi)
     * @since 1.0
     */
    public final SelectionMode getSelectionMode() {
        return selectionModeProperty().get();
    }

    private final DoubleProperty datelineBuffer = new SimpleDoubleProperty(this, "datelineBuffer", 250);

    public double getDatelineBuffer() {
        return datelineBuffer.get();
    }

    public DoubleProperty datelineBufferProperty() {
        return datelineBuffer;
    }

    public void setDatelineBuffer(double datelineBuffer) {
        this.datelineBuffer.set(datelineBuffer);
    }

    // iCal support

    private final ObservableList<Calendar<?>> calendars = FXCollections.observableArrayList();

    /**
     * An observable list of calendars associated with the dateline. Information provided
     * by the calendars can be used by the dateline to visualize events directly inside
     * of it (e.g. national holidays).
     *
     * @return the list of calendars attached to the dateline
     * @since 1.0
     */
    public final ObservableList<Calendar<?>> getCalendars() {
        return calendars;
    }

    // resolution support

    private final ObservableList<Resolution<?>> scaleResolutions = FXCollections.observableArrayList();

    /**
     * An observable list of the currently displayed resolutions within the various
     * scales of the dateline. Example: when the dateline displays "Month" at the top,
     * "Days" in the middle, and "Hours" at the bottom, then the list will have three entries,
     * each entry representing the resolution of its scale.
     *
     * @return the list of currently showing resolutions
     * @see DatelineModel#getResolutions()
     * @since 1.0
     */
    public final ObservableList<Resolution<?>> getScaleResolutions() {
        return scaleResolutions;
    }

    private void buildContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        Timeline timeline = getTimeline();

        contextMenu.getItems().clear();

        CheckMenuItem showTimezoneItem = new CheckMenuItem(Messages.getString("Dateline.MENU_ITEM_SHOW_TIMEZONE"));
        showTimezoneItem.setSelected(timeline.isZoneIdVisible());

        Bindings.bindBidirectional(timeline.zoneIdVisibleProperty(), showTimezoneItem.selectedProperty());

        Menu unitsMenu = new Menu(Messages.getString("Dateline.MENU_TIME_UNIT"));
        Menu zoneIdMenu = new Menu(Messages.getString("Dateline.MENU_TIME_ZONE"));

        MenuItem addScaleItem = new MenuItem(Messages.getString("Dateline.MENU_ITEM_ADD_TOP_SCALE"));
        addScaleItem.setOnAction(addScale());

        MenuItem removeScaleItem = new MenuItem(Messages.getString("Dateline.MENU_ITEM_REMOVE_TOP_SCALE"));
        removeScaleItem.setOnAction(removeScale());

        // TODO: when putting back scale count, add scale, remove scale: ensure
        // to obey min / max scale count constraint defined by the dateline
        // mode.
        Menu scaleCountMenu = new Menu(Messages.getString("Dateline.MENU_SCALES"));

        contextMenu.setOnShowing(evt -> {
            unitsMenu.getItems().clear();
            getModel().getTemporalUnits().forEach(unit -> {
                CheckMenuItem item = new CheckMenuItem(Messages.getString("Dateline." + unit.toString().toUpperCase()));
                item.setSelected(unit.equals(getPrimaryTemporalUnit()));
                unitsMenu.getItems().add(item);
                item.setOnAction(e -> timeline.showTemporalUnit(unit, 50));
            });

            zoneIdMenu.getItems().clear();
            getModel().getAvailableZoneIds().forEach(zoneId -> {
                CheckMenuItem item = new CheckMenuItem(ZoneId.of(zoneId).getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()));
                item.setSelected(getZoneId().getId().equals(zoneId));
                zoneIdMenu.getItems().add(item);
                item.setOnAction(e -> setZoneId(ZoneId.of(zoneId)));
            });

            scaleCountMenu.getItems().clear();
            for (int i = 1; i <= 5; i++) {
                final int count = i;
                MenuItem scaleCountItem = new MenuItem(Integer.toString(i));
                scaleCountItem.setOnAction(e -> getModel().setScaleCount(count));
                scaleCountMenu.getItems().add(scaleCountItem);
            }
        });

        ToggleGroup toggleGroup = new ToggleGroup();
        Menu firstDayMenu = new Menu(Messages.getString("Dateline.MENU_WEEK_START"));
        for (DayOfWeek day : DayOfWeek.values()) {
            RadioMenuItem item = new RadioMenuItem(day.getDisplayName(TextStyle.FULL, Locale.getDefault()));
            if (getFirstDayOfWeek().equals(day)) {
                item.setSelected(true);
            }
            toggleGroup.getToggles().add(item);
            item.setOnAction(evt -> setFirstDayOfWeek(day));
            firstDayMenu.getItems().add(item);
        }

        contextMenu.getItems().add(unitsMenu);
        contextMenu.getItems().add(zoneIdMenu);
        contextMenu.getItems().add(showTimezoneItem);
        contextMenu.getItems().add(firstDayMenu);
        // contextMenu.getItems().add(scaleCountMenu);
        // contextMenu.getItems().add(addScaleItem);
        // contextMenu.getItems().add(removeScaleItem);

        setContextMenu(contextMenu);
    }

    private EventHandler<ActionEvent> removeScale() {
        return evt -> {
            int count = getModel().getScaleCount();
            getModel().setScaleCount(Math.max(1, count - 1));
        };
    }

    private EventHandler<ActionEvent> addScale() {
        return evt -> {
            int count = getModel().getScaleCount();
            getModel().setScaleCount(count + 1);
        };
    }
}
