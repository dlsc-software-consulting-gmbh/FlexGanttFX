/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.msproject.view;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.msproject.model.MSProjectTaskActivity;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.CompletableActivityRenderer;
import com.flexganttfx.view.util.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.sf.mpxj.Task;

public class MSProjectTaskActivityRenderer extends CompletableActivityRenderer<MSProjectTaskActivity> {

	public MSProjectTaskActivityRenderer(GraphicsBase<?> graphics) {
		super(graphics, "MSProject Task");
		setCornersRounded(true);
	}

	@Override
	public ActivityBounds drawActivity(ActivityRef<MSProjectTaskActivity> ref,
			Position position, GraphicsContext gc, double x, double y,
			double width, double height, boolean selected, boolean focused,
			boolean highlighted, boolean pressed) {

		if (ref.getRow().getChildren().isEmpty()) {
			setFill(Color.CORAL);
			setStroke(Color.CORAL.darker());
		} else {
			setFill(Color.CADETBLUE);
			setStroke(Color.CADETBLUE.darker());
		}

		ActivityBounds bounds = super.drawActivity(ref, position, gc, x, y, width, height, selected, focused, highlighted, pressed);

		MSProjectTaskActivity taskActivity = ref.getActivity();
		Task task = taskActivity.getUserObject().getTask();
		String resourceNames = task.getResourceNames();

		drawText(ref, taskActivity.getName(), TextPosition.TRAILING, gc, x, y, width, height, selected, focused, highlighted, pressed);
		drawText(ref, resourceNames, TextPosition.LEADING, gc, x, y, width, height, selected, focused, highlighted, pressed);

		return bounds;
	}
	
	@Override
	protected void drawBorder(ActivityRef<MSProjectTaskActivity> activityRef, Position position, GraphicsContext gc,
			double x, double y, double w, double h, boolean selected, boolean hover, boolean highlighted,
			boolean pressed) {
		super.drawBorder(activityRef, position, gc, x, y, w, h, selected, hover, highlighted, pressed);
	}
}
