/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.model.util.IntervalTree;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Timeline;
import impl.com.flexganttfx.skin.graphics.PathBuilder.PathBuilderResult;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.scene.shape.Path;

import java.time.Instant;
import java.util.Collection;
import java.util.function.Predicate;

public class LinksPane<R extends Row<?, ?, ?>> extends Region {

    private GraphicsBase<R> graphics;

    public LinksPane(GraphicsBase<R> graphics) {
        this.graphics = graphics;

        setMinSize(0, 0);

		/*
         * Don't show links when a row editor is in use.
		 */
        visibleProperty().bind(Bindings.isEmpty(graphics.getRowsEditing()));

        setMouseTransparent(true);

        Timeline timeline = graphics.getTimeline();

        TimelineModel<?> timelineModel = timeline.getModel();

        timelineModel.startTimeProperty().addListener(new WeakInvalidationListener(weakRedrawListener));
        timeline.getModel().millisPerPixelProperty().addListener(weakRedrawListener);

        timeline.modelProperty().addListener((observable, oldModel, newModel) -> {
            if (oldModel != null) {
                oldModel.startTimeProperty().removeListener(weakRedrawListener);
                oldModel.millisPerPixelProperty().removeListener(weakRedrawListener);
            }
            if (newModel != null) {
                newModel.startTimeProperty().addListener(weakRedrawListener);
                newModel.millisPerPixelProperty().removeListener(weakRedrawListener);
            }
        });

        graphics.addEventFilter(ActivityEvent.ACTIVITY_CHANGE, event -> layoutLinks());

        graphics.getRows().addListener(weakRowListChangedListener);

        // TODO: implement
        //graphics.getLinks().addListener((Observable evt) -> layoutLinks());
    }

    private final ListChangeListener<Row<?, ?, ?>> rowListChangedListener = (Change<? extends Row<?, ?, ?>> c) -> graphics.redraw();

    private final WeakListChangeListener<Row<?, ?, ?>> weakRowListChangedListener = new WeakListChangeListener<>(rowListChangedListener);

    private final InvalidationListener redrawListener = observable -> requestLayout();

    private final WeakInvalidationListener weakRedrawListener = new WeakInvalidationListener(redrawListener);

    private final PathBuilder pathBuilder = new PathBuilder();

    private boolean layout;

    public final void layoutLinks() {
        layout = true;
        requestLayout();
    }

