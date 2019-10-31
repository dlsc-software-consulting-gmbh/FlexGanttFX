/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.model;

import java.util.List;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.EqualLinesManager;
import com.flexganttfx.model.repository.IntervalTreeActivityRepository;
import com.flexganttfx.model.util.ActivityHelper;

public class Aircraft extends ModelObject<Group, Row<?, ?, ?>, Flight> {

	public Aircraft(ROW row) {
		setName(row != null ? row.getResource() : "ROOT");
		setLinesManager(new AircraftLineManager(this));
	}

	public void updateInnerLines() {
		IntervalTreeActivityRepository<Flight> repository = (IntervalTreeActivityRepository<Flight>) getRepository();

		List<Flight> flights = repository.getAllActivities();

		int lineCount = 0;

		if (flights.size() > 1) {
			for (int i = 0; i < flights.size(); i++) {
				Flight checkedFlight = flights.get(i);
				checkedFlight.setLineIndex(0);
				if (i > 0) {
					int j = i - 1;
					do {
						Flight flight = flights.get(j);
						if (ActivityHelper.intersect(flight, checkedFlight)) {
							checkedFlight.setLineIndex(Math.max(
									checkedFlight.getLineIndex(),
									flight.getLineIndex() + 1));
						}
						j--;
					} while (j > -1);
				}
			}
		}

		for (int i = 0; i < flights.size(); i++) {
			lineCount = Math.max(lineCount, flights.get(i).getLineIndex() + 1);
		}

		if (lineCount > 1) {
			setLineCount(lineCount);
			setHeight(lineCount * Row.DEFAULT_ROW_HEIGHT);
		}
	}

	@Override
	public String toString() {
		return getName();
	}
}

class AircraftLineManager extends EqualLinesManager<Aircraft, Flight> {

	public AircraftLineManager(Aircraft aircraft) {
		super(aircraft);
	}

	@Override
	public int getLineIndex(Flight flight) {
		return flight.getLineIndex();
	}
}
