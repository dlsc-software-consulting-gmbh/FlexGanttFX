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
        this.graphics.setRowHeaderFactory(graphics -> new RowHeader<>(this.graphics) {
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

    /**
     * Creates the default skin for the event line control.
     *
     * @return the default skin
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new EventlineSkin(this);
    }

    /**
     * Returns the user agent stylesheet for the event line control.
     *
     * @return the stylesheet URL
     */
    @Override
    public String getUserAgentStylesheet() {
        return super.getUserAgentStylesheet(Eventline.class, "eventline.css");
    }

    /**
     * Returns the graphics view shown inside the event line.
     *
     * @return the embedded graphics view
     */
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

    /**
     * Returns the timeline used by the event line.
     *
     * @return the timeline
     */
    public final Timeline getTimeline() {
        return timeline;
    }

    // Frozen row support
    private final ObjectProperty<Row<?, ?, ?>> frozenRow = new SimpleObjectProperty<>(this, "frozenRow");

    /**
     * The frozenRow property. Stores the row currently pinned in the event line.
     *
     * @return the frozenRow property
     */
    public final ObjectProperty<Row<?, ?, ?>> frozenRowProperty() {
        return frozenRow;
    }

    public final Row<?, ?, ?> getFrozenRow() {
        return frozenRow.get();
    }

    public final void setFrozenRow(Row<?, ?, ?> frozenRow) {
        this.frozenRow.set(frozenRow);
    }

    /**
     * The cursorLocation property. Exposes the horizontal location of the time cursor.
     *
     * @return the cursorLocation property
     */
    public final ReadOnlyDoubleProperty cursorLocationProperty() {
        return cursorLocation;
    }

    public final double getCursorLocation() {
        return cursorLocationProperty().get();
    }

    /**
     * The cursorTime property. Exposes the time shown by the time cursor.
     *
     * @return the cursorTime property
     */
    public final ReadOnlyObjectProperty<Instant> cursorTimeProperty() {
        return cursorTime;
    }

    public final Instant getCursorTime() {
        return cursorTimeProperty().get();
    }

    // Show time cursor support

    private final BooleanProperty showTimeCursor = new SimpleBooleanProperty(this, "showTimeCursor", true);

    /**
     * The showTimeCursor property. Controls whether the time cursor is shown.
     *
     * @return the showTimeCursor property
     */
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

    /**
     * The showDSTMarker property. Controls whether daylight-saving markers are shown.
     *
     * @return the showDSTMarker property
     */
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

    /**
     * The dateTimeFormatter property. Stores the formatter used for event line time labels.
     *
     * @return the dateTimeFormatter property
     */
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

    /**
     * The showMarkedTimeInterval property. Controls whether the marked interval is shown.
     *
     * @return the showMarkedTimeInterval property
     */
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

    /**
     * The markedTimeInterval property. Stores the interval highlighted in the event line.
     *
     * @return the markedTimeInterval property
     */
    public final ObjectProperty<TimeInterval> markedTimeIntervalProperty() {
        return markedTimeInterval;
    }

    public final TimeInterval getMarkedTimeInterval() {
        return markedTimeInterval.get();
    }

    public final void setMarkedTimeInterval(TimeInterval timeInterval) {
        markedTimeInterval.set(timeInterval);
    }

    /**
     * Activity renderer used by the eventline's embedded graphics view.
     * It renders frozen-row activities without a stroke so they match the eventline presentation.
     */
    static class EventlineActivityRenderer extends ActivityRenderer {

        /**
         * Constructs a new event line activity renderer.
         *
         * @param graphics the graphics view that owns the renderer
         * @param name the renderer name
         */
        public EventlineActivityRenderer(GraphicsBase graphics, String name) {
            super(graphics, name);
            setStroke(Color.TRANSPARENT);
        }
    }
}
