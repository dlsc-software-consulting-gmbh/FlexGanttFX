/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Window;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * The base class of all renderers used by FlexGanttFX.
 */
public abstract class RendererBase {

    private final String name;
    private final GraphicsBase<?> graphics;

    /**
     * Constructs a new renderer.
     *
     * @param graphics the graphics control where the renderer will be used
     * @param name the name of the renderer (useful for tooling)
     */
    public RendererBase(GraphicsBase<?> graphics, String name) {
        this.name = Objects.requireNonNull(name);
        this.graphics = Objects.requireNonNull(graphics);

        // Listener support / Redraw

        redrawObservable(enabled);
        redrawObservable(snapToPixel);
        redrawObservable(alpha);
    }

    /**
     * If this renderer's snapToPixel property is true, returns a value rounded
     * to the nearest pixel in the horizontal direction, else returns the
     * same value.
     * @param value the space value to be snapped
     * @return value rounded to nearest pixel
     */
    public double snapSpaceX(double value) {
        return snapSpaceX(value, isSnapToPixel());
    }

    /**
     * If this renderer's snapToPixel property is true, returns a value rounded
     * to the nearest pixel in the vertical direction, else returns the
     * same value.
     * @param value the space value to be snapped
     * @return value rounded to nearest pixel
     */
    public double snapSpaceY(double value) {
        return snapSpaceY(value, isSnapToPixel());
    }

    /**
     * If this renderer's snapToPixel property is true, returns a value ceiled
     * to the nearest pixel in the horizontal direction, else returns the
     * same value.
     * @param value the size value to be snapped
     * @return value ceiled to nearest pixel
     */
    public double snapSizeX(double value) {
        return snapSizeX(value, isSnapToPixel());
    }

    /**
     * If this renderer's snapToPixel property is true, returns a value ceiled
     * to the nearest pixel in the vertical direction, else returns the
     * same value.
     * @param value the size value to be snapped
     * @return value ceiled to nearest pixel
     */
    public double snapSizeY(double value) {
        return snapSizeY(value, isSnapToPixel());
    }

    /**
     * If this renderer's snapToPixel property is true, returns a value rounded
     * to the nearest pixel in the horizontal direction, else returns the
     * same value.
     * @param value the position value to be snapped
     * @return value rounded to nearest pixel
     */
    public double snapPositionX(double value) {
        return snapPositionX(value, isSnapToPixel());
    }

    /**
     * If this renderer's snapToPixel property is true, returns a value rounded
     * to the nearest pixel in the vertical direction, else returns the
     * same value.
     * @param value the position value to be snapped
     * @return value rounded to nearest pixel
     */
    public double snapPositionY(double value) {
        return snapPositionY(value, isSnapToPixel());
    }

    private static double getSnapScaleXImpl(Scene scene) {
        if (scene == null) return 1.0;
        Window window = scene.getWindow();
        if (window == null) return 1.0;
        return window.getRenderScaleX();
    }

    private static double getSnapScaleYImpl(Scene scene) {
        if (scene == null) return 1.0;
        Window window = scene.getWindow();
        if (window == null) return 1.0;
        return window.getRenderScaleY();
    }

    private double getSnapScaleX() {
        return getSnapScaleXImpl(graphics.getScene());
    }

    private double getSnapScaleY() {
        return getSnapScaleYImpl(graphics.getScene());
    }

    private double scaledRound(double value, double scale) {
        return Math.round(value * scale) / scale;
    }

    private double scaledCeil(double value, double scale) {
        return Math.ceil(value * scale) / scale;
    }

