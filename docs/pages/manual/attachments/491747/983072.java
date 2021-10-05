/**
 * Copyright (C) 2014 Dirk Lemmermann Software & Consulting (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */

package com.flexganttfx.view.graphics.renderer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.controlsfx.control.PropertySheet.Item;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.activity.CompletableActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.Position;

public class CompletableActivityRenderer<A extends CompletableActivity> extends
        ActivityBarRenderer<A> {

    public CompletableActivityRenderer(GraphicsBase<?> graphics, String name) {
        super(graphics, name);

        setFillCompletion(new Color(0, 0, 0, .3));

        redrawObservable(fillCompletionProperty());
    }

    @Override
    protected ActivityBounds drawActivity(ActivityRef<A> path,
                                          Position position, GraphicsContext gc, double x, double y,
                                          double w, double h, boolean selected, boolean hover,
                                          boolean highlighted, boolean pressed) {

        ActivityBounds bounds = super.drawActivity(path, position, gc, x, y, w,
                h, selected, hover, highlighted, pressed);

        drawCompletion(path, gc, x, y, w, h, selected, hover, highlighted,
                pressed);

        return bounds;
    }

    ;

    protected void drawCompletion(ActivityRef<A> activityRef,
                                  GraphicsContext gc, double x, double y, double w, double h,
                                  boolean selected, boolean hover, boolean highlighted,
                                  boolean pressed) {

        double percentage = activityRef.getActivity().getPercentageComplete();

        double pw = (percentage / 100) * w;
        double my = y + (h - getBarHeight()) / 2;

        gc.setFill(getFillCompletion());

        if (isCornersRounded()) {
            gc.fillRoundRect(x, my, pw, getBarHeight(), getCornerRadius(),
                    getCornerRadius());
        } else {
            gc.fillRect(x, my, pw, getBarHeight());
        }
    }

    private ObjectProperty<Paint> fillCompletion = new SimpleObjectProperty<>(
            this, "fillCompletion");

    public final ObjectProperty<Paint> fillCompletionProperty() {
        return fillCompletion;
    }

    public final void setFillCompletion(Paint completionPaint) {
        fillCompletionProperty().set(completionPaint);
    }

    public final Paint getFillCompletion() {
        return fillCompletionProperty().get();
    }

    @Override
    public ObservableList<Item> getPropertySheetItems() {
        ObservableList<Item> list = super.getPropertySheetItems();

        list.add(new Item() {

            @Override
            public void setValue(Object value) {
                setFillCompletion((Paint) value);
            }

            @Override
            public Object getValue() {
                return getFillCompletion();
            }

            @Override
            public Class<?> getType() {
                return Paint.class;
            }

            @Override
            public String getName() {
                return "Completion";
            }

            @Override
            public String getDescription() {
                return "The paint used for drawing that segment of the activity that represents the completed part.";
            }

            @Override
            public String getCategory() {
                return "Renderer: "
                        + CompletableActivityRenderer.this.getName();
            }
        });

        return list;
    }
}
