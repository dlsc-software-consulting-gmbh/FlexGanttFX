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
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.util.Objects;

/**
 * Draws the time intervals that were selected by the user (or the application)
 * in the dateline.
 *
 * @param <R>
 *            the type of the rows
 *
 * @see Dateline#getSelectedIntervals()
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public class SelectedTimeIntervalsLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public SelectedTimeIntervalsLayer(GraphicsBase<R> graphics) {
		super("Selected Time Intervals", graphics);

		setSelectedTimeIntervalFill(Color.web("#7F9FBB66"));

		redrawObservable(selectedTimeIntervalFillProperty());

		fadeInOutObservable(graphics.showSelectedTimeIntervalsLayerProperty());
	}

	private final ObjectProperty<Paint> selectedTimeIntervalFill = new SimpleObjectProperty<>(this, "selectedTimeIntervalFill");

	public final ObjectProperty<Paint> selectedTimeIntervalFillProperty() {
		return selectedTimeIntervalFill;
	}

	public final Paint getSelectedTimeIntervalFill() {
		return selectedTimeIntervalFillProperty().get();
	}

	public final void setSelectedTimeIntervalFill(Paint fill) {
		Objects.requireNonNull(fill);
		selectedTimeIntervalFillProperty().set(fill);
	}

	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {
		GraphicsBase graphics = getGraphics();
		Timeline timeline = graphics.getTimeline();
		Dateline dateline = timeline.getDateline();

		GraphicsContext gc = canvas.getGraphicsContext2D();

		// draw time interval selections of the dateline
		gc.setFill(getSelectedTimeIntervalFill());
		for (TimeInterval timeInterval : dateline.getSelectedIntervals()) {
			double x1 = getLocation(timeInterval.getStartTime(), canvas);
			double x2 = getLocation(timeInterval.getEndTime(), canvas);
			gc.fillRect(x1, 0, x2 - x1, canvas.getHeight());
		}
	}
}
