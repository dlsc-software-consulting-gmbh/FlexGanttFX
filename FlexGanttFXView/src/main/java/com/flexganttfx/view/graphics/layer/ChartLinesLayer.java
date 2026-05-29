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

import com.flexganttfx.model.Layout;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.property.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * Draws the horizontal grid lines for a row if the row or any of its inner
 * lines are using the chart layout (see {@link ChartLayout}).
 *
 * @param <R>
 *            the type of the rows
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public class ChartLinesLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public ChartLinesLayer(GraphicsBase<R> graphics) {
		super("Chart Lines", graphics);

		setMajorLinesLineWidth(0.5);
		setMajorLinesStroke(Color.GRAY);

		setMinorLinesLineWidth(0.5);
		setMinorLinesStroke(Color.LIGHTGRAY);

		// major properties
		redrawObservable(majorLinesLineWidth);
		redrawObservable(majorLinesStroke);
		redrawObservable(majorLinesVisible);
		redrawObservable(majorLineDashes);

		// minor properties
		redrawObservable(minorLinesLineWidth);
		redrawObservable(minorLinesStroke);
		redrawObservable(minorLinesVisible);
		redrawObservable(minorLineDashes);

		fadeInOutObservable(graphics.showChartLinesLayerProperty());
	}

	private final BooleanProperty majorLinesVisible = new SimpleBooleanProperty(this,
			"majorLinesVisible", true);

	public final BooleanProperty majorLinesVisibleProperty() {
		return majorLinesVisible;
	}

	public final void setMajorLinesVisible(boolean visible) {
		majorLinesVisibleProperty().set(visible);
	}

	public final boolean isMajorLinesVisible() {
		return majorLinesVisibleProperty().get();
	}

	private final ObjectProperty<Paint> majorLinesStroke = new SimpleObjectProperty<>(
			this, "majorLinesStroke");

	public final ObjectProperty<Paint> majorLinesStrokeProperty() {
		return majorLinesStroke;
	}

	public final Paint getMajorLinesStroke() {
		return majorLinesStrokeProperty().get();
	}

	public final void setMajorLinesStroke(Paint stroke) {
		requireNonNull(stroke);
		majorLinesStrokeProperty().set(stroke);
	}

	private final DoubleProperty majorLinesLineWidth = new SimpleDoubleProperty(
			this, "majorLinesLineWidth");

	public final DoubleProperty majorLinesLineWidthProperty() {
		return majorLinesLineWidth;
	}

	public final double getMajorLinesLineWidth() {
		return majorLinesLineWidthProperty().get();
	}

	public final void setMajorLinesLineWidth(double lineWidth) {
		majorLinesLineWidthProperty().set(lineWidth);
	}

	private final BooleanProperty minorLinesVisible = new SimpleBooleanProperty(this,
			"minorLinesVisible", true);

	public final BooleanProperty minorLinesVisibleProperty() {
		return minorLinesVisible;
	}

	public final void setMinorLinesVisible(boolean visible) {
		minorLinesVisibleProperty().set(visible);
	}

	public final boolean isMinorLinesVisible() {
		return minorLinesVisibleProperty().get();
	}

	private final ObjectProperty<Paint> minorLinesStroke = new SimpleObjectProperty<>(
			this, "minorLinesStroke");

	public final ObjectProperty<Paint> minorLinesStrokeProperty() {
		return minorLinesStroke;
	}

	public final Paint getMinorLinesStroke() {
		return minorLinesStrokeProperty().get();
	}

	public final void setMinorLinesStroke(Paint stroke) {
		requireNonNull(stroke);
		minorLinesStrokeProperty().set(stroke);
	}

	private final DoubleProperty minorLinesLineWidth = new SimpleDoubleProperty(
			this, "minorLinesLineWidth");

	public final DoubleProperty minorLinesLineWidthProperty() {
		return minorLinesLineWidth;
	}

	public final double getMinorLinesLineWidth() {
		return minorLinesLineWidthProperty().get();
	}

	public final void setMinorLinesLineWidth(double lineWidth) {
		minorLinesLineWidthProperty().set(lineWidth);
	}

	private final ObjectProperty<double[]> majorLineDashes = new SimpleObjectProperty<>(this, "majorLineDashes");

	public final ObjectProperty<double[]> majorLineDashesProperty() {
		return majorLineDashes;
	}

	public final void setMajorLineDashes(double... dashes) {
		majorLineDashesProperty().set(dashes);
	}

	public final double[] getMajorLineDashes() {
		return majorLineDashesProperty().get();
	}

	private final ObjectProperty<double[]> minorLineDashes = new SimpleObjectProperty<>(this, "minorLineDashes", new double[]{4, 4});

	public final ObjectProperty<double[]> minorLineDashesProperty() {
		return minorLineDashes;
	}

	public final void setMinorLineDashes(double... dashes) {
		minorLineDashesProperty().set(dashes);
	}

	public final double[] getMinorLineDashes() {
		return minorLineDashesProperty().get();
	}

	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime,
			Instant endTime) {

		Row<?, ?, ?> row = canvas.getRow();
		if (row != null) {
			GraphicsContext gc = canvas.getGraphicsContext2D();

			/*
			 * looking up xOffset and width / height several times is not ideal but we want to make sure it
			 * does not get calculated if neither the row nor the lines are using a chart
			 * layout.
			 */
			Layout layout = row.getLayout();
			if (layout instanceof ChartLayout) {
				double height = canvas.getHeight();
				double width = canvas.getWidth();
				drawLines(0, 0, height, width, gc, (ChartLayout) layout);
			}

			int lineCount = row.getLineCount();
			for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
				layout = row.getLineLayout(lineIndex);
				if (layout instanceof ChartLayout) {
					double lineLocation = row.getLineLocation(lineIndex);
					double lineHeight = row.getLineHeight(lineIndex);
					double width = canvas.getWidth();
					drawLines(0, lineLocation, lineHeight, width, gc, (ChartLayout) layout);
				}
			}
		}
	}

	private void drawLines(double xOffset, double yOffset, double height,
			double width, GraphicsContext gc, ChartLayout layout) {

		double padding = layout.getPadding();
		height -= (padding * 2);

		double minChartValue = layout.getMinValue();
		double maxChartValue = layout.getMaxValue();

		double range = maxChartValue - minChartValue;
		double ppv = height / range;

		double zeroLineLocation = yOffset + padding + layout.getMaxValue()
				* ppv;

		// draw minor lines

		gc.setStroke(getMinorLinesStroke());
		gc.setLineWidth(getMinorLinesLineWidth());
		gc.setLineDashes(getMinorLineDashes());

		for (double value : layout.getMinorTicks()) {
			double y = ((int) (zeroLineLocation - value * ppv)) + .5;
			gc.strokeLine(xOffset, y, width, y);
		}

		// draw major lines

		gc.setStroke(getMajorLinesStroke());
		gc.setLineWidth(getMajorLinesLineWidth());
		gc.setLineDashes(getMajorLineDashes());

		for (double value : layout.getMajorTicks()) {
			double y = ((int) (zeroLineLocation - value * ppv)) + .5;
			gc.strokeLine(xOffset, y, width, y);
		}
	}
}
