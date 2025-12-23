/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.timeline;

import java.time.temporal.ChronoUnit;

import javafx.util.Callback;

public class ChronoUnitCellFactory
		implements Callback<ChronoUnit, ChronoUnitDatelineCell> {

	@Override
	public ChronoUnitDatelineCell call(ChronoUnit unit) {
		return new ChronoUnitDatelineCell();
	}
}
