/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.repository;

import com.flexganttfx.model.Activity;

public abstract class MutableActivityRepositoryBase<A extends Activity> extends ActivityRepositoryBase<A> implements MutableActivityRepository<A> {

	public MutableActivityRepositoryBase() {
	}
}
