/**
 * Copyright (C) 2014 Dirk Lemmermann Software & Consulting (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */

package com.flexganttfx.view.graphics.renderer;

import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.YELLOW;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.controlsfx.control.PropertySheet.Item;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.Position;

public class ActivityRenderer<A extends Activity> extends Renderer {

    public ActivityRenderer(GraphicsBase<?> graphics, String name) {
        super(graphics, name);

        setStroke(Color.BLUE);
        setStrokePressed(BLUE.darker());
        setStrokeHighlight(YELLOW.darker());
        setStrokeSelected(RED);
        setStrokeHover(GREEN);

        // Listener support / Redraw

        redrawObservable(stroke);
        redrawObservable(strokePressed);
        redrawObservable(strokeHighlight);
        redrawObservable(strokeSelected);
        redrawObservable(strokeHover);

        redrawObservable(cornerRadius);
        redrawObservable(cornersRounded);

        redrawObservable(effect);
        redrawObservable(lineWidth);
    }

    public final ActivityBounds draw(ActivityRef<A> activityRef,
                                     Position position, GraphicsContext gc, double x, double y,
                                     double w, double h, boolean selected, boolean hover,
                                     boolean highlighted, boolean pressed) {

        gc.save();
        gc.setLineWidth(getLineWidth());

        disableRedrawAfterPropertyChange();

        try {
            return drawActivity(activityRef, position, gc, x, y, w, h,
                    selected, hover, highlighted, pressed);
        } finally {
            enableRedrawAfterPropertyChange();
            gc.restore();
        }
    }

    protected ActivityBounds drawActivity(ActivityRef<A> activityRef,
                                          Position position, GraphicsContext gc, double x, double y,
                                          double w, double h, boolean selected, boolean hover,
                                          boolean highlighted, boolean pressed) {

        gc.save();
        gc.setGlobalAlpha(gc.getGlobalAlpha() * getAlpha());
        drawBackground(activityRef, position, gc, x, y, w, h, selected, hover,
                highlighted, pressed);
        gc.restore();

        gc.save();
        gc.setGlobalAlpha(gc.getGlobalAlpha() * getAlpha());
        drawBorder(activityRef, position, gc, x, y, w, h, selected, hover,
                highlighted, pressed);
        gc.restore();

        return new ActivityBounds(activityRef, x, y, w, h);
    }

    protected void drawBackground(ActivityRef<A> activityRef,
                                  Position position, GraphicsContext gc, double x, double y,
                                  double w, double h, boolean selected, boolean hover,
                                  boolean highlighted, boolean pressed) {

        gc.setFill(getFill(selected, hover, highlighted, pressed));

        Effect effect = getEffect();
        if (effect != null) {
            gc.setEffect(effect);
        }

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

        gc.setEffect(null);
    }

