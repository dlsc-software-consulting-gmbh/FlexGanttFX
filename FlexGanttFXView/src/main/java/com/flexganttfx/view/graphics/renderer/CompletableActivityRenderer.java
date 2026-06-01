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
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.activity.CompletableActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.util.Position;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CompletableActivityRenderer<A extends CompletableActivity> extends
		ActivityBarRenderer<A> {

	public CompletableActivityRenderer(GraphicsBase<?> graphics, String name) {
		super(graphics, name);

		setFillCompletion(new Color(0, 0, 0, .3));
		setFillCompletionHover(new Color(0, 0, 0, .4));
		setFillCompletionPressed(new Color(0, 0, 0, .5));
		setFillCompletionSelected(new Color(0, 0, 0, .3));
		setFillCompletionHighlight(new Color(0, 0, 0, .2));

		redrawObservable(fillCompletionProperty());
		redrawObservable(fillCompletionHoverProperty());
		redrawObservable(fillCompletionPressedProperty());
		redrawObservable(fillCompletionSelectedProperty());
		redrawObservable(fillCompletionHighlightProperty());
	}

	/**
	 * Draws the activity, including its completion overlay, and returns the resulting bounds.
	 *
	 * @param path the activity reference to render
	 * @param position the activity position
	 * @param gc the graphics context
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width
	 * @param h the height
	 * @param selected whether the activity is selected
	 * @param hover whether the activity is hovered
	 * @param highlighted whether the activity is highlighted
	 * @param pressed whether the activity is pressed
	 * @return the bounds of the rendered activity
	 */
	@Override
	protected ActivityBounds drawActivity(ActivityRef<A> path,
			Position position, GraphicsContext gc, double x, double y,
			double w, double h, boolean selected, boolean hover,
			boolean highlighted, boolean pressed) {

		ActivityBounds bounds = super.drawActivity(path, position, gc, x, y, w,
				h, selected, hover, highlighted, pressed);

		drawCompletion(path, gc, x, y, w, h, selected, hover, highlighted,
				pressed);

		return bounds;
	}

	/**
	 * Draws the completion overlay for the activity.
	 *
	 * @param activityRef the activity reference to render
	 * @param gc the graphics context
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width
	 * @param h the height
	 * @param selected whether the activity is selected
	 * @param hover whether the activity is hovered
	 * @param highlighted whether the activity is highlighted
	 * @param pressed whether the activity is pressed
	 */
	protected void drawCompletion(ActivityRef<A> activityRef,
			GraphicsContext gc, double x, double y, double w, double h,
			boolean selected, boolean hover, boolean highlighted,
			boolean pressed) {

		double percentage = activityRef.getActivity().getPercentageComplete();

		double pw = (percentage / 100) * w;
		double my = y + (h - getBarHeight()) / 2;

		gc.setFill(getFillCompletion(selected, hover, highlighted, pressed));

		if (isCornersRounded()) {
			gc.fillRoundRect(x, my, pw, getBarHeight(), getCornerRadius(),
					getCornerRadius());
		} else {
			gc.fillRect(x, my, pw, getBarHeight());
		}
	}

	/**
	 * Returns the completion fill paint for the given activity state.
	 *
	 * @param selected whether the activity is selected
	 * @param hover whether the activity is hovered
	 * @param highlighted whether the activity is highlighted
	 * @param pressed whether the activity is pressed
	 * @return the completion fill paint to use
	 */
	protected Paint getFillCompletion(boolean selected, boolean hover,
			boolean highlighted, boolean pressed) {

		if (pressed) {
			return getFillCompletionPressed();
		} else if (highlighted) {
			return getFillCompletionHighlight();
		} else if (hover) {
			return getFillCompletionHover();
		} else if (selected) {
			return getFillCompletionSelected();
		} else {
			return getFillCompletion();
		}
	}

	private final ObjectProperty<Paint> fillCompletion = new SimpleObjectProperty<>(
			this, "fillCompletion");

	/**
	 * The fillCompletion property. Defines the default fill paint for the completion overlay.
	 *
	 * @return the fillCompletion property
	 */
	public final ObjectProperty<Paint> fillCompletionProperty() {
		return fillCompletion;
	}

	public final void setFillCompletion(Paint fill) {
		fillCompletionProperty().set(fill);
	}

	public final Paint getFillCompletion() {
		return fillCompletionProperty().get();
	}

	private final ObjectProperty<Paint> fillCompletionHover = new SimpleObjectProperty<>(
			this, "fillCompletionHover");

	/**
	 * The fillCompletionHover property. Defines the completion fill paint used while an activity is hovered.
	 *
	 * @return the fillCompletionHover property
	 */
	public final ObjectProperty<Paint> fillCompletionHoverProperty() {
		return fillCompletionHover;
	}

	public final void setFillCompletionHover(Paint fill) {
		fillCompletionHoverProperty().set(fill);
	}

	public final Paint getFillCompletionHover() {
		return fillCompletionHoverProperty().get();
	}

	private final ObjectProperty<Paint> fillCompletionHighlight = new SimpleObjectProperty<>(
			this, "fillCompletionHighlight");

	/**
	 * The fillCompletionHighlight property. Defines the completion fill paint used while an activity is highlighted.
	 *
	 * @return the fillCompletionHighlight property
	 */
	public final ObjectProperty<Paint> fillCompletionHighlightProperty() {
		return fillCompletionHighlight;
	}

	public final void setFillCompletionHighlight(Paint fill) {
		fillCompletionHighlightProperty().set(fill);
	}

	public final Paint getFillCompletionHighlight() {
		return fillCompletionHighlightProperty().get();
	}

	private final ObjectProperty<Paint> fillCompletionSelected = new SimpleObjectProperty<>(
			this, "fillCompletionSelected");

	/**
	 * The fillCompletionSelected property. Defines the completion fill paint used while an activity is selected.
	 *
	 * @return the fillCompletionSelected property
	 */
	public final ObjectProperty<Paint> fillCompletionSelectedProperty() {
		return fillCompletionSelected;
	}

	public final void setFillCompletionSelected(Paint fill) {
		fillCompletionSelectedProperty().set(fill);
	}

	public final Paint getFillCompletionSelected() {
		return fillCompletionSelectedProperty().get();
	}

	private final ObjectProperty<Paint> fillCompletionPressed = new SimpleObjectProperty<>(
			this, "fillCompletionPressed");

	/**
	 * The fillCompletionPressed property. Defines the completion fill paint used while an activity is pressed.
	 *
	 * @return the fillCompletionPressed property
	 */
	public final ObjectProperty<Paint> fillCompletionPressedProperty() {
		return fillCompletionPressed;
	}

	public final void setFillCompletionPressed(Paint fill) {
		fillCompletionPressedProperty().set(fill);
	}

	public final Paint getFillCompletionPressed() {
		return fillCompletionPressedProperty().get();
	}
}
