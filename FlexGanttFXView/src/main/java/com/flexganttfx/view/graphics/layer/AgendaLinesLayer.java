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
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import impl.com.flexganttfx.skin.util.AgendaHelper;
import impl.com.flexganttfx.skin.util.AgendaHelper.AgendaLineLocation;
import javafx.beans.property.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.time.Instant;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Draws the horizontal grid lines for a row if the row or any of its inner
 * lines are using the agenda layout.
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
public class AgendaLinesLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	/**
	 * Constructs a new agenda lines layer.
	 *
	 * @param graphics the graphics view that owns the layer
	 */
	public AgendaLinesLayer(GraphicsBase<R> graphics) {
		super("Agenda Lines", graphics);

		setMajorLinesLineWidth(0.5);
		setMajorLinesStroke(Color.GRAY);

		setMinorLinesLineWidth(0.5);
		setMinorLinesStroke(Color.LIGHTGRAY);

		redrawObservable(majorLinesLineWidth);
		redrawObservable(majorLinesStroke);
		redrawObservable(majorLinesVisible);
		redrawObservable(minorLinesLineWidth);
		redrawObservable(minorLinesStroke);
		redrawObservable(minorLinesVisible);

		fadeInOutObservable(graphics.showAgendaLinesLayerProperty());
	}

	private final BooleanProperty majorLinesVisible = new SimpleBooleanProperty(this,
			"majorLinesVisible", true);

	/**
	 * The majorLinesVisible property. Controls whether major agenda lines are drawn.
	 *
	 * @return the majorLinesVisible property
	 */
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

	/**
	 * The majorLinesStroke property. Stores the stroke used for major agenda lines.
	 *
	 * @return the majorLinesStroke property
	 */
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

	/**
	 * The majorLinesLineWidth property. Stores the line width used for major agenda lines.
	 *
	 * @return the majorLinesLineWidth property
	 */
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

	/**
	 * The minorLinesVisible property. Controls whether minor agenda lines are drawn.
	 *
	 * @return the minorLinesVisible property
	 */
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

	/**
	 * The minorLinesStroke property. Stores the stroke used for minor agenda lines.
	 *
	 * @return the minorLinesStroke property
	 */
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

	/**
	 * The minorLinesLineWidth property. Stores the line width used for minor agenda lines.
	 *
	 * @return the minorLinesLineWidth property
	 */
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

	/**
	 * The majorLineDashes property. Stores the dash pattern used for major agenda lines.
	 *
	 * @return the majorLineDashes property
	 */
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

	/**
	 * The minorLineDashes property. Stores the dash pattern used for minor agenda lines.
	 *
	 * @return the minorLineDashes property
	 */
	public final ObjectProperty<double[]> minorLineDashesProperty() {
		return minorLineDashes;
	}

	public final void setMinorLineDashes(double... dashes) {
		minorLineDashesProperty().set(dashes);
	}

	public final double[] getMinorLineDashes() {
		return minorLineDashesProperty().get();
	}

	/**
	 * Draws agenda grid lines for the row and its inner line layouts.
	 *
	 * @param canvas the canvas to draw on
	 * @param startTime the visible start time
	 * @param endTime the visible end time
	 */
	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime, Instant endTime) {

		final double height = canvas.getHeight();
		final double width = canvas.getWidth();

		final Row<?, ?, ?> row = canvas.getRow();

		if (row != null) {
			GraphicsContext gc = canvas.getGraphicsContext2D();
			Layout layout = row.getLayout();
			if (layout instanceof AgendaLayout) {
				drawLines(0, 0, height, width, gc, (AgendaLayout) layout);
			}

			int lineCount = row.getLineCount();
			for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
				layout = row.getLineLayout(lineIndex);
				if (layout instanceof AgendaLayout) {
					double lineLocation = row.getLineLocation(lineIndex);
					double lineHeight = row.getLineHeight(lineIndex);
					drawLines(0, lineLocation, lineHeight, width, gc, (AgendaLayout) layout);
				}
			}
		}
	}

	private void drawLines(double xOffset, double yOffset, double height, double width, GraphicsContext gc, AgendaLayout layout) {

		yOffset += layout.getPadding();
		height -= (2 * layout.getPadding());

		List<AgendaLineLocation> locations = AgendaHelper.getLineLocations(layout, yOffset, height);

		for (AgendaLineLocation loc : locations) {

			if (isMajorLinesVisible() && !loc.isMinor()) {
				gc.setLineWidth(getMajorLinesLineWidth());
				gc.setLineDashes(getMajorLineDashes());
				gc.setStroke(getMajorLinesStroke());
				gc.strokeLine(xOffset, loc.getLocation(), width, loc.getLocation());
			}

			if (isMinorLinesVisible() && loc.isMinor()) {
				gc.setLineWidth(getMinorLinesLineWidth());
				gc.setLineDashes(getMinorLineDashes());
				gc.setStroke(getMinorLinesStroke());
				gc.strokeLine(xOffset, loc.getLocation(), width, loc.getLocation());
			}
		}
	}
}
