/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Calendar;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.calendar.CalendarActivity;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.ContextMenuParameter;
import com.flexganttfx.view.graphics.GraphicsBase.EditMode;
import com.flexganttfx.view.graphics.GraphicsBase.EditingCallbackParameter;
import com.flexganttfx.view.graphics.LassoEvent;
import com.flexganttfx.view.graphics.LassoEvent.LassoInfo;
import com.flexganttfx.view.graphics.layer.ScaleLayer;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import impl.com.flexganttfx.skin.util.AgendaHelper;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class GraphicsBaseSkin<C extends GraphicsBase<R>, R extends Row<?, ?, ?>> extends SkinBase<C> {

    private final Line verticalCursorLine = new Line(); // early init because it is being used in listener
    private final Line horizontalCursorLine;
    private final Line markedStartTimeLine;
    private final Line markedEndTimeLine;
    private final Region horizontalCursorIndicator;
    private final Cursor lassoCursor;
    private final Rectangle lasso;
    private final LinksCanvas<R> linksCanvas;
    private final DragCanvas<R> dragCanvas;
    private final Pane clippedContent;

    private StartLassoThread startLassoThread;

    private Instant lassoStartTime;
    private Instant lassoEndTime;

    private double lassoY1;
    private double lassoY2;
    private boolean lassoStarted;
    private boolean lassoUsed;

    private double mouseStartX;
    private double mouseX;
    private double mouseY;

    public GraphicsBaseSkin(C graphics) {
        super(graphics);

        Image image = new Image(GraphicsBase.class.getResourceAsStream("cursor-lasso.gif"));
        lassoCursor = new ImageCursor(image, image.getWidth() / 2, image.getHeight() / 2);

        graphics.lassoActiveProperty().addListener(evt -> {
            if (graphics.isLassoActive()) {
                graphics.setCursor(lassoCursor);
            } else {
                graphics.setCursor(Cursor.DEFAULT);
            }
        });

        graphics.setFocusTraversable(true);
        graphics.focusedProperty().addListener(evt -> graphics.isFocused());

        EventHandler<KeyEvent> arrowKeysHandler = event -> {
            Timeline timeline = getTimeline();
            switch (event.getCode()) {
                case RIGHT:
                    if (event.isShiftDown()) {
                        timeline.scrollRightFast();
                    } else {
                        timeline.scrollRight();
                    }
                    event.consume();
                    break;
                case LEFT:
                    if (event.isShiftDown()) {
                        timeline.scrollLeftFast();
                    } else {
                        timeline.scrollLeft();
                    }
                    event.consume();
                    break;
                case BACK_SPACE:
                case DELETE:
                    for (ActivityRef<?> ref : new ArrayList<>(graphics.getSelectedActivities())) {
                        Activity activity = ref.getActivity();
                        Callback<EditingCallbackParameter, Boolean> callback = graphics.getActivityEditingCallback(activity.getClass());
                        EditingCallbackParameter param = new EditingCallbackParameter(ref, EditMode.DELETING);
                        if (callback == null || callback.call(param)) {
                            Row row = ref.getRow();
                            row.removeActivity(ref.getLayer(), ref.getActivity());
                            graphics.getSelectedActivities().remove(ref);
                            graphics.getLinks().removeIf(link -> link.getSourceActivityRef().equals(ref) || link.getTargetActivityRef().equals(ref));
                            graphics.fireEvent(new ActivityEvent(ref, graphics, ActivityEvent.ACTIVITY_DELETED));
                        }
                    }
                    break;
                default:
                    break;
            }
        };

        EventHandler<KeyEvent> plusMinusKeyHandler = event -> {
            Timeline timeline = getTimeline();
            switch (event.getCharacter()) {
                case "-":
                    timeline.zoomOut();
                    break;
                case "+":
                    timeline.zoomIn();
                    break;
                default:
                    break;
            }
        };

        graphics.addEventHandler(KeyEvent.KEY_PRESSED, arrowKeysHandler);
        graphics.addEventHandler(KeyEvent.KEY_TYPED, plusMinusKeyHandler);

        linksCanvas = createLinksCanvas();
        linksCanvas.setManaged(false);

        dragCanvas = createDragCanvas();
        dragCanvas.setManaged(false);

        verticalCursorLine.getStyleClass().add("vertical-cursor");
        verticalCursorLine.setMouseTransparent(true);
        verticalCursorLine.setManaged(false);

        BooleanBinding verticalCursorVisible = Bindings.createBooleanBinding(() -> graphics.getRowsEditing().isEmpty()
                        && graphics.isShowVerticalCursor()
                        && graphics.getPressedActivity() == null
                        && graphics.getTimeline().getEventline()
                        .getCursorTime() != null,
                graphics.getRowsEditing(),
                graphics.showVerticalCursorProperty(),
                graphics.pressedActivityProperty(),
                graphics.getTimeline().getEventline().cursorTimeProperty());

        verticalCursorLine.visibleProperty().bind(verticalCursorVisible);

        horizontalCursorLine = new Line();
        horizontalCursorLine.getStyleClass().add("horizontal-cursor");
        horizontalCursorLine.setMouseTransparent(true);
        horizontalCursorLine.setManaged(false);

        BooleanBinding horizontalCursorVisible = Bindings.createBooleanBinding(() -> graphics.getHoverLayout() != null
                        && graphics.getHoverLayout().isSupportingHorizontalCursorLine()
                        && graphics.isShowScaleLayer()
                        && graphics.isShowHorizontalCursor()
                        && graphics.getRowsEditing().isEmpty()
                        && graphics.getPressedActivity() == null
                        && graphics.isHover(),
                graphics.hoverLayoutProperty(),
                graphics.showScaleLayerProperty(),
                graphics.showHorizontalCursorProperty(),
                graphics.pressedActivityProperty(),
                graphics.getRowsEditing(),
                graphics.hoverProperty());

        horizontalCursorLine.visibleProperty().bind(horizontalCursorVisible);

        horizontalCursorIndicator = new Region();
        horizontalCursorIndicator.getStyleClass().add("horizontal-cursor-indicator");
        horizontalCursorIndicator.setMouseTransparent(true);
        horizontalCursorIndicator.setManaged(false);
        horizontalCursorIndicator.visibleProperty().bind(horizontalCursorVisible);

        markedStartTimeLine = new Line();
        markedStartTimeLine.setManaged(false);
        markedStartTimeLine.setStartY(0);
        markedStartTimeLine.endYProperty().bind(getSkinnable().heightProperty());
        markedStartTimeLine.setMouseTransparent(true);
        markedStartTimeLine.getStyleClass().addAll("marked-time-line", "marked-start-time-line");

        markedEndTimeLine = new Line();
        markedEndTimeLine.setManaged(false);
        markedEndTimeLine.setStartY(0);
        markedEndTimeLine.endYProperty().bind(getSkinnable().heightProperty());
        markedEndTimeLine.setMouseTransparent(true);
        markedEndTimeLine.getStyleClass().addAll("marked-time-line", "marked-end-time-line");

        lasso = new Rectangle();
        lasso.setMouseTransparent(true);
        lasso.setManaged(false);
        lasso.getStyleClass().add("activities-lasso");

        Region region = createRowPaneRegion();

        getChildren().add(region);

        clippedContent = new Pane(
                linksCanvas,
                horizontalCursorLine,
                horizontalCursorIndicator,
                verticalCursorLine,
                lasso,
                markedStartTimeLine,
                markedEndTimeLine,
                dragCanvas
        );

        clippedContent.getStyleClass().add("clipped-content");
        clippedContent.setMouseTransparent(true);

        getChildren().add(clippedContent);

        registerNodeListeners(region);

        graphics.timelineProperty().addListener((observable, oldTimeline, newTimeline) -> {

            if (oldTimeline != null) {
                Eventline eventline1 = oldTimeline.getEventline();
                eventline1.cursorLocationProperty().removeListener(cursorLocationListener);
                eventline1.markedTimeIntervalProperty().removeListener(markedTimeIntervalListener);
                markedStartTimeLine.visibleProperty().unbind();
                markedEndTimeLine.visibleProperty().unbind();
            }

            if (newTimeline != null) {
                Eventline eventline2 = newTimeline.getEventline();
                eventline2.cursorLocationProperty().addListener(cursorLocationListener);
                eventline2.markedTimeIntervalProperty().addListener(markedTimeIntervalListener);

                markedStartTimeLine.visibleProperty().bind(Bindings.and(
                        graphics.showMarkedTimeIntervalProperty(),
                        Bindings.isNotNull(eventline2
                                .markedTimeIntervalProperty())));
                markedEndTimeLine.visibleProperty().bind(Bindings.and(
                        graphics.showMarkedTimeIntervalProperty(),
                        Bindings.isNotNull(eventline2
                                .markedTimeIntervalProperty())));
            }

        });

        if (graphics.getTimeline() != null) {
            Eventline eventline = graphics.getTimeline().getEventline();
            eventline.cursorLocationProperty().addListener(cursorLocationListener);
            eventline.markedTimeIntervalProperty().addListener(markedTimeIntervalListener);

            markedStartTimeLine.visibleProperty()
                    .bind(Bindings.and(
                            graphics.showMarkedTimeIntervalProperty(),
                            Bindings.isNotNull(
                                    eventline.markedTimeIntervalProperty())));
            markedEndTimeLine.visibleProperty()
                    .bind(Bindings.and(
                            graphics.showMarkedTimeIntervalProperty(),
                            Bindings.isNotNull(
                                    eventline.markedTimeIntervalProperty())));
        }

        updateMarkedTimeLines();
    }

    protected Node getClippedContent() {
        return clippedContent;
    }

    protected DragCanvas<R> createDragCanvas() {
        return new DragCanvas<>(getSkinnable());
    }

    protected LinksCanvas<R> createLinksCanvas() {
        return new LinksCanvas<>(getSkinnable());
    }

    public final DragCanvas<R> getDragCanvas() {
        return dragCanvas;
    }

    protected final Rectangle getLasso() {
        return lasso;
    }

    protected final LinksCanvas<R> getLinksCanvas() {
        return linksCanvas;
    }

    protected abstract Region createRowPaneRegion();

    protected abstract List<Row<?, ?, ?>> findLassoSelectedRows();

    protected abstract List<ActivityRef<?>> findLassoSelectedActivities();

    private void performSelection() {
        if (lassoStartTime == null || lassoEndTime == null) {
            return;
        }

        GraphicsBase<R> view = getSkinnable();
        switch (view.getSelectionMode()) {
            case MULTIPLE:
                break;
            case SINGLE:
                view.getSelectedActivities().clear();
                break;
            case NONE:
                return;
        }

        List<ActivityRef<?>> selection = findLassoSelectedActivities();
        selection.removeAll(view.getSelectedActivities());

        view.getSelectedActivities().addAll(selection);
    }

    protected final LassoInfo createLassoInfo(MouseEvent evt) {

        LocalTime localStartTime = getLocalTimeAt(Math.min(lassoY1, lassoY2));

        LocalTime localEndTime = getLocalTimeAt(Math.max(lassoY1, lassoY2));

        VirtualGrid<?> grid = getSkinnable().getVirtualGrid();

        if (grid != null) {
            if (localStartTime != null) {
                localStartTime = grid.adjustTime(localStartTime, false);
            }
            if (localEndTime != null) {
                localEndTime = grid.adjustTime(localEndTime, true);
            }
        }

        List<Row<?, ?, ?>> rows = findLassoSelectedRows();
        List<ActivityRef<?>> activities = findLassoSelectedActivities();

        if (lassoStartTime.isBefore(lassoEndTime)) {
            return new LassoInfo(evt, lassoStartTime, lassoEndTime, localStartTime, localEndTime, rows, activities);
        }

        return new LassoInfo(evt, lassoEndTime, lassoStartTime, localStartTime, localEndTime, rows, activities);
    }

    protected final RowPane<R> getRowPane(ActivityRef<?> ref) {
        for (RowPane<R> pane : getSkinnable().getRowPanes()) {
            if (pane.getRow() != null && pane.getRow().isShowing() && pane.getRow() == ref.getRow()) {
                return pane;
            }
        }

        return null;
    }

    protected final RowCanvas<R> getRowCanvas(ActivityRef<?> ref) {
        RowPane<R> rowPane = getRowPane(ref);
        if (rowPane != null) {
            return rowPane.getCanvas();
        }

        return null;
    }

    protected final Rectangle2D getActivityBounds(ActivityRef<?> ref) {
        RowCanvas<R> canvas = getRowCanvas(ref);

        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;

        if (canvas != null) {

            ActivityBounds activityBounds = canvas.getActivityBounds(ref);

            if (activityBounds != null) {

                Point2D graphicsBounds = getSkinnable().localToScene(0, 0);

                Bounds canvasBounds = canvas.localToScene(canvas.getLayoutBounds());

                double transY = canvasBounds.getMinY() - graphicsBounds.getY();

                x = activityBounds.getMinX();
                y = activityBounds.getMinY() + transY;
                w = activityBounds.getWidth();
                h = activityBounds.getHeight();

            }

        } else {
            R row = (R) ref.getRow();
            if (isRowAboveViewport(row)) {
                y = -100;
            } else {
                y = getSkinnable().getHeight() + 100;
            }

            TimelineModel<?> timelineModel = getTimeline().getModel();

            // TODO: translate
            double x1 = timelineModel.calculateLocationForTime(ref.getActivity().getStartTime());
            double x2 = timelineModel.calculateLocationForTime(ref.getActivity().getEndTime());

            x = x1;
            w = x2 - x1;
            h = row.getHeight();
        }

        return new Rectangle2D(x, y, w, h);
    }

    public final ActivityBounds getActivityBoundsAt(double x, double y) {
        Point2D localToScene = getSkinnable().localToScene(x, y);
        RowCanvas<R> canvas = getRowCanvasAt(y);
        if (canvas != null) {
            Bounds canvasBounds = canvas.localToScene(canvas.getLayoutBounds());
            return canvas.getActivityBounds(
                    localToScene.getX() - canvasBounds.getMinX(),
                    localToScene.getY() - canvasBounds.getMinY());
        }

        return null;
    }

    public final ActivityRef<?> getActivityRefAt(double x, double y) {
        ActivityBounds bounds = getActivityBoundsAt(x, y);
        if (bounds != null) {
            return bounds.getActivityRef();
        }

        return null;
    }

    public final List<ActivityBounds> getAllActivityBoundsAt(double x,
                                                             double y) {
        Point2D localToScene = getSkinnable().localToScene(x, y);
        RowCanvas<R> canvas = getRowCanvasAt(y);
        if (canvas != null) {
            Bounds canvasBounds = canvas.localToScene(canvas.getLayoutBounds());
            return canvas.getAllActivityBounds(
                    localToScene.getX() - canvasBounds.getMinX(),
                    localToScene.getY() - canvasBounds.getMinY());
        }

        return Collections.emptyList();
    }

    public final List<ActivityRef<?>> getAllActivityRefsAt(double x, double y) {
        List<ActivityRef<?>> result = new ArrayList<>();
        List<ActivityBounds> bounds = getAllActivityBoundsAt(x, y);
        result.addAll(bounds.stream().map(ActivityBounds::getActivityRef).collect(Collectors.toList()));
        return result;
    }

    public final List<CalendarActivity> getAllCalendarActivitiesAt(double x,
                                                                   double y) {

        List<CalendarActivity> result = new ArrayList<>();

        Instant time = getSkinnable().getTimeAt(x);
        Dateline dateline = getSkinnable().getTimeline().getDateline();
        TemporalUnit unit = dateline.getPrimaryTemporalUnit();
        ZoneId zoneId = dateline.getZoneId();

        addCalendarEntries(result, getSkinnable().getCalendars(), time, unit,
                zoneId);

        R row = getRowAt(y);

        if (row != null) {
            addCalendarEntries(result, row.getCalendars(), time, unit, row.getZoneId());
        }

        return result;
    }

    private void addCalendarEntries(List<CalendarActivity> result, List<Calendar<?>> calendars, Instant time, TemporalUnit unit, ZoneId zoneId) {

        for (Calendar<?> calendar : calendars) {
            @SuppressWarnings("unchecked")
            Iterator<CalendarActivity> activities = (Iterator<CalendarActivity>) calendar.getActivities(null, time, time, unit, zoneId);
            if (activities != null) {
                while (activities.hasNext()) {
                    result.add(activities.next());
                }
            }
        }
    }

    public final R getRowAt(double y) {
        RowPane<R> pane = getRowPaneAt(y);
        if (pane != null) {
            return pane.getRow();
        }

        return null;
    }

    protected abstract RowPane<R> getRowPaneAt(double y);

    protected abstract boolean isRowAboveViewport(R row);

    protected final RowCanvas<R> getRowCanvasAt(double y) {
        RowPane<R> pane = getRowPaneAt(y);
        if (pane != null) {
            return pane.getCanvas();
        }

        return null;
    }

    public final Layout getLayoutAt(double y) {
        Point2D localToScene = getSkinnable().localToScene(0, y);
        RowCanvas<R> canvas = getRowCanvasAt(y);
        if (canvas != null) {
            Bounds canvasBounds = canvas.localToScene(canvas.getLayoutBounds());
            return canvas.getLayoutAt(localToScene.getY() - canvasBounds.getMinY());
        }

        return null;
    }

    protected final Rectangle2D getLayoutBoundsAt(double y) {
        Point2D localToScene = getSkinnable().localToScene(0, y);
        RowCanvas<R> canvas = getRowCanvasAt(y);
        if (canvas != null) {
            Bounds canvasBounds = canvas.localToScene(canvas.getLayoutBounds());
            return canvas.getLayoutBoundsAt(localToScene.getY() - canvasBounds.getMinY());
        }

        return null;
    }

    public final LocalTime getLocalTimeAt(double y) {
        Layout layout = getLayoutAt(y);
        Rectangle2D layoutBounds = getLayoutBoundsAt(y);

        if (layout instanceof AgendaLayout && layoutBounds != null) {

            AgendaLayout agendaLayout = (AgendaLayout) layout;

            RowCanvas<R> canvas = getRowCanvasAt(y);

            if (canvas != null) {
                Bounds graphicsViewBounds = getSkinnable().localToScreen(getSkinnable().getBoundsInLocal());
                Bounds canvasBounds = canvas.localToScreen(canvas.getBoundsInLocal());

                double padding = layout.getPadding();
                double offset = canvasBounds.getMinY()
                        - graphicsViewBounds.getMinY() + layoutBounds.getMinY()
                        + padding;

                double agendaY = Math.min(layoutBounds.getHeight(), Math.max(0, y - offset));

                return AgendaHelper.getTimeAt(agendaY, layoutBounds.getHeight() - 2 * padding, agendaLayout.getStartTime(), agendaLayout.getEndTime());
            }
        }

        return null;
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        linksCanvas.setWidth(contentWidth);
        linksCanvas.setHeight(contentHeight);

        dragCanvas.setWidth(contentWidth);
        dragCanvas.setHeight(contentHeight);

        lasso.setVisible(false);

        if (lassoStarted && lassoStartTime != null && lassoEndTime != null) {
            Timeline timeline = getTimeline();
            TimelineModel<?> model = timeline.getModel();

            double x1 = model.calculateLocationForTime(lassoStartTime);
            double x2 = model.calculateLocationForTime(lassoEndTime);

            lasso.setX(Math.min(x1, x2));
            lasso.setY(Math.min(lassoY1, lassoY2));
            lasso.setWidth(Math.max(x2, x1) - Math.min(x1, x2));
            lasso.setHeight(Math.max(lassoY1, lassoY2) - Math.min(lassoY1, lassoY2));
            lasso.setVisible(true);

            if (lassoStartTime.isBefore(lassoEndTime)) {
                getTimeline().getEventline().setMarkedTimeInterval(new TimeInterval(lassoStartTime, lassoEndTime));
            } else {
                getTimeline().getEventline().setMarkedTimeInterval(new TimeInterval(lassoEndTime, lassoStartTime));
            }
        }
    }

    @SuppressWarnings("unchecked")
    final void registerNodeListeners(Region node) {
        node.addEventHandler(MouseEvent.ANY, evt -> {
            mouseX = evt.getX();
            mouseY = evt.getY();
        });

        node.addEventHandler(ScrollEvent.SCROLL, evt -> {
            double deltaX = evt.getDeltaX();
            if (deltaX != 0) {
                TimelineModel<?> timelineModel = getTimeline().getModel();
                Instant time = timelineModel.calculateTimeForLocation(-deltaX);
                timelineModel.setStartTime(time);
            }
        });

        node.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, evt1 -> {
             if (!lassoStarted) {
                showContextMenu(evt1);
            }
        });

        EventHandler<MouseEvent> updateHorizontalCursor = evt -> {
            horizontalCursorLine.setStartX(0);
            horizontalCursorLine.setEndX(getSkinnable().getWidth());
            horizontalCursorLine.setStartY(evt.getY());
            horizontalCursorLine.setEndY(evt.getY());
            horizontalCursorLine.setStartX(0);
            GraphicsBase<R> graphicsView = getSkinnable();

            if (graphicsView.isShowScaleLayer()) {
                ScaleLayer<R> layer = graphicsView.getSystemLayer(ScaleLayer.class);
                if (layer != null) {
                    double prefWidth = horizontalCursorIndicator.prefWidth(-1);
                    double prefHeight = horizontalCursorIndicator.prefHeight(-1);
                    horizontalCursorIndicator.resizeRelocate(layer.getScaleWidth() - prefWidth - 2, evt.getY() - prefHeight / 2, prefWidth, prefHeight);
                    horizontalCursorLine.setStartX(layer.getPrefWidth());
                }
            }
        };

        node.addEventHandler(MouseEvent.MOUSE_MOVED, updateHorizontalCursor);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, updateHorizontalCursor);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> updateCursorLocation(-1));
        node.addEventHandler(MouseEvent.MOUSE_MOVED, evt -> updateCursorLocation(evt.getX()));
        node.addEventHandler(MouseEvent.MOUSE_EXITED, evt -> {
            updateCursorLocation(-1);
            getSkinnable().getProperties().put("com.flexganttfx.hover.activity", null);
            getSkinnable().getProperties().put("com.flexganttfx.hover.row", null);
            getSkinnable().getProperties().put("com.flexganttfx.hover.layout", null);
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
            if (contextMenu != null && contextMenu.isShowing()) {
                contextMenu.hide();
            }

            if (!evt.isPrimaryButtonDown()) {
                return;
            }

            getSkinnable().requestFocus();

            Timeline timeline = getTimeline();

            /*
             * Clear the dateline selection of intervals, but only if the
             * primary button was used. This way we can still use the selection
             * while working with the context menu.
             */
            if (evt.isPrimaryButtonDown()) {
                timeline.getDateline().getSelectedIntervals().clear();
            }

            // updateCursorLocation(-1);

            mouseStartX = evt.getScreenX();

            if (evt.isShiftDown()) {
                if (getSkinnable().getEditMode().equals(EditMode.NONE)
                        && getSkinnable().getHoverActivity() == null) {

                    /*
                     * First we assume the lasso has never actually started.
                     * This flag gets set to true in the startLasso() method.
                     */
                    lassoStarted = false;

                    startLasso(evt);
                }
            } else {
                /*
                 * First we assume the lasso has never actually started. This
                 * flag gets set to true in the startLasso() method.
                 */
                lassoStarted = false;

                startLassoThread = new StartLassoThread(evt);
                startLassoThread.start();
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {
            if (lassoStarted) {
                stopLasso(evt);
            }

            if (startLassoThread != null) {
                startLassoThread.cancel();
                startLassoThread = null;
            }

            updateCursorLocation(evt.getX());

            mouseStartX = evt.getScreenX();
        });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
            if (!evt.isPrimaryButtonDown() || evt.isConsumed()) {
                return;
            }

            Timeline timeline = getTimeline();
            TimelineModel<?> timelineModel = timeline.getModel();

            if (getSkinnable().isLassoActive()) {

                lassoEndTime = timelineModel.calculateTimeForLocation(evt.getX());

                if (getSkinnable().isLassoSnapsToGrid()) {
                    lassoEndTime = GridHelper.grid(getSkinnable(), lassoEndTime);
                }

                // hack, this should not be necessary
                if (lassoStartTime == null) {
                    lassoStartTime = lassoEndTime;
                }

                lassoY2 = evt.getY();

                /*
                 * The lasso has to have a minimum size before we consider it
                 * being "used".
                 */
                if (Math.abs(lassoY2 - lassoY1) > 5) {
                    lassoUsed = true;
                }

                // Fire lasso event
                GraphicsBase<Row<?, ?, ?>> graphics = (GraphicsBase<Row<?, ?, ?>>) getSkinnable();
                LassoInfo info = createLassoInfo(evt);
                LassoEvent event = new LassoEvent(graphics,
                        LassoEvent.SELECTION_ONGOING, info);
                getSkinnable().fireEvent(event);

                getSkinnable().requestLayout();
            } else if (getSkinnable().getEditMode().equals(EditMode.NONE)
                    && getSkinnable().isHorizontalDragEnabled()) {
                if (LoggingDomain.NAVIGATION.isLoggable(Level.FINE)) {
                    LoggingDomain.NAVIGATION.fine("scrolling");
                }

                double scrollX = evt.getScreenX();
                double deltaX = mouseStartX - scrollX;

                Instant newStartTime = timelineModel.calculateTimeForLocation(deltaX);

                /*
                 * Hack for the case when the dateline displays SimpleUnit.ONE.
                 * In this case the same instant is shown across several pixels
                 * and the dateline will hardly move.
                 */
                if (newStartTime.equals(timelineModel.getStartTime())) {
                    if (deltaX > 0) {
                        newStartTime = newStartTime.plusMillis(1);
                    } else {
                        newStartTime = newStartTime.minusMillis(1);
                    }
                }

                timelineModel.setStartTime(newStartTime);

                mouseStartX = scrollX;
            }
        });

        node.widthProperty().addListener((value, oldNumber, newNumber) -> getSkinnable().redraw());

        node.heightProperty().addListener((value, oldHeight, newHeight) -> {
            verticalCursorLine.setEndY(newHeight.doubleValue());
            getSkinnable().redraw();
        });
    }

    private void updateCursorLocation(double location) {
        getTimeline().getEventline().getProperties().put("com.flexganttfx.eventline.cursor.location", location);
    }

    class StartLassoThread extends Thread {
        private boolean cancelled;
        private final MouseEvent mouseEvent;

        public StartLassoThread(MouseEvent evt) {
            this.mouseEvent = evt;
        }

        public void cancel() {
            cancelled = true;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!cancelled && (mouseEvent.getX() == mouseX)
                    && (mouseEvent.getY() == mouseY)) {
                Platform.runLater(() -> startLasso(mouseEvent));
            }
        }
    }

    private void startLasso(MouseEvent evt) {
        C graphics = getSkinnable();
        if (!graphics.isLassoEnabled()) {
            return;
        }

        ObservableMap<Object, Object> properties = graphics.getProperties();

        properties.put("com.flexganttfx.currenteditmode", EditMode.NONE);
        properties.put("com.flexganttfx.lassoActive", true);
        properties.put("com.flexganttfx.hover.activity", null);
        properties.put("com.flexganttfx.hover.row", null);
        properties.put("com.flexganttfx.hover.layout", null);

        lassoStarted = true;

        /*
         * The lasso is only considered "used" after the user has performed a
         * minimum drag.
         */
        lassoUsed = false;

        lassoY1 = evt.getY();
        lassoY2 = evt.getY();

        Timeline timeline = getTimeline();
        TimelineModel<?> model = timeline.getModel();

        lassoStartTime = model.calculateTimeForLocation(evt.getX());
        if (graphics.isLassoSnapsToGrid()) {
            lassoStartTime = GridHelper.grid(graphics, lassoStartTime);
        }

        lassoEndTime = lassoStartTime;

        LassoInfo info = createLassoInfo(evt);

        LassoEvent event = new LassoEvent(graphics, LassoEvent.SELECTION_STARTED, info);

        graphics.fireEvent(event);
    }

    private void stopLasso(MouseEvent evt) {
        if (lassoUsed) {
            performSelection();
            lassoUsed = false;
        }

        lassoStarted = false;

        LassoInfo info = createLassoInfo(evt);

        LassoEvent lassoEvent = new LassoEvent(getSkinnable(), LassoEvent.SELECTION_FINISHED, info);

        getSkinnable().fireEvent(lassoEvent);

        getSkinnable().getProperties().put("com.flexganttfx.lassoActive", false);

        if (getSkinnable().getRows().isEmpty()) {

            /*
             * Normally the RowCanvasBehaviour class takes care of clearing the marked
             * time interval, but when there are no rows then there are no canvases and
             * no RowCanvasBehaviour to clear it.
             *
             * See also FLEXFX-271: "Marker lines stay visible when no rows in Gantt chart"
             */
            getTimeline().getEventline().setMarkedTimeInterval(null);
        }

        getSkinnable().requestLayout();
    }

    private final ChangeListener<Number> cursorLocationListener = (value, oldLocation, newLocation) -> {
        verticalCursorLine.setStartX(newLocation.doubleValue());
        verticalCursorLine.setStartY(0);
        verticalCursorLine.setEndX(newLocation.doubleValue());
        verticalCursorLine.setEndY(getSkinnable().getHeight());
    };

    private final InvalidationListener markedTimeIntervalListener = it -> updateMarkedTimeLines();

    private void updateMarkedTimeLines() {
        Timeline timeline = getSkinnable().getTimeline();
        Eventline eventline = timeline.getEventline();
        TimeInterval newInterval = eventline.getMarkedTimeInterval();

        TimelineModel<?> model = timeline.getModel();

        if (newInterval != null) {
            double x1 = model.calculateLocationForTime(newInterval.getStartTime());
            double x2 = model.calculateLocationForTime(newInterval.getEndTime());

            markedStartTimeLine.setStartX(x1);
            markedStartTimeLine.setEndX(x1);
            markedEndTimeLine.setStartX(x2);
            markedEndTimeLine.setEndX(x2);
        }
    }

    protected final Timeline getTimeline() {
        return getSkinnable().getTimeline();
    }

    static class DisabledSelectionModel<T> extends MultipleSelectionModel<T> {
        DisabledSelectionModel() {
            super.setSelectedIndex(-1);
            super.setSelectedItem(null);
        }

        @Override
        public ObservableList<Integer> getSelectedIndices() {
            return FXCollections.emptyObservableList();
        }

        @Override
        public ObservableList<T> getSelectedItems() {
            return FXCollections.emptyObservableList();
        }

        @Override
        public void selectAll() {
        }

        @Override
        public void selectFirst() {
        }

        @Override
        public void selectIndices(int index, int... indicies) {
        }

        @Override
        public void selectLast() {
        }

        @Override
        public void clearAndSelect(int index) {
        }

        @Override
        public void clearSelection() {
        }

        @Override
        public void clearSelection(int index) {
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean isSelected(int index) {
            return false;
        }

        @Override
        public void select(int index) {
        }

        @Override
        public void select(T item) {
        }

        @Override
        public void selectNext() {
        }

        @Override
        public void selectPrevious() {
        }
    }

    private ContextMenu contextMenu;

    @SuppressWarnings("unchecked")
    private void showContextMenu(ContextMenuEvent evt) {
        if (getSkinnable().getContextMenu() != null) {
            return;
        }

        if (contextMenu != null && contextMenu.isShowing()) {
            contextMenu.hide();
        }

        Callback<ContextMenuParameter<R>, ContextMenu> factory = getSkinnable()
                .getContextMenuCallback();

        if (factory != null) {
            RowCanvas<R> rowPane = null;
            R row = null;

            Object target = evt.getTarget();
            if (target instanceof RowCanvas) {
                rowPane = (RowCanvas<R>) target;
                row = rowPane.getRow();
            }

            Layout layout = getLayoutAt(evt.getY());
            LocalTime localTime = getLocalTimeAt(evt.getY());

            Instant time = getSkinnable().getTimeAt(evt.getX());

            ContextMenuParameter<R> input = new ContextMenuParameter<>(
                    getSkinnable(), row,
                    getAllActivityRefsAt(evt.getX(), evt.getY()), layout, time,
                    localTime);

            contextMenu = factory.call(input);

            if (contextMenu != null) {
                contextMenu.show(rowPane, evt.getScreenX(), evt.getScreenY());
            }
        }
    }
}