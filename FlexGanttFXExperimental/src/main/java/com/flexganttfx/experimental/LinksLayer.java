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
package com.flexganttfx.experimental;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.util.IntervalTree;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.layer.SystemLayer;
import impl.com.flexganttfx.skin.graphics.GraphicsBaseSkin;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Level;

public class LinksLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

    public LinksLayer(GraphicsBase<R> graphicsView) {
        super("Links", graphicsView);
    }

    private int counterTotal = 0;
    private int counterDrawn = 0;
    private int counterAbove = 0;
    private int counterBelow = 0;

    @Override
    public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
        // TODO: this layer is still work in progress
        if (true) {
            return;
        }

        if (LoggingDomain.RENDERING.isLoggable(Level.FINE)) {
            LoggingDomain.RENDERING.fine("drawing links layer in row " + canvas.getRow().getName());
        }

        counterDrawn = 0;
        counterTotal = 0;
        counterAbove = 0;
        counterBelow = 0;

        final GraphicsContext gc = canvas.getGraphicsContext2D();

        final GraphicsBase graphics = getGraphics();
        final IntervalTree<ActivityLink> links = graphics.getLinks();

        final Collection<ActivityLink> visibleLinks = links.getIntersectingObjects(
                graphics.getTimeline().getVisibleStartTime().toEpochMilli(),
                graphics.getTimeline().getVisibleEndTime().toEpochMilli());

        long time = 0;

        if (LoggingDomain.PERFORMANCE.isLoggable(Level.FINE)) {
            time = System.currentTimeMillis();
        }

        visibleLinks.forEach(link -> drawLink(canvas, graphics, gc, link));

        if (LoggingDomain.PERFORMANCE.isLoggable(Level.FINE)) {
            LoggingDomain.PERFORMANCE.fine(
                    "total: " + counterTotal +
                            ", above: " + counterAbove +
                            ", below: " + counterBelow +
                            ", rendered: " + counterDrawn +
                            ", timex = " + (System.currentTimeMillis() - time));
        }
    }

    private void drawLink(RowCanvas<R> canvas, GraphicsBase graphics, GraphicsContext gc, ActivityLink link) {
        counterTotal++;

        ActivityRef<?> sourceRef = link.getSourceActivityRef();
        ActivityRef<?> targetRef = link.getTargetActivityRef();

        if (!isShowing(graphics, sourceRef, targetRef)) {
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

                gc.setStroke(Color.BLACK);
                gc.strokeLine(sourceBounds.getMaxX(), 0, sourceBounds.getMaxX(), canvas.getHeight());
//                final LinkRenderer linkRenderer = graphics.getLinkRenderer(link.getClass());
//                linkRenderer.draw(link, gc, sourceBounds, targetBounds);
            }
        }
    }

    private boolean isShowing(GraphicsBase graphics, ActivityRef<?> sourceRef, ActivityRef<?> targetRef) {

        if (!(sourceRef.isPathExpanded() && targetRef.isPathExpanded())) {
            return false;
        }

        if (!sourceRef.getLayer().isVisible() && !targetRef.getLayer().isVisible()) {
            return false;
        }

        ObservableList<R> rows = graphics.getRows();

        int firstIndex = 0;
        int lastIndex = rows.size() - 1;

        Row firstRow = graphics.getRowAt(5);
        Row lastRow = graphics.getRowAt(graphics.getHeight() - 5);

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
