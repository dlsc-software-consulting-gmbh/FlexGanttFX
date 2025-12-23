/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.timeline;

import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.dateline.Resolution.Position;
import com.flexganttfx.model.util.SimpleUnit;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.DatelineCell;

import java.time.Instant;

public class SimpleUnitDatelineCell extends DatelineCell<SimpleUnit> {

	@Override
	public void update(Instant startTime, Instant endTime, Resolution<SimpleUnit> resolution, Dateline dateline, Position scalePosition) {
		super.update(startTime, endTime, resolution, dateline, scalePosition);
		setText(resolution.format(startTime, dateline.getZoneId()));
		getStyleClass().add("dateline-cell-simple");
	}
}
