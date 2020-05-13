/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Layout;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.ChartActivity;
import com.flexganttfx.model.activity.CompletableActivity;
import com.flexganttfx.model.activity.MutableActivity;
import com.flexganttfx.model.activity.MutableChartActivity;
import com.flexganttfx.model.activity.MutableCompletableActivity;
import com.flexganttfx.model.exception.IllegalLineIndexException;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.DragAndDropInfo;
import com.flexganttfx.view.graphics.GraphicsBase.EditMode;
import com.flexganttfx.view.graphics.GraphicsBase.EditModeCallbackParameter;
import com.flexganttfx.view.graphics.GraphicsBase.EditingCallbackParameter;
import com.flexganttfx.view.graphics.GraphicsBase.SelectionMode;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import impl.com.flexganttfx.skin.util.AgendaHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.flexganttfx.core.LoggingDomain.DND;
import static com.flexganttfx.core.LoggingDomain.EDITING;
import static com.flexganttfx.view.graphics.GraphicsBase.EditMode.DRAGGING;
import static com.flexganttfx.view.graphics.GraphicsBase.EditMode.NONE;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.paint.Color.TRANSPARENT;

public final class RowCanvasBehaviour<R extends Row<?, ?, ?>> {

    private static final String DRAGANDDROPINFO = "com.flexganttfx.draganddropinfo";

    private static final String CURRENTEDITMODE = "com.flexganttfx.currenteditmode";

    private static final String CURRENTLYEDITEDACTIVITY = "com.flexganttfx.currentlyeditedactivity";

    private static final Map<EditMode, Cursor> cursorMap = new HashMap<>();

    static {
        for (EditMode op : EditMode.values()) {
            cursorMap.put(op, Cursor.DEFAULT);
        }

        cursorMap.put(EditMode.NONE, Cursor.DEFAULT);

        Image percentageImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-percentage.gif"));
        ImageCursor percentageCursor = new ImageCursor(percentageImage, percentageImage.getWidth(), percentageImage.getHeight() / 2);
        cursorMap.put(EditMode.PERCENTAGE_COMPLETE_CHANGE, percentageCursor);

        Image chartImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-chart-value.gif"));
        ImageCursor chartValueCursor = new ImageCursor(chartImage, chartImage.getWidth() / 2, chartImage.getHeight() / 2);
        cursorMap.put(EditMode.CHART_VALUE_CHANGE, chartValueCursor);

        Image dragImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-move.gif"));
        ImageCursor dragCursor = new ImageCursor(dragImage, dragImage.getWidth() / 2, dragImage.getHeight() / 2);
        cursorMap.put(EditMode.DRAGGING, dragCursor);

        Image dragHorizontalImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-move-horizontal.gif"));
        ImageCursor dragHorizontalCursor = new ImageCursor(dragHorizontalImage, dragHorizontalImage.getWidth() / 2, dragHorizontalImage.getHeight() / 2);
        cursorMap.put(EditMode.DRAGGING_HORIZONTAL, dragHorizontalCursor);

        Image dragVerticalImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-move-vertical.gif"));
        ImageCursor dragVerticalCursor = new ImageCursor(dragVerticalImage, dragVerticalImage.getWidth() / 2, dragVerticalImage.getHeight() / 2);
        cursorMap.put(EditMode.DRAGGING_VERTICAL, dragVerticalCursor);

        Image endTimeImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-end-time.gif"));
        ImageCursor endTimeCursor = new ImageCursor(endTimeImage, endTimeImage.getWidth() / 2, endTimeImage.getHeight() / 2);
        cursorMap.put(EditMode.END_TIME_CHANGE, endTimeCursor);

        Image startTimeImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-start-time.gif"));
        ImageCursor startTimeCursor = new ImageCursor(startTimeImage, startTimeImage.getWidth() / 2, startTimeImage.getHeight() / 2);
        cursorMap.put(EditMode.START_TIME_CHANGE, startTimeCursor);

        Image endTimeAgendaImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-end-time-agenda.gif"));
        ImageCursor endTimeAgendaCursor = new ImageCursor(endTimeAgendaImage, endTimeAgendaImage.getWidth() / 2, endTimeAgendaImage.getHeight() / 2);
        cursorMap.put(EditMode.AGENDA_END_TIME_CHANGE, endTimeAgendaCursor);

        Image startTimeAgendaImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-start-time-agenda.gif"));
        ImageCursor startTimeAgendaCursor = new ImageCursor(startTimeAgendaImage, startTimeAgendaImage.getWidth() / 2, startTimeAgendaImage.getHeight() / 2);
        cursorMap.put(EditMode.AGENDA_START_TIME_CHANGE, startTimeAgendaCursor);

        Image draggingAgendaImage = new Image(GraphicsBase.class.getResourceAsStream("cursor-move-vertical.gif"));
        ImageCursor draggingAgendaCursor = new ImageCursor(draggingAgendaImage, draggingAgendaImage.getWidth() / 2, draggingAgendaImage.getHeight() / 2);
        cursorMap.put(EditMode.AGENDA_DRAGGING, draggingAgendaCursor);
    }

    private static List<ActivityBounds> selectedBounds;

    public static void setCursor(EditMode editMode, Cursor cursor) {
        requireNonNull(editMode);
        requireNonNull(cursor);

        cursorMap.put(editMode, cursor);
    }

    private final RowCanvas<R> canvas;

    private static EditMode editMode = EditMode.NONE;

    private static ActivityBounds activityBounds;

    private static double editStartX;

    private static double editStartY;

    private static TimeInterval oldTimeInterval;

    private static double oldValue;

    private static Point2D offset;

    private MouseEvent lastMouseEvent;

    RowCanvasBehaviour(RowCanvas<R> canvas) {
        requireNonNull(canvas);

        this.canvas = canvas;

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::mouseMoved);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        canvas.addEventHandler(MouseEvent.DRAG_DETECTED, this::dragDetected);
        canvas.addEventHandler(DragEvent.DRAG_OVER, this::dragOver);
        canvas.addEventHandler(DragEvent.DRAG_EXITED, this::dragExited);
        canvas.addEventHandler(DragEvent.DRAG_DROPPED, this::dragDropped);
        canvas.addEventHandler(DragEvent.DRAG_DONE, this::dragDone);

