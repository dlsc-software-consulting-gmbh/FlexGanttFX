/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.timeline;

import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.dateline.Resolution.Position;
import com.flexganttfx.model.util.TimeInterval;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.Instant;
import java.time.temporal.TemporalUnit;

/**
 * Each row / scale in the {@link Dateline} consists of several cells. A cell is
 * a region with a child node of type {@link Text}.<br>
 * <br>
 *
 * <img src="doc-files/dateline-cell.png" alt="Dateline Cell">
 *
 * @param <T>
 *            the type of the temporal unit, e.g. ChronoUnit / SimpleUnit
 *
 * @since 1.0
 */
public abstract class DatelineCell<T extends TemporalUnit> extends Region {

    private static final String DEFAULT_STYLE_CLASS = "dateline-cell";

    private Resolution<T> resolution;
    private Instant startTime;
    private Instant endTime;

    private Dateline dateline;
    private final Text text;
    private Position scalePosition;

    private final InvalidationListener layoutListener = it -> requestLayout();

    private final WeakInvalidationListener weakLayoutListener = new WeakInvalidationListener(layoutListener);

    protected DatelineCell() {
        setMouseTransparent(true);

        text = new Text() {
            @Override
            public boolean isResizable() {
                return true;
            }
        };

        text.setTextOrigin(VPos.CENTER);
        text.setTextAlignment(TextAlignment.LEFT);
        text.getStyleClass().add("text");
        text.setManaged(false);

        getChildren().add(text);
    }

    public void update(Instant startTime, Instant endTime, Resolution<T> resolution, Dateline dateline, Position position) {

        // "dateline-cell, bottom, hours"
        getStyleClass().setAll(DEFAULT_STYLE_CLASS, position.name().toLowerCase(), resolution.getTemporalUnit().toString().toLowerCase());

        if (this.dateline == null && dateline != null) {
        	// we only want to attach listeners
            dateline.translateXProperty().addListener(weakLayoutListener);
			visibleProperty().addListener(weakLayoutListener);
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.dateline = dateline;
        this.resolution = resolution;

        scalePosition = position;
    }

    private double getEffectiveX() {
        return getLayoutX() - dateline.getDatelineBuffer() + dateline.getTranslateX();
    }

    @Override
    protected void layoutChildren() {
        Insets insets = getInsets();

        double w = getWidth() - insets.getLeft() - insets.getRight();
        double h = getHeight() - insets.getTop() - insets.getBottom();

        double prefWidth = text.prefWidth(h);
        double prefHeight = text.prefHeight(-1);

		double usableWidth = w;

        double effectiveX = getEffectiveX();
        if (effectiveX < 0) {
			usableWidth = usableWidth + effectiveX;
		}

        // the "first cell" pushes the text to the right, so that it remains visible as long as possible
        boolean firstCell = effectiveX < 0;

        if (firstCell) {
            text.relocate(Math.min(w - usableWidth, w - prefWidth) + insets.getLeft(), h / 2 - prefHeight / 2);
        } else {
            text.relocate(insets.getLeft(), h / 2 - prefHeight / 2);
        }
    }

    @Override
    protected double computePrefWidth(double height) {
        return text.prefWidth(-1) + getInsets().getLeft() + getInsets().getRight();
    }

    @Override
    protected double computePrefHeight(double width) {
        return text.prefHeight(-1) + getInsets().getTop() + getInsets().getBottom();
    }

    protected void setText(String txt) {
        text.setText(txt);
    }

    public final Resolution<T> getResolution() {
        return resolution;
    }

    public final Dateline getDateline() {
        return dateline;
    }

    public final Instant getStartTime() {
        return startTime;
    }

    public final Instant getEndTime() {
        return endTime;
    }

    public final TimeInterval getInterval() {
        return new TimeInterval(getStartTime(), getEndTime());
    }

    public final Position getScalePosition() {
        return scalePosition;
    }
}