    protected void drawBorder(ActivityRef<A> activityRef, Position position,
                              GraphicsContext gc, double x, double y, double w, double h,
                              boolean selected, boolean hover, boolean highlighted,
                              boolean pressed) {

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

    protected Paint getStroke(boolean selected, boolean hover,
                              boolean highlighted, boolean pressed) {
        if (pressed) {
            return getStrokePressed();
        } else if (highlighted) {
            return getStrokeHighlight();
        } else if (hover) {
            return getStrokeHover();
        } else if (selected) {
            return getStrokeSelected();
        } else {
            return getStroke();
        }
    }

    private BooleanProperty cornersRounded = new SimpleBooleanProperty(this,
            "cornersRounded", true);

    private DoubleProperty cornerRadius = new SimpleDoubleProperty(this,
            "cornerRadius", 6);

    private ObjectProperty<Paint> stroke = new SimpleObjectProperty<>(this,
            "stroke");

    private ObjectProperty<Paint> strokePressed = new SimpleObjectProperty<>(
            this, "strokePressed");

    private ObjectProperty<Paint> strokeHighlight = new SimpleObjectProperty<>(
            this, "strokeHighlight");

    private ObjectProperty<Paint> strokeSelected = new SimpleObjectProperty<>(
            this, "strokeSelected");

    private ObjectProperty<Paint> strokeHover = new SimpleObjectProperty<>(
            this, "strokeHover");

    private ObjectProperty<Effect> effect = new SimpleObjectProperty<>(this,
            "effect", null);

    private DoubleProperty lineWidth = new SimpleDoubleProperty(this,
            "lineWidth", .5);

    public final BooleanProperty cornersRoundedProperty() {
        return cornersRounded;
    }

    public final DoubleProperty cornerRadiusProperty() {
        return cornerRadius;
    }

    public final DoubleProperty lineWidthProperty() {
        return lineWidth;
    }

    public final ObjectProperty<Effect> effectProperty() {
        return effect;
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

    public final void setEffect(Effect effect) {
        this.effect.set(effect);
    }

    public final Effect getEffect() {
        return effect.get();
    }

    public final void setLineWidth(double lineWidth) {
        this.lineWidth.set(lineWidth);
    }

    public final double getLineWidth() {
        return lineWidth.get();
    }

    @Override
    public ObservableList<Item> getPropertySheetItems() {
        ObservableList<Item> items = super.getPropertySheetItems();

        // Stroke

        items.add(new Item() {

            @Override
            public void setValue(Object value) {
                setStroke((Paint) value);
            }

            @Override
            public Object getValue() {
                return getStroke();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Stroke";
            }

            @Override
            public String getDescription() {
                return "The color used for drawing the activity border.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + ActivityRenderer.this.getName();
            }
        });

        // Stroke Highlight

        items.add(new Item() {

            @Override
            public void setValue(Object value) {
                setStrokeHighlight((Paint) value);
            }

            @Override
            public Object getValue() {
                return getStrokeHighlight();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Stroke Highlight";
            }

            @Override
            public String getDescription() {
                return "The color used for drawing the activity border when the activity is currently drawn highlighted.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + ActivityRenderer.this.getName();
            }
        });

        // Stroke Hover

        items.add(new Item() {

            @Override
            public void setValue(Object value) {
                setStrokeHover((Paint) value);
            }

            @Override
            public Object getValue() {
                return getStrokeHover();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Stroke Hover";
            }

            @Override
            public String getDescription() {
                return "The color used for filling the activity border when the mouse cursor hovers over the activity.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + ActivityRenderer.this.getName();
            }
        });

        // Stroke Selected

        items.add(new Item() {

            @Override
            public void setValue(Object value) {
                setStrokeSelected((Paint) value);
            }

            @Override
            public Object getValue() {
                return getStrokeSelected();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Stroke Selected";
            }

            @Override
            public String getDescription() {
                return "The color used for drawing the activity border when the activity is currently selected.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + ActivityRenderer.this.getName();
            }
        });

        // Corners Rounded

        items.add(new Item() {

            @Override
            public void setValue(Object value) {
                setCornersRounded((Boolean) value);
            }

            @Override
            public Object getValue() {
                return isCornersRounded();
            }

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return "Corners Rounded";
            }

            @Override
            public String getDescription() {
                return "Determines if the corners of the activity will be drawn rounded or not.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + ActivityRenderer.this.getName();
            }
        });

        // Corner Radius

        items.add(new Item() {

            @Override
            public void setValue(Object value) {
                setCornerRadius((Double) value);
            }

            @Override
            public Object getValue() {
                return getCornerRadius();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Corner Radius";
            }

            @Override
            public String getDescription() {
                return "The radius used for the activity corners when rounded corners are used.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + ActivityRenderer.this.getName();
            }
        });

        // Effect

        items.add(new Item() {

            @Override
            public void setValue(Object value) {
                setEffect((Effect) value);
            }

            @Override
            public Object getValue() {
                return getEffect();
            }

            @Override
            public Class<?> getType() {
                return Effect.class;
            }

            @Override
            public String getName() {
                return "Effect";
            }

            @Override
            public String getDescription() {
                return "An effect applied to the rendered activity (e.g. drop shadow).";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + ActivityRenderer.this.getName();
            }
        });

        // Line Width

        items.add(new Item() {

            @Override
            public void setValue(Object value) {
                setLineWidth((Double) value);
            }

            @Override
            public Object getValue() {
                return getLineWidth();
            }

            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public String getName() {
                return "Line Width";
            }

            @Override
            public String getDescription() {
                return "The line width used for the border.";
            }

            @Override
            public String getCategory() {
                return "Renderer: " + ActivityRenderer.this.getName();
            }
        });

        return items;
    }
}
