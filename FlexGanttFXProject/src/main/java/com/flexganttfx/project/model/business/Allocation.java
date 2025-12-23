/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.project.model.business;

import java.time.Instant;

public class Allocation {

	private Instant startTime;
	private Instant endTime;
	private double allocation;

	public Allocation() {

	}

	public Instant getStartTime() {
		return startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public double getAllocation() {
		return allocation;
	}
}
