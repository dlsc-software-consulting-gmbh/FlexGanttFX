/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.timeline;

import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.dateline.Resolution.Position;
import com.flexganttfx.model.util.TimeInterval;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
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
	private Text text;
	private Position scalePosition;

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

		this.startTime = startTime;
		this.endTime = endTime;
		this.dateline = dateline;
		this.resolution = resolution;
		this.scalePosition = position;
	}

	@Override
	protected void layoutChildren() {
		Insets insets = getInsets();

		double w = getWidth() - insets.getLeft() - insets.getRight();
		double h = getHeight() - insets.getTop() - insets.getBottom();

		double prefWidth = text.prefWidth(h);
		double prefHeight = text.prefHeight(-1);

		text.resizeRelocate(insets.getLeft(), h / 2 - prefHeight / 2, w, h);

		/*
		 * Text is longer than the available space. We need to adjust its position.
		 */
		if (prefWidth > w) {
			if (getStyleClass().contains("dateline-cell-first")) {
				text.resizeRelocate(getWidth() - prefWidth - insets.getRight(), h / 2 - prefHeight / 2, prefWidth, h);
			} else if (getStyleClass().contains("dateline-cell-last")) {
				text.resizeRelocate(insets.getLeft(), h / 2 - prefHeight / 2, w, h);
			}
		}

		setClip(new Rectangle(0, 0, getWidth(), getHeight()));
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
