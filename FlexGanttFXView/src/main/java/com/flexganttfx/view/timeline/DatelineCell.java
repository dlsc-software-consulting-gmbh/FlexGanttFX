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

    /**
     * Constructs a new dateline cell.
     */
    protected DatelineCell() {
        setMouseTransparent(true);

        text = new Text() {
            /**
             * Returns whether the embedded text node can be resized.
             *
             * @return {@code true}
             */
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

    /**
     * Updates the cell to represent the given interval and scale position.
     *
     * @param startTime the interval start time
     * @param endTime the interval end time
     * @param resolution the resolution shown by the cell
     * @param dateline the owning dateline
     * @param position the scale position of the cell
     */
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

    /**
     * Lays out the cell label within the available bounds.
     */
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

    /**
     * Computes the preferred width of the cell.
     *
     * @param height the available height
     * @return the preferred width
     */
    @Override
    protected double computePrefWidth(double height) {
        return text.prefWidth(-1) + getInsets().getLeft() + getInsets().getRight();
    }

    /**
     * Computes the preferred height of the cell.
     *
     * @param width the available width
     * @return the preferred height
     */
    @Override
    protected double computePrefHeight(double width) {
        return text.prefHeight(-1) + getInsets().getTop() + getInsets().getBottom();
    }

    /**
     * Sets the text shown by the cell.
     *
     * @param txt the text to display
     */
    protected void setText(String txt) {
        text.setText(txt);
    }

    /**
     * Returns the resolution currently shown by the cell.
     *
     * @return the displayed resolution
     */
    public final Resolution<T> getResolution() {
        return resolution;
    }

    /**
     * Returns the dateline that owns the cell.
     *
     * @return the owning dateline
     */
    public final Dateline getDateline() {
        return dateline;
    }

    /**
     * Returns the start time shown by the cell.
     *
     * @return the interval start time
     */
    public final Instant getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time shown by the cell.
     *
     * @return the interval end time
     */
    public final Instant getEndTime() {
        return endTime;
    }

    /**
     * Returns the time interval represented by the cell.
     *
     * @return the represented time interval
     */
    public final TimeInterval getInterval() {
        return new TimeInterval(getStartTime(), getEndTime());
    }

    /**
     * Returns the scale position represented by the cell.
     *
     * @return the scale position
     */
    public final Position getScalePosition() {
        return scalePosition;
    }
}
