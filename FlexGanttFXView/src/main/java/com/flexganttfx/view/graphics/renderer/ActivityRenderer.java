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
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.Position;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.util.Objects;

import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.YELLOW;

public class ActivityRenderer<A extends Activity> extends Renderer {

    public ActivityRenderer(GraphicsBase<?> graphics, String name) {
        super(graphics, name);
        redrawObservable(stroke);
        redrawObservable(strokePressed);
        redrawObservable(strokeHighlight);
        redrawObservable(strokeSelected);
        redrawObservable(strokeHover);
        redrawObservable(cornerRadius);
        redrawObservable(cornersRounded);
        redrawObservable(lineWidth);
    }

    public final ActivityBounds draw(ActivityRef<A> activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {

        final GraphicsBase<?> graphics = getGraphics();

        if (graphics.isSafeRendering()) {
            gc.save();
        }

        gc.setLineWidth(getLineWidth());

        disableRedrawAfterPropertyChange();

        try {
            return drawActivity(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);
        } finally {
            enableRedrawAfterPropertyChange();

            if (graphics.isSafeRendering()) {
                gc.restore();
            }
        }
    }

    protected ActivityBounds drawActivity(ActivityRef<A> activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {

        final GraphicsBase<?> graphics = getGraphics();

        double alpha = gc.getGlobalAlpha();

        try {
            if (graphics.isSafeRendering()) {
                gc.save();
            }
            gc.setGlobalAlpha(alpha * getAlpha());
            drawBackground(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);
        } finally {
            if (graphics.isSafeRendering()) {
                gc.restore();
            } else {
                gc.setGlobalAlpha(alpha);
            }
        }

        try {
            if (graphics.isSafeRendering()) {
                gc.save();
            }
            gc.setGlobalAlpha(alpha * getAlpha());
            drawBorder(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);
        } finally {
            if (graphics.isSafeRendering()) {
                gc.restore();
            } else {
                gc.setGlobalAlpha(alpha);
            }
        }

        return new ActivityBounds(activityRef, x, y, w, h);
    }

    protected void drawBackground(ActivityRef<A> activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {

        gc.setFill(getFill(selected, hover, highlighted, pressed));

        Insets padding = getPadding();
        x += padding.getLeft();
        y += padding.getTop();
        w -= (padding.getLeft() + padding.getRight());
        h -= (padding.getTop() + padding.getBottom());

        if (isCornersRounded()) {
            gc.fillRoundRect(x, y, w, h, getCornerRadius(), getCornerRadius());
        } else {
            gc.fillRect(x, y, w, h);
        }
    }

    protected void drawBorder(ActivityRef<A> activityRef, Position position, GraphicsContext gc, double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted, boolean pressed) {

        gc.setStroke(getStroke(selected, hover, highlighted, pressed));

        Insets padding = getPadding();
        x += padding.getLeft();
        y += padding.getTop();
        w -= (padding.getLeft() + padding.getRight());
        h -= (padding.getTop() + padding.getBottom());

        if (isCornersRounded()) {
            gc.strokeRoundRect(x, y, w, h, getCornerRadius(), getCornerRadius());
        } else {
            gc.strokeRect(x, y, w, h);
        }
    }

    protected Paint getStroke(boolean selected, boolean hover, boolean highlighted, boolean pressed) {
        GraphicsBase<?> g = getGraphics();
        if (pressed) {
            Paint c = getStrokePressed();
            return c != null ? c : g.getActivityStrokePressed();
        } else if (highlighted) {
            Paint c = getStrokeHighlight();
            return c != null ? c : g.getActivityStrokeHighlight();
        } else if (hover) {
            Paint c = getStrokeHover();
            return c != null ? c : g.getActivityStrokeHover();
        } else if (selected) {
            Paint c = getStrokeSelected();
            return c != null ? c : g.getActivityStrokeSelected();
        } else {
            Paint c = getStroke();
            return c != null ? c : g.getActivityStroke();
        }
    }

    private final BooleanProperty cornersRounded = new SimpleBooleanProperty(this, "cornersRounded", false);

    private final DoubleProperty cornerRadius = new SimpleDoubleProperty(this, "cornerRadius", 6);

    private final ObjectProperty<Paint> stroke = new SimpleObjectProperty<>(this, "stroke");

    private final ObjectProperty<Paint> strokePressed = new SimpleObjectProperty<>(this, "strokePressed");

    private final ObjectProperty<Paint> strokeHighlight = new SimpleObjectProperty<>(this, "strokeHighlight");

    private final ObjectProperty<Paint> strokeSelected = new SimpleObjectProperty<>(this, "strokeSelected");

    private final ObjectProperty<Paint> strokeHover = new SimpleObjectProperty<>(this, "strokeHover");

    private final DoubleProperty lineWidth = new SimpleDoubleProperty(this, "lineWidth", .5);

    public final BooleanProperty cornersRoundedProperty() {
        return cornersRounded;
    }

    public final DoubleProperty cornerRadiusProperty() {
        return cornerRadius;
    }

    public final DoubleProperty lineWidthProperty() {
        return lineWidth;
    }

    public final ObjectProperty<Paint> strokeProperty() {
        return stroke;
    }

    public final ObjectProperty<Paint> strokePressedProperty() {
        return strokePressed;
    }

    public final ObjectProperty<Paint> strokeHoverProperty() {
        return strokeHover;
    }

    public final ObjectProperty<Paint> strokeSelectedProperty() {
        return strokeSelected;
    }

    public final ObjectProperty<Paint> strokeHighlightProperty() {
        return strokeHighlight;
    }

    // @formatter:on

    public final Paint getStroke() {
        return stroke.get();
    }

    public final void setStroke(Paint paint) {
        Objects.nonNull(paint);
        this.stroke.set(paint);
    }

    public final Paint getStrokePressed() {
        return strokePressed.get();
    }

    public final void setStrokePressed(Paint paint) {
        Objects.nonNull(paint);
        this.strokePressed.set(paint);
    }

    public final Paint getStrokeHighlight() {
        return strokeHighlight.get();
    }

    public final void setStrokeHighlight(Paint paint) {
        Objects.nonNull(paint);
        this.strokeHighlight.set(paint);
    }

    public final Paint getStrokeSelected() {
        return strokeSelected.get();
    }

    public final void setStrokeSelected(Paint paint) {
        Objects.nonNull(paint);
        this.strokeSelected.set(paint);
    }

    public final Paint getStrokeHover() {
        return strokeHover.get();
    }

    public final void setStrokeHover(Paint paint) {
        Objects.nonNull(paint);
        this.strokeHover.set(paint);
    }

    public final void setCornerRadius(double radius) {
        this.cornerRadius.set(radius);
    }

    public final double getCornerRadius() {
        return cornerRadius.get();
    }

    public final void setCornersRounded(boolean rounded) {
        this.cornersRounded.set(rounded);
    }

    public final boolean isCornersRounded() {
        return cornersRounded.get();
    }

    public final void setLineWidth(double lineWidth) {
        this.lineWidth.set(lineWidth);
    }

    public final double getLineWidth() {
        return lineWidth.get();
    }
}
