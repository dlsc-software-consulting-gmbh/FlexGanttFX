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

/**
 * Canvas used to render drag visuals for activities. It can paint the dragged activity
 * together with the current selection to produce the drag image shown during drag and drop.
 *
 * @param <R> the type of the rows
 */
public class DragCanvas<R extends Row<?, ?, ?>> extends Canvas {

    private final GraphicsBase<R> graphics;

    private boolean includeSelectedActivitiesInDrag;

    private final List<ActivityBounds> renderedBounds = new ArrayList<>();

    /**
     * Constructs a new drag canvas for the given graphics control.
     *
     * @param graphics
     *            the graphics control
     */
    public DragCanvas(GraphicsBase<R> graphics) {
        this.graphics = graphics;

        setMouseTransparent(true);
    }

    /**
     * Returns whether this canvas is resizable.
     *
     * @return true if this canvas is resizable
     */
    @Override
    public boolean isResizable() {
        return true;
    }

    /**
     * Returns the preferred width for the given height.
     *
     * @param height the height
     *
     * @return the preferred width
     */
    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    /**
     * Returns the preferred height for the given width.
     *
     * @param width the width
     *
     * @return the preferred height
     */
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
        ActivityRenderer renderer = graphics.getActivityRenderer(activity.getClass(), bounds.getLayout().getClass());

        ActivityRef<?> draggedActivityRef = draggedBounds.getActivityRef();
        Activity draggedActivity = draggedActivityRef.getActivity();

        Timeline timeline = graphics.getTimeline();

        Instant startTime = info.getDropInterval().getStartTime();
        if (!draggedBounds.equals(bounds)) {
            Duration duration = Duration.between(draggedActivity.getStartTime(), activity.getStartTime());
            startTime = startTime.plus(duration);
        }

        double x = timeline.getModel().calculateLocationForTime(startTime);

        double y = evt.getSceneY() - localToScene(0, 0).getY() - info.getOffset().getY();
        if (!draggedBounds.equals(bounds)) {
            y = y + (bounds.getMinY() - draggedBounds.getMinY());
        }

        renderer.draw(ref, ONLY, gc, x, y, bounds.getWidth(), bounds.getHeight(), false, false, false, false);

        renderedBounds.add(new ActivityBounds(ref, x, y, bounds.getWidth(), bounds.getHeight()));
    }

    /**
     * Returns the rendered bounds.
     *
     * @return the rendered bounds
     */
    protected final List<ActivityBounds> getRenderedBounds() {
        return renderedBounds;
    }

    /**
     * Sets whether selected activities should be included in the drag operation.
     *
     * @param include whether selected activities should be included in the drag operation
     */
    public final void setIncludeSelectedActivitiesInDrag(boolean include) {
        this.includeSelectedActivitiesInDrag = include;
    }

    /**
     * Returns whether selected activities are included in the drag operation.
     *
     * @return true if selected activities are included in the drag operation
     */
    public final boolean isIncludeSelectedActivitiesInDrag() {
        return includeSelectedActivitiesInDrag;
    }
}