    /**
     * If snapToPixel is true, then the value is rounded using Math.round. Otherwise,
     * the value is simply returned. This method will surely be JIT'd under normal
     * circumstances, however on an interpreter it would be better to inline this
     * method. However the use of Math.round here, and Math.ceil in snapSize is
     * not obvious, and so for code maintenance this logic is pulled out into
     * a separate method.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or rounded based on snapToPixel
     */
    private double snapSpaceX(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleX()) : value;
    }

    private double snapSpaceY(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleY()) : value;
    }

    /**
     * If snapToPixel is true, then the value is ceil'd using Math.ceil. Otherwise,
     * the value is simply returned.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or ceil'd based on snapToPixel
     */
    private double snapSizeX(double value, boolean snapToPixel) {
        return snapToPixel ? scaledCeil(value, getSnapScaleX()) : value;
    }

    private double snapSizeY(double value, boolean snapToPixel) {
        return snapToPixel ? scaledCeil(value, getSnapScaleY()) : value;
    }

    /**
     * If snapToPixel is true, then the value is rounded using Math.round. Otherwise,
     * the value is simply returned.
     *
     * @param value The value that needs to be snapped
     * @param snapToPixel Whether to snap to pixel
     * @return value either as passed in or rounded based on snapToPixel
     */
    private double snapPositionX(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleX()) : value;
    }

    private double snapPositionY(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleY()) : value;
    }

    private boolean disableRedrawAfterPropertyChange;

    /**
     * Disables automatic redrawing of the graphics area when one of the
     * observed properties changes. Useful for not going into infinite loops.
     */
    protected final void disableRedrawAfterPropertyChange() {
        disableRedrawAfterPropertyChange = true;
    }

    /**
     * Enabled automatic redrawing of the graphics area when one of the
     * observed properties changes. Useful for not going into infinite loops.
     */
    protected final void enableRedrawAfterPropertyChange() {
        disableRedrawAfterPropertyChange = false;
    }

    private final InvalidationListener redrawListener = observable -> {
        if (!disableRedrawAfterPropertyChange) {
            getGraphics().redraw("renderer triggered redraw");
        }
    };

    /**
     * Registers the given observable as something that requires
     * a redraw of the graphics area. E.g.: the stroke color has
     * changed.
     *
     * @param observable the observable to monitor for changes
     */
    protected void redrawObservable(Observable observable) {
        requireNonNull(observable);
        observable.addListener(redrawListener);
    }

    public final String getName() {
        return name;
    }

    public final GraphicsBase<?> getGraphics() {
        return graphics;
    }

    /**
     * Calculates the x coordinate for the given time. This method only returns
     * valid results when the renderer is used in a layout with horizontal
     * orientation. It will not work in {@link AgendaLayout}.
     *
     * @param time
     *            the time for which to calculate the x coordinate
     * @return the location of the given time point
     * @see TimelineModel#calculateLocationForTime(Instant)
     * @since 1.0
     */
    protected final double getLocation(Instant time, Canvas canvas) {
        Timeline timeline = getGraphics().getTimeline();
        TimelineModel<?> timelineModel = timeline.getModel();
        return timelineModel.calculateLocationForTime(time) + getGraphics().getCanvasBuffer() - canvas.getTranslateX() - timeline.getOffset();
    }

    /**
     * Calculates the time at the given x coordinate. This method only returns
     * valid results when the renderer is used in a layout with horizontal
     * orientation. It will not work in {@link AgendaLayout}.
     *
     * @param location the location for which to return the time
     * @return the time at the given x coordinate
     * @see TimelineModel#calculateTimeForLocation(double)
     * @since 1.0
     */
    protected final Instant getTimeAt(double location) {
        Timeline timeline = getGraphics().getTimeline();
        TimelineModel<?> timelineModel = timeline.getModel();
        return timelineModel.calculateTimeForLocation(location + timeline.getOffset());
    }

    // enabled

	private final BooleanProperty enabled = new SimpleBooleanProperty(this, "enabled", true);

	public final BooleanProperty enabledProperty() {
        return enabled;
    }

	public final boolean isEnabled() {
		return enabled.get();
	}

	public final void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	// snap to pixel

	private final BooleanProperty snapToPixel = new SimpleBooleanProperty(this, "snapToPixel", true);

    public final BooleanProperty snapToPixelProperty() {
        return snapToPixel;
    }

	public final void setSnapToPixel(boolean snap) {
		snapToPixel.set(snap);
	}

	public final boolean isSnapToPixel() {
		return snapToPixel.get();
	}

	// alpha

	private final DoubleProperty alpha = new SimpleDoubleProperty(this, "alpha", 1);

    public final DoubleProperty alphaProperty() {
        return alpha;
    }

	public final void setAlpha(double alpha) {
		this.alpha.set(alpha);
	}

	public final double getAlpha() {
		return alpha.get();
	}
}
