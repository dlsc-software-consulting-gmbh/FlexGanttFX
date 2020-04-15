/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.VBoxGraphics;
import javafx.beans.Observable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VBoxGraphicsSkin<R extends Row<?, ?, ?>> extends GraphicsBaseSkin<VBoxGraphics<R>, R> {

	private VBox vbox;

	public VBoxGraphicsSkin(VBoxGraphics<R> graphics) {
		super(graphics);

		graphics.getRows().addListener((Observable evt) -> updateBox());
		graphics.priorityCallbackProperty().addListener(evt -> updateBox());

		updateBox();
	}

	private void updateBox() {
		vbox.getChildren().clear();

		VBoxGraphics<R> graphics = getSkinnable();
		graphics.getRowPanes().clear();

		Callback<R, Priority> priorityCallback = graphics.getPriorityCallback();
		for (R row : graphics.getRows()) {
			RowPane<R> rowPane = new RowPane<>(graphics);
			rowPane.getStyleClass().add("vbox-row-pane");

			if (priorityCallback != null) {
				Priority priority = priorityCallback.call(row);
				VBox.setVgrow(rowPane, priority);
			}

			rowPane.setRow(row);
			vbox.getChildren().add(rowPane);
			graphics.getRowPanes().add(rowPane);
		}
	}

	@Override
	protected Region createRowPaneRegion() {
		VBoxGraphics<R> graphics = getSkinnable();
		vbox = graphics.getVBox();
		vbox.setFillWidth(true);
		return vbox;
	}

	@Override
	protected RowPane<R> getRowPaneAt(double y) {
		for (Node node : vbox.getChildren()) {
			if (node instanceof RowPane<?>) {
				Point2D point = vbox.localToScene(0, y);
				Bounds sceneNodeBounds = node.localToScene(node.getBoundsInLocal());
				if (sceneNodeBounds.contains(point)) {
					return (RowPane<R>) node;
				}
			}
		}

		return null;
	}

	@Override
	protected boolean isRowAboveViewport(R row) {
		return false;
	}

	@Override
	protected List<Row<?, ?, ?>> findLassoSelectedRows() {
		List<Row<?, ?, ?>> rows = new ArrayList<>();

		Rectangle lasso = getLasso();

		Bounds localLassoBounds = lasso.getBoundsInLocal();
		Bounds sceneLassoBounds = lasso.localToScene(localLassoBounds);

		vbox.getChildren().stream().filter(node -> node instanceof RowPane<?>).forEach(node -> {
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

	@Override
	protected List<ActivityRef<?>> findLassoSelectedActivities() {
		List<ActivityRef<?>> selection = new ArrayList<>();

		Rectangle lasso = getLasso();

		Bounds localLassoBounds = lasso.getBoundsInLocal();
		Bounds sceneLassoBounds = lasso.localToScene(localLassoBounds);

		for (Node node : vbox.getChildren()) {
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