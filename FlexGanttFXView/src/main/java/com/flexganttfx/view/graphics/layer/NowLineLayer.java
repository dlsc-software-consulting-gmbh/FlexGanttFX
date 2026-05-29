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
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.util.Objects;

/**
 * Draws a vertical line at the location of the current time / now time. The
 * current time is defined in the timeline model.
 *
 * @param <R>
 *            the type of the rows
 *
 * @see TimelineModel#getNow()
 * @see TimelineModel#getNowLocation()
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public class NowLineLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public NowLineLayer(GraphicsBase<R> graphics) {
		super("Now Line", graphics);

		strokeProperty().bindBidirectional(graphics.timeNowColorProperty());

		setLineWidth(2.5);

		redrawObservable(strokeProperty());
		redrawObservable(lineWidthProperty());

		fadeInOutObservable(graphics.showNowLineLayerProperty());
	}

	private final ObjectProperty<Paint> stroke = new SimpleObjectProperty<>(this, "stroke");

	public final ObjectProperty<Paint> strokeProperty() {
		return stroke;
	}

	public final Paint getStroke() {
		return strokeProperty().get();
	}

	public final void setStroke(Paint stroke) {
		Objects.requireNonNull(stroke);
		strokeProperty().set(stroke);
	}

	private final DoubleProperty lineWidth = new SimpleDoubleProperty(this, "lineWidth");

	public final DoubleProperty lineWidthProperty() {
		return lineWidth;
	}

	public final double getLineWidth() {
		return lineWidthProperty().get();
	}

	public final void setLineWidth(double lineWidth) {
		lineWidthProperty().set(lineWidth);
	}

	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setStroke(getStroke());
		gc.setLineWidth(getLineWidth());

		TimelineModel<?> model = canvas.getTimelineModel();
		Instant now = model.getNow();

		double nowLocation = getLocation(now, canvas);
		gc.strokeLine(nowLocation, 0, nowLocation, canvas.getHeight());
	}
}
