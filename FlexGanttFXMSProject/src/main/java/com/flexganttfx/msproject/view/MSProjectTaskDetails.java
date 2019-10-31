/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.msproject.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import net.sf.mpxj.Task;

import com.flexganttfx.msproject.model.MSProjectTaskRow;
import com.flexganttfx.view.graphics.GraphicsBase;

public class MSProjectTaskDetails extends GridPane {

	public MSProjectTaskDetails(GraphicsBase<MSProjectTaskRow> graphics,
			MSProjectTaskRow row) {
		setStyle("-fx-background-color: beige; -fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: .5px;");

		setHgap(10);
		setVgap(10);

		Task task = row.getTask();

		Label title = new Label(task.getName());
		title.setFont(Font.font(18));

		Label startLabel = new Label("Start");
		Label finishLabel = new Label("Finish");

		DatePicker startPicker = new DatePicker(
				LocalDate.from(LocalDateTime.ofInstant(task.getStart()
						.toInstant(), ZoneId.systemDefault())));

		DatePicker finishPicker = new DatePicker(
				LocalDate.from(LocalDateTime.ofInstant(task.getFinish()
						.toInstant(), ZoneId.systemDefault())));

		Label percentage = new Label(task.getPercentageComplete().toString()
				+ "%");
		percentage.setFont(Font.font(32));

		TextArea text = new TextArea(task.getNotes());
		text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		Button close = new Button("Close");
		close.setOnAction(evt -> graphics.stopRowEditing(row));

		add(title, 0, 0);
		GridPane.setColumnSpan(title, 4);

		add(startLabel, 0, 1);
		add(startPicker, 1, 1);
		add(finishLabel, 0, 2);
		add(finishPicker, 1, 2);

		add(percentage, 0, 3);
		GridPane.setColumnSpan(percentage, 2);
		GridPane.setHalignment(percentage, HPos.CENTER);

		add(text, 2, 1);
		GridPane.setRowSpan(text, 3);

		add(close, 3, 1);

		setMinSize(0, 0);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		setPrefHeight(200);
	}
}
