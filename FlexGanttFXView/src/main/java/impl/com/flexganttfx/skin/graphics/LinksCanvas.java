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

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.util.IntervalTree;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.LinkRenderer;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * Canvas responsible for drawing visible activity links. It batches redraw requests and
 * rebuilds the link shapes from the current graphics state during layout pulses.
 *
 * @param <R> the type of the rows
 */
public class LinksCanvas<R extends Row<?, ?, ?>> extends Canvas {

    private final GraphicsBase<R> graphics;

    /**
     * Constructs a new links canvas for the given graphics control.
     *
     * @param graphics
     *            the graphics control
     */
    public LinksCanvas(GraphicsBase<R> graphics) {
        this.graphics = graphics;

        /*
         * Don't show links when a row editor is in use.
         */
        visibleProperty().bind(graphics.showLinksProperty().and(Bindings.isEmpty(graphics.getRowsEditing())));

        setMouseTransparent(true);

        graphics.addEventFilter(ActivityEvent.ACTIVITY_CHANGE, event -> requestRedraw("an activity changed"));

        visibleProperty().addListener(it -> {
            if (isVisible()) {
                requestRedraw("visibility of links canvas changed to true");
            }
        });

        final Runnable drawRunnable = () -> {
            if (dirty) {
                draw();
                LoggingDomain.RENDERING.fine("calls to draw links = " + drawCounter + ", actual draws = " + doDrawCounter + ", saved draws = " + (drawCounter - doDrawCounter));
            }
        };

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.removePostLayoutPulseListener(drawRunnable);
            }

            if (newScene != null) {
                newScene.addPostLayoutPulseListener(drawRunnable);
            }
        });
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

    private int counterTotal = 0;
    private int counterDrawn = 0;
    private int counterAbove = 0;
    private int counterBelow = 0;

    private boolean dirty;
    private String reason;

    private static int drawCounter;
    private static int doDrawCounter;

    /**
     * Returns whether a redraw is pending.
     *
     * @return true if a redraw is pending
     */
    public final boolean isDirty() {
        return dirty;
    }

    /**
     * Requests a redraw for the given reason.
     *
     * @param reason the redraw reason
     */
    public void requestRedraw(String reason) {
        this.reason = reason;
        this.dirty = true;

        if (drawCounter < Integer.MAX_VALUE) {
            drawCounter++;
        } else {
            drawCounter = 1;
            doDrawCounter = 1;
        }

        //
        // Super important to also request a layout because the actual drawing only
        // happens after a layout pulse gets fired, which is not guaranteed if the
        // only thing that changed is the content of the canvas.
        //
        if (getParent() != null) {
            getParent().requestLayout();
        }
    }

    /**
     * Draws the canvas contents.
     */
    public final void draw() {
        dirty = false;

        if (doDrawCounter < Integer.MAX_VALUE) {
            doDrawCounter++;
        } else {
            doDrawCounter = 1;
            drawCounter = 1;
        }

        if (!isVisible()) {
            return;
        }

        if (LoggingDomain.RENDERING.isLoggable(Level.FINE)) {
            LoggingDomain.RENDERING.fine("redrawing links, reason: " + reason);
        }

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

        long time = 0;

        if (LoggingDomain.PERFORMANCE.isLoggable(Level.FINE)) {
            time = System.currentTimeMillis();
        }

        visibleLinks.forEach(link -> drawLink(gc, link));

        if (LoggingDomain.PERFORMANCE.isLoggable(Level.FINE)) {
            LoggingDomain.PERFORMANCE.fine(
                    "total: " + counterTotal +
                            ", above: " + counterAbove +
                            ", below: " + counterBelow +
                            ", rendered: " + counterDrawn +
                            ", time = " + (System.currentTimeMillis() - time));
        }

        if (graphics.isDebugMode()) {
            gc.setStroke(Color.RED);
            gc.strokeRect(0, 0, getWidth(), getHeight());
        }
    }

    private void drawLink(GraphicsContext gc, ActivityLink link) {
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

                if (graphics.isSafeRendering()) {
                    gc.save();
                }

                try {
                    double alpha = gc.getGlobalAlpha();
                    gc.setGlobalAlpha(link.getSourceActivityRef().getLayer().getOpacity());
                    final LinkRenderer linkRenderer = graphics.getLinkRenderer(link.getClass());
                    linkRenderer.draw(link, gc, sourceBounds, targetBounds);
                    gc.setGlobalAlpha(alpha);
                } finally {
                    if (graphics.isSafeRendering()) {
                        gc.restore();
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

            return sourceRowShowing && targetRowShowing;
        }

        return true;
    }
}
