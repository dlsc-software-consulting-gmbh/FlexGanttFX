/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.timeline;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.ActivityBase;
import com.flexganttfx.model.activity.CompletableActivityBase;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.activity.MutableCompletableActivityBase;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.SingleRowGraphics;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.util.FlexGanttFXControl;
import impl.com.flexganttfx.skin.timeline.EventlineSkin;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet.Item;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The eventline is a control that displays time cursors and other information
 * that might be relevant for all rows in the Gantt chart. It also supports a
 * "frozen row", which is basically a single row that does not scroll out of
 * sight. To do this the eventline contains a {@link SingleRowGraphics} child
 * node. Activities added to the frozen row (see {@link #frozenRowProperty()})
 * will show up inside the graphics node. A frozen row is very useful when
 * applications want to show global events.
 * <p>
 * <img src="doc-files/eventline.png" alt="Eventline">
 *
 * @since 1.0
 */
public class Eventline extends FlexGanttFXControl {

    private static final String DEFAULT_STYLE_CLASS = "eventline";

    private final Timeline timeline;

    private final SingleRowGraphics<Row<?, ?, ?>> graphics;

    /**
     * Constructs a new eventline.
     *
     * @param timeline the parent timeline container
     * @since 1.0
     */
    public Eventline(Timeline timeline) {
        Objects.requireNonNull(timeline);

        this.timeline = timeline;
        this.graphics = new SingleRowGraphics<>();

        this.graphics.visibleProperty().bind(showFrozenRow);
        this.graphics.managedProperty().bind(showFrozenRow);

        this.graphics.setActivityRenderer(ActivityBase.class, GanttLayout.class, new EventlineActivityRenderer(this.graphics, "Eventline Activity Renderer"));
        this.graphics.setActivityRenderer(MutableActivityBase.class, GanttLayout.class, new EventlineActivityRenderer(this.graphics, "Eventline Mutable Activity Renderer"));
        this.graphics.setActivityRenderer(CompletableActivityBase.class, GanttLayout.class, new EventlineActivityRenderer(this.graphics, "Eventline Completable Activity Renderer"));
        this.graphics.setActivityRenderer(MutableCompletableActivityBase.class, GanttLayout.class, new EventlineActivityRenderer(this.graphics, "Eventline Mutable Completable Activity Renderer"));

        registerListeners();
        setFocusTraversable(true);
        setPrefWidth(0);
        setMinWidth(0);

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        cursorLocationProperty().addListener(observable -> updateCursorTime());

        /*
         * We are "abusing" the properties map to pass new values of read-only
         * properties from the skin to the control.
         */
        getProperties()
                .addListener(
                        (javafx.collections.MapChangeListener.Change<?, ?> change) -> {
                            if (change.getKey().equals("com.flexganttfx.eventline.cursor.location")) {
                                if (change.getValueAdded() != null) {
                                    Double mode = (Double) change.getValueAdded();
                                    cursorLocation.set(mode);
                                }
                            }
                        });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new EventlineSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return super.getUserAgentStylesheet(Eventline.class, "eventline.css");
    }

    class EventlineActivityRenderer extends ActivityRenderer {

        public EventlineActivityRenderer(GraphicsBase graphics, String name) {
            super(graphics, name);
            setStroke(Color.TRANSPARENT);
        }
    }

    private final BooleanProperty showFrozenRow = new SimpleBooleanProperty(this, "showFrozenRow", false);

    public final BooleanProperty showFrozenRowProperty() {
        return showFrozenRow;
    }

    public final boolean isShowFrozenRow() {
        return showFrozenRow.get();
    }

    public final void setShowFrozenRow(boolean showFrozenRow) {
        this.showFrozenRow.set(showFrozenRow);
    }

    public final SingleRowGraphics<Row<?, ?, ?>> getGraphics() {
        return graphics;
    }

    private void registerListeners() {

        final ChangeListener<Instant> startTimeListener = (value, oldInstant, newInstant) -> updateCursorTime();

        getTimeline().getModel().startTimeProperty().addListener(startTimeListener);

        final ChangeListener<Number> mppListener = (value, oldNumber, newNumber) -> updateCursorTime();

        getTimeline().getModel().millisPerPixelProperty().addListener(mppListener);

        getTimeline().modelProperty().addListener(
                (value, oldModel, newModel) -> {

                    if (oldModel != null) {
                        oldModel.startTimeProperty().removeListener(startTimeListener);
                        oldModel.millisPerPixelProperty().removeListener(mppListener);
                    }

                    if (newModel != null) {
                        newModel.startTimeProperty().addListener(startTimeListener);
                        newModel.millisPerPixelProperty().addListener(mppListener);
                    }
                });
    }

    private void updateCursorTime() {
        TimelineModel<?> timelineModel = getTimeline().getModel();
        double location = getCursorLocation();
        if (location < 0 || location > getWidth()) {
            cursorTime.set(null);
        } else {
            cursorTime.set(timelineModel.calculateTimeForLocation(location));
        }
    }

    public final Timeline getTimeline() {
        return timeline;
    }

    // Row support
    private final ObjectProperty<Row<?, ?, ?>> frozenRow = new SimpleObjectProperty<>(this, "frozenRow");

    public final ObjectProperty<Row<?, ?, ?>> frozenRowProperty() {
        return frozenRow;
    }

    public final Row<?, ?, ?> getFrozenRow() {
        return frozenRow.get();
    }

    public final void setFrozenRow(Row<?, ?, ?> frozenRow) {
        this.frozenRow.set(frozenRow);
    }

    // Cursor time support.

    private final ReadOnlyDoubleWrapper cursorLocation = new ReadOnlyDoubleWrapper(this, "cursorLocation", 100);

    public final ReadOnlyDoubleProperty cursorLocationProperty() {
        return cursorLocation;
    }

    public final double getCursorLocation() {
        return cursorLocationProperty().get();
    }

    private final ReadOnlyObjectWrapper<Instant> cursorTime = new ReadOnlyObjectWrapper<>(this, "cursorTime");

    public final ReadOnlyObjectProperty<Instant> cursorTimeProperty() {
        return cursorTime;
    }

    public final Instant getCursorTime() {
        return cursorTimeProperty().get();
    }

    private final BooleanProperty showTimeCursor = new SimpleBooleanProperty(this, "showTimeCursor", true);

    public final BooleanProperty showTimeCursorProperty() {
        return showTimeCursor;
    }

    public final boolean isShowTimeCursor() {
        return showTimeCursorProperty().get();
    }

    public final void setShowTimeCursor(boolean show) {
        showTimeCursorProperty().set(show);
    }

    // DST marker support.

    private final BooleanProperty showDSTMarker = new SimpleBooleanProperty(this, "showDSTMarker", true);

    public final BooleanProperty showDSTMarkerProperty() {
        return showDSTMarker;
    }

    public final boolean isShowDSTMarker() {
        return showDSTMarkerProperty().get();
    }

    public final void setShowDSTMarker(boolean show) {
        showDSTMarkerProperty().set(show);
    }

    // Date formatter support.

    private final ObjectProperty<DateTimeFormatter> dateTimeFormatter = new SimpleObjectProperty<>(this, "dateTimeFormatter", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));

    public final ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty() {
        return dateTimeFormatter;
    }

    public final void setDateTimeFormatter(DateTimeFormatter formatter) {
        dateTimeFormatterProperty().set(formatter);
    }

    public final DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter.get();
    }

    // Marked time interval support.

    private final BooleanProperty showMarkedTimeInterval = new SimpleBooleanProperty(this, "showMarkedTimeInterval", true);

    public final BooleanProperty showMarkedTimeIntervalProperty() {
        return showMarkedTimeInterval;
    }

    public final boolean isShowMarkedTimeInterval() {
        return showMarkedTimeIntervalProperty().get();
    }

    public final void setShowMarkedTimeInterval(boolean show) {
        showMarkedTimeIntervalProperty().set(show);
    }

    private final ObjectProperty<TimeInterval> markedTimeInterval = new SimpleObjectProperty<>(this, "markedTimeInterval");

    public final ObjectProperty<TimeInterval> markedTimeIntervalProperty() {
        return markedTimeInterval;
    }

    public final void setMarkedTimeInterval(TimeInterval timeInterval) {
        markedTimeInterval.set(timeInterval);
    }

    public final TimeInterval getMarkedTimeInterval() {
        return markedTimeInterval.get();
    }

    private static final String EVENTLINE_PROPERTIES_CATEGORY = "Control: Eventline";

    public final List<Item> getPropertySheetItems() {

        List<Item> items = new ArrayList<>();

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(showTimeCursorProperty());
            }

            @Override
            public void setValue(Object value) {
                setShowTimeCursor((Boolean) value);
            }

            @Override
            public Object getValue() {
                return isShowTimeCursor();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Time Cursor";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the display of the time cursor.";
            }

            @Override
            public String getCategory() {
                return EVENTLINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(showFrozenRowProperty());
            }

            @Override
            public void setValue(Object value) {
                setShowFrozenRow((Boolean) value);
            }

            @Override
            public Object getValue() {
                return isShowFrozenRow();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Frozen Row";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the use of a frozen row.";
            }

            @Override
            public String getCategory() {
                return EVENTLINE_PROPERTIES_CATEGORY;
            }
        });

        items.add(new Item() {

            @Override
            public Optional<ObservableValue<?>> getObservableValue() {
                return Optional.of(showMarkedTimeIntervalProperty());
            }

            @Override
            public void setValue(Object value) {
                setShowMarkedTimeInterval((Boolean) value);
            }

            @Override
            public Object getValue() {
                return isShowMarkedTimeInterval();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Show Marked Time Intervals";
            }

            @Override
            public String getDescription() {
                return "Enables / disables the display of a marked time interval.";
            }

            @Override
            public String getCategory() {
                return EVENTLINE_PROPERTIES_CATEGORY;
            }
        });

        return items;
    }
}
