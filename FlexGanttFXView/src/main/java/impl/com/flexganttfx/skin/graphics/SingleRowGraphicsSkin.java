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
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.SingleRowGraphics;
import javafx.geometry.Bounds;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Graphics skin for controls that show a single row. It hosts one {@link RowPane} and maps
 * lasso selection results back to the activities of that row.
 *
 * @param <R> the type of the rows
 */
public class SingleRowGraphicsSkin<R extends Row<?, ?, ?>>
		extends GraphicsBaseSkin<SingleRowGraphics<R>, R> {

	private RowPane<R> rowPane;

	/**
	 * Constructs a new skin for the given single-row graphics.
	 *
	 * @param graphics
	 *            the graphics control
	 */
	public SingleRowGraphicsSkin(SingleRowGraphics<R> graphics) {
		super(graphics);
	}

	/**
	 * Creates the row pane region.
	 *
	 * @return the row pane region
	 */
	@Override
	protected final Region createRowPaneRegion() {
		rowPane = new RowPane<>(getSkinnable());
		getSkinnable().prefHeightProperty().bind(rowPane.prefHeightProperty());
		R row = getSkinnable().getRows().get(0);
		rowPane.setRow(row);
		rowPane.getStyleClass().add("single-row-pane");
		getSkinnable().getRowPanes().add(rowPane);
		return rowPane;
	}

	/**
	 * Finds the rows inside the lasso selection.
	 *
	 * @return the selected rows
	 */
	@Override
	protected final List<Row<?, ?, ?>> findLassoSelectedRows() {
		List<Row<?, ?, ?>> list = new ArrayList<>();
		R row = rowPane.getRow();
		if (row != null) {
			list.add(row);
		}
		return list;
	}

	/**
	 * Finds the activities inside the lasso selection.
	 *
	 * @return the selected activities
	 */
	@Override
	protected final List<ActivityRef<?>> findLassoSelectedActivities() {
		Rectangle lasso = getLasso();
		Bounds lassoBounds = lasso.getBoundsInLocal();

		RowCanvas<R> canvas = rowPane.getCanvas();

		List<ActivityBounds> selections = canvas.getActivityBounds(
				lasso.getBoundsInLocal().getMinX() - getRowHeaderWidth(),
				Math.max(0, lassoBounds.getMinY()),
				lasso.getBoundsInLocal().getWidth(),
				lasso.getBoundsInLocal().getHeight());

		return selections.stream().map(ActivityBounds::getActivityRef).collect(Collectors.toList());
	}

	/**
	 * Returns the row pane at the given y coordinate.
	 *
	 * @param y the y coordinate
	 *
	 * @return the row pane at the given y coordinate
	 */
	@Override
	protected final RowPane<R> getRowPaneAt(double y) {
		return rowPane;
	}

	/**
	 * Returns whether the given row is above the viewport.
	 *
	 * @param row the row
	 *
	 * @return true if the row is above the viewport
	 */
	@Override
	protected boolean isRowAboveViewport(R row) {
		return false;
	}
}
