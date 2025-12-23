/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.timeline;

import javafx.util.Callback;

import com.flexganttfx.model.util.SimpleUnit;

public class SimpleUnitCellFactory implements
		Callback<SimpleUnit, SimpleUnitDatelineCell> {

	@Override
	public SimpleUnitDatelineCell call(SimpleUnit param) {
		return new SimpleUnitDatelineCell();
	}
}
