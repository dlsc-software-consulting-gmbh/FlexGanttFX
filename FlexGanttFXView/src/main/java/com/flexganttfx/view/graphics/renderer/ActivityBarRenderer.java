/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.Position;
import javafx.beans.property.*;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Objects;

public class ActivityBarRenderer<A extends Activity> extends ActivityRenderer<A> {

    public enum TextPosition {
        LEFT, CENTER, RIGHT,

        ABOVE, ABOVE_LEFT, ABOVE_RIGHT,

        BELOW, BELOW_LEFT, BELOW_RIGHT,

        LEADING, TRAILING,
    }

    private static final Color WHITE = new Color(1, 1, 1, .3);

    public ActivityBarRenderer(GraphicsBase<?> graphics, String name) {
        super(graphics, name);

        setTextFill(Color.BLACK);
        setTextFillHover(Color.BLACK);
        setTextFillHighlight(Color.BLACK);
        setTextFillSelected(Color.BLACK);
        setTextFillPressed(Color.BLACK);

        redrawObservable(autoFixText);
        redrawObservable(barHeight);
        redrawObservable(font);
        redrawObservable(glossy);
        redrawObservable(textFill);
        redrawObservable(textGap);

        redrawObservable(textFill);
        redrawObservable(textFillSelected);
        redrawObservable(textFillHover);
        redrawObservable(textFillPressed);
        redrawObservable(textFillHighlight);
    }

    @Override
    protected ActivityBounds drawActivity(ActivityRef<A> activityRef,
                                          Position position, GraphicsContext gc, double x, double y, double w,
                                          double h, boolean selected, boolean hover, boolean highlighted,
                                          boolean pressed) {

        drawBackground(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);

        drawBorder(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);

        double my = y;
        double mh = h;
        double barHeight = getBarHeight();

        if (barHeight > 0) {
            my = y + (h - barHeight) / 2;
            mh = barHeight;
        }

        if (isMilestone(activityRef)) {
            return new ActivityBounds(activityRef, x - barHeight / 2, my, barHeight, barHeight);
        }

        return new ActivityBounds(activityRef, x, my, w, mh);
    }

    @Override
    protected void drawBackground(ActivityRef<A> activityRef, Position position,
                                  GraphicsContext gc, double x, double y, double w, double h,
                                  boolean selected, boolean hover, boolean highlighted,
                                  boolean pressed) {

        if (isMilestone(activityRef)) {
            drawMilestoneBackground(activityRef, gc, x, y, w, h, selected, hover, highlighted, pressed);
        } else {
            drawActivityBackground(gc, x, y, w, h, selected, hover, highlighted, pressed);
        }
    }

    private boolean isMilestone(ActivityRef<?> activityRef) {
        Activity activity = activityRef.getActivity();
        return activity.getStartTime().equals(activity.getEndTime());
    }

    private void drawActivityBackground(
            GraphicsContext gc, double x, double y, double w, double h,
            boolean selected, boolean hover, boolean highlighted,
            boolean pressed) {

        double my = y;
        double bh = h;

        double barHeight = getBarHeight();

        if (barHeight > 0) {
            my = y + (h - barHeight) / 2;
            bh = barHeight;
        }

        boolean glossy = isGlossy();

        if (selected) {
            gc.setStroke(getStrokeSelected());

            if (isCornersRounded()) {
                double cornerRadius = getCornerRadius();
                gc.strokeRoundRect(x - 3, my - 3, w + 6, bh + 6, cornerRadius, cornerRadius);
            } else {
                gc.strokeRect(x - 3, my - 3, w + 6, bh + 6);
            }
        }

        gc.setFill(getFill(false, hover, highlighted, pressed));

        if (isCornersRounded()) {

            double cornerRadius = getCornerRadius();

            gc.fillRoundRect(x, my, w, bh, cornerRadius, cornerRadius);

            if (glossy) {
                gc.setFill(WHITE);
                gc.fillRoundRect(x, my, w, bh / 2, cornerRadius, cornerRadius);
            }
        } else {
            gc.fillRect(x, my, w, bh);

            if (glossy) {
                gc.setFill(WHITE);
                gc.fillRect(x, my, w, bh / 2);
            }
        }
    }

