/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.project.model.business;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

public class Task {

	private String title = "";
	private LocalDate startDate = LocalDate.now().plusDays(1);
	private LocalDate endDate = LocalDate.now().plusDays(6);

	public Task() {
	}

	public Task(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		requireNonNull(title);
		this.title = title;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate date) {
		requireNonNull(date);
		this.startDate = date;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate date) {
		requireNonNull(date);
		this.endDate = date;
	}
}
