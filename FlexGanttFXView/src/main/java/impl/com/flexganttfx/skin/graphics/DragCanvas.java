/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.DragAndDropInfo;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.timeline.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.DragEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.flexganttfx.view.util.Position.ONLY;

public class DragCanvas<R extends Row<?, ?, ?>> extends Canvas {

    private final GraphicsBase<R> graphics;

    private boolean includeSelectedActivitiesInDrag;

    private List<ActivityBounds> renderedBounds = new ArrayList<>();

    public DragCanvas(GraphicsBase<R> graphics) {
        this.graphics = graphics;

        setMouseTransparent(true);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    /**
     * Draws the current drag operation based on the information found inside the
     * info parameter. Applications can override this method and add additional functionality
     * if needed. For this the application can use the bounds found in the "rendered bounds"
     * list.
     *
     * @param info the drag information
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void draw(DragAndDropInfo info) {
        renderedBounds.clear();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (info != null) {
            ActivityBounds draggedBounds = info.getActivityBounds();
            draw(info, gc, draggedBounds, draggedBounds);

            if (includeSelectedActivitiesInDrag) {
                info.getSelectedActivities().forEach(activity -> draw(info, gc, draggedBounds, activity));
            }
        }
    }

    private void draw(DragAndDropInfo info, GraphicsContext gc, ActivityBounds draggedBounds, ActivityBounds bounds) {
        DragEvent evt = info.getDragEvent();
        ActivityRef<?> ref = bounds.getActivityRef();
        Activity activity = ref.getActivity();
        ActivityRenderer renderer = graphics.getActivityRenderer(
                activity.getClass(), bounds.getLayout().getClass());

        ActivityRef<?> draggedActivityRef = draggedBounds.getActivityRef();
        Activity draggedActivity = draggedActivityRef.getActivity();

        Timeline timeline = graphics.getTimeline();

        Instant startTime = info.getDropInterval().getStartTime();
        if (!draggedBounds.equals(bounds)) {
            Duration duration = Duration.between(draggedActivity.getStartTime(), activity.getStartTime());
            startTime = startTime.plus(duration);
        }

        double x = timeline.getModel().calculateLocationForTime(
                startTime);

        double y = evt.getSceneY() - localToScene(0, 0).getY()
                - info.getOffset().getY();
        if (!draggedBounds.equals(bounds)) {
            y = y + (bounds.getMinY() - draggedBounds.getMinY());
        }

        renderer.draw(ref, ONLY, gc, x, y, bounds.getWidth(),
                bounds.getHeight(), false, false, false, false);

        renderedBounds.add(new ActivityBounds(ref, x, y, bounds.getWidth(), bounds.getHeight()));
    }

    protected final List<ActivityBounds> getRenderedBounds() {
        return renderedBounds;
    }

    public final void setIncludeSelectedActivitiesInDrag(boolean include) {
        this.includeSelectedActivitiesInDrag = include;
    }

    public final boolean isIncludeSelectedActivitiesInDrag() {
        return includeSelectedActivitiesInDrag;
    }
}
