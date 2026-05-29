/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.emirates.model;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.EqualLinesManager;
import com.flexganttfx.model.repository.IntervalTreeActivityRepository;
import com.flexganttfx.model.util.ActivityHelper;

import java.util.List;

public class Aircraft extends ModelObject<Group, Row<?, ?, ?>, Flight> {

	public Aircraft(String name) {
		setName(name);
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
