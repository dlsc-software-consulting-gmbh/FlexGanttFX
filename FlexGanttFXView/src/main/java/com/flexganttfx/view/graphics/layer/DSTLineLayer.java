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
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.zone.ZoneOffsetTransition;
import java.util.Objects;

/**
 * Draws a vertical line at the location of the next daylight savings time change.
 *
 * @param <R>
 *            the type of the rows
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 8.8
 */
public class DSTLineLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	/**
	 * Constructs a new daylight-saving marker layer.
	 *
	 * @param graphics the graphics view that owns the layer
	 */
	public DSTLineLayer(GraphicsBase<R> graphics) {
		super("DST Line", graphics);

		setStroke(Color.DARKORANGE);
		setLineWidth(2.5);

		redrawObservable(strokeProperty());
		redrawObservable(lineWidthProperty());

		fadeInOutObservable(graphics.showDSTLineLayerProperty());
	}

	private final ObjectProperty<Paint> stroke = new SimpleObjectProperty<>(this, "stroke");

	/**
	 * The stroke property. Stores the paint used for the daylight-saving marker.
	 *
	 * @return the stroke property
	 */
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

	/**
	 * The lineWidth property. Stores the stroke width of the daylight-saving marker.
	 *
	 * @return the lineWidth property
	 */
	public final DoubleProperty lineWidthProperty() {
		return lineWidth;
	}

	public final double getLineWidth() {
		return lineWidthProperty().get();
	}

	public final void setLineWidth(double lineWidth) {
		lineWidthProperty().set(lineWidth);
	}

	/**
	 * Draws a marker at the next daylight-saving transition when applicable.
	 *
	 * @param canvas the canvas to draw on
	 * @param startTime the visible start time
	 * @param endTime the visible end time
	 */
	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setStroke(getStroke());
		gc.setLineWidth(getLineWidth());

		TimelineModel<?> model = canvas.getTimelineModel();
		Instant now = model.getNow();

		ZoneId zoneId;

		final Timeline timeline = canvas.getGraphics().getTimeline();
		final Dateline dateline = timeline.getDateline();
		final TemporalUnit unit = dateline.getPrimaryTemporalUnit();

		if (unit != null && (unit.equals(ChronoUnit.HOURS) || unit.equals(ChronoUnit.MINUTES))) {
			R row = canvas.getRow();
			if (row != null) {
				zoneId = row.getZoneId();
			} else {
				zoneId = dateline.getZoneId();
			}

			if (zoneId != null) {
				final ZoneOffsetTransition transition = zoneId.getRules().nextTransition(startTime);
				if (transition != null) {
					double location = getLocation(transition.getInstant(), canvas);
					gc.strokeLine(location, 0, location, canvas.getHeight());
				}
			}
		}
	}
}
