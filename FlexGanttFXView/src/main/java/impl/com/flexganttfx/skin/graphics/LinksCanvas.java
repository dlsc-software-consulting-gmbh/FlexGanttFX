/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.util.IntervalTree;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.LinkRenderer;
import com.flexganttfx.view.timeline.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.time.Instant;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Level;

public class LinksCanvas<R extends Row<?, ?, ?>> extends Canvas {

    private GraphicsBase<R> graphics;

    public LinksCanvas(GraphicsBase<R> graphics) {
        this.graphics = graphics;
        this.linkRenderer = new LinkRenderer(graphics, "Default Link Renderer");

        /*
         * Don't show links when a row editor is in use.
         */
        visibleProperty().bind(Bindings.isEmpty(graphics.getRowsEditing()));

        setMouseTransparent(true);

        graphics.addEventFilter(ActivityEvent.ACTIVITY_CHANGE, event -> draw("an activity changed"));
        graphics.getRows().addListener(weakRowListChangedListener);

        connectToTimeline();
    }

    private void connectToTimeline() {
        Timeline timeline = graphics.getTimeline();

        final ChangeListener<Instant> startTimeListener = (obs, oldTime, newTime) -> {
//            double x = timeline.getModel().calculateLocationForTime(oldTime);
//
//            double newTranslateX = getTranslateX() + x;
//
//            if (Math.abs(newTranslateX) < graphics.getCanvasBuffer()) {
//                setTranslateX(newTranslateX);
//
//                Instant st = graphics.getTimeAt(0);
//                Instant et = graphics.getTimeAt(graphics.getWidth());
//
//                boolean contained = (st.equals(drawingStartTime) || st.isAfter(drawingStartTime)) && (et.equals(drawingEndTime) || et.isBefore(drawingEndTime));
//
//                if (!contained) {
//                    draw("start time changed");
//                }
//            } else {
//                randomTranslateX((newTranslateX - getTranslateX()) < 0);
            Platform.runLater(() -> {
                draw("start time changed");
            });
//            }
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

    @Override
    public boolean isResizable() {
        return true;
    }

    private final ListChangeListener<Row<?, ?, ?>> rowListChangedListener = (Change<? extends Row<?, ?, ?>> c) -> graphics.redraw();

    private final WeakListChangeListener<Row<?, ?, ?>> weakRowListChangedListener = new WeakListChangeListener<>(rowListChangedListener);

    private final LinkRenderer linkRenderer;

    private int counterTotal = 0;
    private int counterDrawn = 0;
    private int counterAbove = 0;
    private int counterBelow = 0;

    public void draw(String reason) {
        System.out.println("redrawing links because " + reason);

        counterDrawn = 0;
        counterTotal = 0;
        counterAbove = 0;
        counterBelow = 0;


        final GraphicsContext gc = getGraphicsContext2D();

        gc.clearRect(0, 0, getWidth(), getHeight());

        final IntervalTree<ActivityLink> links = graphics.getLinks();

        final Collection<ActivityLink> visibleLinks = links.getIntersectingObjects(
                graphics.getTimeline().getVisibleStartTime().toEpochMilli(),
                graphics.getTimeline().getVisibleEndTime().toEpochMilli());

        long time = System.currentTimeMillis();
        visibleLinks.forEach(link -> drawLink(gc, link));

        if (LoggingDomain.RENDERING.isLoggable(Level.FINER)) {
            LoggingDomain.RENDERING.finer(
                    "total: " + counterTotal +
                    ", above: " + counterAbove +
                    ", below: " + counterBelow +
                    ", rendered: " + counterDrawn +
                    ", time = " + (System.currentTimeMillis() - time));
        }
    }

    private void drawLink(GraphicsContext gc, ActivityLink<?> link) {
        counterTotal++;

        ActivityRef<?> sourceRef = link.getSourceActivityRef();
        ActivityRef<?> targetRef = link.getTargetActivityRef();

        if (!isShowing(sourceRef, targetRef)) {
            return;
        }

        GraphicsBaseSkin<?, ?> skin = (GraphicsBaseSkin<?, ?>) graphics.getSkin();

        if (skin != null) {

            counterDrawn++;

            Rectangle2D sourceBounds = skin.getActivityBounds(sourceRef);
            Rectangle2D targetBounds = skin.getActivityBounds(targetRef);

            if (sourceBounds != null && targetBounds != null) {

                RowCanvas sourceCanvas = skin.getRowCanvas(sourceRef);
                RowCanvas targetCanvas = skin.getRowCanvas(targetRef);

                if (sourceCanvas != null) {
                    sourceBounds = new Rectangle2D(sourceBounds.getMinX() - graphics.getCanvasBuffer() + sourceCanvas.getTranslateX(), sourceBounds.getMinY(), sourceBounds.getWidth(), sourceBounds.getHeight());
                }

                if (targetCanvas != null) {
                    targetBounds = new Rectangle2D(targetBounds.getMinX() - graphics.getCanvasBuffer() + targetCanvas.getTranslateX(), targetBounds.getMinY(), targetBounds.getWidth(), targetBounds.getHeight());
                }

                linkRenderer.draw(link, gc, sourceBounds, targetBounds);
            }
        }
    }

    private boolean isShowing(ActivityRef<?> sourceRef, ActivityRef<?> targetRef) {

        if (!(sourceRef.isPathExpanded() && targetRef.isPathExpanded())) {
            return false;
        }

        if (!sourceRef.getLayer().isVisible() && !targetRef.getLayer().isVisible()) {
            return false;
        }

        ObservableList<R> rows = graphics.getRows();

        int firstIndex = 0;
        int lastIndex = rows.size() - 1;

        R firstRow = graphics.getRowAt(5);
        R lastRow = graphics.getRowAt(getHeight() - 5);

        if (firstRow != null) {
            firstIndex = rows.indexOf(firstRow);
        }

        if (lastRow != null) {
            lastIndex = rows.indexOf(lastRow);
        }

        Row<?, ?, ?> sourceRow = sourceRef.getRow();
        Row<?, ?, ?> targetRow = targetRef.getRow();

        int sourceIndex = rows.indexOf(sourceRow);
        int targetIndex = rows.indexOf(targetRow);

        if (sourceIndex < firstIndex && targetIndex < firstIndex) {
            counterAbove++;
            return false;
        }

        if (sourceIndex > lastIndex && targetIndex > lastIndex) {
            counterBelow++;
            return false;
        }

        final Predicate rowFilter = graphics.getRowFilter();

        if (rowFilter != null) {
            boolean sourceRowShowing = rowFilter.test(sourceRow) || sourceRow.hasChildren(rowFilter);
            boolean targetRowShowing = rowFilter.test(targetRow) || targetRow.hasChildren(rowFilter);

            if (!(sourceRowShowing && targetRowShowing)) {
                return false;
            }
        }

        return true;
    }
}