    private int counter = 0;

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (layout) {
            counter = 0;
            getChildren().clear();
            final IntervalTree<ActivityLink> links = graphics.getLinks();
            final Collection<ActivityLink> visibleLinks = links.getIntersectingObjects(graphics.getTimeline().getVisibleStartTime().toEpochMilli(), graphics.getTimeline().getVisibleStartTime().toEpochMilli());
            long time = System.currentTimeMillis();
            visibleLinks.forEach(this::layoutLink);
            System.out.println("rendered links count: " + counter + ", time = " + (System.currentTimeMillis() - time));
            layout = false;
        }
    }

    private void layoutLink(ActivityLink<?> link) {
        ActivityRef<?> sourceRef = link.getSourceActivityRef();
        ActivityRef<?> targetRef = link.getTargetActivityRef();

        if (!isShowing(sourceRef, targetRef)) {
            return;
        }

        counter++;

        GraphicsBaseSkin<?, ?> skin = (GraphicsBaseSkin<?, ?>) graphics.getSkin();

        if (skin != null) {

            Rectangle2D sourceBounds = skin.getActivityBounds(sourceRef);
            Rectangle2D targetBounds = skin.getActivityBounds(targetRef);

            if (sourceBounds != null && targetBounds != null) {

                RowCanvas sourceCanvas = skin.getRowCanvas(sourceRef);
                RowCanvas targetCanvas = skin.getRowCanvas(targetRef);

                if (sourceCanvas != null) {
                    sourceBounds = new Rectangle2D(sourceBounds.getMinX() - graphics.getCanvasBuffer() + sourceCanvas.getTranslateX() , sourceBounds.getMinY(), sourceBounds.getWidth(), sourceBounds.getHeight());
                }

                if (targetCanvas != null) {
                    targetBounds = new Rectangle2D(targetBounds.getMinX() - graphics.getCanvasBuffer() + targetCanvas.getTranslateX() , targetBounds.getMinY(), targetBounds.getWidth(), targetBounds.getHeight());
                }

                Layer sourceLayer = sourceRef.getLayer();

                PathBuilderResult result = null;

                switch (link.getType()) {
                    case END_TO_END:
                        result = pathBuilder.buildPathEndToEnd(sourceBounds, targetBounds);
                        break;
                    case END_TO_START:
                        result = pathBuilder.buildPathEndToStart(sourceBounds, targetBounds);
                        break;
                    case START_TO_END:
                        result = pathBuilder.buildPathStartToEnd(sourceBounds, targetBounds);
                        break;
                    case START_TO_START:
                        result = pathBuilder.buildPathStartToStart(sourceBounds, targetBounds);
                        break;
                    default:
                        break;
                }

                if (result != null) {
                    Path path = result.getPath();

                    path.opacityProperty().bind(sourceLayer.opacityProperty());
                    path.setManaged(false);
                    path.setMouseTransparent(true);
                    path.getStyleClass().add("link");
                    getChildren().add(path);

                    Region startRegion = new Region();
                    startRegion.opacityProperty().bind(sourceLayer.opacityProperty());
                    startRegion.setManaged(false);
                    startRegion.setMouseTransparent(true);
                    startRegion.getStyleClass().add("link-start-handle");
                    getChildren().add(startRegion);
                    startRegion.applyCss();
                    double startRegionWidth = startRegion.prefWidth(-1);
                    startRegion.resizeRelocate(result.getStart().getX(), result.getStart().getY(), startRegionWidth, startRegion.getPrefHeight());

                    Region endRegion = new Region();
                    endRegion.opacityProperty().bind(sourceLayer.opacityProperty());
                    endRegion.setManaged(false);
                    endRegion.setMouseTransparent(true);
                    endRegion.getStyleClass().add("link-end-handle");
                    getChildren().add(endRegion);
                    endRegion.applyCss();
                    double endRegionWidth = endRegion.prefWidth(-1);
                    endRegion.resizeRelocate(result.getEnd().getX(), result.getEnd().getY(), endRegionWidth, endRegion.getPrefHeight());

                    switch (link.getType()) {
                        case START_TO_START:
                        case START_TO_END:
                            startRegion.setRotate(180);
                            startRegion.getStyleClass().add("link-start-handle-rotated");
                            break;
                        default:
                            break;
                    }

                    switch (link.getType()) {

                        case END_TO_END:
                        case START_TO_END:
                            endRegion.setRotate(180);
                            endRegion.getStyleClass().add("link-end-handle-rotated");
                            break;
                        default:
                            break;
                    }

                    if (result.getStart().getY() == result.getEnd().getY() && result.getEnd().getX() - result.getStart().getX() < startRegionWidth + endRegionWidth) {
                        startRegion.setVisible(false);
                    }
                }
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
            return false;
        }

        if (sourceIndex > lastIndex && targetIndex > lastIndex) {
            return false;
        }

        final Predicate rowFilter = graphics.getRowFilter();

        if (rowFilter != null) {
            boolean sourceRowShowing = rowFilter.test(sourceRow) || sourceRow.hasChildren(rowFilter);
            boolean targetRowShowing = rowFilter.test(targetRow) || targetRow.hasChildren(rowFilter);

            if (! (sourceRowShowing && targetRowShowing)) {
                return false;
            }
        }

        Timeline timeline = graphics.getTimeline();

        Instant visibleStart = timeline.getVisibleStartTime();
        Instant visibleEnd = timeline.getVisibleEndTime();

        Activity sourceActivity = sourceRef.getActivity();
        Activity targetActivity = targetRef.getActivity();

        if (sourceActivity.getEndTime().isBefore(visibleStart) && targetActivity.getEndTime().isBefore(visibleStart)) {
            return false;
        }

        return !sourceActivity.getStartTime().isAfter(visibleEnd) || !targetActivity.getStartTime().isAfter(visibleEnd);
    }
}
