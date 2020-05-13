/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
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
import com.flexganttfx.view.graphics.GraphicsBase.RowHeader;
import com.flexganttfx.view.graphics.SingleRowGraphics;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.util.FlexGanttFXControl;
import impl.com.flexganttfx.skin.timeline.EventlineSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

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
    private final ReadOnlyDoubleWrapper cursorLocation = new ReadOnlyDoubleWrapper(this, "cursorLocation", 100);
    private final ReadOnlyObjectWrapper<Instant> cursorTime = new ReadOnlyObjectWrapper<>(this, "cursorTime");

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

        this.graphics.setActivityRenderer(ActivityBase.class, GanttLayout.class, new EventlineActivityRenderer(this.graphics, "Eventline Activity Renderer"));
        this.graphics.setActivityRenderer(MutableActivityBase.class, GanttLayout.class, new EventlineActivityRenderer(this.graphics, "Eventline Mutable Activity Renderer"));
        this.graphics.setActivityRenderer(CompletableActivityBase.class, GanttLayout.class, new EventlineActivityRenderer(this.graphics, "Eventline Completable Activity Renderer"));
        this.graphics.setActivityRenderer(MutableCompletableActivityBase.class, GanttLayout.class, new EventlineActivityRenderer(this.graphics, "Eventline Mutable Completable Activity Renderer"));
        this.graphics.setShowMarkedTimeInterval(false);
        this.graphics.setShowNowLineLayer(false);
        this.graphics.setRowHeaderFactory(graphics -> new RowHeader<Row<?, ?, ?>>() {
            {
                getStyleClass().add("eventline-row-header");
                setAlignment(Pos.CENTER);
                textProperty().bind(rowHeaderTitleProperty());
            }
        });

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
        getProperties().addListener(
                (javafx.collections.MapChangeListener.Change<?, ?> change) -> {
                    if (change.getKey().equals("com.flexganttfx.eventline.cursor.location")) {
                        if (change.getValueAdded() != null) {
                            Double location = (Double) change.getValueAdded();
                            cursorLocation.set(location);
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

    public final SingleRowGraphics<Row<?, ?, ?>> getGraphics() {
        return graphics;
    }

    // Row headers support

    private final StringProperty rowHeaderTitle = new SimpleStringProperty(this, "rowHeaderTitle", "Scale");

    /**
     * Stores the title text used for the row header "column" on the right-hand
     * side. The header can also be completely replaced by retrieving the graphics
     * from the eventline and registering a new row header factory on it.
     *
     * @return the row header title
     * @see Eventline#getGraphics()
     * @see GraphicsBase#setRowHeaderFactory(Callback)
     * @since 11.11.0
     */
    public final StringProperty rowHeaderTitleProperty() {
        return rowHeaderTitle;
    }

    public final String getRowHeaderTitle() {
        return rowHeaderTitle.get();
    }

    public final void setRowHeaderTitle(String rowHeaderTitle) {
        this.rowHeaderTitle.set(rowHeaderTitle);
    }

    // Cursor time support.

    private void registerListeners() {

        final ChangeListener<Instant> startTimeListener = (value, oldInstant, newInstant) -> updateCursorTime();

        getTimeline().getModel().startTimeProperty().addListener(startTimeListener);

        final ChangeListener<Number> mppListener = (value, oldNumber, newNumber) -> updateCursorTime();

        getTimeline().getModel().millisPerPixelProperty().addListener(mppListener);

        getTimeline().modelProperty().addListener((value, oldModel, newModel) -> {
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

    // Frozen row support
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

    public final ReadOnlyDoubleProperty cursorLocationProperty() {
        return cursorLocation;
    }

    public final double getCursorLocation() {
        return cursorLocationProperty().get();
    }

    public final ReadOnlyObjectProperty<Instant> cursorTimeProperty() {
        return cursorTime;
    }

    public final Instant getCursorTime() {
        return cursorTimeProperty().get();
    }

    // Show time cursor support

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

    public final DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter.get();
    }

    public final void setDateTimeFormatter(DateTimeFormatter formatter) {
        dateTimeFormatterProperty().set(formatter);
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

    public final TimeInterval getMarkedTimeInterval() {
        return markedTimeInterval.get();
    }

    public final void setMarkedTimeInterval(TimeInterval timeInterval) {
        markedTimeInterval.set(timeInterval);
    }

    class EventlineActivityRenderer extends ActivityRenderer {

        public EventlineActivityRenderer(GraphicsBase graphics, String name) {
            super(graphics, name);
            setStroke(Color.TRANSPARENT);
        }
    }
}
