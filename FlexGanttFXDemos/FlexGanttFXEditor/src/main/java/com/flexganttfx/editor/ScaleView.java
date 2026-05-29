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
package com.flexganttfx.editor;

import static javafx.geometry.VPos.CENTER;
import static javafx.scene.text.TextAlignment.RIGHT;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import com.flexganttfx.model.layout.AgendaLayout;

import impl.com.flexganttfx.skin.util.AgendaHelper;
import impl.com.flexganttfx.skin.util.AgendaHelper.AgendaLineLocation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * A custom control for rendering a vertical time scale. This control is used by
 * the {@link AgendaEditor} control on the left-hand side to display the time
 * range defined by the agenda layout used by the editor. This class delegates the
 * calculation of the line locations to the {@link AgendaHelper} class.
 */
public class ScaleView extends Canvas {

	private final AgendaLayout layout;

	public ScaleView(AgendaLayout layout) {
		this.layout = layout;

		setWidth(75);
		setHeight(50);

		widthProperty().addListener(evt -> draw());
		heightProperty().addListener(evt -> draw());

		layout.startTimeProperty().addListener(evt -> draw());
		layout.endTimeProperty().addListener(evt -> draw());

		draw();
	}

	public void draw() {

		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());

		gc.setFont(Font.font(10));

		DateTimeFormatter formatter = DateTimeFormatter
				.ofLocalizedTime(FormatStyle.SHORT);

		double width = getWidth();

		double padding = layout.getPadding();
		List<AgendaLineLocation> lines = AgendaHelper.getLineLocations(layout,
				padding, getHeight() - 2 * padding);

		for (AgendaLineLocation loc : lines) {

			if (!loc.isMinor()) {
				gc.setLineWidth(.5);
				gc.setStroke(Color.DARKGRAY);
				gc.strokeLine(width - 10, loc.getLocation(), width, loc.getLocation());

				gc.setFill(Color.GRAY);
				gc.setTextAlign(RIGHT);
				gc.setTextBaseline(CENTER);
				gc.fillText(formatter.format(loc.getTime()), width - 10 - 3,
						loc.getLocation());
			}
		}
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public double prefHeight(double width) {
		return getHeight();
	}

	@Override
	public double prefWidth(double height) {
		return getWidth();
	}
}
