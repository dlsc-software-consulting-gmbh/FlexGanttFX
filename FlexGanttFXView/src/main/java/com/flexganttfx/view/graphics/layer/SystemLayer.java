/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.RendererBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.WeakChangeListener;
import javafx.util.Duration;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * System layers are used in the background and foreground of each row. A
 * background layer gets drawn <u>before</u> the activities are drawn while a
 * foreground layer gets drawn <u>after</u> the activities are drawn. Each layer
 * is specialized on drawing one type of information: current time, selected
 * time intervals, grid lines, and so on. The graphics view manages the layers
 * in two lists and provides convenience methods to easily look them up.
 *
 * @param <R> the type of the rows
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public abstract class SystemLayer<R extends Row<?, ?, ?>> extends RendererBase {

	private final ChangeListener<Boolean> fadeInOutListener = (observable, oldVisibility, newVisibility) -> {
		if (newVisibility) {
			fade(1);
		} else {
			fade(0);
		}
	};

	private final ChangeListener<Boolean> weakFadeInOutListener = new WeakChangeListener<>(fadeInOutListener);

	/**
	 * Constructs a new system layer for the given graphics view.
	 *
	 * @param name the layer name
	 * @param graphicsView the owning graphics view
	 */
	public SystemLayer(String name, GraphicsBase<R> graphicsView) {
		super(graphicsView, name);

		redrawObservable(visibleProperty());
		redrawObservable(opacityProperty());
	}

	/**
	 * Registers an observable that triggers fade animations when its value changes.
	 *
	 * @param observable the visibility observable to monitor
	 */
	protected void fadeInOutObservable(ObservableBooleanValue observable) {
		requireNonNull(observable);
		observable.addListener(weakFadeInOutListener);

		if (!observable.get()) {
			opacity.set(0);
		}
	}

	private void fade(double opacityTarget) {
		if (getGraphics().isFadeInOutVisibilityChanges()) {
			KeyValue keyValue = new KeyValue(opacity, opacityTarget);
			KeyFrame keyFrame = new KeyFrame(Duration.millis(getGraphics().getFadeInOutVisibilityChangesDuration()), keyValue);
			Timeline timeline = new Timeline(keyFrame);
			timeline.play();
		} else {
			opacity.set(opacityTarget);
		}
	}

	private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible", true);

	/**
	 * The visible property. Controls whether the layer is rendered.
	 *
	 * @return the visible property
	 */
	public final BooleanProperty visibleProperty() {
		return visible;
	}

	public final boolean isVisible() {
		return visibleProperty().get();
	}

	public final void setVisible(boolean visible) {
		visibleProperty().set(visible);
	}

	private final ReadOnlyDoubleWrapper opacity = new ReadOnlyDoubleWrapper(this, "opacity", 1);

	/**
	 * The opacity property. Exposes the opacity used to paint the layer.
	 *
	 * @return the opacity property
	 */
	public final ReadOnlyDoubleProperty opacityProperty() {
		return opacity.getReadOnlyProperty();
	}

	public final double getOpacity() {
		return opacityProperty().get();
	}

	/**
	 * Draws the layer for the given row canvas and visible time range.
	 *
	 * @param canvas the canvas to draw on
	 * @param startTime the visible start time
	 * @param endTime the visible end time
	 */
	public abstract void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime);
}
