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
import com.flexganttfx.view.graphics.SplitPaneGraphics;
import javafx.beans.Observable;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SplitPaneGraphicsSkin<R extends Row<?, ?, ?>> extends
		GraphicsBaseSkin<SplitPaneGraphics<R>, R> {

	private final SplitPane splitPane;

	public SplitPaneGraphicsSkin(SplitPaneGraphics<R> graphics) {
		super(graphics);

		splitPane = graphics.getSplitPane();

		graphics.getRows().addListener((Observable evt) -> updateSplitPane());

		updateSplitPane();
	}

	private void updateSplitPane() {
		splitPane.getItems().clear();

		SplitPaneGraphics<R> graphics = getSkinnable();
		graphics.getRowPanes().clear();

		Callback<R, Boolean> resizableCallback = graphics
				.getResizableCallback();
		for (R row : graphics.getRows()) {
			RowPane<R> rowPane = new RowPane<>(graphics);
			rowPane.getStyleClass().add("splitpane-row-pane");
			if (resizableCallback != null) {
				Boolean resizable = resizableCallback.call(row);
				if (resizable != null) {
					SplitPane.setResizableWithParent(rowPane, resizable);
				}
			}

			rowPane.setRow(row);
			splitPane.getItems().add(rowPane);
			graphics.getRowPanes().add(rowPane);
		}
	}

	/**
	 * Creates the row pane region.
	 *
	 * @return the row pane region
	 */
	@Override
	protected Region createRowPaneRegion() {
		SplitPaneGraphics<R> graphics = getSkinnable();
		SplitPane splitPane = graphics.getSplitPane();
		splitPane.setOrientation(Orientation.VERTICAL);
		return splitPane;
	}

	/**
	 * Returns the row pane at the given y coordinate.
	 *
	 * @param y the y coordinate
	 *
	 * @return the row pane at the given y coordinate
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected RowPane<R> getRowPaneAt(double y) {
		for (Node node : splitPane.getItems()) {
			if (node instanceof RowPane<?>) {
				Point2D point = splitPane.localToScene(0, y);
				Bounds sceneNodeBounds = node.localToScene(node
						.getBoundsInLocal());
				if (sceneNodeBounds.contains(point)) {
					return (RowPane<R>) node;
				}
			}
		}

		return null;
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

	/**
	 * Finds the rows inside the lasso selection.
	 *
	 * @return the selected rows
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List<Row<?, ?, ?>> findLassoSelectedRows() {
		List<Row<?, ?, ?>> rows = new ArrayList<>();

		Rectangle lasso = getLasso();

		Bounds localLassoBounds = lasso.getBoundsInLocal();
		Bounds sceneLassoBounds = lasso.localToScene(localLassoBounds);

		splitPane.getItems().stream().filter(node -> node instanceof RowPane<?>).forEach(node -> {
			Bounds sceneNodeBounds = node.localToScene(node
					.getBoundsInLocal());
			if (sceneNodeBounds.intersects(sceneLassoBounds)) {
				RowPane<R> pane = (RowPane<R>) node;
				R row = pane.getRow();
				if (row != null) {
					rows.add(row);
				}
			}
		});

		return rows;
	}

	/**
	 * Finds the activities inside the lasso selection.
	 *
	 * @return the selected activities
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List<ActivityRef<?>> findLassoSelectedActivities() {
		List<ActivityRef<?>> selection = new ArrayList<>();

		Rectangle lasso = getLasso();

		Bounds localLassoBounds = lasso.getBoundsInLocal();
		Bounds sceneLassoBounds = lasso.localToScene(localLassoBounds);

		for (Node node : splitPane.getItems()) {
			if (node instanceof RowPane<?>) {
				Bounds sceneNodeBounds = node.localToScene(node.getBoundsInLocal());

				if (sceneNodeBounds.intersects(sceneLassoBounds)) {
					RowPane<R> rowPane = (RowPane<R>) node;
					RowCanvas<R> rowCanvas = rowPane.getCanvas();

					double x = localLassoBounds.getMinX() - getRowHeaderWidth();
					double y = Math.max(0, sceneLassoBounds.getMinY() - sceneNodeBounds.getMinY());

					double w = localLassoBounds.getWidth();
					double h = localLassoBounds.getHeight();

					if (sceneNodeBounds.getMinY() > sceneLassoBounds.getMinY()) {
						y -= (sceneNodeBounds.getMinY() - sceneLassoBounds.getMinY());
					}
					List<ActivityBounds> activityBounds = rowCanvas.getActivityBounds(x, y, w, h);

					List<ActivityRef<?>> refs = activityBounds.stream().map(ActivityBounds::getActivityRef).collect(Collectors.toList());

					selection.addAll(refs);
				}
			}
		}

		return selection;
	}
}