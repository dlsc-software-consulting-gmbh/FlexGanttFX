/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.model;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.repository.IntervalTreeActivityRepository;

public class ModelObject<Parent extends Row<?, ?, ?>, Child extends Row<?, ?, ?>, A extends Activity> extends Row<Parent, Child, A> {

	public ModelObject() {
	}

	public final int getFlights() {
		IntervalTreeActivityRepository<A> repository = (IntervalTreeActivityRepository<A>) getRepository();
		return repository.getAllActivities().size();
	}
}
