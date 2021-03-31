/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.project.model.view;

import static com.flexganttfx.project.view.TaskGanttChart.ROW_HEIGHT;
import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.ZoneOffset;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.flexganttfx.model.Row;
import com.flexganttfx.project.model.business.Task;
import com.flexganttfx.project.view.Layers;

public class TaskRow extends Row<TaskRow, TaskRow, TaskActivity> {

	private Task task;

	public TaskRow(Task task) {
		this();

		this.task = requireNonNull(task);

		TaskActivity activity = new TaskActivity(task);

		setTitle(task.getTitle());
		setStartDate(task.getStartDate());
		setEndDate(task.getEndDate());

		titleProperty().addListener(it -> task.setTitle(getTitle()));
		startDateProperty()
				.addListener(it -> task.setStartDate(getStartDate()));
		endDateProperty().addListener(it -> task.setEndDate(getEndDate()));

		startDateProperty().addListener(
				it -> applyNewStartTime(activity));
		endDateProperty().addListener(
				it -> applyNewEndTime(activity));

		addActivity(Layers.taskLayer, activity);
	}

	private void applyNewEndTime(TaskActivity activity) {
		System.out.println("applying new end time");
		removeActivity(Layers.taskLayer, activity);
		activity.setEndTime(getEndDate().atStartOfDay()
				.toInstant(ZoneOffset.UTC));
		addActivity(Layers.taskLayer, activity);
	}

	private void applyNewStartTime(TaskActivity activity) {
		System.out.println("applying new start time");
		removeActivity(Layers.taskLayer, activity);
		activity.setStartTime(getStartDate().atStartOfDay()
				.toInstant(ZoneOffset.UTC));
		addActivity(Layers.taskLayer, activity);
	}

	/**
	 * This constructor will only be used for the row that is used for creating
	 * new rows.
	 */
	public TaskRow() {
		setHeight(ROW_HEIGHT);
		setMinHeight(ROW_HEIGHT);
		setMaxHeight(ROW_HEIGHT);
	}

	public Task getTask() {
		return task;
	}

	private final StringProperty title = new SimpleStringProperty(this, "title");

	public final StringProperty titleProperty() {
		return title;
	}

	public final void setTitle(String title) {
		titleProperty().set(title);
	}

	public final String getTitle() {
		return titleProperty().get();
	}

	private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>(
			this, "startDate");

	public final ObjectProperty<LocalDate> startDateProperty() {
		return startDate;
	}

	public final void setStartDate(LocalDate date) {
		startDateProperty().set(date);
	}

	public final LocalDate getStartDate() {
		return startDateProperty().get();
	}

	// end date

	private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>(
			this, "endDate");

	public final ObjectProperty<LocalDate> endDateProperty() {
		return endDate;
	}

	public final void setEndDate(LocalDate date) {
		endDateProperty().set(date);
	}

	public final LocalDate getEndDate() {
		return endDateProperty().get();
	}

}
