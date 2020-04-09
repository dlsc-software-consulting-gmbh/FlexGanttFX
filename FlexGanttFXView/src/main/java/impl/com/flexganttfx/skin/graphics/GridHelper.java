/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.model.dateline.Resolution;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Timeline;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public final class GridHelper {

	private GridHelper() {
	}

	public static Instant grid(GraphicsBase<?> graphics, Instant time) {
		Instant time1 = GridHelper.grid(graphics, time, false);
		Instant time2 = GridHelper.grid(graphics, time, true);

		Duration diff1 = Duration.between(time, time1);
		Duration diff2 = Duration.between(time, time2);

		if (diff1.abs().compareTo(diff2.abs()) < 0) {
			return time1;
		} else {
			return time2;
		}
	}

	public static Instant grid(GraphicsBase<?> graphics, Instant time, boolean roundUp) {

		VirtualGrid<?> grid;

		if (graphics.isAutoGridEnabled()) {
			Timeline timeline = graphics.getTimeline();
			Dateline dateline = timeline.getDateline();
			Resolution<?> resolution = dateline.getScaleResolutions().get(0);
			grid = resolution.createGrid();
		} else {
			grid = graphics.getVirtualGrid();
		}

		if (grid != null) {
			Timeline timeline = graphics.getTimeline();
			Dateline dateline = timeline.getDateline();
			DayOfWeek firstDayOfWeek = dateline.getFirstDayOfWeek();
			ZoneId zoneId = dateline.getZoneId();
			time = grid.adjustTime(time, zoneId, roundUp, firstDayOfWeek);
		}

		return time;
	}

	public static LocalTime grid(GraphicsBase<?> graphics, LocalTime time, boolean roundUp) {
		/*
		 * There is no auto-grid for vertical local time calculations.
		 */
		VirtualGrid<?> grid = graphics.getVirtualGrid();

		if (grid != null) {
			time = grid.adjustTime(time, roundUp);
		}

		return time;
	}
}
