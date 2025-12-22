/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
/**
 *
 */
package com.flexganttfx.experimental;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.VBoxGraphics;
import com.flexganttfx.view.timeline.Timeline;

public class LensSkin<R extends Row<?, ?, ?>> extends SkinBase<Lens<R>> {

	private final VBoxGraphics<R> graphics;
	private final Timeline timeline;

	public LensSkin(Lens<R> lens) {
		super(lens);

		InvalidationListener updateListener = it -> updateView();

		lens.startIndexProperty().addListener(updateListener);
		lens.getRows().addListener(updateListener);

		timeline = new Timeline();
		timeline.setModel(lens.getGraphics().getTimeline().getModel());

		graphics = new VBoxGraphics<>();
		graphics.setTimeline(timeline);
		graphics.setPriorityCallback(row -> Priority.NEVER);
		Bindings.bindContent(graphics.getLayers(), lens.getGraphics()
				.getLayers());

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(timeline);
		borderPane.setCenter(graphics);

		getChildren().add(borderPane);

		updateView();
	}

	private void updateView() {
		List<R> rows = getSkinnable().getRows();
		int startIndex = getSkinnable().getStartIndex();

		List<R> result = new ArrayList<>();

		// DoubleProperty heightProperty = new SimpleDoubleProperty();
		// NumberBinding binding = Bindings.add(0,
		// timeline.prefHeightProperty());

		for (int i = 0; i < getSkinnable().getRowCount(); i++) {
			int index = startIndex + i;
			if (index >= rows.size()) {
				break;
			}

			R row = rows.get(index);
			row.getProperties().put("com.flexganttfx.row.showing", true);
			// binding = Bindings.add(binding, row.heightProperty());
			result.add(row);
		}

		graphics.getRows().setAll(result);
	}
}