        canvas.getGraphics().addEventFilter(KeyEvent.ANY, this::updateEditModeAfterKeyEvent);
    }

    private void draw() {
        canvas.requestRedraw("row canvas behaviour call");
    }

    /**
     * Changes the current edit mode to NONE and redraws the canvas.
     */
    public void stopEdit() {
        editMode = EditMode.NONE;
        draw();
    }

    private void updateEditModeAfterKeyEvent(KeyEvent evt) {
        if (activityBounds != null && lastMouseEvent != null
                && canvas.getRow() != null
                && activityBounds.getRow() == canvas.getRow()) {

            lastMouseEvent = new MouseEvent(lastMouseEvent.getSource(),
                    lastMouseEvent.getTarget(), lastMouseEvent.getEventType(),
                    lastMouseEvent.getX(), lastMouseEvent.getY(),
                    lastMouseEvent.getScreenX(), lastMouseEvent.getScreenY(),
                    lastMouseEvent.getButton(), lastMouseEvent.getClickCount(),
                    evt.isShiftDown(), evt.isControlDown(), evt.isAltDown(),
                    evt.isMetaDown(), lastMouseEvent.isPrimaryButtonDown(),
                    lastMouseEvent.isMiddleButtonDown(),
                    lastMouseEvent.isSecondaryButtonDown(),
                    lastMouseEvent.isSynthesized(),
                    lastMouseEvent.isPopupTrigger(),
                    lastMouseEvent.isStillSincePress(), null);

            updateEditMode();
        }
    }

    public static final DataFormat DRAG_INFO = new DataFormat("FlexGanttFX/dragInfo");

    public static final class DragInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        private final EditMode editMode;

        private final double xOffset;

        private final boolean shortcutDown;

        private final boolean shiftDown;

        private final boolean altDown;

        private DragInfo(EditMode editMode, double xOffset, boolean shortcutDown, boolean shiftDown, boolean altDown) {
            requireNonNull(editMode);
            this.editMode = editMode;
            this.xOffset = xOffset;
            this.shortcutDown = shortcutDown;
            this.shiftDown = shiftDown;
            this.altDown = altDown;
        }

        public EditMode getEditMode() {
            return editMode;
        }

        public double getOffset() {
            return xOffset;
        }

        public boolean isShortcutDown() {
            return shortcutDown;
        }

        public boolean isShiftDown() {
            return shiftDown;
        }

        public boolean isAltDown() {
            return altDown;
        }
    }

    private void dragDetected(MouseEvent event) {
        DND.fine("drag detected: " + event);

        switch (editMode) {
            case DRAGGING:
            case DRAGGING_VERTICAL:

                if (activityBounds != null) {

                    GraphicsBase<R> graphics = canvas.getGraphics();
                    Callback<ActivityRef<?>, Image> dragImageProvider = graphics.getDragImageProvider();

                    Dragboard dragBoard = canvas.startDragAndDrop(TransferMode.MOVE);

                    Image image;

                    switch (graphics.getDragAndDropFeedback()) {
                        case NATIVE:
                            if (dragImageProvider != null) {

                                image = dragImageProvider.call(activityBounds.getActivityRef());

                            } else {

                                WritableImage writableImage = new WritableImage((int) (activityBounds.getWidth()), (int) (activityBounds.getHeight()));
                                SnapshotParameters snapshotParameters = new SnapshotParameters();
                                snapshotParameters.setViewport(activityBounds);
                                snapshotParameters.setFill(TRANSPARENT);
                                image = canvas.snapshot(snapshotParameters, writableImage);
                            }

                            dragBoard.setDragView(image);
                            break;
                        case RENDERED:
                        case RENDERED_GRID_SNAPPED:
                            dragBoard.setDragView(new WritableImage(1, 1));
                            break;
                        default:
                            break;
                    }

                    ClipboardContent content = new ClipboardContent();
                    content.put(DRAG_INFO, new DragInfo(editMode, offset.getX(), event.isShortcutDown(), event.isShiftDown(), event.isAltDown()));
                    content.putString(activityBounds.getActivity().getName());
                    dragBoard.setContent(content);

                    selectedBounds = computeSelectedBoundsOnSameRow();

                    R row = canvas.getRow();

                    ActivityRef<?> activityRef = activityBounds.getActivityRef();

                    switch (editMode) {
                        case DRAGGING:
                            fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.DRAG_STARTED, row, row, oldTimeInterval));
                            break;
                        case DRAGGING_VERTICAL:
                            fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.VERTICAL_DRAG_STARTED, row, row, oldTimeInterval));
                            break;
                        default:
                            break;
                    }
                }

                break;
            default:
                break;
        }
    }

    private Point2D lastDragLocation;

    private boolean dragPreviouslyAccepted;

    private void dragOver(DragEvent evt) {
        if (DND.isLoggable(Level.FINE)) {
            DND.fine("drag over: " + evt);
        }

        Point2D dragLocation = new Point2D(getGraphicsX(evt), evt.getY());

        if (lastDragLocation != null && lastDragLocation.equals(dragLocation)) {
            if (DND.isLoggable(Level.FINE)) {
                DND.fine("returning early");
            }
            if (dragPreviouslyAccepted) {
                evt.acceptTransferModes(TransferMode.ANY);
            } else {
                evt.acceptTransferModes(TransferMode.NONE);
            }
            return;
        }

        lastDragLocation = dragLocation;

        Dragboard dragboard = evt.getDragboard();
        if (!dragboard.hasContent(DRAG_INFO)) {
            DND.fine("no drag info found, not handling drag over");
            return;
        }

        GraphicsBase<R> graphics = canvas.getGraphics();

        TimeInterval dragInterval = getDragInterval(evt);

        updateMarkedTimeInterval(dragInterval);

        R row = canvas.getRow();

        ActivityRef<?> activityRef = activityBounds.getActivityRef();

        graphics.getProperties().put("com.flexganttfx.hover.row", row);

        Layout layout = canvas.getLayoutAt(evt.getY());
        graphics.getProperties().put("com.flexganttfx.hover.layout", layout);

        DragAndDropInfo dragAndDropInfo = new DragAndDropInfo(row, activityBounds, selectedBounds, dragInterval, evt, offset);

        switch (graphics.getDragAndDropFeedback()) {
            case RENDERED:
            case RENDERED_GRID_SNAPPED:
                GraphicsBaseSkin<?, ?> skin = (GraphicsBaseSkin<?, ?>) graphics.getSkin();
                DragCanvas<?> dragCanvas = skin.getDragCanvas();
                dragCanvas.draw(dragAndDropInfo);
                break;
            case NATIVE:
                break;
        }

        graphics.getProperties().put(DRAGANDDROPINFO, dragAndDropInfo);

        /*
         * Callback determines if drop is possible.
         */

        Callback<DragAndDropInfo, Boolean> callback;
        if (row != null) {
            callback = graphics.getRowDragAndDropCallback(row.getClass());

            if (callback != null) {
                if (callback.call(dragAndDropInfo)) {
                    LoggingDomain.DND.fine("accepting transfer mode ANY");
                    dragPreviouslyAccepted = true;
                    evt.acceptTransferModes(TransferMode.ANY);
                } else {
                    LoggingDomain.DND.fine("not accepting any transfer mode");
                    dragPreviouslyAccepted = false;
                    evt.acceptTransferModes(TransferMode.NONE);
                }
            } else {
                LoggingDomain.DND.fine("accepting transfer mode ANY");
                dragPreviouslyAccepted = true;
                evt.acceptTransferModes(TransferMode.ANY);
            }

            DragInfo dragInfo = (DragInfo) dragboard.getContent(DRAG_INFO);
            EditMode dragEditMode = dragInfo.getEditMode();

            switch (dragEditMode) {
                case DRAGGING:
                    fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.DRAG_ONGOING, activityRef.getRow(), row, oldTimeInterval));
                    break;
                case DRAGGING_VERTICAL:
                    fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.VERTICAL_DRAG_ONGOING, activityRef.getRow(), row, oldTimeInterval));
                    break;
                default:
                    break;
            }

            evt.consume();
        }
    }

    /*
     * This method finds all activities on the same row (this row) that are currently selected and that
     * can also be edited with the current edit mode, e.g. a vertical drag.
     */
    private List<ActivityBounds> computeSelectedBoundsOnSameRow() {
        GraphicsBase<R> graphics = canvas.getGraphics();
        List<ActivityBounds> bounds = new ArrayList<>();
        Row<?, ?, ?> row = canvas.getRow();
        for (ActivityRef<?> activityRef : graphics.getSelectedActivities()) {
            if (!activityRef.equals(activityBounds.getActivity()) && activityRef.getRow().equals(row)) {
                Activity activity = activityRef.getActivity();
                if (activity instanceof MutableActivity) {
                    Callback<EditingCallbackParameter, Boolean> activityEditingCallback = graphics.getActivityEditingCallback(((MutableActivity) activity).getClass());
                    EditingCallbackParameter param = new EditingCallbackParameter(activityRef, editMode);
                    if (activityEditingCallback.call(param)) {
                        bounds.add(canvas.getActivityBounds(activityRef));
                    }
                }
            }
        }
        return bounds;
    }

    private TimeInterval getDragInterval(DragEvent evt) {
        Dragboard dragboard = evt.getDragboard();
        DragInfo dragInfo = (DragInfo) dragboard.getContent(DRAG_INFO);

        GraphicsBase<R> graphics = canvas.getGraphics();
        ActivityRef<?> activityRef = activityBounds.getActivityRef();
        MutableActivity activity = (MutableActivity) activityRef.getActivity();

        if (dragInfo.getEditMode().equals(EditMode.DRAGGING)) {

            Duration duration = Duration.between(activity.getStartTime(), activity.getEndTime());
            Instant newStartTime = calculateTimeForLocation(getGraphicsX(evt) - dragInfo.getOffset());
            Instant newEndTime = newStartTime.plus(duration);

            switch (graphics.getDragAndDropFeedback()) {
                case RENDERED_GRID_SNAPPED:
                    if (newStartTime.isBefore(activity.getStartTime())) {
                        // moving left
                        Instant newStartTime1 = GridHelper.grid(graphics, newStartTime, false);
                        Instant newStartTime2 = GridHelper.grid(graphics, newStartTime, true);

                        // durations can be negative, let's use abs
                        if (Math.abs(Duration.between(newStartTime, newStartTime1).toMillis()) < Math.abs(Duration.between(newStartTime, newStartTime2).toMillis())) {
                            newStartTime = newStartTime1;
                        } else {
                            newStartTime = newStartTime2;
                        }
                        newEndTime = newStartTime.plus(duration);
                    } else {
                        // moving right
                        Instant newEndTime1 = GridHelper.grid(graphics, newEndTime, false);
                        Instant newEndTime2 = GridHelper.grid(graphics, newEndTime, true);

                        // durations can be negative, let's use abs
                        if (Math.abs(Duration.between(newEndTime, newEndTime1).toMillis()) < Math.abs(Duration.between(newEndTime, newEndTime2).toMillis())) {
                            newEndTime = newEndTime1;
                        } else {
                            newEndTime = newEndTime2;
                        }
                        newStartTime = newEndTime.minus(duration);
                    }
                    break;
                case NATIVE:
                case RENDERED:
                    break;
            }

            if (DND.isLoggable(Level.FINER)) {
                DND.finer("drag interval start: " + newStartTime);
                DND.finer("drag interval end: " + newEndTime);
            }

            return new TimeInterval(newStartTime, newEndTime);
        }

        return new TimeInterval(activity.getStartTime(), activity.getEndTime());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void dragDropped(DragEvent evt) {
        DND.fine("drag dropped: " + evt);

        if (!evt.getDragboard().hasContent(DRAG_INFO)) {
            DND.fine("no drag info found, not handling the drop");
            return;
        }

        clearDragCanvas();

        updateMarkedTimeInterval(null);

        Dragboard dragboard = evt.getDragboard();
        DragInfo dragInfo = (DragInfo) dragboard.getContent(DRAG_INFO);

        EditMode dragEditMode = dragInfo.getEditMode();

        ActivityRef<?> oldActivityRef = activityBounds.getActivityRef();
        MutableActivity activity = (MutableActivity) oldActivityRef.getActivity();
        Row oldRow = oldActivityRef.getRow();

        TimeInterval dragInterval = getDragInterval(evt);

        /*
         * Add to the new row.
         */
        Row newRow = canvas.getRow();

        if (newRow == null) {
            // this is an empty row
            return;
        }

        GraphicsBase<?> graphics = canvas.getGraphics();
        Callback<DragAndDropInfo, Boolean> callback = graphics.getRowDragAndDropCallback(newRow.getClass());
        DragAndDropInfo dragAndDropInfo = new DragAndDropInfo(newRow, activityBounds, selectedBounds, dragInterval, evt, offset);

        if (callback.call(dragAndDropInfo)) {

            /*
             * Detach before updating the start and end time.
             */
            oldActivityRef.detachFromRow();

            if (dragEditMode == DRAGGING) {
                activity.setStartTime(dragInterval.getStartTime());
                activity.setEndTime(dragInterval.getEndTime());
            }

            Callback<DragAndDropInfo, Layer> layerProvider = graphics.getDropLayerProvider();

            Layer newLayer = layerProvider.call(dragAndDropInfo);

            if (newLayer == null) {
                throw new IllegalArgumentException("the drop layer provider has returned no layer for the dropped activity");
            }

            if (!graphics.getLayers().contains(newLayer)) {
                throw new IllegalArgumentException("the drop layer provider has returned a layer that does not exist in the Gantt chart");
            }

            newRow.addActivity(newLayer, activity);

            ActivityRef<?> newActivityRef = new ActivityRef<Activity>(newRow, newLayer, activity);

            /*
             * Fix the activity links.
             */
            fixLinks(oldActivityRef, newActivityRef);

            /*
             * Fire event.
             */
            switch (dragEditMode) {
                case DRAGGING:
                    fireEvent(new ActivityEvent(oldActivityRef, canvas, ActivityEvent.DRAG_FINISHED, oldRow, newRow, oldTimeInterval));
                    break;
                case DRAGGING_VERTICAL:
                    fireEvent(new ActivityEvent(oldActivityRef, canvas, ActivityEvent.VERTICAL_DRAG_FINISHED, oldRow, newRow, oldTimeInterval));
                    break;
                default:
                    break;
            }
        }

        draw();

        lastDragLocation = null;
        dragPreviouslyAccepted = false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void fixLinks(ActivityRef<?> oldActivityRef, ActivityRef<?> newActivityRef) {

        for (ActivityLink link : canvas.getGraphics().getLinks().getIntersectingObjects(0, Long.MAX_VALUE)) {
            if (link.getSourceActivityRef().equals(oldActivityRef)) {
                link.setSourceActivityRef(newActivityRef);
            }

            if (link.getTargetActivityRef().equals(oldActivityRef)) {
                link.setTargetActivityRef(newActivityRef);
            }
        }
    }

    private void dragExited(DragEvent evt) {
        DND.fine("drag exited: " + evt);
        canvas.getGraphics().getProperties().remove(DRAGANDDROPINFO);
        clearDragCanvas();
    }

    private void dragDone(DragEvent evt) {
        DND.fine("drag done: " + evt);
        selectedBounds.clear();

        stopAutoScrollIfNeeded();

        GraphicsBase<R> graphics = canvas.getGraphics();

        graphics.getProperties().put(DRAGANDDROPINFO, null);

        clearDragCanvas();

        clearCurrentlyEditedActivity();
        draw();

        Dragboard dragboard = evt.getDragboard();
        DragInfo dragInfo = (DragInfo) dragboard.getContent(DRAG_INFO);

        EditMode dragEditMode = dragInfo.getEditMode();

        /*
         * Fire event.
         */
        switch (dragEditMode) {
            case DRAGGING:
                fireEvent(new ActivityEvent(activityBounds.getActivityRef(), canvas, ActivityEvent.DRAG_DONE));
                break;
            case DRAGGING_VERTICAL:
                fireEvent(new ActivityEvent(activityBounds.getActivityRef(), canvas, ActivityEvent.VERTICAL_DRAG_DONE));
                break;
            default:
                break;
        }

        lastDragLocation = null;
        dragPreviouslyAccepted = false;
        activityBounds = null;
    }

    private void clearDragCanvas() {
        GraphicsBaseSkin<?, ?> skin = (GraphicsBaseSkin<?, ?>) canvas.getGraphics().getSkin();
        DragCanvas<?> dragCanvas = skin.getDragCanvas();
        dragCanvas.draw(null);
    }

    private void handleSelection(MouseEvent event) {
        EDITING.fine(event.toString());
        EDITING.fine("consumed: " + event.isConsumed());
        EDITING.fine("edit mode: " + editMode);
        EDITING.fine("popup trigger: " + event.isPopupTrigger());

        if (!event.isConsumed()) {
            GraphicsBase<R> graphics = canvas.getGraphics();
            ActivityBounds bounds = canvas.getActivityBounds(event.getX(), event.getY());

            ObservableList<ActivityRef<?>> selectedActivities = graphics.getSelectedActivities();

            if (bounds != null) {
                ActivityRef<?> activityRef = bounds.getActivityRef();

                if (event.isShiftDown() || event.isShortcutDown()) {
                    EDITING.fine("selecting with SHIFT or CTRL/META down -> multi selection");
                    if (selectedActivities.contains(activityRef)) {
                        EDITING.fine("activity already in selection, removing it now");
                        selectedActivities.remove(activityRef);
                    } else {
                        EDITING.fine("adding activity to list of selected activities");
                        if (graphics.getSelectionMode() == SelectionMode.SINGLE) {
                            selectedActivities.setAll(activityRef);
                        } else {
                            selectedActivities.add(activityRef);
                        }
                    }
                } else {
                    EDITING.fine("selecting without SHIFT or CTRL/META down -> single selection");
                    if (!selectedActivities.contains(activityRef)) {
                        selectedActivities.setAll(activityRef);
                    }
                }
            } else if (!(event.isPopupTrigger() || event.isShiftDown() || event.isShortcutDown())) {
                EDITING.fine("no bounds found, clearing current selection");
                selectedActivities.clear();
            }
        }
    }

    private void mouseReleased(MouseEvent event) {
        EDITING.finest("mouse released: " + event);

        if (!mouseWasPressed) {
            return;
        }

        stopAutoScrollIfNeeded();

        GraphicsBase<R> graphics = canvas.getGraphics();
        graphics.getProperties().put("com.flexganttfx.pressed.activity", null);

        if (editMode != EditMode.NONE && activityBounds != null && event.getButton().equals(PRIMARY)) {
            clearCurrentlyEditedActivity();

            draw();

            fireActivityChangeFinished();
        }

        updateMarkedTimeInterval(null);

        mouseWasPressed = false;
        activityBounds = null;
    }

    class ScrollThread extends Thread {
        private boolean running = true;
        private double xOffset;
        private double yOffset;
        private MouseEvent evt;

        public ScrollThread() {
            super("Autoscrolling Row Canvas");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (running) {

                Platform.runLater(() -> {
                    scrollX();
                    scrollY();
                });

                try {
                    sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void scrollX() {
            TimelineModel<?> model = canvas.getTimelineModel();
            Instant targetTime = calculateTimeForLocation(xOffset);
            Instant oldStartTime = model.getStartTime();
            model.setStartTime(targetTime);
            Instant newStartTime = model.getStartTime();

            // Important check, otherwise the activity will get lost.
            // See FLEXFX-277: "Activity "lost" if dragged to a timeline horizon boundary"
            if (!oldStartTime.equals(newStartTime)) {
                editStartX -= xOffset;
                doMouseDragged(evt);
            }
        }

        private void scrollY() {
            VirtualFlow<?> flow = getFlow();
            if (flow != null) {
                /*
                 * Flow only exists when using ListView.
                 */
                flow.adjustPixels(yOffset);
                doMouseDragged(evt);
            }
        }

        private VirtualFlow<?> getFlow() {
            return (VirtualFlow<?>) canvas.getGraphics().lookup("VirtualFlow");
        }

        public void stopRunning() {
            this.running = false;
        }

        public void setDelta(double xOffset, double yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        public void setMouseEvent(MouseEvent event) {
            this.evt = event;
        }
    }

    private ScrollThread scrollThread;

    private void autoscroll(double xOffset, double yOffset, MouseEvent event) {
        if (scrollThread == null) {
            scrollThread = new ScrollThread();
            scrollThread.start();
        }

        scrollThread.setMouseEvent(event);
        scrollThread.setDelta(xOffset, yOffset);
    }

    private void stopAutoScrollIfNeeded() {
        if (scrollThread != null) {
            scrollThread.stopRunning();
            scrollThread = null;
        }
    }

    private void startAutoscrollIfNeeded(MouseEvent evt) {

        final GraphicsBase<?> graphics = canvas.getGraphics();

        double x = getGraphicsX(evt);

        double sceneY = evt.getSceneY();

        double xOffset = 0;

        if (x < 0) {
            xOffset = x;
        } else {
            final double visibleCanvasSize = graphics.getWidth() - getRowHeadersWidth();
            if (x > visibleCanvasSize) {
                xOffset = x - visibleCanvasSize;
            }
        }

        double yOffset = 0;

        /*
         * We only perform autoscroll in y direction if the graphics view is
         * using a list view.
         */
        if (graphics instanceof ListViewGraphics) {
            if (sceneY > graphics.localToScene(0, 0).getY() + graphics.getHeight()) {
                yOffset = sceneY - (graphics.localToScene(0, 0).getY() + canvas.getGraphics().getHeight());
            } else if (sceneY < graphics.localToScene(0, 0).getY()) {
                yOffset = sceneY - graphics.localToScene(0, 0).getY();
            }
        }

        if (xOffset == 0 && yOffset == 0) {
            stopAutoScrollIfNeeded();
            doMouseDragged(evt);
        } else {
            autoscroll(xOffset, yOffset, evt);
        }
    }

    private double getRowHeadersWidth() {
        return canvas.getGraphics().isShowRowHeaders() ? canvas.getGraphics().getRowHeadersWidth() : 0;
    }

    private double getGraphicsX(MouseEvent evt) {
        return getGraphicsX(evt.getSceneX());
    }

    private double getGraphicsX(DragEvent evt) {
        return getGraphicsX(evt.getSceneX());
    }

    private double getGraphicsX(double evtX) {
        final GraphicsBase<?> graphics = canvas.getGraphics();
        double x = graphics.sceneToLocal(new Point2D(evtX, 0)).getX();
        return x - getRowHeadersWidth();
    }

    private void mouseDragged(MouseEvent event) {
        if (!mouseWasPressed) {
            return;
        }

        if (EDITING.isLoggable(Level.FINEST)) {
            EDITING.finest("mouse dragged: " + event);
        }

        switch (editMode) {
            case NONE:
            case DRAGGING:
            case DRAGGING_VERTICAL:
                return;
            default:
                startAutoscrollIfNeeded(event);
                break;
        }
    }

    private void doMouseDragged(MouseEvent event) {
        if (editMode != EditMode.NONE && activityBounds != null && event.getButton().equals(PRIMARY)) {

            if (EDITING.isLoggable(Level.FINER)) {
                EDITING.finer("dragging with editing operation " + editMode);
            }

            ActivityRef<?> activityRef = activityBounds.getActivityRef();
            activityRef.detachFromRow();

            switch (editMode) {
                case NONE:
                    // Nothing to do here
                    break;
                case DRAGGING:
                case DRAGGING_VERTICAL:
                    /*
                     * These notifications will be handled by the drag and drop
                     * support.
                     */
                    break;
                case DRAGGING_HORIZONTAL:
                    changeStartAndEndTime(event);
                    activityRef.attachToRow();
                    break;
                case CHART_VALUE_CHANGE:
                    changeChartValue(event);
                    activityRef.attachToRow();
                    break;
                case CHART_VALUE_HIGH_CHANGE:
                    // TODO implement this?
                    //changeChartHighValue(event);
                    activityRef.attachToRow();
                    break;
                case CHART_VALUE_LOW_CHANGE:
                    // TODO implement this?
                    //changeChartLowValue(event);
                    activityRef.attachToRow();
                    break;
                case START_TIME_CHANGE:
                    changeStartTime(event);
                    activityRef.attachToRow();
                    break;
                case END_TIME_CHANGE:
                    changeEndTime(event);
                    activityRef.attachToRow();
                    break;
                case PERCENTAGE_COMPLETE_CHANGE:
                    changePercentageComplete(event);
                    activityRef.attachToRow();
                    break;
                case AGENDA_ASSIGNING:
                    // TODO: implement this?
                    activityRef.attachToRow();
                    break;
                case AGENDA_DRAGGING:
                    changeStartAndEndTimeAgenda(event);
                    activityRef.attachToRow();
                    break;
                case AGENDA_END_TIME_CHANGE:
                    changeEndTimeAgenda(event);
                    activityRef.attachToRow();
                    break;
                case AGENDA_START_TIME_CHANGE:
                    changeStartTimeAgenda(event);
                    activityRef.attachToRow();
                    break;
                default:
                    break;
            }

            fireActivityChangeOngoing();
        }
    }

    private void fireEvent(ActivityEvent event) {
        if (LoggingDomain.EVENTS.isLoggable(Level.FINE)) {
            LoggingDomain.EVENTS.fine("firing event: " + event);
        }

        GraphicsBase<R> graphics = canvas.getGraphics();
        graphics.fireEvent(event);
    }

    private void changeStartTime(MouseEvent event) {
        Instant time = GridHelper.grid(canvas.getGraphics(), calculateTimeForLocation(event));
        MutableActivity activity = (MutableActivity) activityBounds.getActivity();
        if (time.isAfter(activity.getEndTime())) {
            activity.setStartTime(activity.getEndTime());
        } else {
            activity.setStartTime(time);
        }

        updateMarkedTimeInterval(new TimeInterval(activity.getStartTime(), activity.getEndTime()));

        draw();
    }

    private Instant calculateTimeForLocation(MouseEvent event) {
        return calculateTimeForLocation(getGraphicsX(event));
    }

    private Instant calculateTimeForLocation(double x) {
        final TimelineModel<?> timelineModel = canvas.getTimelineModel();
        return timelineModel.calculateTimeForLocation(x + timelineModel.getOffset());
    }

    private void changeEndTime(MouseEvent event) {
        Instant time = GridHelper.grid(canvas.getGraphics(), calculateTimeForLocation(event));
        MutableActivity activity = (MutableActivity) activityBounds.getActivity();
        if (time.isBefore(activity.getStartTime())) {
            activity.setEndTime(activity.getStartTime());
        } else {
            activity.setEndTime(time);
        }

        updateMarkedTimeInterval(new TimeInterval(activity.getStartTime(), activity.getEndTime()));

        draw();
    }

    private void changeStartAndEndTime(MouseEvent event) {
        MutableActivity activity = (MutableActivity) activityBounds.getActivity();

        Instant timeA = GridHelper.grid(canvas.getGraphics(), calculateTimeForLocation(editStartX));
        Instant timeB = GridHelper.grid(canvas.getGraphics(), calculateTimeForLocation(event));

        if (!timeA.equals(timeB)) {
            Duration deltaTime = Duration.between(timeA, timeB);
            activity.setStartTime(activity.getStartTime().plus(deltaTime));
            activity.setEndTime(activity.getEndTime().plus(deltaTime));
            editStartX = getGraphicsX(event);
        }

        updateMarkedTimeInterval(new TimeInterval(activity.getStartTime(), activity.getEndTime()));

        draw();
    }

    private void updateMarkedTimeInterval(TimeInterval markedTimeInterval) {
        if (canvas.getGraphics().isAutoMarkedTimeInterval()) {
            Eventline eventline = getEventline();
            if (activityBounds != null && activityBounds.getLayout() instanceof AgendaLayout) {
                eventline.setMarkedTimeInterval(null);
            } else {
                eventline.setMarkedTimeInterval(markedTimeInterval);
            }
        }
    }

    private void changePercentageComplete(MouseEvent event) {
        double deltaX = Math.max(0, event.getX() - activityBounds.getMinX());
        double percent = Math.min(100, deltaX / activityBounds.getWidth() * 100);
        MutableCompletableActivity completable = (MutableCompletableActivity) activityBounds.getActivity();
        completable.setPercentageComplete(percent);
        draw();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void changeChartValue(MouseEvent event) {
        Row row = canvas.getRow();
        Layout layout = row.getLayout();
        double height = row.getHeight();
        double y = 0;

        MutableChartActivity chartActivity = (MutableChartActivity) activityBounds
                .getActivity();

        int lineIndex = row.getLineIndex(chartActivity);
        if (lineIndex > -1) {
            try {
                layout = row.getLineLayout(lineIndex);
                height = row.getLineHeight(lineIndex);
                y = row.getLineLocation(lineIndex);
            } catch (IllegalLineIndexException e) {
                e.printStackTrace();
            }
        }

        EDITING.fine("row / line location = " + y);
        EDITING.fine("row / line height = " + height);

        ChartLayout chartLayout = (ChartLayout) layout;

        double padding = chartLayout.getPadding();
        double minChartValue = chartLayout.getMinValue();
        double maxChartValue = chartLayout.getMaxValue();

        double range = maxChartValue - minChartValue;

        double usedHeight = height - 2 * padding;

        double pixelValue = range / usedHeight;

        EDITING.fine("min chart value = " + minChartValue);
        EDITING.fine("max chart value = " + maxChartValue);

        double localY = Math.min(padding + usedHeight, Math.max(padding, event.getY() - y));
        EDITING.fine("local y = " + localY);

        double chartValueAtLocalY = (localY - padding) * pixelValue;
        chartValueAtLocalY = chartLayout.getMaxValue() - chartValueAtLocalY;

        double chartValue = Math.max(minChartValue, Math.min(maxChartValue, chartValueAtLocalY));

        EDITING.fine("chart value = " + chartValue);

        chartActivity.setChartValue(chartValue);
        draw();
    }

    private void changeStartAndEndTimeAgenda(MouseEvent event) {
        ZoneId rowZoneId = canvas.getRow().getZoneId();
        TimelineModel<?> timelineModel = canvas.getTimelineModel();

        AgendaLayout layout = (AgendaLayout) activityBounds.getLayout();

        LocalTime oldLocalTime = timeAt(layout, editStartY);
        LocalTime newLocalTime = timeAt(layout, event.getY());

        Instant oldTime = calculateTimeForLocation(editStartX);
        Instant newTime = calculateTimeForLocation(event);

        LocalDate oldLocalDate = ZonedDateTime.ofInstant(oldTime, rowZoneId).toLocalDate();
        LocalDate newLocalDate = ZonedDateTime.ofInstant(newTime, rowZoneId).toLocalDate();

        ZonedDateTime oldZonedDateTime = ZonedDateTime.of(oldLocalDate, oldLocalTime, rowZoneId);
        ZonedDateTime newZonedDateTime = ZonedDateTime.of(newLocalDate, newLocalTime, rowZoneId);

        Duration moveDuration = Duration.between(oldZonedDateTime, newZonedDateTime);

        MutableActivity activity = (MutableActivity) activityBounds.getActivity();
        Duration activityDuration = Duration.between(activity.getStartTime(), activity.getEndTime());

        Instant oldStartTime = activity.getStartTime();
        Instant newStartTime = GridHelper.grid(canvas.getGraphics(), oldStartTime.plus(moveDuration), editStartY > event.getY());
        Instant newEndTime = newStartTime.plus(activityDuration);

        if (!newStartTime.equals(oldStartTime)) {
            activity.setStartTime(newStartTime);
            activity.setEndTime(newEndTime);

            draw();

            editStartY = event.getY();
            editStartX = getGraphicsX(event);
        }
    }

    private void changeStartTimeAgenda(MouseEvent event) {
        ZoneId rowZoneId = canvas.getRow().getZoneId();

        AgendaLayout layout = (AgendaLayout) activityBounds.getLayout();
        LocalTime timeAt = timeAt(layout, event.getY());

        ZonedDateTime dateAndTimeAt = ZonedDateTime.ofInstant(calculateTimeForLocation(event), rowZoneId);

        LocalDate dateAt = dateAndTimeAt.toLocalDate();

        MutableActivity activity = (MutableActivity) activityBounds.getActivity();
        Instant startTime = activity.getStartTime();
        ZonedDateTime localDateTime = ZonedDateTime.ofInstant(startTime, canvas.getRow().getZoneId()).with(timeAt).with(dateAt);

        Instant time = Instant.from(localDateTime);

        if (!java.time.Duration.between(time, activity.getEndTime()).minus(layout.getMinDuration()).isNegative()) {
            activity.setStartTime(GridHelper.grid(canvas.getGraphics(), time));
        } else {
            activity.setStartTime(GridHelper.grid(canvas.getGraphics(), activity.getEndTime().minus(layout.getMinDuration())));
        }

        draw();
    }

    private void changeEndTimeAgenda(MouseEvent event) {
        ZoneId rowZoneId = canvas.getRow().getZoneId();

        AgendaLayout layout = (AgendaLayout) activityBounds.getLayout();
        LocalTime timeAt = timeAt(layout, event.getY());

        ZonedDateTime dateAndTimeAt = ZonedDateTime.ofInstant(calculateTimeForLocation(event), rowZoneId);

        LocalDate dateAt = dateAndTimeAt.toLocalDate();

        MutableActivity activity = (MutableActivity) activityBounds.getActivity();
        Instant endTime = activity.getEndTime();
        ZonedDateTime localDateTime = ZonedDateTime.ofInstant(endTime, canvas.getRow().getZoneId()).with(timeAt).with(dateAt);

        Instant time = Instant.from(localDateTime);

        if (!java.time.Duration.between(activity.getStartTime(), time).minus(layout.getMinDuration()).isNegative()) {
            activity.setEndTime(GridHelper.grid(canvas.getGraphics(), time));
        } else {
            activity.setEndTime(GridHelper.grid(canvas.getGraphics(), activity.getStartTime().plus(layout.getMinDuration())));
        }

        draw();
    }

    private LocalTime timeAt(AgendaLayout layout, double y) {
        double yOffset = 0;
        double height = canvas.getHeight();

        int lineIndex = activityBounds.getLineIndex();
        if (lineIndex >= 0) {
            Row<?, ?, ?> row = canvas.getRow();

            yOffset = row.getLineLocation(lineIndex);
            height = row.getLineHeight(lineIndex);
        }

        height -= 2 * layout.getPadding();

        return AgendaHelper.getTimeAt(
                Math.min(height,
                        Math.max(0, y - yOffset - layout.getPadding())),
                height, layout.getStartTime(), layout.getEndTime());
    }

    /*
     * Any editing may only happen if the mouse was initially pressed. Otherwise
     * ignore mouse drag and mouse release events. Unfortunately this is needed
     * as part of a work-around (see issue #FLEXFX-179): when a popup window is
     * open somewhere (e.g. a context menu) then the row canvas will not receive
     * a mouse pressed event.
     */
    private boolean mouseWasPressed;

    private void mousePressed(MouseEvent event) {
        EDITING.fine(event.toString());
        EDITING.fine("editing bounds: " + activityBounds);
        EDITING.fine("editing operation: " + editMode);

        mouseWasPressed = true;

        handleSelection(event);

        editStartX = getGraphicsX(event);
        editStartY = event.getY();

        EDITING.finest("editing start x: " + editStartX);
        EDITING.finest("editing start y: " + editStartY);

        /*
         * We have to find the bounds again, not just in the mouse moved
         * handler, but also here, in the mouse pressed handler. Because after a
         * drop the user might start a new edit right away without first moving
         * the mouse.
         */
        activityBounds = canvas.getActivityBounds(event.getX(), event.getY());

        if (activityBounds != null) {

            offset = new Point2D(event.getX() - activityBounds.getMinX(), event.getY() - activityBounds.getMinY());

            GraphicsBase<R> graphics = canvas.getGraphics();
            graphics.getProperties().put("com.flexganttfx.pressed.activity", activityBounds.getActivityRef());

            /*
             * A little trick to make the graphics view update a read-only property.
             */
            graphics.getProperties().put(CURRENTEDITMODE, editMode);

            if (!editMode.equals(EditMode.NONE) && event.getButton().equals(PRIMARY)) {

                oldTimeInterval = new TimeInterval(activityBounds.getActivity().getStartTime(), activityBounds.getActivity().getEndTime());

                if (editMode.equals(EditMode.PERCENTAGE_COMPLETE_CHANGE)) {
                    oldValue = ((CompletableActivity) activityBounds.getActivity()).getPercentageComplete();
                } else if (editMode.equals(EditMode.CHART_VALUE_CHANGE)) {
                    oldValue = ((ChartActivity) activityBounds.getActivity()).getChartValue();
                } else {
                    oldValue = -1;
                }

                updateCurrentlyEditedActivity();

                fireActivityChangeStarted();
            }
        }
    }

    private void updateCurrentlyEditedActivity() {
        if (activityBounds != null) {
            GraphicsBase<R> graphics = canvas.getGraphics();
            graphics.getProperties().put(CURRENTLYEDITEDACTIVITY, activityBounds.getActivityRef());
        }
    }

    private void clearCurrentlyEditedActivity() {
        /*
         * Passing anything else but an activity ref will clear the currently
         * edited activity property.
         */
        canvas.getGraphics().getProperties().put(CURRENTLYEDITEDACTIVITY, "");
    }

    private Eventline getEventline() {
        GraphicsBase<R> graphics = canvas.getGraphics();
        Timeline timeline = graphics.getTimeline();
        return timeline.getEventline();
    }

    private void mouseMoved(MouseEvent event) {
        if (EDITING.isLoggable(FINEST)) {
            EDITING.finest("mouse moved: " + event);
        }

        lastMouseEvent = event;

        GraphicsBase<R> graphics = canvas.getGraphics();

        graphics.getProperties().put("com.flexganttfx.hover.row", canvas.getRow());

        Layout layout = canvas.getLayoutAt(event.getY());
        graphics.getProperties().put("com.flexganttfx.hover.layout", layout);

        activityBounds = canvas.getActivityBounds(event.getX(), event.getY());

        if (activityBounds != null) {
            graphics.getProperties().put("com.flexganttfx.hover.activity", activityBounds.getActivityRef());

            updateEditMode();

            if (EDITING.isLoggable(FINER)) {
                EDITING.finer("found bounds: " + activityBounds);
                EDITING.finer("edit mode: " + editMode);
            }

        } else {
            editMode = NONE;
            graphics.getProperties().put("com.flexganttfx.hover.activity", null);
            graphics.getProperties().put(CURRENTEDITMODE, editMode);
            graphics.setCursor(Cursor.DEFAULT);
        }
    }

    private void updateEditMode() {
        if (activityBounds != null && lastMouseEvent != null) {
            GraphicsBase<R> graphics = canvas.getGraphics();
            editMode = lookupEditMode(activityBounds, lastMouseEvent);
            graphics.getProperties().put(CURRENTEDITMODE, editMode);
            graphics.setCursor(cursorMap.get(editMode));
        }
    }

    private EditMode lookupEditMode(ActivityBounds bounds, MouseEvent event) {
        Activity activity = bounds.getActivity();

        if (activity instanceof MutableActivity) {
            MutableActivity mutable = (MutableActivity) activity;

            GraphicsBase<R> graphics = canvas.getGraphics();

            Layout layout = bounds.getLayout();

            Callback<EditModeCallbackParameter, EditMode> editModeCallback = graphics.getEditModeCallback(mutable.getClass(), layout.getClass());

            if (editModeCallback == null) {
                throw new IllegalArgumentException("no edit mode controller found for activity of type " + activity.getClass());
            }

            if (EDITING.isLoggable(Level.FINER)) {
                EDITING.finer("using edit mode controller of type " + editModeCallback.getClass());
            }

            EditModeCallbackParameter parameter = new EditModeCallbackParameter(bounds, event);
            EditMode editMode = editModeCallback.call(parameter);
            EditingCallbackParameter editingParameter = new EditingCallbackParameter(bounds.getActivityRef(), editMode);
            Callback<EditingCallbackParameter, Boolean> editingCallback = graphics.getActivityEditingCallback(mutable.getClass());

            if (editingCallback != null) {
                if (editingCallback.call(editingParameter)) {
                    if (EDITING.isLoggable(Level.FINER)) {
                        EDITING.finer("using activity edit policy of type "
                                + editingCallback.getClass());
                    }
                    return editMode;
                }
            } else {
                if (EDITING.isLoggable(Level.FINER)) {
                    EDITING.finer(
                            "no editing policy found for activity of type "
                                    + mutable.getClass().getName());
                }
            }
        }

        return EditMode.NONE;
    }

    private void fireActivityChangeStarted() {
        if (!mouseWasPressed) {
            return;
        }

        ActivityRef<?> activityRef = activityBounds.getActivityRef();

        switch (editMode) {
            case NONE:
                // Nothing to do here
                break;
            case DRAGGING:
            case DRAGGING_VERTICAL:
                /*
                 * These notifications will be handled by the drag and drop support.
                 */
                break;
            case DRAGGING_HORIZONTAL:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.HORIZONTAL_DRAG_STARTED, oldTimeInterval));
                break;
            case CHART_VALUE_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_VALUE_CHANGE_STARTED, oldValue));
                break;
            case CHART_VALUE_HIGH_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_HIGH_VALUE_CHANGE_STARTED, oldValue));
                break;
            case CHART_VALUE_LOW_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_LOW_VALUE_CHANGE_STARTED, oldValue));
                break;
            case START_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.START_TIME_CHANGE_STARTED, oldTimeInterval.getStartTime()));
                break;
            case END_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.END_TIME_CHANGE_STARTED, oldTimeInterval.getEndTime()));
                break;
            case PERCENTAGE_COMPLETE_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.PERCENTAGE_CHANGE_STARTED, oldValue));
                break;
            case AGENDA_ASSIGNING:
                // TODO: implement
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.DRAG_STARTED, activityBounds.getRow(), activityBounds.getRow(), oldTimeInterval));
                break;
            case AGENDA_DRAGGING:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.HORIZONTAL_DRAG_STARTED, oldTimeInterval));
                break;
            case AGENDA_END_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.END_TIME_CHANGE_STARTED, oldTimeInterval.getEndTime()));
                break;
            case AGENDA_START_TIME_CHANGE:
                activityRef.attachToRow();
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.START_TIME_CHANGE_STARTED, oldTimeInterval.getStartTime()));
                break;
            default:
                break;
        }
    }

    private void fireActivityChangeFinished() {

        if (!mouseWasPressed) {
            return;
        }

        ActivityRef<?> activityRef = activityBounds.getActivityRef();

        switch (editMode) {
            case NONE:
                // Nothing to do here.
                break;
            case DRAGGING:
            case DRAGGING_VERTICAL:
                /*
                 * These notifications will be handled by the drag and drop support.
                 */
                break;
            case DRAGGING_HORIZONTAL:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.HORIZONTAL_DRAG_FINISHED, oldTimeInterval));
                break;
            case CHART_VALUE_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_VALUE_CHANGE_FINISHED, oldValue));
                break;
            case CHART_VALUE_HIGH_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_HIGH_VALUE_CHANGE_FINISHED, oldValue));
                break;
            case CHART_VALUE_LOW_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_LOW_VALUE_CHANGE_FINISHED, oldValue));
                break;
            case START_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.START_TIME_CHANGE_FINISHED, oldTimeInterval.getStartTime()));
                break;
            case END_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.END_TIME_CHANGE_FINISHED, oldTimeInterval.getEndTime()));
                break;
            case PERCENTAGE_COMPLETE_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.PERCENTAGE_CHANGE_FINISHED, oldValue));
                break;
            case AGENDA_ASSIGNING:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.DRAG_FINISHED, activityBounds.getRow(), activityBounds.getRow(), oldTimeInterval));
                break;
            case AGENDA_DRAGGING:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.HORIZONTAL_DRAG_FINISHED, oldTimeInterval));
                break;
            case AGENDA_END_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.END_TIME_CHANGE_FINISHED, oldTimeInterval.getEndTime()));
                break;
            case AGENDA_START_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.START_TIME_CHANGE_FINISHED, oldTimeInterval.getStartTime()));
                break;
            default:
                break;
        }
    }

    private void fireActivityChangeOngoing() {
        if (!mouseWasPressed) {
            return;
        }

        ActivityRef<?> activityRef = activityBounds.getActivityRef();

        switch (editMode) {
            case NONE:
                // Nothing to do here
                break;
            case DRAGGING:
            case DRAGGING_VERTICAL:
                /*
                 * These notifications will be handled by the drag and drop support.
                 */
                break;
            case DRAGGING_HORIZONTAL:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.HORIZONTAL_DRAG_ONGOING, oldTimeInterval));
                break;
            case CHART_VALUE_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_VALUE_CHANGE_ONGOING, oldValue));
                break;
            case CHART_VALUE_HIGH_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_HIGH_VALUE_CHANGE_ONGOING, oldValue));
                break;
            case CHART_VALUE_LOW_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_LOW_VALUE_CHANGE_ONGOING, oldValue));
                break;
            case START_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.START_TIME_CHANGE_ONGOING, oldTimeInterval.getStartTime()));
                break;
            case END_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.END_TIME_CHANGE_ONGOING, oldTimeInterval.getEndTime()));
                break;
            case PERCENTAGE_COMPLETE_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.PERCENTAGE_CHANGE_ONGOING, oldValue));
                break;
            case AGENDA_ASSIGNING:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.DRAG_ONGOING, activityBounds.getRow(), activityBounds.getRow(), oldTimeInterval));
                break;
            case AGENDA_DRAGGING:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.HORIZONTAL_DRAG_ONGOING, oldTimeInterval));
                break;
            case AGENDA_END_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.END_TIME_CHANGE_ONGOING, oldTimeInterval.getEndTime()));
                break;
            case AGENDA_START_TIME_CHANGE:
                fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.START_TIME_CHANGE_ONGOING, oldTimeInterval.getStartTime()));
                break;
            default:
                break;
        }
    }
}
