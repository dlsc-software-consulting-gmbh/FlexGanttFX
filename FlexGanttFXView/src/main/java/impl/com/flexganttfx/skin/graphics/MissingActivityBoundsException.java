/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import java.text.MessageFormat;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;

public class MissingActivityBoundsException extends Exception {

	private static final long serialVersionUID = -6806986448373406748L;
	private final Activity activity;
	private final Row<?, ?, ?> row;
	private final int lineIndex;
	private final ActivityRenderer<?> renderer;

	public MissingActivityBoundsException(ActivityRenderer<?> renderer,
			Activity activity, Row<?, ?, ?> row, int lineIndex) {
		super(
				MessageFormat
						.format("The renderer of type {0} returned no bounds after drawing activity \"{1}\" in row \"{2}\" (line index = {3}).",
								renderer.getClass(), activity.getName(),
								row.getName(), lineIndex));

		this.renderer = renderer;
		this.activity = activity;
		this.row = row;
		this.lineIndex = lineIndex;
	}

	public Activity getActivity() {
		return activity;
	}

	public Row<?, ?, ?> getRow() {
		return row;
	}

	public ActivityRenderer<?> getRenderer() {
		return renderer;
	}

	public int getLineIndex() {
		return lineIndex;
	}
}
