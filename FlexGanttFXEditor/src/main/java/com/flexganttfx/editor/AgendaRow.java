/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.editor;

import static com.flexganttfx.model.layout.AgendaLayout.LayoutStrategy.OVERLAPPING;

import java.time.LocalTime;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.AgendaLayout;

public class AgendaRow extends Row<AgendaRow, AgendaRow, AgendaEntry> {

	public AgendaRow(String name) {
		super(name);

		setHeight(300);
		setMinHeight(100);
		setMaxHeight(1200);

		AgendaLayout agendaLayout = new AgendaLayout();
		agendaLayout.setLayoutStrategy(OVERLAPPING);
		agendaLayout.setStartTime(LocalTime.of(7, 0));
		agendaLayout.setEndTime(LocalTime.of(17, 0));
		agendaLayout.setPadding(30);
		agendaLayout.setOverlapOffset(0);

		setLayout(agendaLayout);
	}
}