    private void drawMilestoneBackground(ActivityRef<A> activityRef,
                                         GraphicsContext gc, double x, double y, double w, double h,
                                         boolean selected, boolean hover, boolean highlighted,
                                         boolean pressed) {

        double my = y;
        double bh = h;

        double barHeight = getBarHeight();

        if (barHeight > 0) {
            my = y + (h - barHeight) / 2;
            bh = barHeight;
        }

        boolean glossy = isGlossy();

        double[] xx = new double[]{x, x + bh / 2, x, x - bh / 2, x};
        double[] yy = new double[]{my, my + bh / 2, my + bh, my + bh / 2, my};

        if (selected) {
            double[] xxSelected = new double[]{x, x + bh / 2 + 2, x, x - bh / 2 - 2, x};
            double[] yySelected = new double[]{my - 2, my + bh / 2, my + bh + 2, my + bh / 2, my - 2};

            gc.setStroke(getStrokeSelected());
            gc.strokePolygon(xxSelected, yySelected, 5);
        }

        gc.setFill(getFill(false, hover, highlighted, pressed));
        gc.fillPolygon(xx, yy, 5);

        if (glossy) {
            gc.setFill(WHITE);
            gc.fillPolygon(xx, yy, 3);
        }
    }

    @Override
    protected void drawBorder(ActivityRef<A> activityRef, Position position,
                              GraphicsContext gc, double x, double y, double w, double h,
                              boolean selected, boolean hover, boolean highlighted,
                              boolean pressed) {

        if (isMilestone(activityRef)) {
            drawMilestoneBorder(activityRef, gc, x, y, w, h, selected, hover, highlighted, pressed);
        } else {
            drawActivityBorder(activityRef, gc, x, y, w, h, selected, hover, highlighted, pressed);
        }
    }

    private void drawActivityBorder(ActivityRef<A> activityRef,
                                    GraphicsContext gc, double x, double y, double w, double h,
                                    boolean selected, boolean hover, boolean highlighted,
                                    boolean pressed) {

        gc.setStroke(getStroke(false, hover, highlighted, pressed));

        double my = y;
        double bh = h;
        double barHeight = getBarHeight();

        if (barHeight > 0) {
            my = y + (h - barHeight) / 2;
            bh = barHeight;
        }

        if (isCornersRounded()) {
            double cornerRadius = getCornerRadius();
            gc.strokeRoundRect(x, my, w, bh, cornerRadius, cornerRadius);
        } else {
            gc.strokeRect(x, my, w, bh);
        }
    }

    private void drawMilestoneBorder(ActivityRef<A> activityRef,
                                     GraphicsContext gc, double x, double y, double w, double h,
                                     boolean selected, boolean hover, boolean highlighted,
                                     boolean pressed) {

        gc.setStroke(getStroke(false, hover, highlighted, pressed));

        double my = y;
        double bh = h;
        double barHeight = getBarHeight();

        if (barHeight > 0) {
            my = y + (h - barHeight) / 2;
            bh = barHeight;
        }

        double[] xx = new double[]{x, x + bh / 2, x, x - bh / 2, x};
        double[] yy = new double[]{my, my + bh / 2, my + bh, my + bh / 2, my};

        gc.strokePolygon(xx, yy, 5);
    }

