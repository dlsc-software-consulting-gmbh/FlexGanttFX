/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras;

import com.flexganttfx.extras.util.Messages;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * A simple row controls view with only one button called "Edit". Pressing the button
 * invokes {@link GraphicsBase#startRowEditing(Row)}.
 *
 * @param <R> the row type
 */
public class RowControls<R extends Row<?, ?, ?>> extends HBox {

	/**
	 * Constructs new row controls.
	 *
	 * @param graphics
	 *            the target graphics view
	 * @param row
	 *            the row for which the controls will be used
	 */
	public RowControls(GraphicsBase<R> graphics, R row) {
		setPickOnBounds(false);
		setMinSize(0, 0);
		setAlignment(Pos.TOP_RIGHT);
		setFillHeight(true);

		Button editButton = new Button(Messages.getString("RowControls.BUTTON_EDIT"));
		editButton.getStyleClass().add("row-controls-button");
		editButton.setOnAction(evt -> graphics.startRowEditing(row));
		getChildren().add(editButton);
	}
}