/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
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

public class SingleRowGraphicsSkin<R extends Row<?, ?, ?>>
		extends GraphicsBaseSkin<SingleRowGraphics<R>, R> {

	private RowPane<R> rowPane;

	public SingleRowGraphicsSkin(SingleRowGraphics<R> graphics) {
		super(graphics);
	}

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

	@Override
	protected final List<Row<?, ?, ?>> findLassoSelectedRows() {
		List<Row<?, ?, ?>> list = new ArrayList<>();
		R row = rowPane.getRow();
		if (row != null) {
			list.add(row);
		}
		return list;
	}

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

	@Override
	protected final RowPane<R> getRowPaneAt(double y) {
		return rowPane;
	}

	@Override
	protected boolean isRowAboveViewport(R row) {
		return false;
	}
}