    protected void drawText(ActivityRef<A> activityRef, String text,
                            TextPosition position, GraphicsContext gc, double x, double y,
                            double w, double h, boolean selected, boolean hover,
                            boolean highlighted, boolean pressed) {

        double availableWidth;

        switch (position) {
            case LEADING:
            case TRAILING:
                availableWidth = Double.MAX_VALUE;
                break;
            case CENTER:
                availableWidth = Math.max(0, Math.min((x < 0 ? w + x : w), gc.getCanvas().getWidth() - (x < 0 ? 0 : x)));
                break;
            default:
                availableWidth = Math.max(0, Math.min((x < 0 ? w + x : w) - 2 * getTextGap(), gc.getCanvas().getWidth() - (x < 0 ? 0 : x)));
                break;
        }

        if (!isTextVisible(text, position, availableWidth)) {
            return;
        }

        double my = y;
        double bh = h;

        double barHeight = getBarHeight();

        if (barHeight > 0) {
            my = y + (h - barHeight) / 2;
            bh = barHeight;
        }

        gc.setFill(getTextFill(selected, hover, highlighted, pressed));

        double textX = 0;
        double textY = 0;
        double textGap = isMilestone(activityRef) ? barHeight / 2 + getTextGap() : getTextGap();

        switch (position) {
            case LEADING:
                textX = x - textGap;
                textY = my + bh / 2;
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.setTextBaseline(VPos.CENTER);
                break;

            case TRAILING:
                textX = x + w + textGap;
                textY = my + bh / 2;
                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.CENTER);
                break;

            case LEFT:
                boolean autoFixText = isAutoFixText();
                textX = autoFixText ? Math.max(0, x + textGap) : x + textGap;
                textY = my + bh / 2;
                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.CENTER);
                break;

            case RIGHT:
                textX = Math.min(gc.getCanvas().getWidth(), x + w) - textGap;
                textY = my + bh / 2;
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.setTextBaseline(VPos.CENTER);
                break;

            case CENTER:
                textX = (x < 0 ? 0 : x) + availableWidth / 2;
                textY = my + bh / 2;
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);
                break;

            case ABOVE:
                textX = (x < 0 ? 0 : x) + availableWidth / 2;
                textY = my;
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.BOTTOM);
                break;

            case ABOVE_LEFT:
                textX = (x < 0 ? 0 : x);
                textY = my;
                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.BOTTOM);
                break;

            case ABOVE_RIGHT:
                textX = Math.min(gc.getCanvas().getWidth(), x + w);
                textY = my;
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.setTextBaseline(VPos.BOTTOM);
                break;

            case BELOW:
                textX = (x < 0 ? 0 : x) + availableWidth / 2;
                textY = my + bh;
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.TOP);
                break;

            case BELOW_LEFT:
                textX = (x < 0 ? 0 : x);
                textY = my + bh;
                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.TOP);
                break;

            case BELOW_RIGHT:
                textX = Math.min(gc.getCanvas().getWidth(), x + w);
                textY = my + bh;
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.setTextBaseline(VPos.TOP);
                break;

            default:
                break;

        }

        gc.setFont(getFont());

        switch (position) {
            case LEADING:
            case TRAILING:
                gc.fillText(text, snapPositionX(textX), snapPositionY(textY));
                break;
            default:
                gc.fillText(text, snapPositionX(textX), snapPositionY(textY), availableWidth);
                break;
        }
    }

    /**
     * Determines if the given text for the given position will be drawn or not
     * depending on the available width. The default strategy is to not allow
     * the text to show if the available width is less than 10 pixels, the text
     * is null, or the text length multiplied by three is larger than the
     * available width.
     *
     * @param text
     *            the text to draw
     * @param position
     *            the text position
     * @param availableWidth
     *            the availble width of the entire activity
     * @return true if the text can be drawn
     * @since 1.5
     */
    protected boolean isTextVisible(String text, TextPosition position, double availableWidth) {
        return !(availableWidth < 10) && text != null && !(text.length() * 3 > availableWidth);
    }

    protected Paint getTextFill(boolean selected, boolean hover, boolean highlighted, boolean pressed) {
        if (pressed) {
            return getTextFillPressed();
        } else if (highlighted) {
            return getTextFillHighlight();
        } else if (hover) {
            return getTextFillHover();
        } else if (selected) {
            return getTextFillSelected();
        } else {
            return getTextFill();
        }
    }

    // @formatter:off
    private final DoubleProperty barHeight = new SimpleDoubleProperty(this, "barHeight", 10);
    private final DoubleProperty textGap = new SimpleDoubleProperty(this, "textGap", 8);
    private final BooleanProperty glossy = new SimpleBooleanProperty(this, "glossy", false);
    private final BooleanProperty autoFixText = new SimpleBooleanProperty(this, "autoFixText", true);

    private final ObjectProperty<Paint> textFill = new SimpleObjectProperty<>(this, "textFill");
    private final ObjectProperty<Paint> textFillHover = new SimpleObjectProperty<>(this, "textFillHover");
    private final ObjectProperty<Paint> textFillHighlight = new SimpleObjectProperty<>(this, "textFillHighlight");
    private final ObjectProperty<Paint> textFillSelected = new SimpleObjectProperty<>(this, "textFillSelected");
    private final ObjectProperty<Paint> textFillPressed = new SimpleObjectProperty<>(this, "textFillPressed");

    private final ObjectProperty<Font> font = new SimpleObjectProperty<>(this, "font", Font.font(10));

    // @formatter:on

    public final DoubleProperty barHeightProperty() {
        return barHeight;
    }

    public final double getBarHeight() {
        return barHeightProperty().get();
    }

    public final void setBarHeight(double height) {
        barHeightProperty().set(height);
    }

    public final DoubleProperty textGapProperty() {
        return textGap;
    }

    public final void setTextGap(double gap) {
        textGapProperty().set(gap);
    }

    public final double getTextGap() {
        return textGapProperty().get();
    }

    public final ObjectProperty<Paint> textFillProperty() {
        return textFill;
    }

    public final void setTextFill(Paint fill) {
        Objects.nonNull(fill);
        textFillProperty().set(fill);
    }

    public final Paint getTextFill() {
        return textFillProperty().get();
    }

    public final ObjectProperty<Paint> textFillHoverProperty() {
        return textFillHover;
    }

    public final void setTextFillHover(Paint fill) {
        Objects.nonNull(fill);
        textFillHoverProperty().set(fill);
    }

    public final Paint getTextFillHover() {
        return textFillHover.get();
    }

    public final ObjectProperty<Paint> textFillHighlightProperty() {
        return textFillHighlight;
    }

    public final void setTextFillHighlight(Paint fill) {
        Objects.nonNull(fill);
        textFillHighlightProperty().set(fill);
    }

    public final Paint getTextFillHighlight() {
        return textFillHighlightProperty().get();
    }

    public final ObjectProperty<Paint> textFillPressedProperty() {
        return textFillPressed;
    }

    public final void setTextFillPressed(Paint fill) {
        Objects.nonNull(fill);
        textFillPressedProperty().set(fill);
    }

    public final Paint getTextFillPressed() {
        return textFillPressedProperty().get();
    }

    public final ObjectProperty<Paint> textFillSelectedProperty() {
        return textFillSelected;
    }

    public final void setTextFillSelected(Paint fill) {
        Objects.nonNull(fill);
        textFillSelectedProperty().set(fill);
    }

    public final Paint getTextFillSelected() {
        return textFillSelectedProperty().get();
    }

    public final ObjectProperty<Font> fontProperty() {
        return font;
    }

    public final void setFont(Font font) {
        Objects.nonNull(font);
        fontProperty().set(font);
    }

    public final Font getFont() {
        return fontProperty().get();
    }

    public final BooleanProperty glossyProperty() {
        return glossy;
    }

    public final void setGlossy(boolean glossy) {
        glossyProperty().set(glossy);
    }

    public final boolean isGlossy() {
        return glossyProperty().get();
    }

    public final BooleanProperty autoFixTextProperty() {
        return autoFixText;
    }

    public final void setAutoFixText(boolean auto) {
        autoFixTextProperty().set(auto);
    }

    public final boolean isAutoFixText() {
        return autoFixTextProperty().get();
    }
}
